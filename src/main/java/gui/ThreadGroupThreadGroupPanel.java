package gui;

import data.ThreadGroup;
import data.*;

class ThreadGroupThreadGroupPanel extends TablePanel
{
    private final ThreadGroup o_threadGroup;
    
    ThreadGroupThreadGroupPanel(ThreadGroup p_threadGroup)
    {
        super(new ThreadGroupListTableModel(p_threadGroup), 
              new CellRenderer(null));
        o_threadGroup = p_threadGroup;
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_threadWidth);
		fixColumnWidth(3, GUIConstants.s_statsWidth);
		fixColumnWidth(4, GUIConstants.s_statsWidth);
		fixColumnWidth(5, GUIConstants.s_statsWidth);

	}
    
    private void showParentThreadGroup(int p_index)
    {
        if(p_index != -1)
        {
            ThreadGroup x_threadGroup = (ThreadGroup) ThreadGroupHelper.getAllActiveThreadGroups(o_threadGroup).get(p_index);
            WindowManager.getInstance().openComponentWindow(x_threadGroup.getParentComponent(), false, 0);
        }
    }

    private void showThreadGroup(int p_col, int p_row)
    {
        if(p_row != -1)
        {
			int x_tab = p_col > 2 ? p_col - 2 : 0;
            ThreadGroup x_threadGroup = (ThreadGroup) ThreadGroupHelper.getAllActiveThreadGroups(o_threadGroup).get(p_row);
            WindowManager.getInstance().openComponentWindow(x_threadGroup, false, x_tab);
        }
    }

    void tableRowDoubleClicked(int col, int row)
    {
		switch(col) {
			case 1: showParentThreadGroup(row); break;
			default: showThreadGroup(col, row);
		}
    }
}
