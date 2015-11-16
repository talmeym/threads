package gui;

import data.*;
import data.Thread;
import util.*;

import java.util.*;

class ReminderTableModel extends ComponentTableModel
{
    private List o_dueReminders;
    
    ReminderTableModel(Thread p_thread)
    {
        super(p_thread,
              new String[] {"Creation Date", "Thread", "Name", "Due Date", "Due"});
        TimeUpdater.getInstance().addTimeUpdateListener(this);
    }
    
    public int getRowCount()
    {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null)
        {
            return 0;
        }
        
        o_dueReminders = ThreadHelper.getAllDueReminders(x_thread);
        
        return o_dueReminders.size();
    }

    public Class getColumnClass(int col)
    {        
        switch(col)
        {
        case 0: 
        case 3: return Date.class;
        default: return String.class; 
        }        
    }

    public Object getValueAt(int row, int col)
    {
        Reminder x_dueReminder = (Reminder) o_dueReminders.get(row); 
        
        switch(col)
        {
        case 0: return x_dueReminder.getCreationDate(); 
        case 1: return x_dueReminder.getItem().getThread().getText();
        case 2: return x_dueReminder.getText();
        case 3: return x_dueReminder.getDate();
        default: return DateHelper.getDateStatus(x_dueReminder.getDate());
        }
    }
}
