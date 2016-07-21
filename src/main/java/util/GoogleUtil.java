package util;

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
import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Calendar;
import data.*;
import data.Thread;
import gui.ProgressCallBack;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.*;
import java.util.*;

public class GoogleUtil {

	public static final DateFormat s_dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final String FROM_GOOGLE = "From Google";

	public static final int COMP_UPDATED = 0;
	public static final int EVENT_UPDATED = 1;
	public static final int COMP_CREATED = 2;
	public static final int EVENT_DELETED = 3;

	public static final List<UUID> linkedComponents = new ArrayList<UUID>();
	public static Thread s_topLevelThread = null;

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static FileDataStoreFactory dataStoreFactory;
	private static HttpTransport httpTransport;
	private static com.google.api.services.calendar.Calendar client;

	private static Credential authorize() throws Exception {
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(GoogleUtil.class.getResourceAsStream("/client_secrets.json")));

		if (clientSecrets.getDetails().getClientId().startsWith("Enter") || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar into calendar-cmdline-sample/src/main/resources/client_secrets.json");
			System.exit(1);
		}

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory).build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	private static synchronized void initialise() throws Exception {
		if(client == null) {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(new java.io.File("credstore"));
			Credential credential = authorize();
			client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName("Threads").build();
		}
	}

	public static void syncWithGoogle(Thread thread) {
		s_topLevelThread = thread;
		List<UUID> syncedComponents = new ArrayList<UUID>();
		int[] stats = new int[4]; // comp updated 0, event updated 1, comp created 2, event deleted 3

		try {
			initialise();
			String calendarId = findCalendar();
			List<Event> events = getEvents(calendarId);

			if(events != null) {
				for(Event event: events) {
					String summary = event.getSummary();
					String description = event.getDescription();

					DateTime date = event.getStart().getDate();
					DateTime dateTime = event.getStart().getDateTime();
					Date start = new Date(date != null ? date.getValue() : dateTime.getValue());

					// TODO hack ??
					if(date != null && date.isDateOnly()) {
						start = DateUtil.makeStartOfDay(start);
					}

					if(description != null) {
						UUID id = UUID.fromString(description);
						Component component = thread.findComponent(id);

						if(component != null) {
							syncedComponents.add(component.getId());

							Date componentModified = component.getModifiedDate();
							Date eventModified = new Date(event.getUpdated().getValue());

							if(eventModified.after(componentModified)) {
								if(component instanceof Item) {
									Item item = (Item) component;

									if(!(summary.equals(item.getText()) && start.equals(item.getDueDate()))) {
										item.setText(summary);
										item.setDueDate(start);
										stats[COMP_UPDATED] += 1;
									}
								}
								if(component instanceof Reminder) {
									Reminder reminder = (Reminder) component;

									if(!(summary.equals(reminder.getText()) && start.equals(reminder.getDueDate()))) {
										reminder.setText(summary);
										reminder.setDueDate(start);
										stats[COMP_UPDATED] += 1;
									}
								}
							} else {
								if(component instanceof Item) {
									Item item = (Item) component;

									if(!(summary.equals(item.getText()) && start.equals(item.getDueDate()))) {
										populateEvent(event, item.getId(), item.getText(), item.getDueDate());
										client.events().update(calendarId, event.getId(), event).execute();
										stats[EVENT_UPDATED] += 1;
									}
								}
								if(component instanceof Reminder) {
									Reminder reminder = (Reminder) component;

									if(!(summary.equals(reminder.getText()) && start.equals(reminder.getDueDate()))) {
										populateEvent(event, reminder.getId(), reminder.getText(), reminder.getDueDate());
										client.events().update(calendarId, event.getId(), event).execute();
										stats[EVENT_UPDATED] += 1;
									}
								}
							}
						} else {
							client.events().delete(calendarId, event.getId()).execute();
							stats[EVENT_DELETED] += 1;
						}
					} else {
						Item item = new Item(summary);
						item.setDueDate(start);
						Thread threadToAddTo = null;
						syncedComponents.add(item.getId());

						for(int i = 0; i< thread.getThreadItemCount(); i++) {
							ThreadItem threadItem = thread.getThreadItem(i);

							if(threadItem.getText().equals(FROM_GOOGLE) && threadItem instanceof Thread) {
								threadToAddTo = (Thread) threadItem;
								break;
							}
						}

						if(threadToAddTo == null) {
							threadToAddTo = new Thread(FROM_GOOGLE);
							thread.addThreadItem(threadToAddTo);
						}

						threadToAddTo.addThreadItem(item);

						event.setDescription(item.getId().toString());
						client.events().patch(calendarId, event.getId(), event).execute();
						stats[COMP_CREATED] += 1;
					}
				}
			}

			synchronized (linkedComponents) {
				linkedComponents.clear();
				linkedComponents.addAll(syncedComponents);
			}

			System.out.println("Google Calendar Sync: " + stats[COMP_UPDATED] + " components updated, " + stats[EVENT_UPDATED] + " events updated, " + stats[COMP_CREATED] + " components created, " + stats[EVENT_DELETED] + " events deleted.");
		} catch (Throwable t) {
			System.out.println("Error talking to Google Calendar: " + t.getClass().getName() + ":" + t.getMessage());
		}
	}

	public static void linkItemsToGoogle(List<Item> items, ProgressCallBack... callbacks) {
		if(callbacks != null) {
			int x_count = LookupHelper.countActiveSyncableComponents(items);

			for(ProgressCallBack callback: callbacks) {
				callback.started(x_count);
			}
		}

		try {
			initialise();
			String calendarId = findCalendar();
			List<Event> events = getEvents(calendarId);

			for(Item item : items) {
				boolean foundEvent = false;

				if(events != null) {
					for(Event event: events) {
						if(item.getId().toString().equals(event.getDescription())) {
							populateEvent(event, item.getId(), item.getText(), item.getDueDate());
							client.events().update(calendarId, event.getId(), event).execute();
							foundEvent = true;
						}
					}
				}

				if(!foundEvent) {
					addEvent(calendarId, item);
				}

				for(int i = 0; i < item.getReminderCount(); i++) {
					Reminder reminder = item.getReminder(i);

					if(reminder.isActive()) {
						foundEvent = false;

						if(events != null) {
							for(Event event: events) {
								if(reminder.getId().toString().equals(event.getDescription())) {
									populateEvent(event, reminder.getId(), reminder.getText(), reminder.getDueDate());
									client.events().update(calendarId, event.getId(), event).execute();
									foundEvent = true;
								}
							}
						}

						if(!foundEvent) {
							addEvent(calendarId, reminder);
						}
					}

					if (callbacks != null) {
						for(ProgressCallBack callback: callbacks) {
							callback.progress(reminder.getText());
						}
					}

					linkedComponents.add(reminder.getId());
				}

				if (callbacks != null) {
					for(ProgressCallBack callback: callbacks) {
						callback.progress(item.getText());
					}
				}

				linkedComponents.add(item.getId());
			}

			if(callbacks != null) {
				for(ProgressCallBack callback: callbacks) {
					callback.finished();
				}
			}

			GoogleSyncer.getInstance().googleSynced();
		} catch (Throwable t) {
			System.out.println("Error talking to Google Calendar: " + t.getClass().getName() + ":" + t.getMessage());
		}
	}

	public static void linkRemindersToGoogle(List<Reminder> reminders, ProgressCallBack... callbacks) {
		if(callbacks != null) {
			for(ProgressCallBack callback: callbacks) {
				callback.started(reminders.size());
			}
		}

		try {
			initialise();
			String calendarId = findCalendar();
			List<Event> events = getEvents(calendarId);

			for(Reminder reminder : reminders) {
				boolean foundEvent = false;

				if(events != null) {
					for(Event event: events) {
						if(reminder.getId().toString().equals(event.getDescription())) {
							populateEvent(event, reminder.getId(), reminder.getText(), reminder.getDueDate());
							client.events().update(calendarId, event.getId(), event).execute();
							foundEvent = true;
						}
					}
				}

				if(!foundEvent) {
					addEvent(calendarId, reminder);
				}

				if (callbacks != null) {
					for(ProgressCallBack callback: callbacks) {
						callback.progress(reminder.getText());
					}
				}

				linkedComponents.add(reminder.getId());
			}

			if(callbacks != null) {
				for(ProgressCallBack callback: callbacks) {
					callback.finished();
				}
			}

			GoogleSyncer.getInstance().googleSynced();
		} catch (Throwable t) {
			System.out.println("Error talking to Google: " + t.getClass().getName() + ":" + t.getMessage());
		}
	}

	private static String findCalendar() throws IOException {
		if(s_topLevelThread == null) {
			throw new IllegalStateException("Google not successfully synced");
		}

		CalendarList feed = client.calendarList().list().execute();

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
		entry.setSummary("Threads - " + s_topLevelThread.getText());
		entry.setDescription(s_topLevelThread.getId().toString());
		Calendar calendar = client.calendars().insert(entry).execute();
		return calendar.getId();
	}

	private static String addEvent(String calendarId, Item item) throws IOException {
		return client.events().insert(calendarId, populateEvent(new Event(), item.getId(), item.getText(), item.getDueDate())).execute().getId();
	}

	private static String addEvent(String calendarId, Reminder reminder) throws IOException {
		return client.events().insert(calendarId, populateEvent(new Event(), reminder.getId(), reminder.getText(), reminder.getDueDate())).execute().getId();
	}

	private static Event populateEvent(Event event, UUID id, String text, Date dueDate) {
		event.setSummary(text);
		event.setDescription(id.toString());

		if(DateUtil.isAllDay(dueDate)) {
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
		reminders.setOverrides(Arrays.asList(popupReminder));
		reminders.setUseDefault(false);
		event.setReminders(reminders);

		return event;
	}

	private static List<Event> getEvents(String calendarId) throws IOException {
		Events feed = client.events().list(calendarId).execute();
		return feed.getItems();
	}

	public static boolean isLinked(Component component) {
		synchronized (linkedComponents) {
			return linkedComponents.contains(component.getId());
		}
	}
}