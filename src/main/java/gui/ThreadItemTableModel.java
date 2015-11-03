package gui;

import java.util.Date;

import util.DateHelper;
import data.Item;
import data.Thread;

class ThreadItemTableModel extends ComponentTableModel
{
    ThreadItemTableModel(Thread p_thread)
    {
        super(p_thread,
              new String[]{"Creation Date", "Item", "Due Date", "Due"});    
    }
    
    public int getRowCount()
    {
        Thread x_thread = (Thread) getComponent();
        
        if(x_thread == null)
        {
            return 0;
        }
        
        return x_thread.getItemCount();
    }

    public Class getColumnClass(int col)
    {
        switch(col)
        {
        case 0: 
        case 2: return Date.class;
        default: return String.class;
        }
    }
    
    public Object getValueAt(int row, int col)
    {
        Thread x_thread = (Thread) getComponent();
        Item x_item = x_thread.getItem(row); 
        
        switch(col)
        {
        case 0: return x_item.getCreationDate(); 
        case 1: return x_item.getText();
        case 2:
            if(x_item.getDeadline() != null)
            {
                return x_item.getDeadline().getDueDate();
            }
        default:
            if(x_item.getDeadline() != null)
            {
                return DateHelper.getDateStatus(x_item.getDeadline().getDueDate());
            }
        }
        
        return null;
    }
}
