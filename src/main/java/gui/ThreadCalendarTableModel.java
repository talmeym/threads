package gui;

import data.*;
import data.Thread;

import java.util.*;

public class ThreadCalendarTableModel extends ComponentTableModel {
	private int o_month;

	ThreadCalendarTableModel(Component p_component) {
		super(p_component, new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"});
		o_month = Calendar.getInstance().get(Calendar.MONTH);
	}

	@Override
	public int getRowCount() {
		return 5;
	}

	@Override
	public Class getColumnClass(int col) {
		return Object[].class;
	}

	@Override
	public Object getValueAt(int p_row, int p_col) {
		Date x_date = getDate(p_row, p_col);
		List<Item> x_actions = LookupHelper.getAllActions((Thread) getComponent(), x_date);
		Object[] x_value = new Object[x_actions.size() + 1];
		x_value[0] = x_date;
		System.arraycopy(x_actions.toArray(new Item[x_actions.size()]), 0, x_value, 1, x_actions.size());
		return x_value;
	}

	public Date getDate(int p_row, int p_col) {
		int x_offset = getOffset();
		return getDate(p_col + (p_row * 7) + x_offset);
	}

	private Date getDate(int p_dayOfMonth) {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(Calendar.MONTH, o_month);
		x_calendar.set(Calendar.DAY_OF_MONTH, 1);
		x_calendar.set(Calendar.HOUR_OF_DAY, 0);
		x_calendar.set(Calendar.MINUTE, 0);
		x_calendar.set(Calendar.MILLISECOND, 0);
		x_calendar.add(Calendar.DAY_OF_MONTH, p_dayOfMonth);
		return x_calendar.getTime();
	}

	private int getOffset() {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(Calendar.MONTH, o_month);
		x_calendar.set(Calendar.DAY_OF_MONTH, 0);
		int x_firstDayOfWeek = x_calendar.get(Calendar.DAY_OF_WEEK);
		return (x_firstDayOfWeek - 1) * -1;
	}

	public int getMonth() {
		return o_month;
	}

	public void setMonth(int p_month) {
		this.o_month = p_month;
		fireTableDataChanged();
	}
}