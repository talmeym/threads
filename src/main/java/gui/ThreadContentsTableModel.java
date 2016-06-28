package gui;

import data.Thread;
import data.*;
import util.GoogleUtil;

import java.util.Date;

class ThreadContentsTableModel extends ComponentTableModel {
    ThreadContentsTableModel(Thread p_thread) {
        super(p_thread, new String[]{"Creation Date", "Type", "Name", "Th.", "Up.", "Ac.", ""});
    }

    public int getRowCount() {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null) {
            return 0;
        }
        
        return x_thread.getThreadItemCount();
    }

    public Class getColumnClass(int col) {
        switch(col) {
			case 0: return Date.class;
			default: return String.class;
        }         
    }

    public Object getValueAt(int row, int col) {
		ThreadItem x_threadItem = ((Thread)getComponent()).getThreadItem(row);
        
        switch(col) {
			case 0: return x_threadItem.getCreationDate();
			case 1: return x_threadItem.getType();
			case 2: return x_threadItem.getText();
			case 3: return x_threadItem instanceof Thread ? LookupHelper.getAllActiveThreads((Thread) x_threadItem).size() : "";
			case 4: return x_threadItem instanceof Thread ? LookupHelper.getAllActiveUpdates((Thread) x_threadItem).size() : "";
			case 5: return x_threadItem instanceof Thread ? LookupHelper.getAllActiveActions((Thread) x_threadItem).size() : "";
			default: return GoogleUtil.isLinked(x_threadItem);
        }
    }
}
