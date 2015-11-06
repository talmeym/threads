package gui;

import data.Thread;
import data.*;

import java.util.List;

class ThreadListTableModel extends ComponentTableModel
{
    private List o_activeThreads;
    
    ThreadListTableModel(Thread p_thread)
    {
        super(p_thread,
              new String[]{"Creation Date", "Parent", "Name", "Threads", "Updates", "Actions"});
        
    }
    
    public int getRowCount()
    {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null)
        {
            return 0;
        }
        
        o_activeThreads = ThreadHelper.getAllActiveThreads(x_thread);
        
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
        case 1: return x_thread.getThread().getText();
        case 2: return x_thread.getText();
        case 3: return ThreadHelper.getAllActiveThreads(x_thread).size();
		case 4: return ThreadHelper.getUpdateItems(x_thread).size();
		default: return ThreadHelper.getActionItems(x_thread).size();
        }
    }
}