package threads.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.*;
import com.google.api.services.calendar.model.*;
import threads.data.*;
import threads.data.Thread;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.*;
import java.util.*;
import java.util.function.Consumer;

import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport;
import static java.util.Collections.*;
import static java.util.UUID.fromString;
import static threads.util.DateUtil.*;

public class GoogleUtil {

	private static final DateFormat s_dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final String s_FROM_GOOGLE = "From Google";
	private static final String s_NAME_TXT = "name.txt";

	private static final int s_COMP_UPDATED = 0;
	private static final int s_EVENT_UPDATED = 1;
	private static final int s_COMP_CREATED = 2;
	private static final int s_EVENT_DELETED = 3;

	private static final Map<GoogleAccount, List<UUID>> s_linkedComponents = new HashMap<>();
	private static Thread s_topLevelThread = null;

	private static final JsonFactory s_JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static FileDataStoreFactory s_dataStoreFactory;
	private static HttpTransport s_httpTransport;

	private static File s_credStoreDir = new File("credstore");
	private static List<GoogleAccount> s_googleAccounts = new ArrayList<>();

	static synchronized void initialise(Thread p_topLevelThread) throws GeneralSecurityException, IOException {
		s_topLevelThread = p_topLevelThread;
		s_httpTransport = newTrustedTransport();
		File[] x_folders = s_credStoreDir.listFiles();

		if(x_folders != null) {
			for(File x_clientFolder : x_folders) {
				s_dataStoreFactory = new FileDataStoreFactory(x_clientFolder);
				Calendar x_client = new Calendar.Builder(s_httpTransport, s_JSON_FACTORY, authorize()).setApplicationName("Threads").build();
				s_googleAccounts.add(new GoogleAccount(readNameFromFile(x_clientFolder), x_client));
			}
		}
	}

	static boolean addNewGoogleAccount(String p_name) {
		try {
			File x_clientFolder = new File(s_credStoreDir, "client_" + s_googleAccounts.size());
			s_dataStoreFactory = new FileDataStoreFactory(x_clientFolder);
			s_googleAccounts.add(new GoogleAccount(p_name, new Calendar.Builder(s_httpTransport, s_JSON_FACTORY, authorize()).setApplicationName("Threads").build()));
			writeNameToFile(p_name, x_clientFolder);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private static void writeNameToFile(String p_name, File p_clientFolder) throws IOException {
		BufferedWriter x_nameFileWriter = new BufferedWriter(new FileWriter(new File(p_clientFolder, s_NAME_TXT)));
		x_nameFileWriter.write(p_name, 0, p_name.length());
		x_nameFileWriter.flush();
		x_nameFileWriter.close();
	}

	private static String readNameFromFile(File p_clientFolder) throws IOException {
		try (BufferedReader x_nameFileReader = new BufferedReader(new FileReader(new File(p_clientFolder, s_NAME_TXT)))) {
			return x_nameFileReader.readLine();
		}
	}

	private static Credential authorize() throws IOException {
		GoogleClientSecrets x_clientSecrets = GoogleClientSecrets.load(s_JSON_FACTORY, new InputStreamReader(GoogleUtil.class.getResourceAsStream("/client_secrets.json")));

		if (x_clientSecrets.getDetails().getClientId().startsWith("Enter") || x_clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar into .../src/main/resources/client_secrets.json");
			System.exit(1);
		}

		GoogleAuthorizationCodeFlow x_flow = new GoogleAuthorizationCodeFlow.Builder(s_httpTransport, s_JSON_FACTORY, x_clientSecrets, singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(s_dataStoreFactory).build();
		return new AuthorizationCodeInstalledApp(x_flow, new LocalServerReceiver()).authorize("user");
	}

	static void syncWithGoogle() {
		List<UUID> x_syncedComponents = new ArrayList<>();
		int[] x_stats = new int[4]; // comp updated 0, event updated 1, comp created 2, event deleted 3

		try {
			for (GoogleAccount x_googleAccount: s_googleAccounts) {
				Calendar x_client = x_googleAccount.getCalendarClient();
				String x_calendarId = findCalendar(x_client);
				List<Event> x_events = getEvents(x_client, x_calendarId);

				for (Event x_event : x_events) {
					String x_summary = x_event.getSummary();
					String x_description = x_event.getDescription();

					DateTime x_date = x_event.getStart().getDate();
					DateTime x_dateTime = x_event.getStart().getDateTime();
					Date x_start = new Date(x_date != null ? x_date.getValue() : x_dateTime.getValue());

					if (x_date != null && x_date.isDateOnly()) {
						x_start = makeStartOfDay(x_start);
					}

					if (x_description != null && !x_description.trim().isEmpty()) {
						Search x_search = new Search.Builder().withId(fromString(x_description)).build();
						List<Component> x_results = s_topLevelThread.search(x_search);
						Component x_component = x_results.size() > 0 ? x_results.get(0) : null;

						if (x_component != null) {
							x_syncedComponents.add(x_component.getId());

							Date x_componentModified = x_component.getModifiedDate();
							Date x_eventModified = new Date(x_event.getUpdated().getValue());

							if (x_eventModified.after(x_componentModified)) {
								if (x_component instanceof Item) {
									Item x_item = (Item) x_component;

									if (!(x_summary.equals(x_item.getText()) && x_start.equals(x_item.getDueDate()))) {
										x_item.setText(x_summary);
										x_item.setDueDate(x_start);
										x_stats[s_COMP_UPDATED] += 1;
									}
								}
								if (x_component instanceof Reminder) {
									Reminder x_reminder = (Reminder) x_component;

									if (!(x_summary.equals(x_reminder.getText()) && x_start.equals(x_reminder.getDueDate()))) {
										x_reminder.setText(x_summary);
										x_reminder.setDueDate(x_start);
										x_stats[s_COMP_UPDATED] += 1;
									}
								}
							} else {
								if (x_component instanceof Item) {
									Item x_item = (Item) x_component;

									if (!(x_summary.equals(x_item.getText()) && x_start.equals(x_item.getDueDate()))) {
										x_client.events().update(x_calendarId, x_event.getId(), populateEvent(x_event, x_item.getId(), x_item.getText(), x_item.getDueDate())).execute();
										x_stats[s_EVENT_UPDATED] += 1;
									}
								}
								if (x_component instanceof Reminder) {
									Reminder x_reminder = (Reminder) x_component;

									if (!(x_summary.equals(x_reminder.getText()) && x_start.equals(x_reminder.getDueDate()))) {
										x_client.events().update(x_calendarId, x_event.getId(), populateEvent(x_event, x_reminder.getId(), x_reminder.getText(), x_reminder.getDueDate())).execute();
										x_stats[s_EVENT_UPDATED] += 1;
									}
								}
							}
						} else {
							x_client.events().delete(x_calendarId, x_event.getId()).execute();
							x_stats[s_EVENT_DELETED] += 1;
						}
					} else {
						Item x_item = new Item(x_summary, x_start);
						x_syncedComponents.add(x_item.getId());
						Thread x_threadToAddTo = null;

						for (ThreadItem x_threadItem : s_topLevelThread.getThreadItems()) {
							if (x_threadItem.getText().equals(s_FROM_GOOGLE) && x_threadItem instanceof Thread) {
								x_threadToAddTo = (Thread) x_threadItem;
								break;
							}
						}

						if (x_threadToAddTo == null) {
							x_threadToAddTo = new Thread(s_FROM_GOOGLE);
							s_topLevelThread.addThreadItem(x_threadToAddTo);
						}

						x_threadToAddTo.addThreadItem(x_item);

						x_event.setDescription(x_item.getId().toString());
						x_client.events().patch(x_calendarId, x_event.getId(), x_event).execute();
						x_stats[s_COMP_CREATED] += 1;
					}
				}

				synchronized (s_linkedComponents) {
					s_linkedComponents.computeIfAbsent(x_googleAccount, k -> new ArrayList<>());
					s_linkedComponents.get(x_googleAccount).clear();
					s_linkedComponents.get(x_googleAccount).addAll(x_syncedComponents);
				}

				System.out.println("Calendar Sync [" + new Date() + "][" + x_googleAccount.getName() + "]: " + x_events.size() + " events from google, " + x_stats[s_COMP_UPDATED] + " components updated, " + x_stats[s_EVENT_UPDATED] + " events updated, " + x_stats[s_COMP_CREATED] + " components created, " + x_stats[s_EVENT_DELETED] + " events deleted.");
			}
		} catch(Throwable t){
			System.out.println("Google Calendar Sync [" + new Date() + "]: Error talking to Google Calendar: " + t.getClass().getName() + ":" + t.getMessage());
		}
	}

	static void linkHasDueDatesToGoogle(GoogleAccount p_googleAccount, List<HasDueDate> p_hasDueDates, ProgressCallBack... p_callbacks) {
		callBack(p_callbacks, c -> c.started(p_hasDueDates.size()));

		try {
			Calendar x_client = p_googleAccount.getCalendarClient();
			String x_calendarId = findCalendar(x_client);
			List<Event> x_events = getEvents(x_client, x_calendarId);

			for(HasDueDate x_hasDueDate : p_hasDueDates) {
                if(findEvent(x_events, x_hasDueDate) == null) {
					x_client.events().insert(x_calendarId, populateEvent(new Event(), x_hasDueDate.getId(), x_hasDueDate.getText(), x_hasDueDate.getDueDate())).execute();
				}

				callBack(p_callbacks, c -> c.progress(x_hasDueDate.getText()));
				s_linkedComponents.get(p_googleAccount).add(x_hasDueDate.getId());
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

	private static String findCalendar(com.google.api.services.calendar.Calendar p_client) throws IOException {
		if(s_topLevelThread == null) {
			throw new IllegalStateException("Google not successfully synced");
		}

		CalendarList x_feed = p_client.calendarList().list().execute();

		if (x_feed.getItems() != null) {
			for (CalendarListEntry entry : x_feed.getItems()) {
				String x_id = entry.getId();
				String x_description = entry.getDescription();

				if(x_description != null && x_description.equals(s_topLevelThread.getId().toString())) {
					return x_id;
				}
			}
		}

		com.google.api.services.calendar.model.Calendar x_entry = new com.google.api.services.calendar.model.Calendar();
		x_entry.setSummary("Threads - " + s_topLevelThread.getText());
		x_entry.setDescription(s_topLevelThread.getId().toString());
		com.google.api.services.calendar.model.Calendar x_calendar = p_client.calendars().insert(x_entry).execute();
		return x_calendar.getId();
	}

	private static Event populateEvent(Event p_event, UUID p_id, String p_text, Date p_dueDate) {
		p_event.setSummary(p_text);
		p_event.setDescription(p_id.toString());

		if(isAllDay(p_dueDate)) {
			p_event.setStart(new EventDateTime().set("date", new DateTime(s_dateFormat.format(p_dueDate))));
			p_event.setEnd(new EventDateTime().set("date", new DateTime(s_dateFormat.format(p_dueDate))));
		} else {
			p_event.setStart(new EventDateTime().setDateTime(new DateTime(p_dueDate)));
			p_event.setEnd(new EventDateTime().setDateTime(new DateTime(p_dueDate)));
		}

		EventReminder x_popupReminder = new EventReminder();
		x_popupReminder.setMinutes(0);
		x_popupReminder.setMethod("popup");

		Event.Reminders x_reminders = new Event.Reminders();
		x_reminders.setOverrides(singletonList(x_popupReminder));
		x_reminders.setUseDefault(false);
		p_event.setReminders(x_reminders);

		return p_event;
	}

	private static List<Event> getEvents(com.google.api.services.calendar.Calendar x_client, String p_calendarId) throws IOException {
		List<Event> x_events = new ArrayList<>();
		Events response = x_client.events().list("primary").setCalendarId(p_calendarId).execute();
		x_events.addAll(response.getItems());
		String nextPageToken = response.getNextPageToken();

		while(nextPageToken != null) {
			response = x_client.events().list("primary").setCalendarId(p_calendarId).setPageToken(nextPageToken).execute();
			x_events.addAll(response.getItems());
			nextPageToken = response.getNextPageToken();
		}

		return x_events;
	}

	public static boolean isLinked(Component component) {
		return googleAccount(component) != null;
	}

	public static GoogleAccount googleAccount(Component component) {
		synchronized (s_linkedComponents) {
			for(GoogleAccount x_googleAccount: s_linkedComponents.keySet()) {
				if(s_linkedComponents.get(x_googleAccount).contains(component.getId())) {
					return x_googleAccount;
				}
			}

			return null;
		}
	}

	public static List<GoogleAccount> getGoogleAccounts() {
		return new ArrayList<>(s_googleAccounts); // defensive copy
	}
}