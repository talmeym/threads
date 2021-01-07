package threads.gui;

import threads.data.Item;
import threads.data.Reminder;

import javax.swing.*;

import static java.awt.Color.black;
import static java.awt.Color.gray;

class ItemReminderCellRenderer extends BaseCellRenderer<Item, Reminder> {
	@Override
	public java.awt.Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
		java.awt.Component x_awtComponent = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
		Reminder x_reminder = o_tableModel.getDataItem(p_row, p_col);
		x_awtComponent.setForeground(x_reminder.isActive() ? black : gray);
		return x_awtComponent;
	}
}
