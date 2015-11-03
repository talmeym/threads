package gui;

import java.util.List;

import data.Thread;
import data.ThreadGroup;
import data.ThreadGroupHelper;
import data.ThreadHelper;

class ThreadListTableModel extends ComponentTableModel
{
    private List o_activeThreads;
    
    ThreadListTableModel(ThreadGroup p_threadGroup)
    {
        super(p_threadGroup,
              new String[]{"Creation Date", "Parent", "Name", "Updates", "Actions"});
        
    }
    
    public int getRowCount()
    {
        ThreadGroup x_threadGroup = (ThreadGroup) getComponent();
        
        if(x_threadGroup == null)
        {
            return 0;
        }
        
        o_activeThreads = ThreadGroupHelper.getActiveThreads(x_threadGroup);
        
        return o_activeThreads.size();
    }

    public Class getColumnClass(int col)
    {        
        return String.class;
    }

    public Object getValueAt(int row, int col)
    {
        Thread x_thread = (Thread) o_activeThreads.get(row); 
        
        switch(col)
        {
        case 0: return x_thread.getCreationDate(); 
        case 1: return x_thread.getThreadGroup().getText();
        case 2: return x_thread.getText();
        case 3: return new Integer(ThreadHelper.getUpdateItems(x_thread).size());
        default: return new Integer(ThreadHelper.getActionItems(x_thread).size());
        }
    }
}
