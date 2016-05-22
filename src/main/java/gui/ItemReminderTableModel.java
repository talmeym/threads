package gui;

import data.*;
import util.DateHelper;

import java.util.Date;

public class ItemReminderTableModel extends ComponentTableModel {
    public ItemReminderTableModel(Item p_item) {
        super(p_item, new String[] {"Creation Date", "Text", "Due Date", "Due"});
    }
    
    public int getRowCount() {
        Item x_item = (Item) getComponent();
        
        if(x_item == null || x_item.getDueDate() == null) {
            return 0;
        }
        
        return x_item.getReminderCount();
    }

    public Class getColumnClass(int col) {
        switch(col) {
			case 0:
			case 2: return Date.class;
			default: return String.class;
        }        
    }

    public Object getValueAt(int row, int col) {
		Reminder x_reminder = ((Item)getComponent()).getReminder(row);
        
        switch(col) {
			case 0: return x_reminder.getCreationDate();
			case 1: return x_reminder.getText();
			case 2: return x_reminder.getDueDate();
			default: return DateHelper.getDateStatus(x_reminder.getDueDate());
        }
    }    
}
