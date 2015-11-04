package gui;

import data.*;
import data.ThreadGroup;
import util.*;

import java.util.*;

class UpdateItemTableModel extends ComponentTableModel
{
    private List o_updateItems;
    
    UpdateItemTableModel (ThreadGroup p_threadGroup)
    {
        super(p_threadGroup,
              new String[]{"Creation Date", "Thread", "Item", "Updated"});
        TimeUpdater.getInstance().addTimeUpdateListener(this);
    }
    
    public int getRowCount()
    {
        ThreadGroup x_threadGroup = (ThreadGroup) getComponent();
        
        if(x_threadGroup == null)
        {
            return 0;
        }

        o_updateItems = ThreadGroupHelper.getLatestUpdateItems((ThreadGroup)x_threadGroup);
        
        return o_updateItems.size();
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
        Item x_updateItem = (Item) o_updateItems.get(row); 
        
        switch(col)
        {
        case 0: return x_updateItem.getCreationDate(); 
        case 1: return x_updateItem.getThread().getText();
        case 2: return x_updateItem.getText();
        default: return DateHelper.getDateStatus(x_updateItem.getCreationDate());
        }
    }
}
