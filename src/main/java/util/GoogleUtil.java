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
import com.google.api.client.util.store.DataStoreFactory;
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

	/**
	 * Be sure to specify the name of your application. If the application name is {@code null} or
	 * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
	 */
	private static final String APPLICATION_NAME = "Threads";

	/** Directory to store user credentials. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File("credstore");

	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
	 * globally shared instance across your application.
	 */
	private static FileDataStoreFactory dataStoreFactory;

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static com.google.api.services.calendar.Calendar client;

	/** Authorizes the installed application to access user's protected data. */
	private static Credential authorize() throws Exception {
		// load client secrets
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(GoogleUtil.class.getResourceAsStream("/client_secrets.json")));

		if (clientSecrets.getDetails().getClientId().startsWith("Enter") || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar into calendar-cmdline-sample/src/main/resources/client_secrets.json");
			System.exit(1);
		}

		// set up authorization code flow
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory).build();

		// authorize
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	public static synchronized void syncWithGoogle(Thread thread) {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			Credential credential = authorize();
			client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

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

						if(component != null && component.isActive()) {
							Date componentModified = component.getModifiedDate();
							Date eventModified = new Date(event.getUpdated().getValue());

							if(eventModified.after(componentModified)) {
								if(component instanceof Item) {
									Item item = (Item) component;

									if(!summary.equals(item.getText())) {
										item.setText(summary);
									}
									if(!start.equals(item.getDueDate())) {
										item.setDueDate(start);
									}
								}
								if(component instanceof Reminder) {
									Reminder reminder = (Reminder) component;

									if(!summary.equals(reminder.getText())) {
										reminder.setText(summary);
									}
									if(!start.equals(reminder.getDueDate())) {
										reminder.setDueDate(start);
									}
								}
							} else {
								if(component instanceof Item) {
									Item item = (Item) component;
									populateEvent(event, item.getId(), item.getText(), item.getDueDate());
									client.events().update(calendarId, event.getId(), event).execute();
								}
								if(component instanceof Reminder) {
									Reminder reminder = (Reminder) component;
									populateEvent(event, reminder.getId(), reminder.getText(), reminder.getDueDate());
									client.events().update(calendarId, event.getId(), event).execute();
								}
							}
						} else {
							client.events().delete(calendarId, event.getId()).execute();
						}
					} else {
						Item item = new Item(summary);
						item.setDueDate(start);
						Thread threadToAddTo = null;

						for(int i = 0; i< thread.getThreadItemCount(); i++) {
							ThreadItem threadItem = thread.getThreadItem(i);

							if(threadItem.getText().equals(FROM_GOOGLE) && threadItem instanceof Thread) {
								threadToAddTo = (Thread) threadItem;
							}
						}

						if(threadToAddTo == null) {
							threadToAddTo = new Thread(FROM_GOOGLE);
							thread.addThreadItem(threadToAddTo);
						}

						threadToAddTo.addThreadItem(item);

						event.setDescription(item.getId().toString());
						client.events().patch(calendarId, event.getId(), event).execute();
					}
				}
			}
		} catch (Throwable t) {
//			System.out.println("Error talking to Google: " + t.getMessage());
			t.printStackTrace();
		}

		System.out.println("Google Sync Done.");
	}

	public static synchronized void linkItemsToGoogle(List<Item> items, ProgressCallBack... callbacks) {
		if(callbacks != null) {
			for(ProgressCallBack callback: callbacks) {
				callback.started(items.size());
			}
		}

		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			Credential credential = authorize();
			client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

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
				}

				if (callbacks != null) {
					for(ProgressCallBack callback: callbacks) {
						callback.progress(item.getText());
					}
				}
			}

			if(callbacks != null) {
				for(ProgressCallBack callback: callbacks) {
					callback.finished();
				}
			}
		} catch (Throwable t) {
//			System.out.println("Error talking to Google: " + t.getMessage());
			t.printStackTrace();
		}
	}

	public static synchronized void linkRemindersToGoogle(List<Reminder> reminders, ProgressCallBack... callbacks) {
		if(callbacks != null) {
			for(ProgressCallBack callback: callbacks) {
				callback.started(reminders.size());
			}
		}

		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			Credential credential = authorize();
			client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

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
			}

			if(callbacks != null) {
				for(ProgressCallBack callback: callbacks) {
					callback.finished();
				}
			}
		} catch (Throwable t) {
			System.out.println("Error talking to Google: " + t.getMessage());
		}
	}

	private static String findCalendar() throws IOException {
		CalendarList feed = client.calendarList().list().execute();

		if (feed.getItems() != null) {
			for (CalendarListEntry entry : feed.getItems()) {
				String id = entry.getId();
				String summary = entry.getSummary();

				if(summary.equals("Threads")) {
					return id;
				}
			}
		}

		Calendar entry = new Calendar();
		entry.setSummary("Threads");
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
}