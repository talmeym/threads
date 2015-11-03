package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import util.DueDateComparator;

public class ThreadHelper
{
    static Item getLatestUpdate(Thread p_thread)
    {
        for(int i = 0; i < p_thread.getItemCount(); i++)
        {
            Item x_item = p_thread.getItem(i);
            
            if(x_item.isActive() && x_item.getDeadline() == null)
            {
                return x_item;
            }
        }
        
        return null;
    }
    
    public static List getUpdateItems(Thread p_thread)
    {
        List x_updateItems = new ArrayList();
        
        for(int i = 0; i < p_thread.getItemCount(); i++)
        {
            Item x_item = p_thread.getItem(i);
            
            if(x_item.isActive() && x_item.getDeadline() == null)
            {
                x_updateItems.add(x_item);
            }
        }
        
        
        return x_updateItems;
    }
    
    public static List getActionItems(Thread p_thread)
    {
        List x_actionItems = new ArrayList();
        
        for(int i = 0; i < p_thread.getItemCount(); i++)
        {
            Item x_item = p_thread.getItem(i);
            
            if(x_item.isActive() && x_item.getDeadline() != null)
            {
                x_actionItems.add(x_item);
            }
        }
        
        
        return x_actionItems;
    }
    
    public static Date getLatestUpdateDate(Thread p_thread)
    {
        Item x_update = getLatestUpdate(p_thread);        
        return x_update != null ? x_update.getCreationDate() : p_thread.getCreationDate();
    }
    
    public static List getReminders(Thread p_thread)
    {
        List x_result = new ArrayList();        
        Item[] x_items = p_thread.getItems();
        
        for(int i = 0; i < x_items.length; i++)
        {
            Item x_item = x_items[i];
            
            if(x_item.isActive())
            {
                x_result.addAll(ItemHelper.getReminder(x_item));
            }
        }
        
        Collections.sort(x_result, new DueDateComparator()); 
        return x_result;
    }
}
