package gui;

import data.*;
import data.ThreadGroup;

public class ThreadGroupActionPanel extends TablePanel
{
    private final ThreadGroup o_threadGroup;
    
    ThreadGroupActionPanel(ThreadGroup p_threadGroup)
    {
        super(new ActionItemTableModel(p_threadGroup), 
              new ActionCellRenderer(p_threadGroup));
        o_threadGroup = p_threadGroup;
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_threadWidth);
        fixColumnWidth(3, GUIConstants.s_creationDateWidth);
        fixColumnWidth(4, GUIConstants.s_dateStatusWidth);
    }
    
    private void showThread(int p_index)
    {
        if(p_index != -1)
        {
            Item x_threadItem = (Item) ThreadGroupHelper.getAllActiveActions(o_threadGroup).get(p_index);
            WindowManager.getInstance().openComponentWindow(x_threadItem.getThreadGroup(), false, 0);
        }
    }

    private void showItem(int p_index)
    {
        if(p_index != -1)
        {
            Item x_threadItem = (Item) ThreadGroupHelper.getAllActiveActions(o_threadGroup).get(p_index);
            WindowManager.getInstance().openComponentWindow(x_threadItem, false, 0);
        }
    }
    
    void tableRowDoubleClicked(int col, int row)
    {
        if(col == 1)
        {
            showThread(row);
        }
        else
        {
            showItem(row);
        }
    }
}
