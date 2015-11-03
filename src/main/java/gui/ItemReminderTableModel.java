package gui;

import java.util.Date;

import util.DateHelper;
import data.Item;
import data.Reminder;

public class ItemReminderTableModel extends ComponentTableModel
{
    public ItemReminderTableModel(Item p_item)
    {
        super(p_item, 
              new String[] {"Creation Date", "Text", "Date", "Time Till Due"});
    }
    
    public int getRowCount()
    {
        Item x_item = (Item) getComponent();
        
        if(x_item == null || x_item.getDeadline() == null)
        {
            return 0;
        }
        
        return x_item.getDeadline().getReminderCount();
    }

    public Class getColumnClass(int col)
    {        
        switch(col)
        {
        case 0: 
        case 2: return Date.class;
        default: return String.class; 
        }        
    }

    public Object getValueAt(int row, int col)
    {
        Item x_item = (Item) getComponent();
        Reminder x_reminder = x_item.getDeadline().getReminder(row);
        
        switch(col)
        {
        case 0: return x_reminder.getCreationDate(); 
        case 1: return x_reminder.getText();
        case 2: return x_reminder.getDate();
        default: return DateHelper.getDateStatus(x_reminder.getDate(), x_reminder.getDeadline().getDueDate());
        }
    }    
}
