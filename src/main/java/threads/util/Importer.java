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
import com.google.api.services.calendar.model.*;
import threads.Threads;
import threads.data.Configuration;
import threads.data.Item;
import threads.data.Thread;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static threads.util.DateUtil.makeStartOfDay;

public class Importer {
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

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        s_httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        s_dataStoreFactory = new FileDataStoreFactory(new File("credstore"));
        s_client = new com.google.api.services.calendar.Calendar.Builder(s_httpTransport, s_JSON_FACTORY, authorize()).setApplicationName("Threads").build();

        CalendarList x_feed = s_client.calendarList().list().execute();

        Thread x_topLevelThread = new Thread("Archive");

        if (x_feed.getItems() != null) {
            for (CalendarListEntry x_entry : x_feed.getItems()) {
                String x_summary = x_entry.getSummary();

                if(!x_summary.contains("Threads")) {
                    Thread x_thread = new Thread(x_summary);
                    x_topLevelThread.addThreadItem(x_thread);

                    List<Event> x_eventList = getEvents(x_entry.getId());

                    for(Event x_event: x_eventList) {
                        EventDateTime x_start = x_event.getStart();

                        if(x_start != null) {
                            DateTime x_date = x_start.getDate();
                            DateTime x_dateTime = x_start.getDateTime();
                            Date x_dueDate = new Date(x_date != null ? x_date.getValue() : x_dateTime.getValue());

                            if(x_date != null && x_date.isDateOnly()) {
                                x_dueDate = makeStartOfDay(x_dueDate);
                            }

                            Item x_item = new Item(x_event.getSummary(), x_dueDate);
                            x_item.setNotes(x_event.getDescription());
                            x_item.setActive(false);

                            System.out.println(x_event.getSummary());

                            x_thread.addThreadItem(x_item);
                        }
                    }
                }
            }
        }

        File x_file = new File("archive.xml");
        new Threads(new Configuration(x_file, x_topLevelThread, new ArrayList<>()));
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
}
