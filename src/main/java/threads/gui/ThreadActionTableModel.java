package threads.gui;

import threads.data.*;
import threads.data.Thread;
import threads.util.TimedUpdater;

import java.util.*;

import static threads.data.LookupHelper.getAllActiveActions;
import static threads.util.DateUtil.*;
import static threads.util.GoogleUtil.googleAccount;

class ThreadActionTableModel extends ComponentTableModel<Thread, Item> {
	private boolean o_onlyNext7Days = true;

	ThreadActionTableModel(Thread p_thread) {
        super(p_thread, new String[] {"Thread", "Action", "Due Date", "Due", ""});
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
        Item x_action = getDataItem(row, col);
        
        switch(col) {
			case 0: return x_action.getParentThread().getText();
			case 1: return x_action.getText();
			case 2: return getFormattedDate(x_action.getDueDate());
			case 3: return getDateStatus(x_action.getDueDate());
			default: return googleAccount(x_action);
        }
    }

    @Override
	List<Item> getDataItems() {
		return getAllActiveActions(getComponent(), o_onlyNext7Days);
	}

	boolean onlyNext7Days() {
		return o_onlyNext7Days;
	}

	void setOnlyNext7Days(boolean p_onlyNext7Days) {
		o_onlyNext7Days = p_onlyNext7Days;
		reloadData();
	}
}
