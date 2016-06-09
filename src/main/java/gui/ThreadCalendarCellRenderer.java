package gui;

import data.Item;
import util.DateUtil;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.*;
import java.util.*;

public class ThreadCalendarCellRenderer implements TableCellRenderer {
	public static final DateFormat s_12HrTimeFormat = new SimpleDateFormat("h:mmaa");

	private int o_month;

	public ThreadCalendarCellRenderer() {
		o_month = Calendar.getInstance().get(Calendar.YEAR);
	}

	@Override
	public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final Object[] x_values = (Object[]) value;
		Date x_date = (Date) x_values[0];
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.setTime(x_date);
		boolean x_today = DateUtil.istoday(x_date);

		Object[] x_listValues = new Object[x_values.length];
		System.arraycopy(x_values, 1, x_listValues, 1, x_values.length - 1);
		x_listValues[0] = x_today ? "Today" : new SimpleDateFormat(x_calendar.get(Calendar.DAY_OF_MONTH) == 1  || (row == 0 && column == 0) ? "d MMM" : "d").format(x_date);

		final JList x_list = new JList(x_listValues);
		x_list.setCellRenderer(new MyListCellRenderer());
		x_list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        x_list.setBackground(column > 4 ? ColourConstants.s_weekendColour : Color.white);

		if(x_calendar.get(Calendar.MONTH) != o_month) {
			x_list.setBackground(ColourConstants.s_offMonthColour);
		}

		if(x_today) {
			x_list.setBackground(ColourConstants.s_selectedColour);
		}

		return x_list;
	}

	public void setMonth(int p_month) {
		this.o_month = p_month;
	}

	public static class MyListCellRenderer extends JLabel implements ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList jList, Object p_value, int p_index, boolean p_isSelected, boolean p_cellHasFocus) {
			if(p_value instanceof String) {
				setText((String)p_value);
				setForeground(Color.black);
			} else {
				Item x_item = (Item) p_value;
				String x_text = buildTextForItem(x_item);
				setText(x_text);
				setForeground(x_item.isActive() ? Color.black : Color.gray);
			}

			setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
			return this;
		}

		public static String buildTextForItem(Item x_item) {
			StringBuilder x_builder = new StringBuilder();

			if(!DateUtil.isAllDay(x_item.getDueDate())) {
				x_builder.append(s_12HrTimeFormat.format(x_item.getDueDate()).toLowerCase()).append(". ");
			}

			return x_builder.append(x_item.getText()).toString().replaceAll(":00", "");
		}

	}
}
