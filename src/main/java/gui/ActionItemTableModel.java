package gui;

import java.util.Date;
import java.util.List;

import util.DateHelper;
import util.TimeUpdater;
import data.Item;
import data.ThreadGroup;
import data.ThreadGroupHelper;

class ActionItemTableModel extends ComponentTableModel
{
    private List o_actionItems;
    
    ActionItemTableModel(ThreadGroup p_threadGroup)
    {
        super(p_threadGroup, 
              new String[] {"Creation Date", "Thread", "Item", "Due Date", "Due"});
        TimeUpdater.getInstance().addTimeUpdateListener(this);
    }
    
    public int getRowCount()
    {
        ThreadGroup x_threadGroup = (ThreadGroup) getComponent();
        
        if(x_threadGroup == null)
        {
            return 0;
        }
        
        o_actionItems = ThreadGroupHelper.getActionItems((ThreadGroup)x_threadGroup);
        
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
        Item x_actionItem = (Item) o_actionItems.get(row); 
        
        switch(col)
        {
        case 0: return x_actionItem.getCreationDate(); 
        case 1: return x_actionItem.getThread().getText();
        case 2: return x_actionItem.getText();
        case 3: return x_actionItem.getDeadline().getDueDate();
        default: return DateHelper.getDateStatus(x_actionItem.getDeadline().getDueDate());
        }
    }
}
