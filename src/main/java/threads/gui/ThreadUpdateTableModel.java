package threads.gui;

import threads.data.Item;
import threads.data.Thread;
import threads.util.TimedUpdater;

import java.util.Date;
import java.util.List;

import static threads.data.LookupHelper.getAllActiveUpdates;
import static threads.util.DateUtil.getDateStatus;
import static threads.util.DateUtil.getFormattedDate;

class ThreadUpdateTableModel extends ComponentTableModel<Thread, Item> {
	ThreadUpdateTableModel(Thread p_thread) {
        super(p_thread, new String[]{"Thread", "Update", "Update Date", "Updated"});
        TimedUpdater.getInstance().addActivityListener(this::fireTableDataChanged);
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
