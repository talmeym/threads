package gui;

import data.*;
import data.Thread;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.Component;
import java.text.*;
import java.util.*;
import java.util.List;

import static java.util.Calendar.*;

public class ActionCellRenderer extends DefaultTableCellRenderer
{
	public static final DateFormat s_dateTimeFormat = new SimpleDateFormat("dd MMM yy");
	public static final DateFormat s_TimeFormat = new SimpleDateFormat("h:mm");
	public static final DateFormat s_amPmFormat = new SimpleDateFormat("aa");
	public static final DateFormat s_dayFormat = new SimpleDateFormat("EEEE h:mm");

    private final Thread o_thread;

    ActionCellRenderer(Thread p_thread)
    {
        o_thread = p_thread;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Object x_value = value;

        if(x_value instanceof Date)
        {
			x_value = getFormattedDate((Date) x_value);
        }

        java.awt.Component x_component =
            super.getTableCellRendererComponent(table,
                                                x_value,
                                                isSelected,
                                                hasFocus,
                                                row,
                                                column);

		x_component.setForeground(Color.BLACK);
		x_component.setBackground(Color.WHITE);

		List x_actionItems = ThreadHelper.getAllActiveActions(o_thread);
        Item x_item = (Item) x_actionItems.get(row);

		setColourForTime(x_component, x_item);

		if(isSelected) {
			x_component.setForeground(Color.BLACK);
			x_component.setBackground(new Color(155, 255, 255));
		}

        return x_component;
    }

	private String getFormattedDate(Date x_dueDate) {
		Date x_now = new Date();
		Date x_lastThingToday = getLastThingToday();
		Date x_lastThingTomorrow = getLastThingTomorrow();

		String x_value;

		if(x_dueDate.before(x_lastThingToday))
		{
			x_value = "Today " + s_TimeFormat.format(x_dueDate) + s_amPmFormat.format(x_dueDate).toLowerCase();
		}
		else if(x_dueDate.before(x_lastThingTomorrow))
		{
			x_value = "Tomorrow " + s_TimeFormat.format(x_dueDate) + s_amPmFormat.format(x_dueDate).toLowerCase();
		}
		else if((x_dueDate.getTime() - x_now.getTime()) < (1000 * 60 * 60 * 24 * 7)) // within 7 days
		{
			x_value = s_dayFormat.format(x_dueDate) + s_amPmFormat.format(x_dueDate).toLowerCase();
		} else {
		 	x_value = s_dateTimeFormat.format(x_dueDate);
		}

		x_value = x_value.replace(":00", "");
		x_value = x_value.replace(" 12am", "");

		return x_value;
	}

	private void setColourForTime(Component x_component, Item x_item) {
		Date x_dueDate = x_item.getDueDate();
		Date x_now = new Date();
		Date x_lastThingToday = getLastThingToday();
		Date x_lastThingTomorrow = getLastThingTomorrow();

		if(x_now.after(x_dueDate))
		{
			x_component.setBackground(new Color(255, 155, 155)); // gone by
		}
		else if(x_dueDate.before(x_lastThingToday))
		{
			x_component.setBackground(new Color(255, 203, 100)); // today
		}
		else if(x_dueDate.before(x_lastThingTomorrow))
		{
			x_component.setBackground(new Color(255, 255, 155)); // tomorrow
		}
		else if((x_dueDate.getTime() - x_now.getTime()) < (1000 * 60 * 60 * 24 * 7)) // within 7 days
		{
			x_component.setBackground(new Color(155,255,155));
		}
		else
		{
			x_component.setBackground(Color.WHITE);
		}
	}

	private Date getLastThingToday()
	{
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(HOUR_OF_DAY, 23);
		x_calendar.set(MINUTE, 59);
		x_calendar.set(Calendar.SECOND, 59);
		x_calendar.set(Calendar.MILLISECOND, 999);
		return x_calendar.getTime();
	}

	private Date getLastThingTomorrow()
	{
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(HOUR_OF_DAY, 23);
		x_calendar.set(MINUTE, 59);
		x_calendar.set(Calendar.SECOND, 59);
		x_calendar.set(Calendar.MILLISECOND, 999);
		x_calendar.roll(DATE, true);
		return x_calendar.getTime();
	}
}
