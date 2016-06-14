package util;

import java.util.*;

import static java.util.Calendar.*;

public class DateUtil {
    public static String getDateStatus(Date p_date) {
        return getDateStatus(p_date, new Date(), " ago");
    }
    
    private static String getDateStatus(Date p_dueDate, Date p_refDate, String p_beforeStr) {
		// make all day events be measure against start of today
		p_refDate = isAllDay(p_dueDate) ? makeStartOfDay(p_refDate) : p_refDate;

		int x_weeks = 0;
        int x_days;
        int x_hours;
        int x_minutes;
        int x_seconds;

        long x_diff = p_dueDate.getTime() - p_refDate.getTime();
        long x_time = Math.abs(x_diff);
		boolean x_past = x_diff < 0;
        x_diff = Math.abs(x_diff);

        x_days = (int) (x_time / (1000 * 60 * 60 * 24));
        
        if(x_days > 10) {
            x_weeks = x_days / 7;
            x_days = x_days % 7;
        }
        
        x_hours = (int) ((x_time / (1000 * 60 * 60)) % 24);
        x_minutes = (int) ((x_time / (1000 * 60)) % 60);
		x_seconds = (int) ((x_time / (1000)) % 60);

        StringBuilder x_buffer = new StringBuilder();

		if(!x_past && x_seconds > 0) {
			x_minutes += 1;

			if(x_minutes == 60) {
				x_hours += 1;
				x_minutes = 0;

				if(x_hours == 24) {
					x_days += 1;
					x_hours %= 24;

					if(x_days > 10) {
						x_weeks += 1;
						x_days %= 7;
					}
				}
			}
		}

		if(x_days > 0 && !(x_past ? p_dueDate.after(DateUtil.getFirstThingYesterday()) : p_dueDate.before(DateUtil.getLastThingTomorrow()))) {
			if(x_hours > 0 || x_minutes > 0 || x_seconds > 0) {
				x_days = x_days + 1;

				if((x_weeks == 0 && x_days > 10) || (x_weeks > 0 && x_days == 7)) {
					x_weeks = x_weeks + 1;
					x_days = x_days % 7;
				}
			}
        }

		if(showingWeeks(x_weeks)) {
			x_buffer.append(x_weeks).append("w ");
		}

		if(showingDays(x_weeks, x_days)) {
			x_buffer.append(x_days).append("d ");
		}

		if(showingHours(x_past, x_hours, p_dueDate)) {
			x_buffer.append(x_hours).append("h ");
		}

		if(showingMinutes(x_past, x_minutes, p_dueDate)) {
			x_buffer.append(x_minutes).append("m ");
		}

		if(x_past) {
			if(x_diff < 60 * 1000) {
				x_buffer.append("Now");
			} else {
				x_buffer.append(p_beforeStr);
			}
		} else {
			if(x_diff == 0) {
				x_buffer.append("Today");
			}
		}

        return x_buffer.toString();
    }

	public static Date makeStartOfDay(Date p_date) {
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

	public static boolean istoday(Date p_date) {
	    return isSameDay(p_date, new Date());
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

	private static boolean showingMinutes(boolean x_past, int x_mins, Date p_dueDate) {
		return x_mins > 0 && (x_past ? p_dueDate.after(DateUtil.getFirstThingYesterday()) : p_dueDate.before(DateUtil.getLastThingTomorrow()));
	}

	private static boolean showingHours(boolean x_past, int x_hours, Date p_dueDate) {
		return x_hours > 0 && (x_past ? p_dueDate.after(DateUtil.getFirstThingYesterday()) : p_dueDate.before(DateUtil.getLastThingTomorrow()));
	}

	private static boolean showingDays(int x_weeks, int x_days) {
		return x_days > 0 && x_weeks < 6;
	}

	private static boolean showingWeeks(int x_weeks) {
		return x_weeks > 0;
	}

	public static Date getLastThingToday() {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(HOUR_OF_DAY, 23);
		x_calendar.set(MINUTE, 59);
		x_calendar.set(Calendar.SECOND, 59);
		x_calendar.set(Calendar.MILLISECOND, 999);
		return x_calendar.getTime();
	}

	public static Date getLastThingTomorrow() {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(HOUR_OF_DAY, 23);
		x_calendar.set(MINUTE, 59);
		x_calendar.set(Calendar.SECOND, 59);
		x_calendar.set(Calendar.MILLISECOND, 999);
		x_calendar.roll(DATE, true);
		return x_calendar.getTime();
	}

	public static Date getFirstThingToday() {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(HOUR_OF_DAY, 0);
		x_calendar.set(MINUTE, 0);
		x_calendar.set(Calendar.SECOND, 0);
		x_calendar.set(Calendar.MILLISECOND, 0);
		return x_calendar.getTime();
	}

	public static Date getFirstThingYesterday() {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(HOUR_OF_DAY, 0);
		x_calendar.set(MINUTE, 0);
		x_calendar.set(Calendar.SECOND, 0);
		x_calendar.set(Calendar.MILLISECOND, 0);
		x_calendar.roll(DATE, false);
		return x_calendar.getTime();
	}
}
