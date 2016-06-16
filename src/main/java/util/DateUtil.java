package util;

import gui.ColourConstants;

import java.awt.*;
import java.text.*;
import java.util.*;

import static java.util.Calendar.*;

public class DateUtil {
	private static final SimpleDateFormat s_timeFormat = new SimpleDateFormat("HH:mm");
	
	public static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy");
	public static final DateFormat s_12HrTimeFormat = new SimpleDateFormat("h:mmaa");
	public static final DateFormat s_dayFormat = new SimpleDateFormat("EEEE h:mmaa");


	public static String getDateStatus(Date p_date) {
        return getDateStatus(p_date, new Date(), "ago");
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

		if(x_days > 0 && !(x_past ? p_dueDate.after(getFirstThingYesterday()) : p_dueDate.before(getLastThingTomorrow()))) {
			if((x_hours > 0 || x_minutes > 0 || x_seconds > 0) && timeIsBefore(p_dueDate, p_refDate)) {
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

		if(x_past ? x_diff < 60 * 1000 : x_diff == 0) {
			x_buffer.append("Now");
		}

		if(x_past && x_diff >= 60 * 1000) {
			x_buffer.append(p_beforeStr);
		}

        return x_buffer.toString();
    }

	private static boolean timeIsBefore(Date p_dueDate, Date p_refDate) {
		try {
			Date x_dueTime = s_timeFormat.parse(s_timeFormat.format(p_dueDate));
			Date x_refTime = s_timeFormat.parse(s_timeFormat.format(p_refDate));
			boolean x_before = x_dueTime.before(x_refTime);
			return x_before;
		} catch(ParseException pe) {
			// do nothing, shouldn't happen
			throw new RuntimeException("Something happened", pe);
		}
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

	public static boolean isToday(Date p_date) {
	    return isSameDay(p_date, getFirstThingToday());
	}

	public static boolean isYesterday(Date p_date) {
	    return isSameDay(p_date, getFirstThingYesterday());
	}

	public static boolean isTomorrow(Date p_date) {
	    return isSameDay(p_date, getLastThingTomorrow());
	}

	public static boolean isWithin7Days(Date p_dueDate) {
		Date x_now = isAllDay(p_dueDate) ? makeStartOfDay(new Date()) : new Date();
		return Math.abs(p_dueDate.getTime() - x_now.getTime()) < (1000 * 60 * 60 * 24 * 7);
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
		return x_mins > 0 && (x_past ? p_dueDate.after(getFirstThingYesterday()) : p_dueDate.before(getLastThingTomorrow()));
	}

	private static boolean showingHours(boolean x_past, int x_hours, Date p_dueDate) {
		return x_hours > 0 && (x_past ? p_dueDate.after(getFirstThingYesterday()) : p_dueDate.before(getLastThingTomorrow()));
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

	public static String getFormattedDate(Date p_dueDate) {
		Date x_now = isAllDay(p_dueDate) ? makeStartOfDay(new Date()) : new Date();
		String x_value;

		if(isYesterday(p_dueDate)) {
			x_value = "Yesterday " + s_12HrTimeFormat.format(p_dueDate).toLowerCase();
		} else if(isToday(p_dueDate)) {
			x_value = "Today " + s_12HrTimeFormat.format(p_dueDate).toLowerCase();
		} else if(isYesterday(p_dueDate)) {
			x_value = "Tomorrow " + s_12HrTimeFormat.format(p_dueDate).toLowerCase();
		} else if(Math.abs(p_dueDate.getTime() - x_now.getTime()) < (1000 * 60 * 60 * 24 * 7)) { // within 7 days
			x_value = s_dayFormat.format(p_dueDate).toLowerCase();
			String x_firstLetter = x_value.substring(0, 1);
			x_value = x_value.replaceFirst(x_firstLetter, x_firstLetter.toUpperCase());
		} else {
			x_value = s_dateFormat.format(p_dueDate);
		}

		return x_value.replace(":00", "").replace(" 12am", "");
	}


	public static Color getColourForTime(Date p_dueDate) {
		Date x_now = isAllDay(p_dueDate) ? makeStartOfDay(new Date()) : new Date();

		if(isAllDay(p_dueDate) ? p_dueDate.before(getFirstThingToday()) : p_dueDate.before(x_now)) {
			return ColourConstants.s_goneByColour; // gone by
		} else if(p_dueDate.before(getLastThingToday())) {
			return ColourConstants.s_todayColour; // today
		} else if(p_dueDate.before(getLastThingTomorrow())) {
			return ColourConstants.s_tomorrowColour; // tomorrow
		} else if((p_dueDate.getTime() - x_now.getTime()) < (1000 * 60 * 60 * 24 * 7)) { // within 7 days
			return ColourConstants.s_thisWeekColour;
		}

		return Color.WHITE;
	}
}
