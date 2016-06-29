package gui;

import data.Thread;
import data.*;
import util.*;

import java.util.Date;

class ThreadContentsTableModel extends ComponentTableModel {
    ThreadContentsTableModel(Thread p_thread) {
        super(p_thread, new String[]{"Creation Date", "Type", "Name", "Info", ""});
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
			case 3:
				if(x_threadItem instanceof Thread) {
					int x_threads = LookupHelper.getAllActiveThreads((Thread) x_threadItem).size();
					int x_updates = LookupHelper.getAllActiveUpdates((Thread) x_threadItem).size();
					int x_actions = LookupHelper.getAllActiveActions((Thread) x_threadItem).size();
					return "Ths: " + x_threads + " Ups: " + x_updates + " Acs:" + x_actions;
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

				return "";
			default: return GoogleUtil.isLinked(x_threadItem);
        }
    }
}
