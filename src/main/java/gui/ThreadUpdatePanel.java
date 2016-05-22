package gui;

import data.*;
import data.Thread;

public class ThreadUpdatePanel extends TablePanel {
    private final Thread o_thread;
    
    public ThreadUpdatePanel(Thread p_thread) {
        super(new UpdateItemTableModel(p_thread), new CellRenderer(null));
        o_thread = p_thread;
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_threadWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusWidth);
    }

    private void showThread(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);
			Thread x_thread = x_threadItem.getThread();

			if(x_thread != o_thread) {
				WindowManager.getInstance().openComponentWindow(x_thread, false, 0);
			}
        }
    }

    private void showItem(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);
            WindowManager.getInstance().openComponentWindow(x_threadItem, false, 0);
        }
    }

	@Override
	void tableRowClicked(int col, int row) {
		//do nothing
	}

    void tableRowDoubleClicked(int col, int row) {
        switch(col) {
			case 1: showThread(row); break;
			default: showItem(row); break;
        }
    }
}
