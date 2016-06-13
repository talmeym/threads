package gui;

import data.*;
import data.Thread;
import util.*;

import java.util.*;

class ThreadUpdateTableModel extends ComponentTableModel {
	ThreadUpdateTableModel(Thread p_thread) {
        super(p_thread, new String[]{"Thread", "Update", "Update Date", "Updated"});
        TimeUpdater.getInstance().addTimeUpdateListener(this);
    }

	@Override
	public int getRowCount() {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null) {
            return 0;
        }

        return LookupHelper.getAllActiveUpdates(x_thread).size();
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
        Item x_updateItem = LookupHelper.getAllActiveUpdates((Thread) getComponent()).get(row);
        
        switch(col) {
			case 0: return x_updateItem.getParentThread().getText();
			case 1: return x_updateItem.getText();
			case 2: return x_updateItem.getCreationDate();
			default: return DateUtil.getDateStatus(x_updateItem.getCreationDate());
        }
    }
}
