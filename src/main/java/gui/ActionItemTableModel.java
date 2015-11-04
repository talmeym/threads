package gui;

import data.*;
import data.ThreadGroup;
import util.*;

import java.util.*;

class ActionItemTableModel extends ComponentTableModel
{
    private List o_actionItems;
    
    ActionItemTableModel(ThreadGroup p_threadGroup)
    {
        super(p_threadGroup, 
              new String[] {"Creation Date", "Thread", "Name", "Due Date", "Due"});
        TimeUpdater.getInstance().addTimeUpdateListener(this);
    }
    
    public int getRowCount()
    {
        ThreadGroup x_threadGroup = (ThreadGroup) getComponent();
        
        if(x_threadGroup == null)
        {
            return 0;
        }
        
        o_actionItems = ThreadGroupHelper.getAllActiveActions(x_threadGroup);
        
        return o_actionItems.size();
    }

    public Class getColumnClass(int col)
    {        
        switch(col)
        {
        case 0: 
        case 3: return Date.class;
        default: return String.class; 
        }        
    }

    public Object getValueAt(int row, int col)
    {
        Item x_item = (Item) o_actionItems.get(row);
        
        switch(col)
        {
        case 0: return x_item.getCreationDate();
        case 1: return x_item.getThreadGroup().getText();
        case 2: return x_item.getText();
        case 3: return x_item.getDueDate();
        default: return DateHelper.getDateStatus(x_item.getDueDate());
        }
    }
}
