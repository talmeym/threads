package gui;

import java.util.List;

import data.ThreadGroup;
import data.ThreadGroupHelper;

class ThreadGroupListTableModel extends ComponentTableModel
{
    private List o_activeThreadGroups;
    
    ThreadGroupListTableModel(ThreadGroup p_threadGroup)
    {
        super(p_threadGroup,
              new String[]{"Creation Date", "Parent", "Name", "Threads", "ThreadGroups"});
        
    }
    
    public int getRowCount()
    {
        ThreadGroup x_threadGroup = (ThreadGroup) getComponent();
        
        if(x_threadGroup == null)
        {
            return 0;
        }
        
        o_activeThreadGroups = ThreadGroupHelper.getActiveThreadGroups(x_threadGroup);
        
        return o_activeThreadGroups.size();
    }

    public Class getColumnClass(int col)
    {        
        return String.class;
    }

    public Object getValueAt(int row, int col)
    {
        ThreadGroup x_threadGroup = (ThreadGroup) o_activeThreadGroups.get(row); 
        
        switch(col)
        {
        case 0: return x_threadGroup.getCreationDate(); 
        case 1: return x_threadGroup.getThreadGroup().getText();
        case 2: return x_threadGroup.getText();
        case 3: return new Integer(ThreadGroupHelper.getActiveThreads(x_threadGroup).size());
        default: return new Integer(ThreadGroupHelper.getActiveThreadGroups(x_threadGroup).size());
        }
    }
}
