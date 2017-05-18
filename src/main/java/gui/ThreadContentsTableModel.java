package gui;

import data.Thread;
import data.*;
import util.*;

import javax.swing.*;
import java.util.*;

import static data.LookupHelper.*;

class ThreadContentsTableModel extends ComponentTableModel<Thread, ThreadItem> {
	private TableDataCache<String> o_cache = new TableDataCache<>();

    ThreadContentsTableModel(Thread p_thread) {
        super(p_thread, new String[]{"Creation Date", "Type", "Name", "Info", ""});
    }

    @Override
    public Class getColumnClass(int col) {
        switch(col) {
			case 0: return Date.class;
			case 1: return ComponentType.class;
			default: return String.class;
        }         
    }

    @Override
    public Object getValueAt(int row, int col) {
		ThreadItem x_threadItem = getDataItem(row, col);
        
        switch(col) {
			case 0: return x_threadItem.getCreationDate();
			case 1: return x_threadItem.getType();
			case 2: return x_threadItem.getText();
			case 3: return o_cache.fillOrGet(row, col, () -> getInfoString(x_threadItem));
			default: return GoogleUtil.isLinked(x_threadItem);
        }
    }

	private String getInfoString(ThreadItem x_threadItem) {
		if(x_threadItem instanceof Thread) {
			int x_th = getAllActiveThreads((Thread) x_threadItem).size();
			int x_up = getAllActiveUpdates((Thread) x_threadItem).size();
			int x_ac = getAllActiveActions((Thread) x_threadItem, false).size();
			return x_th + " ths, " + x_up + " ups, " + x_ac + " acs";
		}

		if(x_threadItem instanceof Item) {
			Item x_item = (Item) x_threadItem;

			if(x_item.getDueDate() != null) {
				if(x_item.getDueDate().before(new Date())) {
					return "Due " + DateUtil.getDateStatus(x_item.getDueDate());
				}

				return "Due in " + DateUtil.getDateStatus(x_item.getDueDate());
			}

			return "Updated " + DateUtil.getDateStatus(x_item.getCreationDate());
		}

		return ""; // never get here
	}

	@Override
	List<ThreadItem> getDataItems() {
    	return getComponent().getThreadItems();
	}

	@Override
	void reloadData() {
		o_cache.invalidate();
		super.reloadData();
	}
}
