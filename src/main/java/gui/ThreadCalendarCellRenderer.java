package gui;

import data.*;
import util.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.*;

class ThreadCalendarCellRenderer implements TableCellRenderer {

	private int o_year;
	private int o_month;

	ThreadCalendarCellRenderer() {
		o_year = Calendar.getInstance().get(Calendar.YEAR);
		o_month = Calendar.getInstance().get(Calendar.MONTH);
	}

	@Override
	public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final Object[] x_values = (Object[]) value;
		Date x_date = (Date) x_values[0];
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.setTime(x_date);

		Object[] x_listValues = new Object[x_values.length];
		System.arraycopy(x_values, 1, x_listValues, 1, x_values.length - 1);
		x_listValues[0] = DateUtil.isToday(x_date) ? "Today" : new SimpleDateFormat(x_calendar.get(Calendar.DAY_OF_MONTH) == 1  || (row == 0 && column == 0) ? "d MMM" : "d").format(x_date);

		final JList<Object> x_list = new JList<>(x_listValues);
		x_list.setCellRenderer(new MyListCellRenderer());
		x_list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        x_list.setBackground(column > 4 ? ColourConstants.s_weekendColour : Color.white);

		if(!(x_calendar.get(Calendar.YEAR) == o_year && x_calendar.get(Calendar.MONTH) == o_month)) {
			x_list.setBackground(ColourConstants.s_offMonthColour);
		}

		setColourForTime(x_date, x_list);
		return x_list;
	}

	void setTime(int p_year, int p_month) {
		this.o_year = p_year;
		this.o_month = p_month;
	}

	static class MyListCellRenderer extends JLabel implements ListCellRenderer<Object> {
		@Override
		public Component getListCellRendererComponent(JList jList, Object p_value, int p_index, boolean p_isSelected, boolean p_cellHasFocus) {
			setIcon(null);

			if(p_value instanceof String) {
				setText((String)p_value);
				setForeground(Color.black);
				setEnabled(true);
			} else {
				data.Component x_component = (data.Component) p_value;
				setIcon(getIconForComponent(x_component));
				setText(buildTextForItem(x_component));
				setEnabled(x_component.isActive());
			}

			setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
			return this;
		}

		private Icon getIconForComponent(data.Component x_component) {
			if(x_component instanceof Item) {
				Item x_item = (Item) x_component;
				return x_item.getDueDate() != null ? ImageUtil.getActionIcon() : ImageUtil.getUpdateIcon();
			}

			return ImageUtil.getReminderIcon();
		}

		static String buildTextForItem(data.Component x_component) {
			StringBuilder x_builder = new StringBuilder();
			Date x_dueDate = x_component instanceof HasDueDate && ((HasDueDate)x_component).getDueDate() != null ? ((HasDueDate)x_component).getDueDate() : x_component.getModifiedDate();

			if(!DateUtil.isAllDay(x_dueDate)) {
				x_builder.append(new SimpleDateFormat("h:mmaa").format(x_dueDate).toLowerCase()).append(". ");
			}

			return x_builder.append(x_component.getText()).toString().replaceAll(":00", "");
		}

		static String buildToolTipTextForItem(data.Component x_component) {
			return (x_component instanceof Reminder ? x_component.getParentComponent().getParentComponent().getText() + " : " : "") + x_component.getParentComponent().getText() + " : " + x_component.getText();
		}

	}

	private static void setColourForTime(Date p_dueDate, JList p_list) {
		if(anyDueItems(p_list)) {
			p_list.setBackground(ColourConstants.s_goneByColour);
		} else if(DateUtil.isToday(p_dueDate)) {
			p_list.setBackground(ColourConstants.s_todayColour);
		} else if(DateUtil.isTomorrow(p_dueDate)) {
			p_list.setBackground(ColourConstants.s_tomorrowColour);
		} else if(DateUtil.isWithin7Days(p_dueDate, false)) {
			p_list.setBackground(ColourConstants.s_thisWeekColour);
		}
	}

	private static boolean anyDueItems(JList p_list) {
		for(int i = 0; i < p_list.getModel().getSize(); i++) {
			Object x_obj = p_list.getModel().getElementAt(i);

			if(x_obj instanceof HasDueDate && ((HasDueDate)x_obj).isDue()) {
				return true;
			}
		}
		return false;
	}

}
