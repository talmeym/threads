package gui;

import data.*;
import data.Thread;
import data.ThreadGroup;

public class ThreadGroupUpdatePanel extends TablePanel
{
    private final ThreadGroup o_threadGroup;
    
    public ThreadGroupUpdatePanel(ThreadGroup p_threadGroup)
    {
        super(new UpdateItemTableModel(p_threadGroup), 
              new CellRenderer(null));
        o_threadGroup = p_threadGroup;
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_threadWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusWidth);
    }
    

    private void showThread(int p_index)
    {
        if(p_index != -1)
        {
            Item x_threadItem = (Item) ThreadGroupHelper.getLatestUpdateItems(o_threadGroup).get(p_index); 
            WindowManager.getInstance().openComponentWindow(x_threadItem.getThread(), false);
        }
    }

    private void showItem(int p_index)
    {
        if(p_index != -1)
        {
            Item x_threadItem = (Item) ThreadGroupHelper.getLatestUpdateItems(o_threadGroup).get(p_index); 
            WindowManager.getInstance().openComponentWindow(x_threadItem, false);
        }
    }
    
    private void addItem(int p_index)
    {
        if(p_index != -1)
        {
            Item x_threadItem = (Item) ThreadGroupHelper.getLatestUpdateItems(o_threadGroup).get(p_index);
            Thread x_thread = x_threadItem.getThread();
            Item x_newItem = new Item();
            x_thread.addItem(x_newItem);
            WindowManager.getInstance().openComponentWindow(x_newItem, true);
        }
    }
    
    void tableRowDoubleClicked(int col, int row)
    {
        switch(col)
        {
        case 0: addItem(row); break;
        case 1: showThread(row); break;
        default: showItem(row); break;
        }
    }
}
