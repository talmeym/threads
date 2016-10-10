package gui;

import data.*;
import data.Thread;
import util.*;

import static data.LookupHelper.getAllActiveReminders;

class ThreadReminderTableModel extends ComponentTableModel<Thread, Reminder> {
	private boolean o_onlyDueReminders = true;

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
        
        return getAllActiveReminders(x_thread, o_onlyDueReminders).size();
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
		return getAllActiveReminders(getComponent(), o_onlyDueReminders).get(p_row);
	}

	public boolean onlyDueReminders() {
		return o_onlyDueReminders;
	}

	public void setOnlyDueReminders(boolean p_onlyDueReminders) {
		o_onlyDueReminders = p_onlyDueReminders;
		fireTableDataChanged();
	}
}
