package gui;

import data.Thread;
import data.*;

class ThreadThreadPanel extends TablePanel
{
    private final Thread o_thread;
    
    ThreadThreadPanel(Thread p_thread)
    {
        super(new ThreadListTableModel(p_thread),
              new CellRenderer(null));
        o_thread = p_thread;
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_threadWidth);
		fixColumnWidth(3, GUIConstants.s_statsWidth);
		fixColumnWidth(4, GUIConstants.s_statsWidth);
		fixColumnWidth(5, GUIConstants.s_statsWidth);

	}
    
    private void showParentThread(int p_index)
    {
        if(p_index != -1)
        {
            Thread x_thread = (Thread) ThreadHelper.getAllActiveThreads(o_thread).get(p_index);
            WindowManager.getInstance().openComponentWindow(x_thread.getParentComponent(), false, 0);
        }
    }

    private void showThread(int p_col, int p_row)
    {
        if(p_row != -1)
        {
			int x_tab = p_col > 2 ? p_col - 2 : 0;
            Thread x_thread = (Thread) ThreadHelper.getAllActiveThreads(o_thread).get(p_row);
            WindowManager.getInstance().openComponentWindow(x_thread, false, x_tab);
        }
    }

    void tableRowDoubleClicked(int col, int row)
    {
		switch(col) {
			case 1: showParentThread(row); break;
			default: showThread(col, row);
		}
    }
}
