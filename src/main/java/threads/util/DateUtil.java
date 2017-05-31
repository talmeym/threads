package threads.util;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Integer.parseInt;
import static java.util.Calendar.*;

public class DateUtil {
	public static final int TODAY = 0;
	private static final int YESTERDAY = -1;
	private static final int TOMORROW = 1;

	private static final SimpleDateFormat s_timeFormat = new SimpleDateFormat("HH:mm");
	
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy");
	private static final DateFormat s_12HrTimeFormat = new SimpleDateFormat("h:mmaa");
	private static final DateFormat s_dayFormat = new SimpleDateFormat("EEEE h:mmaa");

	public static String getDateStatus(Date p_date) {
        return getDateStatus(p_date, new Date(), "ago");
    }
    
    public static String getDateStatus(Date p_dueDate, Date p_refDate, String p_beforeStr) {
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

		if(x_days > 0 && !(x_past ? p_dueDate.after(getFirstThing(YESTERDAY)) : p_dueDate.before(getLastThing(TOMORROW)))) {
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
			String format1 = s_timeFormat.format(p_dueDate);
			String format2 = s_timeFormat.format(p_refDate);

			if(format1.trim().length() == 0 || format2.trim().length() == 0) {
				System.out.println(p_dueDate + " => " + format1 + " *** " + p_refDate + " => " + format2);
			}

			Date x_dueTime = s_timeFormat.parse(format1);
			Date x_refTime = s_timeFormat.parse(format2);
			return x_dueTime.before(x_refTime);
		} catch (Exception e) {
			System.out.println("Exception p_dueDate " + p_dueDate + " p_refDate " + p_refDate);
			throw new RuntimeException("Something happened", e);
		}
	}

	public static Date makeStartOfDay(Date p_date) {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.setTime(p_date);
		x_calendar.set(HOUR_OF_DAY, 0);
		x_calendar.set(MINUTE, 0);
		x_calendar.set(SECOND, 0);
		x_calendar.set(MILLISECOND, 0);
		return x_calendar.getTime();
	}

	public static boolean isAllDay(Date p_date) {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.setTime(p_date);
		return x_calendar.get(HOUR_OF_DAY) == 0 && x_calendar.get(MINUTE) == 0;
	}

	public static boolean isToday(Date p_date) {
	    return isSameDay(p_date, getFirstThing(TODAY));
	}

	private static boolean isYesterday(Date p_date) {
	    return isSameDay(p_date, getFirstThing(YESTERDAY));
	}

	public static boolean isTomorrow(Date p_date) {
	    return isSameDay(p_date, getFirstThing(TOMORROW));
	}

	public static boolean isWithin7Days(Date p_dueDate, boolean p_includePast) {
		Date x_date = new Date();
		Date x_now = isAllDay(p_dueDate) || timeIsBefore(p_dueDate, x_date) ? makeStartOfDay(x_date) : x_date;
		long x_diff = p_dueDate.getTime() - x_now.getTime();
		int x_sevenDays = 1000 * 60 * 60 * 24 * 7;
		return p_includePast ? Math.abs(x_diff) < x_sevenDays : x_diff > 0 && x_diff < x_sevenDays;
	}

	public static boolean isBefore7DaysFromNow(Date p_dueDate) {
		Date x_date = new Date();
		Date x_now = isAllDay(p_dueDate) || timeIsBefore(p_dueDate, x_date) ? makeStartOfDay(x_date) : x_date;
		long x_diff = p_dueDate.getTime() - x_now.getTime();
		int x_sevenDays = 1000 * 60 * 60 * 24 * 7;
		return x_diff < 0 || x_diff < x_sevenDays;
	}

	public static boolean isSameDay(Date p_date, Date p_referenceDate) {
		Calendar x_calendar1 = Calendar.getInstance();
		x_calendar1.setTime(p_date);

		Calendar x_calendar2 = Calendar.getInstance();
		x_calendar2.setTime(p_referenceDate);

		return x_calendar1.get(YEAR) == x_calendar2.get(YEAR)
				&& x_calendar1.get(MONTH) == x_calendar2.get(MONTH)
				&& x_calendar1.get(DAY_OF_MONTH) == x_calendar2.get(DAY_OF_MONTH);
	}

	private static boolean showingMinutes(boolean x_past, int x_mins, Date p_dueDate) {
		return x_mins > 0 && (x_past ? p_dueDate.after(getFirstThing(YESTERDAY)) : p_dueDate.before(getLastThing(TOMORROW)));
	}

	private static boolean showingHours(boolean x_past, int x_hours, Date p_dueDate) {
		return x_hours > 0 && (x_past ? p_dueDate.after(getFirstThing(YESTERDAY)) : p_dueDate.before(getLastThing(TOMORROW)));
	}

	private static boolean showingDays(int x_weeks, int x_days) {
		return x_days > 0 && x_weeks < 6;
	}

	private static boolean showingWeeks(int x_weeks) {
		return x_weeks > 0;
	}

	private static Date getLastThing(int p_offset) {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(HOUR_OF_DAY, 23);
		x_calendar.set(MINUTE, 59);
		x_calendar.set(SECOND, 59);
		x_calendar.set(MILLISECOND, 999);
		x_calendar.add(DATE, p_offset);
		return x_calendar.getTime();
	}

	public static Date getFirstThing(int p_offset) {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(HOUR_OF_DAY, 0);
		x_calendar.set(MINUTE, 0);
		x_calendar.set(SECOND, 0);
		x_calendar.set(MILLISECOND, 0);
		x_calendar.add(DATE, p_offset);
		return x_calendar.getTime();
	}

	public static String getFormattedDate(Date p_dueDate) {
		String x_value;

		if(isYesterday(p_dueDate)) {
			x_value = "Yesterday " + s_12HrTimeFormat.format(p_dueDate).toLowerCase();
		} else if(isToday(p_dueDate)) {
			x_value = "Today " + s_12HrTimeFormat.format(p_dueDate).toLowerCase();
		} else if(isTomorrow(p_dueDate)) {
			x_value = "Tomorrow " + s_12HrTimeFormat.format(p_dueDate).toLowerCase();
		} else if(isWithin7Days(p_dueDate, true)) {
			x_value = s_dayFormat.format(p_dueDate).toLowerCase();
			String x_firstLetter = x_value.substring(0, 1);
			x_value = x_value.replaceFirst(x_firstLetter, x_firstLetter.toUpperCase());
		} else {
			x_value = s_dateFormat.format(p_dueDate);
		}

		return x_value.replace(":00", "").replace(" 12am", "");
	}

	public static Date deriveDate(String p_text, Date p_date) {
		if(p_text.contains(" ")) {
			String x_firstPart = p_text.substring(0, p_text.indexOf(" "));

			if(x_firstPart.endsWith(",")) {
				x_firstPart = x_firstPart.substring(0, x_firstPart.length() - 1);
			}

			Date x_derivedTime = parseTime(x_firstPart);

			if(x_derivedTime != null) {
				Calendar x_calendar = Calendar.getInstance();
				x_calendar.setTime(p_date);
				Calendar x_derivedCal = Calendar.getInstance();
				x_derivedCal.setTime(x_derivedTime);
				x_calendar.set(HOUR_OF_DAY, x_derivedCal.get(HOUR_OF_DAY));
				x_calendar.set(MINUTE, x_derivedCal.get(MINUTE));
				return x_calendar.getTime();
			}
		}

		return null;
	}

	private static Date parseTime(String p_firstPart) {
		Date x_date = null;

		try {
			x_date = new SimpleDateFormat("hh:mmaa").parse(p_firstPart.toUpperCase());
		} catch (ParseException e) {
			try {
				x_date = new SimpleDateFormat("HH:mm").parse(p_firstPart);
			} catch (ParseException f) {
				try {
					x_date = new SimpleDateFormat("hhaa").parse(p_firstPart.toUpperCase());
				} catch (ParseException g) {
					// do nothing
				}
			}
		}

		return x_date;
	}

	public static Date deriveReminderDate(String p_text, Date p_date) {
		Date x_deriveDate = deriveDate(p_text, p_date);

		if(x_deriveDate != null) {
			return x_deriveDate;
		}

		if(p_text.contains(" ")) {
			String x_firstPart = p_text.substring(0, p_text.indexOf(" "));

			if(x_firstPart.endsWith(",")) {
				x_firstPart = x_firstPart.substring(0, x_firstPart.length() - 1);
			}

			Integer x_derivedOffset = parseOffset(x_firstPart);

			if(x_derivedOffset != null) {
				return new Date(p_date.getTime() + x_derivedOffset);
			}
		}

		return null;
	}

	private static Integer parseOffset(String p_firstPart) {
		try {
			Object[] x_results = new MessageFormat("{0,number}{1}").parse(p_firstPart);

			if(x_results != null && x_results.length == 2) {
				int x_number = parseInt(x_results[0].toString());
				char x_letter = x_results[1].toString().toCharArray()[0];

				switch(x_letter) {
					case 's': return x_number * 1000;
					case 'm': return x_number * 1000 * 60;
					case 'h': return x_number * 1000 * 60 * 60;
					case 'd': return x_number * 1000 * 60 * 60 * 24;
					case 'w': return x_number * 1000 * 60 * 60 * 24 * 7;
				}
			}
		} catch (IllegalArgumentException | ParseException e) {
			e.printStackTrace();
		}

		return null;
	}
}
