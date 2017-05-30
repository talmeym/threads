package gui;

import data.*;
import data.Thread;

import java.util.List;

import static data.LookupHelper.getAllActiveReminders;
import static util.DateUtil.*;
import static util.GoogleUtil.isLinked;

class ThreadReminderTableModel extends ComponentTableModel<Thread, Reminder> {
	private boolean o_onlyDueReminders = true;

	ThreadReminderTableModel(Thread p_thread) {
        super(p_thread, new String[] {"Action", "Reminder", "Due Date", "Due", ""});
	}

	@Override
    public Class getColumnClass(int col) {
        return String.class;
    }

	@Override
    public Object getValueAt(int row, int col) {
        Reminder x_dueReminder = getDataItem(row, col);
        
        switch(col) {
			case 0: return x_dueReminder.getParentItem().getText();
			case 1: return x_dueReminder.getText();
			case 2: return getFormattedDate(x_dueReminder.getDueDate());
			case 3: return getDateStatus(x_dueReminder.getDueDate());
			default: return isLinked(x_dueReminder);
        }
    }

    @Override
	List<Reminder> getDataItems() {
    	return getAllActiveReminders(getComponent(), o_onlyDueReminders);
	}

	boolean onlyDueReminders() {
		return o_onlyDueReminders;
	}

	void setOnlyDueReminders(boolean p_onlyDueReminders) {
		o_onlyDueReminders = p_onlyDueReminders;
		reloadData();
	}
}
