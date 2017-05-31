package threads.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.*;
import threads.data.*;
import threads.data.Thread;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

import static java.util.UUID.fromString;
import static threads.util.DateUtil.isAllDay;
import static threads.util.DateUtil.makeStartOfDay;

public class GoogleUtil {

	private static final DateFormat s_dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final String s_FROM_GOOGLE = "From Google";

	private static final int s_COMP_UPDATED = 0;
	private static final int s_EVENT_UPDATED = 1;
	private static final int s_COMP_CREATED = 2;
	private static final int s_EVENT_DELETED = 3;

	private static final List<UUID> s_linkedComponents = new ArrayList<>();
	private static Thread s_topLevelThread = null;

	private static final JsonFactory s_JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static FileDataStoreFactory s_dataStoreFactory;
	private static HttpTransport s_httpTransport;
	private static com.google.api.services.calendar.Calendar s_client;

	private static Credential authorize() throws IOException {
		GoogleClientSecrets x_clientSecrets = GoogleClientSecrets.load(s_JSON_FACTORY, new InputStreamReader(GoogleUtil.class.getResourceAsStream("/client_secrets.json")));

		if (x_clientSecrets.getDetails().getClientId().startsWith("Enter") || x_clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar into calendar-cmdline-sample/src/main/resources/client_secrets.json");
			System.exit(1);
		}

		GoogleAuthorizationCodeFlow x_flow = new GoogleAuthorizationCodeFlow.Builder(s_httpTransport, s_JSON_FACTORY, x_clientSecrets, Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(s_dataStoreFactory).build();
		return new AuthorizationCodeInstalledApp(x_flow, new LocalServerReceiver()).authorize("user");
	}

	static synchronized void initialise(Thread p_topLevelThread) throws GeneralSecurityException, IOException {
		s_topLevelThread = p_topLevelThread;

		if(s_client == null) {
			s_httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			s_dataStoreFactory = new FileDataStoreFactory(new File("credstore"));
			s_client = new com.google.api.services.calendar.Calendar.Builder(s_httpTransport, s_JSON_FACTORY, authorize()).setApplicationName("threads.Threads").build();
		}
	}

	static void syncWithGoogle() {
		List<UUID> x_syncedComponents = new ArrayList<>();
		int[] x_stats = new int[4]; // comp updated 0, event updated 1, comp created 2, event deleted 3

		try {
			String x_calendarId = findCalendar();
			List<Event> x_events = getEvents(x_calendarId);

			if(x_events != null) {
				for(Event x_event: x_events) {
					String x_summary = x_event.getSummary();
					String x_description = x_event.getDescription();

					DateTime x_date = x_event.getStart().getDate();
					DateTime x_dateTime = x_event.getStart().getDateTime();
					Date x_start = new Date(x_date != null ? x_date.getValue() : x_dateTime.getValue());

					if(x_date != null && x_date.isDateOnly()) {
						x_start = makeStartOfDay(x_start);
					}

					if(x_description != null && !x_description.trim().isEmpty()) {
						Search x_search = new Search.Builder().withId(fromString(x_description)).build();
						List<Component> x_results = s_topLevelThread.search(x_search);
						Component x_component = x_results.size() > 0 ? x_results.get(0) : null;

						if(x_component != null) {
							x_syncedComponents.add(x_component.getId());

							Date x_componentModified = x_component.getModifiedDate();
							Date x_eventModified = new Date(x_event.getUpdated().getValue());

							if(x_eventModified.after(x_componentModified)) {
								if(x_component instanceof Item) {
									Item x_item = (Item) x_component;

									if(!(x_summary.equals(x_item.getText()) && x_start.equals(x_item.getDueDate()))) {
										x_item.setText(x_summary);
										x_item.setDueDate(x_start);
										x_stats[s_COMP_UPDATED] += 1;
									}
								}
								if(x_component instanceof Reminder) {
									Reminder x_reminder = (Reminder) x_component;

									if(!(x_summary.equals(x_reminder.getText()) && x_start.equals(x_reminder.getDueDate()))) {
										x_reminder.setText(x_summary);
										x_reminder.setDueDate(x_start);
										x_stats[s_COMP_UPDATED] += 1;
									}
								}
							} else {
								if(x_component instanceof Item) {
									Item x_item = (Item) x_component;

									if(!(x_summary.equals(x_item.getText()) && x_start.equals(x_item.getDueDate()))) {
										s_client.events().update(x_calendarId, x_event.getId(), populateEvent(x_event, x_item.getId(), x_item.getText(), x_item.getDueDate())).execute();
										x_stats[s_EVENT_UPDATED] += 1;
									}
								}
								if(x_component instanceof Reminder) {
									Reminder x_reminder = (Reminder) x_component;

									if(!(x_summary.equals(x_reminder.getText()) && x_start.equals(x_reminder.getDueDate()))) {
										s_client.events().update(x_calendarId, x_event.getId(), populateEvent(x_event, x_reminder.getId(), x_reminder.getText(), x_reminder.getDueDate())).execute();
										x_stats[s_EVENT_UPDATED] += 1;
									}
								}
							}
						} else {
							s_client.events().delete(x_calendarId, x_event.getId()).execute();
							x_stats[s_EVENT_DELETED] += 1;
						}
					} else {
						Item x_item = new Item(x_summary, x_start);
						x_syncedComponents.add(x_item.getId());
						Thread x_threadToAddTo = null;

						for(ThreadItem x_threadItem: s_topLevelThread.getThreadItems()) {
							if(x_threadItem.getText().equals(s_FROM_GOOGLE) && x_threadItem instanceof Thread) {
								x_threadToAddTo = (Thread) x_threadItem;
								break;
							}
						}

						if(x_threadToAddTo == null) {
							x_threadToAddTo = new Thread(s_FROM_GOOGLE);
							s_topLevelThread.addThreadItem(x_threadToAddTo);
						}

						x_threadToAddTo.addThreadItem(x_item);

						x_event.setDescription(x_item.getId().toString());
							s_client.events().patch(x_calendarId, x_event.getId(), x_event).execute();
						x_stats[s_COMP_CREATED] += 1;
					}
				}
			}

			synchronized (s_linkedComponents) {
				s_linkedComponents.clear();
				s_linkedComponents.addAll(x_syncedComponents);
			}

			System.out.println("Google Calendar Sync [" + new Date() + "]: " + x_events.size() + " events from google, " + x_stats[s_COMP_UPDATED] + " components updated, " + x_stats[s_EVENT_UPDATED] + " events updated, " + x_stats[s_COMP_CREATED] + " components created, " + x_stats[s_EVENT_DELETED] + " events deleted.");
		} catch (Throwable t) {
			System.out.println("Google Calendar Sync [" + new Date() + "]: Error talking to Google Calendar: " + t.getClass().getName() + ":" + t.getMessage());
		}
	}

	static void linkHasDueDatesToGoogle(List<HasDueDate> p_hasDueDates, ProgressCallBack... p_callbacks) {
		callBack(p_callbacks, c -> c.started(p_hasDueDates.size()));

		try {
			String x_calendarId = findCalendar();
			List<Event> x_events = getEvents(x_calendarId);

			for(HasDueDate x_hasDueDate : p_hasDueDates) {
                if(findEvent(x_events, x_hasDueDate) == null) {
					s_client.events().insert(x_calendarId, populateEvent(new Event(), x_hasDueDate.getId(), x_hasDueDate.getText(), x_hasDueDate.getDueDate())).execute();
				}

				callBack(p_callbacks, c -> c.progress(x_hasDueDate.getText()));
				s_linkedComponents.add(x_hasDueDate.getId());
			}

			callBack(p_callbacks, ProgressCallBack::success);
			GoogleSyncer.getInstance().componentsSynced(p_hasDueDates);
		} catch (Throwable t) {
			callBack(p_callbacks, c -> c.error(t.getMessage()));
		}
	}

	private static Event findEvent(List<Event> x_events, HasDueDate x_item) {
		String x_id = x_item.getId().toString();

		if(x_events != null) {
			for(Event x_event: x_events) {
				if(x_id.equals(x_event.getDescription())) {
					return x_event;
				}
			}
		}

		return null;
	}

	private static void callBack(ProgressCallBack[] p_callbacks, Consumer<ProgressCallBack> function) {
		for(ProgressCallBack callback: p_callbacks) {
			function.accept(callback);
		}
	}

	private static String findCalendar() throws IOException {
		if(s_topLevelThread == null) {
			throw new IllegalStateException("Google not successfully synced");
		}

		CalendarList feed = s_client.calendarList().list().execute();

		if (feed.getItems() != null) {
			for (CalendarListEntry entry : feed.getItems()) {
				String id = entry.getId();
				String description = entry.getDescription();

				if(description != null && description.equals(s_topLevelThread.getId().toString())) {
					return id;
				}
			}
		}

		Calendar entry = new Calendar();
		entry.setSummary("threads.Threads - " + s_topLevelThread.getText());
		entry.setDescription(s_topLevelThread.getId().toString());
		Calendar calendar = s_client.calendars().insert(entry).execute();
		return calendar.getId();
	}

	private static Event populateEvent(Event event, UUID id, String text, Date dueDate) {
		event.setSummary(text);
		event.setDescription(id.toString());

		if(isAllDay(dueDate)) {
			event.setStart(new EventDateTime().set("date", new DateTime(s_dateFormat.format(dueDate))));
			event.setEnd(new EventDateTime().set("date", new DateTime(s_dateFormat.format(dueDate))));
		} else {
			event.setStart(new EventDateTime().setDateTime(new DateTime(dueDate)));
			event.setEnd(new EventDateTime().setDateTime(new DateTime(dueDate)));
		}

		EventReminder popupReminder = new EventReminder();
		popupReminder.setMinutes(0);
		popupReminder.setMethod("popup");

		Event.Reminders reminders = new Event.Reminders();
		reminders.setOverrides(Collections.singletonList(popupReminder));
		reminders.setUseDefault(false);
		event.setReminders(reminders);

		return event;
	}

	private static List<Event> getEvents(String calendarId) throws IOException {
		Events response = s_client.events().list("primary").setCalendarId(calendarId).execute();
		List<Event> events = response.getItems();
		String nextPageToken = response.getNextPageToken();

		while(nextPageToken != null) {
			response = s_client.events().list("primary").setCalendarId(calendarId).setPageToken(nextPageToken).execute();
			events.addAll(response.getItems());
			nextPageToken = response.getNextPageToken();
		}

		return events;
	}

	public static boolean isLinked(Component component) {
		synchronized (s_linkedComponents) {
			return s_linkedComponents.contains(component.getId());
		}
	}
}