package threads.util;

import com.google.api.services.calendar.Calendar;

public class GoogleAccount {
	private final String o_name;
	private final Calendar o_calendarClient;

	GoogleAccount(String p_name, Calendar p_calendarClient) {
		o_name = p_name;
		o_calendarClient = p_calendarClient;
	}

	public String getName() {
		return o_name;
	}

	Calendar getCalendarClient() {
		return o_calendarClient;
	}

	public String toString() {
		return o_name;
	}
}
