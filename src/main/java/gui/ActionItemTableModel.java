package gui;

import data.*;
import data.Thread;
import util.*;

import java.util.*;

class ActionItemTableModel extends ComponentTableModel
{
    private List o_actionItems;
    
    ActionItemTableModel(Thread p_thread)
    {
        super(p_thread,
              new String[] {"Creation Date", "Thread", "Name", "Due Date", "Due"});
        TimeUpdater.getInstance().addTimeUpdateListener(this);
    }
    
    public int getRowCount()
    {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null)
        {
            return 0;
        }
        
        o_actionItems = ThreadHelper.getAllActiveActions(x_thread);
        
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
        case 1: return x_item.getThread().getText();
        case 2: return x_item.getText();
        case 3: return x_item.getDueDate();
        default: return DateHelper.getDateStatus(x_item.getDueDate());
        }
    }
}
