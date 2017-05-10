package gui;

import data.Thread;
import data.*;
import util.*;

import javax.swing.*;
import java.util.*;

import static data.LookupHelper.*;

class ThreadContentsTableModel extends ComponentTableModel<Thread, ThreadItem> {
    ThreadContentsTableModel(Thread p_thread) {
        super(p_thread, new String[]{"Creation Date", "Type", "Name", "Info", ""});
		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
    }

    @Override
    public Class getColumnClass(int col) {
        switch(col) {
			case 0: return Date.class;
			case 1: return Icon.class;
			default: return String.class;
        }         
    }

    @Override
    public Object getValueAt(int row, int col) {
		ThreadItem x_threadItem = getDataItem(row, col);
        
        switch(col) {
			case 0: return x_threadItem.getCreationDate();
			case 1: return ImageUtil.getIconForType(x_threadItem.getType());
			case 2: return x_threadItem.getText();
			case 3:
				if(x_threadItem instanceof Thread) {
					int x_th = getAllActiveThreads((Thread) x_threadItem).size();
					int x_up = getAllActiveUpdates((Thread) x_threadItem).size();
					int x_ac = getAllActiveActions((Thread) x_threadItem, false).size();
					return "Ths: " + x_th + " Ups: " + x_up + " Acs:" + x_ac;
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
			default: return GoogleUtil.isLinked(x_threadItem);
        }
    }

    @Override
	List<ThreadItem> getDataItems() {
    	return getComponent().getThreadItems();
	}
}
