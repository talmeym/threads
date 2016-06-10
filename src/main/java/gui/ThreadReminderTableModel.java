package gui;

import data.*;
import data.Thread;
import util.*;

import java.util.*;

class ThreadReminderTableModel extends ComponentTableModel {
    ThreadReminderTableModel(Thread p_thread) {
        super(p_thread, new String[] {"Creation Date", "Action", "Reminder", "Due Date", "Due"});
    }

	@Override
	public int getRowCount() {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null) {
            return 0;
        }
        
        return LookupHelper.getAllDueReminders(x_thread).size();
    }

	@Override
    public Class getColumnClass(int col) {
        switch(col) {
			case 0:
			case 3: return Date.class;
			default: return String.class;
        }        
    }

	@Override
    public Object getValueAt(int row, int col) {
        Reminder x_dueReminder = LookupHelper.getAllDueReminders((Thread) getComponent()).get(row);
        
        switch(col) {
			case 0: return x_dueReminder.getCreationDate();
			case 1: return x_dueReminder.getItem().getText();
			case 2: return x_dueReminder.getText();
			case 3: return x_dueReminder.getDueDate();
			default: return DateUtil.getDateStatus(x_dueReminder.getDueDate());
        }
    }
}
