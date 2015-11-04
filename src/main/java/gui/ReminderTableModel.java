package gui;

import data.*;
import data.ThreadGroup;
import util.*;

import java.util.*;

class ReminderTableModel extends ComponentTableModel
{
    private List o_dueReminders;
    
    ReminderTableModel(ThreadGroup p_threadGroup)
    {
        super(p_threadGroup, 
              new String[] {"Creation Date", "Thread", "Name", "Due Date", "Due"});
        TimeUpdater.getInstance().addTimeUpdateListener(this);
    }
    
    public int getRowCount()
    {
        ThreadGroup x_threadGroup = (ThreadGroup) getComponent();
        
        if(x_threadGroup == null)
        {
            return 0;
        }
        
        o_dueReminders = ThreadGroupHelper.getAllReminders(x_threadGroup);
        
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
        case 1: return x_dueReminder.getItem().getThreadGroup().getText();
        case 2: return x_dueReminder.getText();
        case 3: return x_dueReminder.getDate();
        default: return DateHelper.getDateStatus(x_dueReminder.getDate());
        }
    }
}
