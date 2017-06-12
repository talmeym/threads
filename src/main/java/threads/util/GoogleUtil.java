package threads.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Event.ExtendedProperties;
import threads.data.*;
import threads.data.Thread;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport;
import static com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.UUID.fromString;
import static threads.util.DateUtil.isAllDay;
import static threads.util.DateUtil.makeStartOfDay;

public class GoogleUtil {

	private static final DateFormat s_dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat s_dateTimeFormat = new SimpleDateFormat("dd/MM/yy'T'HH:mm");

	private static final String s_FROM_GOOGLE = "From Google";
	private static final String s_APP_NAME = "Threads";
	private static final String s_THREADS_ID = "ThreadsID";

	private static final int s_COMP_UPDATED = 0;
	private static final int s_EVENT_UPDATED = 1;
	private static final int s_COMP_CREATED = 2;
	private static final int s_EVENT_DELETED = 3;

	private static final JsonFactory s_JSON_FACTORY = getDefaultInstance();
	private static HttpTransport s_httpTransport;

	private static final File s_credStoreDir = new File("credstore");

	private static final List<GoogleAccount> s_googleAccounts = new ArrayList<>();
	private static final List<Configuration> s_configurations = new ArrayList<>();
	private static final Map<GoogleAccount, List<UUID>> s_linkedComponents = new HashMap<>();

	static void initialise() throws GeneralSecurityException, IOException {
		s_httpTransport = newTrustedTransport();
		File[] x_folders = s_credStoreDir.listFiles();

		if(x_folders != null) {
			for(File x_clientFolder : x_folders) {
                addGoogleAccount(x_clientFolder, x_clientFolder.getName());
			}
		}
	}

    public synchronized static void addConfiguration(Configuration p_configuration) {
        s_configurations.add(p_configuration);
    }

	public synchronized static void removeConfiguration(Configuration p_configuration) {
        s_configurations.remove(p_configuration);
    }

	static boolean addNewGoogleAccount(String p_name) {
		try {
			File x_clientFolder = new File(s_credStoreDir, p_name);
            addGoogleAccount(x_clientFolder, p_name);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

    private synchronized static void addGoogleAccount(File x_clientFolder, String x_name) throws IOException {
        FileDataStoreFactory x_dataStoreFactory = new FileDataStoreFactory(x_clientFolder);
        Calendar x_client = new Calendar.Builder(s_httpTransport, s_JSON_FACTORY, authorize(x_dataStoreFactory)).setApplicationName(s_APP_NAME).build();
        s_googleAccounts.add(new GoogleAccount(x_name, x_client));
    }

	private static Credential authorize(DataStoreFactory p_dataStoreFactory) throws IOException {
		GoogleClientSecrets x_clientSecrets = GoogleClientSecrets.load(s_JSON_FACTORY, new InputStreamReader(GoogleUtil.class.getResourceAsStream("/client_secrets.json")));

		if (x_clientSecrets.getDetails().getClientId().startsWith("Enter") || x_clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar into .../src/main/resources/client_secrets.json");
			System.exit(1);
		}

		GoogleAuthorizationCodeFlow x_flow = new GoogleAuthorizationCodeFlow.Builder(s_httpTransport, s_JSON_FACTORY, x_clientSecrets, singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(p_dataStoreFactory).build();
		return new AuthorizationCodeInstalledApp(x_flow, new LocalServerReceiver()).authorize("user");
	}

	static synchronized void syncWithGoogle() {
		List<UUID> x_syncedComponents = new ArrayList<>();

		try {
			for (GoogleAccount x_googleAccount: s_googleAccounts) {
				int[] x_stats = new int[4]; // comp updated 0, event updated 1, comp created 2, event deleted 3
				Calendar x_client = x_googleAccount.getCalendarClient();

				for(Configuration x_configuration: s_configurations) {
				    Thread x_topLevelThread = x_configuration.getTopLevelThread();
                    String x_calendarId = findCalendar(x_topLevelThread, x_client);

                    if(x_calendarId != null) {
                        List<Event> x_events = getEvents(x_client, x_calendarId);

                        for (Event x_event : x_events) {
                            String x_summary = x_event.getSummary();
                            String x_description = x_event.getDescription();
                            Date x_start = getDate(x_event.getStart());
                            String x_threadsId = getThreadsId(x_event);

                            if (x_threadsId != null && !x_threadsId.trim().isEmpty()) {
                                Search x_search = new Search.Builder().withId(fromString(x_threadsId)).build();
                                List<Component> x_results = x_topLevelThread.search(x_search);
                                HasDueDate x_hasDueDate = (HasDueDate) (x_results.size() > 0 ? x_results.get(0) : null);

                                if (x_hasDueDate != null) {
                                    x_syncedComponents.add(x_hasDueDate.getId());

                                    Date x_componentModified = x_hasDueDate.getModifiedDate();
                                    Date x_eventModified = new Date(x_event.getUpdated().getValue());

                                    if (x_eventModified.after(x_componentModified)) {
                                        if (!(nullProofEqual(x_summary, x_hasDueDate.getText()) && nullProofEqual(x_description, x_hasDueDate.getNotes()) && nullProofEqual(x_start, x_hasDueDate.getDueDate()))) {
                                            x_hasDueDate.setText(x_summary);
                                            x_hasDueDate.setNotes(x_description);
                                            x_hasDueDate.setDueDate(x_start);
                                            x_stats[s_COMP_UPDATED] += 1;
                                        }
                                    } else {
                                        if (!(nullProofEqual(x_summary, x_hasDueDate.getText()) && nullProofEqual(x_description, x_hasDueDate.getNotes()) && nullProofEqual(x_start, x_hasDueDate.getDueDate()))) {
                                            x_client.events().update(x_calendarId, x_event.getId(), populateEvent(x_event, x_hasDueDate.getId(), x_hasDueDate.getText(), x_hasDueDate.getNotes(), x_hasDueDate.getDueDate())).execute();
                                            x_stats[s_EVENT_UPDATED] += 1;
                                        }
                                    }
                                } else {
                                    x_client.events().delete(x_calendarId, x_event.getId()).execute();
                                    x_stats[s_EVENT_DELETED] += 1;
                                }
                            } else {
                                Item x_item = new Item(x_summary, x_start);
                                x_item.setNotes(x_event.getDescription());
                                x_syncedComponents.add(x_item.getId());
                                Thread x_threadToAddTo = null;

                                for (ThreadItem x_threadItem : x_topLevelThread.getThreadItems()) {
                                    if (x_threadItem.getText().equals(s_FROM_GOOGLE) && x_threadItem instanceof Thread) {
                                        x_threadToAddTo = (Thread) x_threadItem;
                                        break;
                                    }
                                }

                                if (x_threadToAddTo == null) {
                                    x_threadToAddTo = new Thread(s_FROM_GOOGLE);
                                    x_topLevelThread.addThreadItem(x_threadToAddTo);
                                }

                                x_threadToAddTo.addThreadItem(x_item);

                                addThreadsIdToEvent(x_item.getId(), x_event);
                                x_client.events().patch(x_calendarId, x_event.getId(), x_event).execute();
                                x_stats[s_COMP_CREATED] += 1;
                            }
                        }

				        System.out.println("CalendarSync[" + s_dateTimeFormat.format(new Date()) + "][" + x_googleAccount.getName() + "][" + x_configuration.getXmlFile().getName() + "]: " + x_events.size() + " events from google, " + x_stats[s_COMP_UPDATED] + " components updated, " + x_stats[s_EVENT_UPDATED] + " events updated, " + x_stats[s_COMP_CREATED] + " components created, " + x_stats[s_EVENT_DELETED] + " events deleted.");
                    }
				}

				synchronized (s_linkedComponents) {
					s_linkedComponents.computeIfAbsent(x_googleAccount, k -> new ArrayList<>());
					s_linkedComponents.get(x_googleAccount).clear();
					s_linkedComponents.get(x_googleAccount).addAll(x_syncedComponents);
				}
			}
		} catch(Throwable t){
			System.out.println("CalendarSync[" + s_dateTimeFormat.format(new Date()) + "]: Error talking to Google: " + t.getClass().getName() + ":" + t.getMessage());
		}
	}

	private static String getThreadsId(Event x_event) {
		String x_threadsId = null;

		if(x_event.getExtendedProperties() != null && x_event.getExtendedProperties().getPrivate() != null) {
			x_threadsId = x_event.getExtendedProperties().getPrivate().get(s_THREADS_ID);
		}
		return x_threadsId;
	}

	private static Date getDate(EventDateTime x_startEvent) {
		DateTime x_date = x_startEvent.getDate();
		DateTime x_dateTime = x_startEvent.getDateTime();
		Date x_start = new Date(x_date != null ? x_date.getValue() : x_dateTime.getValue());

		if (x_date != null && x_date.isDateOnly()) {
			x_start = makeStartOfDay(x_start);
		}
		return x_start;
	}

	private static boolean nullProofEqual(Object p_obj1, Object p_obj2) {
		if(p_obj1 != null && p_obj2 != null) {
			return p_obj1.equals(p_obj2);
		}

		return p_obj1 == null && p_obj2 == null;
	}

	private static void addThreadsIdToEvent(UUID p_id, Event p_event) {
		ExtendedProperties x_extendedProperties = p_event.getExtendedProperties() != null ? p_event.getExtendedProperties() : new ExtendedProperties();
		HashMap<String, String> x_privates = x_extendedProperties.getPrivate() != null ? new HashMap<>(x_extendedProperties.getPrivate()) : new HashMap<>();
		x_privates.put(s_THREADS_ID, p_id.toString());
		x_extendedProperties.setPrivate(x_privates);
		p_event.setExtendedProperties(x_extendedProperties);
	}

	static void linkToGoogle(GoogleAccount p_googleAccount, List<HasDueDate> p_hasDueDates, ProgressCallBack... p_callbacks) {
		callBack(p_callbacks, c -> c.started(p_hasDueDates.size()));

        try {
			Calendar x_client = p_googleAccount.getCalendarClient();
			String x_calendarId = findOrCreateCalendar((Thread) p_hasDueDates.get(0).getHierarchy().get(0), x_client);
			List<Event> x_events = getEvents(x_client, x_calendarId);

			for(HasDueDate x_hasDueDate : p_hasDueDates) {
                if(findEvent(x_events, x_hasDueDate) == null) {
					x_client.events().insert(x_calendarId, populateEvent(new Event(), x_hasDueDate.getId(), x_hasDueDate.getText(), x_hasDueDate.getNotes(), x_hasDueDate.getDueDate())).execute();
				}

				callBack(p_callbacks, c -> c.progress(x_hasDueDate.getText()));

                synchronized(s_linkedComponents) {
                    s_linkedComponents.get(p_googleAccount).add(x_hasDueDate.getId());
                }
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
				if(x_event.getExtendedProperties() != null && x_event.getExtendedProperties().getPrivate() != null) {
					if(x_id.equals(x_event.getExtendedProperties().getPrivate().get(s_THREADS_ID))) {
						return x_event;
					}
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

	private static String findOrCreateCalendar(Thread p_topLevelThread, Calendar p_client) throws IOException {
        String x_id = findCalendar(p_topLevelThread, p_client);

        if (x_id == null) {
            com.google.api.services.calendar.model.Calendar x_entry = new com.google.api.services.calendar.model.Calendar();
            x_entry.setSummary("Threads - " + p_topLevelThread.getText());
            x_entry.setDescription(p_topLevelThread.getId().toString());
            com.google.api.services.calendar.model.Calendar x_calendar = p_client.calendars().insert(x_entry).execute();
            x_id = x_calendar.getId();
        }

        return x_id;
	}

    private static String findCalendar(Thread p_topLevelThread, Calendar p_client) throws IOException {
        CalendarList x_feed = p_client.calendarList().list().execute();

        if (x_feed.getItems() != null) {
            for (CalendarListEntry x_entry : x_feed.getItems()) {
                String x_id = x_entry.getId();
                String x_description = x_entry.getDescription();

                if(x_description != null && x_description.equals(p_topLevelThread.getId().toString())) {
					String x_expectedSummary = "Threads - " + p_topLevelThread.getText();

					if(!x_entry.getSummary().equals(x_expectedSummary)) {
                		x_entry.setSummary(x_expectedSummary);
						p_client.calendarList().patch(x_id, x_entry).execute();
					}

                    return x_id;
                }
            }
        }

        return null;
    }

    private static Event populateEvent(Event p_event, UUID p_id, String p_text, String p_notes, Date p_dueDate) {
		addThreadsIdToEvent(p_id, p_event);
		p_event.setSummary(p_text);
		p_event.setDescription(p_notes);

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

	public static boolean isLinked(Component p_component) {
		return googleAccount(p_component) != null;
	}

	public static GoogleAccount googleAccount(Component p_component) {
		synchronized (s_linkedComponents) {
			for(GoogleAccount x_googleAccount: s_linkedComponents.keySet()) {
				if(s_linkedComponents.get(x_googleAccount).contains(p_component.getId())) {
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