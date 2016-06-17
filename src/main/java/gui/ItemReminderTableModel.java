package gui;

import data.*;
import util.DateUtil;

import java.util.Date;

public class ItemReminderTableModel extends ComponentTableModel {
    public ItemReminderTableModel(Item p_item) {
        super(p_item, new String[] {"Text", "Due Date", "Due"});
    }

	@Override
    public int getRowCount() {
        Item x_item = (Item) getComponent();
        
        if(x_item == null || x_item.getDueDate() == null) {
            return 0;
        }
        
        return x_item.getReminderCount();
    }

	@Override
    public Class getColumnClass(int col) {
        switch(col) {
			case 1: return Date.class;
			default: return String.class;
        }        
    }

	@Override
    public Object getValueAt(int row, int col) {
		Reminder x_reminder = ((Item)getComponent()).getReminder(row);
        
        switch(col) {
			case 0: return x_reminder.getText();
			case 1: return x_reminder.getDueDate();
			default: return DateUtil.getDateStatus(x_reminder.getDueDate());
        }
    }    
}
