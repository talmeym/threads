package gui;

import data.*;
import data.Thread;
import util.DateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.Component;
import java.text.*;
import java.util.*;

public class ActionCellRenderer extends DefaultTableCellRenderer {
	public static final DateFormat s_createDateFormat = new SimpleDateFormat("dd MMM yy HH:mm");
	public static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy");
	public static final DateFormat s_12HrTimeFormat = new SimpleDateFormat("h:mmaa");
	public static final DateFormat s_dayFormat = new SimpleDateFormat("EEEE h:mmaa");

    private final Thread o_thread;

    ActionCellRenderer(Thread p_thread) {
        o_thread = p_thread;
    }

    public Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
        if(p_value instanceof Date) {
			p_value = p_col == 0 ? s_createDateFormat.format((Date) p_value) : getFormattedDate((Date) p_value);
        }

        java.awt.Component x_component = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
		x_component.setForeground(Color.black);

		Item x_item = LookupHelper.getAllActiveActions(o_thread).get(p_row);
		x_component.setBackground(getColourForTime(x_item.getDueDate()));

		if(p_isSelected) {
			x_component.setBackground(ColourConstants.s_selectedColour);
		}

        return x_component;
    }

	private String getFormattedDate(Date x_dueDate) {
		Date x_now = new Date();
		String x_value;

		if(x_dueDate.before(x_now)) {
			x_value = s_dateFormat.format(x_dueDate) + " " + s_12HrTimeFormat.format(x_dueDate).toLowerCase();
		}else if(x_dueDate.before(DateUtil.getLastThingToday())) {
			x_value = "Today " + s_12HrTimeFormat.format(x_dueDate).toLowerCase();
		} else if(x_dueDate.before(DateUtil.getLastThingTomorrow())) {
			x_value = "Tomorrow " + s_12HrTimeFormat.format(x_dueDate).toLowerCase();
		} else if((x_dueDate.getTime() - x_now.getTime()) < (1000 * 60 * 60 * 24 * 7)) { // within 7 days
			x_value = s_dayFormat.format(x_dueDate).toLowerCase();
			String x_firstLetter = x_value.substring(0, 1);
			x_value = x_value.replaceFirst(x_firstLetter, x_firstLetter.toUpperCase());
		} else {
		 	x_value = s_dateFormat.format(x_dueDate);
		}

		return x_value.replace(":00", "").replace(" 12am", "");
	}

	private Color getColourForTime(Date p_dueDate) {
		Date x_now = new Date();

		if(p_dueDate.before(x_now)) {
			return ColourConstants.s_goneByColour; // gone by
		} else if(p_dueDate.before(DateUtil.getLastThingToday())) {
			return ColourConstants.s_todayColour; // today
		} else if(p_dueDate.before(DateUtil.getLastThingTomorrow())) {
			return ColourConstants.s_tomorrowColour; // tomorrow
		} else if((p_dueDate.getTime() - x_now.getTime()) < (1000 * 60 * 60 * 24 * 7)) { // within 7 days
			return ColourConstants.s_thisWeekColour;
		}

		return Color.WHITE;
	}
}