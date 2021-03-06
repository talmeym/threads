package threads.gui;

import threads.data.HasDueDate;
import threads.data.Reminder;
import threads.data.Thread;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.awt.Color.black;
import static java.awt.Color.white;
import static java.util.Calendar.*;
import static javax.swing.BorderFactory.createEmptyBorder;
import static threads.gui.ColourConstants.*;
import static threads.util.DateUtil.*;
import static threads.util.ImageUtil.getSmallIconForType;

class ThreadCalendarCellRenderer extends BaseCellRenderer<Thread, Date> {

	private int o_year;
	private int o_month;

	ThreadCalendarCellRenderer() {
		o_year = Calendar.getInstance().get(YEAR);
		o_month = Calendar.getInstance().get(MONTH);
	}

	@Override
	public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final Object[] x_values = (Object[]) value;
		Date x_date = (Date) x_values[0];
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.setTime(x_date);

		Object[] x_listValues = new Object[x_values.length];
		System.arraycopy(x_values, 1, x_listValues, 1, x_values.length - 1);
		x_listValues[0] = isToday(x_date) ? "Today" : new SimpleDateFormat(x_calendar.get(DAY_OF_MONTH) == 1  || (row == 0 && column == 0) ? "d MMM" : "d").format(x_date);

		final JList<Object> x_list = new JList<>(x_listValues);
		x_list.setCellRenderer(new MyListCellRenderer());
		x_list.setBorder(createEmptyBorder(5, 5, 5, 5));
        x_list.setBackground(column > 4 ? s_weekendColour : white);

		if(!(x_calendar.get(YEAR) == o_year && x_calendar.get(MONTH) == o_month)) {
			x_list.setBackground(s_offMonthColour);
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
				setForeground(black);
				setEnabled(true);
			} else {
				threads.data.Component x_component = (threads.data.Component) p_value;
				setIcon(getSmallIconForType(x_component.getType()));
				setText(buildTextForItem(x_component));
				setEnabled(x_component.isActive());
			}

			setBorder(createEmptyBorder(0, 0, 2, 0));
			return this;
		}

		static String buildTextForItem(threads.data.Component x_component) {
			StringBuilder x_builder = new StringBuilder();
			Date x_dueDate = x_component instanceof HasDueDate && ((HasDueDate)x_component).getDueDate() != null ? ((HasDueDate)x_component).getDueDate() : x_component.getModifiedDate();

			if(!isAllDay(x_dueDate)) {
				x_builder.append(new SimpleDateFormat("h:mmaa").format(x_dueDate).toLowerCase()).append(". ");
			}

			return x_builder.append(x_component.getText()).toString().replaceAll(":00", "");
		}

		static String buildToolTipTextForItem(threads.data.Component x_component) {
			return (x_component instanceof Reminder ? x_component.getParentComponent().getParentComponent().getText() + " : " : "") + x_component.getParentComponent().getText() + " : " + x_component.getText();
		}

	}

	private static void setColourForTime(Date p_dueDate, JList p_list) {
		if(anyDueItems(p_list)) {
			p_list.setBackground(s_goneByColour);
		} else if(isToday(p_dueDate)) {
			p_list.setBackground(s_todayColour);
		} else if(isTomorrow(p_dueDate)) {
			p_list.setBackground(s_tomorrowColour);
		} else if(isWithin7Days(p_dueDate, false)) {
			p_list.setBackground(s_thisWeekColour);
		}
	}

	private static boolean anyDueItems(JList p_list) {
		ListModel x_model = p_list.getModel();

		for(int i = 0; i < x_model.getSize(); i++) {
			Object x_obj = x_model.getElementAt(i);

			if(x_obj instanceof HasDueDate && ((HasDueDate)x_obj).isDue()) {
				return true;
			}
		}

		return false;
	}

}
