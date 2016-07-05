package gui;

import data.*;
import data.Thread;
import util.*;

class ThreadReminderTableModel extends ComponentTableModel<Thread, Reminder> {
    ThreadReminderTableModel(Thread p_thread) {
        super(p_thread, new String[] {"Action", "Reminder", "Due Date", "Due", ""});
		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
	}

	@Override
	public int getRowCount() {
        Thread x_thread = getComponent();
        
        if(x_thread == null) {
            return 0;
        }
        
        return LookupHelper.getAllDueReminders(x_thread).size();
    }

	@Override
    public Class getColumnClass(int col) {
        return String.class;
    }

	@Override
    public Object getValueAt(int row, int col) {
        Reminder x_dueReminder = getDataItem(row, col);
        
        switch(col) {
			case 0: return x_dueReminder.getItem().getText();
			case 1: return x_dueReminder.getText();
			case 2: return DateUtil.getFormattedDate(x_dueReminder.getDueDate());
			case 3: return DateUtil.getDateStatus(x_dueReminder.getDueDate());
			default: return GoogleUtil.isLinked(x_dueReminder);
        }
    }

	@Override
	Reminder getDataItem(int p_row, int p_col) {
		return LookupHelper.getAllDueReminders(getComponent()).get(p_row);
	}
}
