package gui;

import data.Thread;
import data.*;

class ThreadThreadTableModel extends ComponentTableModel {
    ThreadThreadTableModel(Thread p_thread) {
        super(p_thread, new String[]{"Parent", "Thread", "Th.", "Up.", "Ac."});
    }

	@Override
    public int getRowCount() {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null) {
            return 0;
        }
        
        return LookupHelper.getAllActiveThreads(x_thread).size();
    }

	@Override
    public Class getColumnClass(int col) {
        return String.class;
    }

	@Override
    public Object getValueAt(int row, int col) {
        Thread x_thread = LookupHelper.getAllActiveThreads((Thread) getComponent()).get(row);
        
        switch(col) {
			case 0: return x_thread.getParentThread().getText();
			case 1: return x_thread.getText();
			case 2: return LookupHelper.getAllActiveThreads(x_thread).size();
			case 3: return LookupHelper.getActiveUpdates(x_thread).size();
			default: return LookupHelper.getActiveActions(x_thread).size();
        }
    }
}
