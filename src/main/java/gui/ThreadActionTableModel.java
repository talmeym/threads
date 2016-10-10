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
    
    public int getRowCount() {
        Thread x_thread = getComponent();
        
        if(x_thread == null) {
            return 0;
        }
        
        return LookupHelper.getAllActiveActions(x_thread, o_onlyNext7Days).size();
    }

    public Class getColumnClass(int col) {
        switch(col) {
			case 0: return Date.class;
			default: return String.class;
        }        
    }

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
	Item getDataItem(int p_row, int p_col) {
		return LookupHelper.getAllActiveActions(getComponent(), o_onlyNext7Days).get(p_row);
	}

	public boolean onlyNext7Days() {
		return o_onlyNext7Days;
	}

	public void setOnlyNext7Days(boolean p_onlyNext7Days) {
		o_onlyNext7Days = p_onlyNext7Days;
		fireTableDataChanged();
	}
}
