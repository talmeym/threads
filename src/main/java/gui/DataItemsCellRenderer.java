package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.List;

import static util.DateUtil.*;
import static util.ImageUtil.getGoogleSmallIcon;

abstract class DataItemsCellRenderer<INPUT_COMP extends data.Component, OUTPUT_COMP extends data.Component> extends DefaultTableCellRenderer {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");

	private List<OUTPUT_COMP> o_list;

	DataItemsCellRenderer(INPUT_COMP p_component) {
		o_list = getDataItems(p_component);
		p_component.addComponentChangeListener(e -> o_list = getDataItems(p_component));
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
		} else if(p_value instanceof Icon) {
			setIcon((Icon) p_value);
			setHorizontalAlignment(JTextField.CENTER);
		} else if(p_value instanceof Date) {
			setText(s_dateFormat.format((Date)p_value));
		} else {
			setText(String.valueOf(p_value));
		}
	}

	public java.awt.Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
		java.awt.Component x_awtComponent = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
		setBorder(noFocusBorder);

		OUTPUT_COMP x_component = o_list.get(p_row);

		if(x_component != null) {
			x_awtComponent.setForeground(x_component.isActive() ? Color.black : Color.gray);
			x_awtComponent.setBackground(Color.white);
			customSetup(x_component, x_awtComponent);

			if(p_isSelected) {
				x_awtComponent.setBackground(ColourConstants.s_selectedColour);
			}

		}

		return x_awtComponent;
	}

	abstract List<OUTPUT_COMP> getDataItems(INPUT_COMP p_component);

	void customSetup(OUTPUT_COMP p_component, java.awt.Component p_awtComponent) {
		// do nothing by default
	}

	static Color getColourForTime(Date p_dueDate) {
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
