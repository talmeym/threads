package gui;

import data.*;
import data.Thread;

import java.util.List;

class ThreadThreadTableModel extends ComponentTableModel<Thread, Thread> {
	private TableDataCache<Integer> o_cache = new TableDataCache<>();

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
			case 2: return o_cache.fillOrGet(row, col, () -> LookupHelper.getAllActiveThreads(x_thread).size());
			case 3: return o_cache.fillOrGet(row, col, () -> LookupHelper.getActiveUpdates(x_thread).size());
			default: return o_cache.fillOrGet(row, col, () -> LookupHelper.getActiveActions(x_thread, false).size());
        }
    }

    @Override
	List<Thread> getDataItems() {
    	return LookupHelper.getAllActiveThreads(getComponent());
	}

	@Override
	void reloadData() {
		o_cache.invalidate();
		super.reloadData();
	}
}
