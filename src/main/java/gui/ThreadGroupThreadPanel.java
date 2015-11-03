package gui;

import data.Component;
import data.Item;
import data.Thread;
import data.ThreadGroup;
import data.ThreadGroupHelper;

class ThreadGroupThreadPanel extends TablePanel
{
    private final ThreadGroup o_threadGroup;
    
    ThreadGroupThreadPanel(ThreadGroup p_threadGroup)
    {
        super(new ThreadListTableModel(p_threadGroup), 
              new CellRenderer(null));
        o_threadGroup = p_threadGroup;
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_threadWidth);
        fixColumnWidth(3, GUIConstants.s_creationDateWidth);
        fixColumnWidth(4, GUIConstants.s_creationDateWidth);
    }
        
    protected void addItem(int p_index)
    {
        if(p_index != -1)
        {
            Thread x_thread = (Thread) ThreadGroupHelper.getActiveThreads(o_threadGroup).get(p_index);
            Item x_item = new Item();
            x_thread.addItem(x_item);
            WindowManager.getInstance().openComponentWindow(x_item, true);                
        }
    }
    
    private void showItem(int p_index)
    {
        if(p_index != -1)
        {
            Thread x_thread = (Thread)ThreadGroupHelper.getActiveThreads(o_threadGroup).get(p_index);
            WindowManager.getInstance().openComponentWindow(x_thread, false);
        }
    }

    private void showParent(int p_index)
    {
        if(p_index != -1)
        {
            Component x_component = ((Thread)ThreadGroupHelper.getActiveThreads(o_threadGroup).get(p_index)).getThreadGroup();
            WindowManager.getInstance().openComponentWindow(x_component, false);
        }
    }
    
    void tableRowDoubleClicked(int col, int row)
    {
        switch(col)
        {
        case 1: showParent(row); break;
        case 0:
        case 2: showItem(row); break;
        default: addItem(row);
        }
    }

}
