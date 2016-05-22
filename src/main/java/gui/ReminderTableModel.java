package gui;

import data.*;
import data.Thread;
import util.*;

import java.util.*;

class ReminderTableModel extends ComponentTableModel {
    ReminderTableModel(Thread p_thread) {
        super(p_thread, new String[] {"Creation Date", "Action", "Reminder", "Due Date", "Due"});
        TimeUpdater.getInstance().addTimeUpdateListener(this);
    }
    
    public int getRowCount() {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null) {
            return 0;
        }
        
        return ThreadHelper.getAllDueReminders(x_thread).size();
    }

    public Class getColumnClass(int col) {
        switch(col) {
			case 0:
			case 3: return Date.class;
			default: return String.class;
        }        
    }

    public Object getValueAt(int row, int col) {
        Reminder x_dueReminder = ThreadHelper.getAllDueReminders((Thread)getComponent()).get(row);
        
        switch(col) {
			case 0: return x_dueReminder.getCreationDate();
			case 1: return x_dueReminder.getItem().getText();
			case 2: return x_dueReminder.getText();
			case 3: return x_dueReminder.getDueDate();
			default: return DateHelper.getDateStatus(x_dueReminder.getDueDate());
        }
    }
}
