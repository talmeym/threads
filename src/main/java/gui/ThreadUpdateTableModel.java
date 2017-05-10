package gui;

import data.*;
import data.Thread;
import util.*;

import java.util.*;

class ThreadUpdateTableModel extends ComponentTableModel<Thread, Item> {
	ThreadUpdateTableModel(Thread p_thread) {
        super(p_thread, new String[]{"Thread", "Update", "Update Date", "Updated"});
		TimeUpdater.getInstance().addTimeUpdateListener(this);
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
			case 2: return DateUtil.getFormattedDate(x_update.getModifiedDate());
			default: return DateUtil.getDateStatus(x_update.getModifiedDate());
        }
    }

    @Override
	List<Item> getDataItems() {
		return LookupHelper.getAllActiveUpdates(getComponent());
	}
}
