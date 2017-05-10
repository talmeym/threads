package gui;

import data.Thread;
import data.*;

import java.util.List;

class ThreadThreadTableModel extends ComponentTableModel<Thread, Thread> {
		ThreadThreadTableModel(Thread p_thread) {
        super(p_thread, new String[]{"Parent", "Thread", "Threads", "Updates", "Actions"});
    }

	@Override
    public Class getColumnClass(int col) {
        return String.class;
    }

	@Override
    public Object getValueAt(int row, int col) {
        Thread x_thread = getDataItem(row, col);
        
        switch(col) {
			case 0: return x_thread.getParentThread().getText();
			case 1: return x_thread.getText();
			case 2: return LookupHelper.getAllActiveThreads(x_thread).size();
			case 3: return LookupHelper.getActiveUpdates(x_thread).size();
			default: return LookupHelper.getActiveActions(x_thread, false).size();
        }
    }

    @Override
	List<Thread> getDataItems() {
    	return LookupHelper.getAllActiveThreads(getComponent());
	}
}
