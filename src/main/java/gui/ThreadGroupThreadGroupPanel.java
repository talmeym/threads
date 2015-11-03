package gui;

import data.ThreadGroup;
import data.ThreadGroupHelper;

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
        fixColumnWidth(3, GUIConstants.s_creationDateWidth);
        fixColumnWidth(4, GUIConstants.s_creationDateWidth);
    }
    
    private void showGroup(int p_index)
    {
        if(p_index != -1)
        {
            ThreadGroup x_threadGroup = (ThreadGroup) ThreadGroupHelper.getActiveThreadGroups(o_threadGroup).get(p_index);
            WindowManager.getInstance().openComponentWindow(x_threadGroup, false);
        }
    }

    void tableRowDoubleClicked(int col, int row)
    {
        showGroup(row);
    }
}
