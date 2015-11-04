package gui;

import data.ThreadGroup;
import data.*;

import java.util.Date;

class ComponentListTableModel extends ComponentTableModel
{
    ComponentListTableModel(ThreadGroup p_threadGroup)
    {
        super(p_threadGroup, 
              new String[]{"Creation Date", "Type", "Name", "Threads", "Updates", "Actions"});
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
        ThreadGroupItem x_threadGroupItem = x_threadGroup.getThreadGroupItem(row);
        
        switch(col)
        {
			case 0: return x_threadGroupItem.getCreationDate();
        	case 1: return x_threadGroupItem.getType();
			case 2: return x_threadGroupItem.getText();
			case 3: return x_threadGroupItem instanceof ThreadGroup ? ThreadGroupHelper.getAllActiveThreadGroups((ThreadGroup)x_threadGroupItem).size() : "";
			case 4: return x_threadGroupItem instanceof ThreadGroup ? ThreadGroupHelper.getAllActiveUpdates((ThreadGroup)x_threadGroupItem).size() : "";
			default: return x_threadGroupItem instanceof ThreadGroup ? ThreadGroupHelper.getAllActiveActions((ThreadGroup)x_threadGroupItem).size() : "";
        }
    }
}
