package gui;

import data.Thread;
import data.*;

import java.util.Date;

class ComponentListTableModel extends ComponentTableModel
{
    ComponentListTableModel(Thread p_thread)
    {
        super(p_thread,
              new String[]{"Creation Date", "Type", "Name", "Threads", "Updates", "Actions"});
    }

    public int getRowCount()
    {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null)
        {
            return 0;
        }
        
        return x_thread.getThreadItemCount();
    }

    public Class getColumnClass(int col)
    {        
        switch(col)
        {
        case 0: return Date.class;
        default: return String.class; 
        }         
    }

    public Object getValueAt(int row, int col)
    {
        Thread x_thread = (Thread) getComponent();
        ThreadItem x_threadItem = x_thread.getThreadItem(row);
        
        switch(col)
        {
			case 0: return x_threadItem.getCreationDate();
        	case 1: return x_threadItem.getType();
			case 2: return x_threadItem.getText();
			case 3: return x_threadItem instanceof Thread ? ThreadHelper.getAllActiveThreads((Thread) x_threadItem).size() : "";
			case 4: return x_threadItem instanceof Thread ? ThreadHelper.getAllActiveUpdates((Thread) x_threadItem).size() : "";
			default: return x_threadItem instanceof Thread ? ThreadHelper.getAllActiveActions((Thread) x_threadItem).size() : "";
        }
    }
}
