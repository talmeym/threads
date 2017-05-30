package gui;

import data.*;
import data.Thread;

import java.util.*;

import static data.LookupHelper.getAllActiveUpdates;
import static util.DateUtil.*;

class ThreadUpdateTableModel extends ComponentTableModel<Thread, Item> {
	ThreadUpdateTableModel(Thread p_thread) {
        super(p_thread, new String[]{"Thread", "Update", "Update Date", "Updated"});
    }

	@Override
    public Class getColumnClass(int col) {
        switch(col) {
			case 0: return Date.class;
			default: return String.class;
        }        
    }

	@Override
    public Object getValueAt(int row, int col) {
        Item x_update = getDataItem(row, col);
        
        switch(col) {
			case 0: return x_update.getParentThread().getText();
			case 1: return x_update.getText();
			case 2: return getFormattedDate(x_update.getModifiedDate());
			default: return getDateStatus(x_update.getModifiedDate());
        }
    }

    @Override
	List<Item> getDataItems() {
		return getAllActiveUpdates(getComponent());
	}
}
