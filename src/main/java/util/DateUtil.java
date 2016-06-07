package util;

import java.util.*;

public class DateUtil {
    public static String getDateStatus(Date p_date) {
        return getDateStatus(p_date, new Date(), " ago");
    }
    
    private static String getDateStatus(Date p_date1, Date p_date2, String p_beforeStr) {
		// make all day events be measure against start of today
		p_date2 = isAllDay(p_date1) ? makeStartOfDay(p_date2) : p_date2;

		int x_weeks = 0;
        int x_days;
        int x_hours;
        int x_mins;
        
        long x_diff = p_date1.getTime() - p_date2.getTime();
        long x_time = Math.abs(x_diff);
        
        x_days = (int) (x_time / (1000 * 60 * 60 * 24));
        
        if(x_days > 10) {
            x_weeks = x_days / 7;
            x_days = x_days % 7;
        }
        
        x_hours = (int) ((x_time / (1000 * 60 * 60)) % 24);
        x_mins = (int) ((x_time / (1000 * 60)) % 60);

		// hack to correct minutes
        x_mins++;

		if(x_mins == 60) {
			x_hours++;
			x_mins = 0;
		}

        StringBuilder x_buffer = new StringBuilder();
        
        if(showingWeeks(x_weeks)) {
            x_buffer.append(x_weeks).append("W ");
        }

        if(showingDays(x_weeks, x_days)) {
            x_buffer.append(x_days).append("D ");
        }

        if(showingHours(x_weeks, x_days, x_hours)) {
            x_buffer.append(x_hours).append("H ");
        }

        if(showingMinutes(x_weeks, x_days, x_mins)) {
            x_buffer.append(x_mins).append("M ");
        }

        if(x_diff < 0) {
            x_buffer.append(p_beforeStr);
        }

        return x_buffer.toString();
    }

	private static Date makeStartOfDay(Date p_date) {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.setTime(p_date);
		x_calendar.set(Calendar.HOUR_OF_DAY, 0);
		x_calendar.set(Calendar.MINUTE, 0);
		x_calendar.set(Calendar.SECOND, 0);
		x_calendar.set(Calendar.MILLISECOND, 0);
		return x_calendar.getTime();
	}

	public static boolean isAllDay(Date p_date) {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.setTime(p_date);
		return x_calendar.get(Calendar.HOUR_OF_DAY) == 0 && x_calendar.get(Calendar.MINUTE) == 0;
	}

	public static boolean isSameDay(Date p_date, Date p_referenceDate) {
		Calendar x_calendar1 = Calendar.getInstance();
		x_calendar1.setTime(p_date);

		Calendar x_calendar2 = Calendar.getInstance();
		x_calendar2.setTime(p_referenceDate);

		return x_calendar1.get(Calendar.YEAR) == x_calendar2.get(Calendar.YEAR)
				&& x_calendar1.get(Calendar.MONTH) == x_calendar2.get(Calendar.MONTH)
				&& x_calendar1.get(Calendar.DAY_OF_MONTH) == x_calendar2.get(Calendar.DAY_OF_MONTH);
	}

	private static boolean showingMinutes(int x_weeks, int x_days, int x_mins) {
		return x_mins > 0 && x_weeks == 0 && x_days < 2;
	}

	private static boolean showingHours(int x_weeks, int x_days, int x_hours) {
		return x_hours > 0 && x_weeks == 0 && x_days < 7;
	}

	private static boolean showingDays(int x_weeks, int x_days) {
		return x_days > 0 && x_weeks < 5;
	}

	private static boolean showingWeeks(int x_weeks) {
		return x_weeks > 0;
	}
}
