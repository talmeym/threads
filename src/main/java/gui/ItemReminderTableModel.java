package gui;

import data.*;
import util.*;

import java.util.List;

class ItemReminderTableModel extends ComponentTableModel<Item, Reminder> {
    ItemReminderTableModel(Item p_item) {
        super(p_item, new String[] {"Text", "Due Date", "Due", ""});
    }

	@Override
    public Class getColumnClass(int col) {
		return String.class;
    }

	@Override
    public Object getValueAt(int row, int col) {
		Reminder x_reminder = getComponent().getReminder(row);
        
        switch(col) {
			case 0: return x_reminder.getText();
			case 1: return DateUtil.getFormattedDate(x_reminder.getDueDate());
			case 2: return DateUtil.getDateStatus(x_reminder.getDueDate(), x_reminder.getParentItem().getDueDate(), "before");
			default: return GoogleUtil.isLinked(x_reminder);
        }
    }

	@Override
	List<Reminder> getDataItems() {
		return getComponent().getReminders();
	}
}
