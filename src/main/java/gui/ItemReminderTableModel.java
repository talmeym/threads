package gui;

import data.*;
import util.*;

public class ItemReminderTableModel extends ComponentTableModel<Item, Reminder> {
    public ItemReminderTableModel(Item p_item) {
        super(p_item, new String[] {"Text", "Due Date", "Due", ""});
		TimeUpdater.getInstance().addTimeUpdateListener(this);
    }

	@Override
    public int getRowCount() {
        Item x_item = getComponent();
        
        if(x_item == null || x_item.getDueDate() == null) {
            return 0;
        }
        
        return x_item.getReminderCount();
    }

	@Override
    public Class getColumnClass(int col) {
		return String.class;
    }

	@Override
    public Object getValueAt(int row, int col) {
		Reminder x_reminder = getComponent().getReminder(row);
        
        switch(col) {
			case 0: return x_reminder.getText();
			case 1: return DateUtil.getFormattedDate(x_reminder.getDueDate());
			case 2: return DateUtil.getDateStatus(x_reminder.getDueDate(), x_reminder.getItem().getDueDate(), "before");
			default: return GoogleUtil.isLinked(x_reminder);
        }
    }

	@Override
	Reminder getDataItem(int p_row, int p_col) {
		return getComponent().getReminder(p_row);
	}
}
