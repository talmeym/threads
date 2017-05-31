package threads.gui;

import threads.data.Item;
import threads.data.Reminder;

import java.util.List;

import static threads.util.DateUtil.getDateStatus;
import static threads.util.DateUtil.getFormattedDate;
import static threads.util.GoogleUtil.isLinked;

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
			case 1: return getFormattedDate(x_reminder.getDueDate());
			case 2: return getDateStatus(x_reminder.getDueDate(), x_reminder.getParentItem().getDueDate(), "before");
			default: return isLinked(x_reminder);
        }
    }

	@Override
	List<Reminder> getDataItems() {
		return getComponent().getReminders();
	}
}
