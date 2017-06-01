package threads.gui;

import threads.data.*;
import threads.data.Thread;

import javax.swing.*;

import static threads.gui.ColourConstants.s_selectedColour;

class ThreadReminderCellRenderer extends BaseCellRenderer<Thread, Reminder> {
	@Override
	public java.awt.Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
		java.awt.Component x_awtComponent = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
		Reminder x_reminder = o_tableModel.getDataItem(p_row, p_col);
		x_awtComponent.setBackground(p_isSelected ? s_selectedColour : getColourForTime(x_reminder.getDueDate()));
		return x_awtComponent;
	}
}