package threads.gui;

import threads.data.Item;
import threads.data.Thread;
import threads.data.View;
import threads.util.TimedUpdater;

import java.util.Date;
import java.util.List;

import static threads.data.LookupHelper.getAllActiveActions;
import static threads.data.View.SEVENDAYS;
import static threads.util.DateUtil.getDateStatus;
import static threads.util.DateUtil.getFormattedDate;
import static threads.util.GoogleUtil.googleAccount;

class ThreadActionTableModel extends ComponentTableModel<Thread, Item> {
	private View o_view = SEVENDAYS;

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
		return getAllActiveActions(getComponent(), o_view == null ? SEVENDAYS : o_view);
	}

	View getView() {
		return o_view;
	}

	void setView(View p_view) {
		o_view = p_view;
		reloadData();
	}
}
