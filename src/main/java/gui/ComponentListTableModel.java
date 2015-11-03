package gui;

import java.util.Date;

import data.Thread;
import data.ThreadGroup;
import data.ThreadGroupItem;

class ComponentListTableModel extends ComponentTableModel
{
    ComponentListTableModel(ThreadGroup p_threadGroup)
    {
        super(p_threadGroup, 
              new String[]{"Creation Date", "Type", "Name"});
    }

    public int getRowCount()
    {
        ThreadGroup x_threadGroup = (ThreadGroup) getComponent();
        
        if(x_threadGroup == null)
        {
            return 0;
        }
        
        return x_threadGroup.getThreadGroupItemCount();
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
        ThreadGroup x_threadGroup = (ThreadGroup) getComponent();
        ThreadGroupItem x_threadGroupItem = (ThreadGroupItem) x_threadGroup.getThreadGroupItem(row); 
        
        switch(col)
        {
        case 0: return x_threadGroupItem.getCreationDate(); 
        case 1: return x_threadGroupItem instanceof Thread ? "Thread" : "Thread Group"; 
        default: return x_threadGroupItem.getText();
        }
    }
}
