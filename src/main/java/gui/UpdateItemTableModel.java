package gui;

import data.*;
import data.Thread;
import util.*;

import java.util.*;

class UpdateItemTableModel extends ComponentTableModel
{
    private List o_updateItems;
    
    UpdateItemTableModel (Thread p_thread)
    {
        super(p_thread,
              new String[]{"Creation Date", "Thread", "Name", "Updated"});
        TimeUpdater.getInstance().addTimeUpdateListener(this);
    }
    
    public int getRowCount()
    {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null)
        {
            return 0;
        }

        o_updateItems = ThreadHelper.getAllActiveUpdates(x_thread);
        
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
