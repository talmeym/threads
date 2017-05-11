package gui;

import data.*;
import data.Thread;
import util.*;

import java.util.*;

class ThreadActionTableModel extends ComponentTableModel<Thread, Item> {
	private boolean o_onlyNext7Days = true;

	ThreadActionTableModel(Thread p_thread) {
        super(p_thread, new String[] {"Thread", "Action", "Due Date", "Due", ""});
		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
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
        Item x_action = getDataItem(row, col);
        
        switch(col) {
			case 0: return x_action.getParentThread().getText();
			case 1: return x_action.getText();
			case 2: return DateUtil.getFormattedDate(x_action.getDueDate());
			case 3: return DateUtil.getDateStatus(x_action.getDueDate());
			default: return GoogleUtil.isLinked(x_action);
        }
    }

    @Override
	List<Item> getDataItems() {
		return LookupHelper.getAllActiveActions(getComponent(), o_onlyNext7Days);
	}

	boolean onlyNext7Days() {
		return o_onlyNext7Days;
	}

	void setOnlyNext7Days(boolean p_onlyNext7Days) {
		o_onlyNext7Days = p_onlyNext7Days;
		reloadData();
	}
}
