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

import static java.lang.String.valueOf;
import static util.DateUtil.*;
import static util.ImageUtil.getGoogleSmallIcon;

class ThreadActionCellRenderer extends DefaultTableCellRenderer {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");

    private final Thread o_thread;

    ThreadActionCellRenderer(Thread p_thread) {
        o_thread = p_thread;
    }

	@Override
	public void setValue(Object p_value) {
		setText("");
		setIcon(null);
		setHorizontalAlignment(JTextField.LEFT);

		if(p_value instanceof Boolean) {
			if((Boolean) p_value) {
				setIcon(getGoogleSmallIcon());
				setHorizontalAlignment(JTextField.CENTER);
			}
		} else if(p_value instanceof Date) {
			setText(s_dateFormat.format((Date)p_value));
		} else {
			setText(valueOf(p_value));
		}
	}

    public Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
        java.awt.Component x_component = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
		setBorder(noFocusBorder);

		x_component.setForeground(Color.black);
		x_component.setBackground(Color.white);

		List<Item> x_actions = LookupHelper.getAllActiveActions(o_thread, true);

		if(p_row < x_actions.size()) {
			x_component.setBackground(getColourForTime(x_actions.get(p_row).getDueDate()));
		}

		if(p_isSelected) {
			x_component.setBackground(ColourConstants.s_selectedColour);
		}

        return x_component;
    }

	private static Color getColourForTime(Date p_dueDate) {
		Date x_now = isAllDay(p_dueDate) ? makeStartOfDay(new Date()) : new Date();

		if(isAllDay(p_dueDate) ? p_dueDate.before(getFirstThing(TODAY)) : p_dueDate.before(x_now)) {
			return ColourConstants.s_goneByColour;
		} else if(isToday(p_dueDate)) {
			return ColourConstants.s_todayColour;
		} else if(isTomorrow(p_dueDate)) {
			return ColourConstants.s_tomorrowColour;
		} else if(isWithin7Days(p_dueDate, true)) {
			return ColourConstants.s_thisWeekColour;
		}

		return Color.WHITE;
	}
}