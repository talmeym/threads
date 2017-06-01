package threads.gui;

import threads.data.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.awt.Color.black;
import static java.awt.Color.white;
import static java.lang.String.valueOf;
import static threads.gui.ColourConstants.*;
import static threads.util.DateUtil.*;
import static threads.util.ImageUtil.getGoogleSmallIcon;
import static threads.util.ImageUtil.getIconForType;

class BaseCellRenderer<COMPONENT extends threads.data.Component, DATA_TYPE> extends DefaultTableCellRenderer {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");

	ComponentTableModel<COMPONENT, DATA_TYPE> o_tableModel;

	@Override
	public void setValue(Object p_value) {
		setText("");
		setIcon(null);
		setHorizontalAlignment(LEFT);

		if(p_value instanceof Boolean) {
			if((Boolean) p_value) {
				setIcon(getGoogleSmallIcon());
				setHorizontalAlignment(CENTER);
			}
		} else if(p_value instanceof ComponentType) {
			setIcon(getIconForType((ComponentType)p_value));
			setHorizontalAlignment(CENTER);
		} else if(p_value instanceof Icon) {
			setIcon((Icon)p_value);
			setHorizontalAlignment(CENTER);
		} else if(p_value instanceof Date) {
			setText(s_dateFormat.format((Date)p_value));
		} else {
			setText(valueOf(p_value));
		}
	}

	void setTableModel(ComponentTableModel<COMPONENT, DATA_TYPE> p_tableModel) {
		o_tableModel = p_tableModel;
	}

	public java.awt.Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
		java.awt.Component x_awtComponent = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
		setBorder(noFocusBorder);
		x_awtComponent.setForeground(black);
		x_awtComponent.setBackground(p_isSelected ? s_selectedColour : white);
		return x_awtComponent;
	}

	static Color getColourForTime(Date p_dueDate) {
		Date x_now = isAllDay(p_dueDate) ? makeStartOfDay(new Date()) : new Date();

		if(isAllDay(p_dueDate) ? p_dueDate.before(getFirstThing(TODAY)) : p_dueDate.before(x_now)) {
			return s_goneByColour;
		} else if(isToday(p_dueDate)) {
			return s_todayColour;
		} else if(isTomorrow(p_dueDate)) {
			return s_tomorrowColour;
		} else if(isWithin7Days(p_dueDate, true)) {
			return s_thisWeekColour;
		}

		return white;
	}
}
