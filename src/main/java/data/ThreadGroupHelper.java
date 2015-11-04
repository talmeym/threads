package data;

import util.*;

import java.util.*;

public class ThreadGroupHelper
{
    public static List getLatestUpdateItems(ThreadGroup p_threadGroup)
    {
        List x_result = new ArrayList();
        ThreadGroupItem[] x_items = p_threadGroup.getThreadGroupItems();
        
        for(int i = 0; i < x_items.length; i++)
        {
            ThreadGroupItem x_item = x_items[i];
            
            if(x_item.isActive())
            {
                if(x_item instanceof Thread)
                {
                    Item x_latestItem = ThreadHelper.getLatestUpdate((Thread) x_item);
                    
                    if(x_latestItem != null)
                    {
                        x_result.add(x_latestItem);
                    }
                }
                else if(x_item instanceof ThreadGroup)
                {
                    x_result.addAll(ThreadGroupHelper.getLatestUpdateItems((ThreadGroup)x_item));
                }
            }
        }

        Collections.sort(x_result, new CreationDateComparator());        
        return x_result;
    }
    
    public static List getActionItems(ThreadGroup p_threadGroup)
    {
        List x_result = new ArrayList();
        ThreadGroupItem[] x_items = p_threadGroup.getThreadGroupItems();
        
        for(int i = 0; i < x_items.length; i++)
        {
            ThreadGroupItem x_item = x_items[i];
            
            if(x_item.isActive())
            {
                if(x_item instanceof Thread)
                {
                    x_result.addAll(ThreadHelper.getActionItems((Thread)x_item));
                }
                else if(x_item instanceof ThreadGroup)
                {
                    x_result.addAll(ThreadGroupHelper.getActionItems((ThreadGroup)x_item));
                }
            }
        }
        
        Collections.sort(x_result, new DueDateComparator());        
        return x_result;
    }
    
    public static List getActiveThreads(ThreadGroup p_threadGroup)
    {
        List x_result = new ArrayList();        
        ThreadGroupItem[] x_items = p_threadGroup.getThreadGroupItems();
        
        for(int i = 0; i < x_items.length; i++)
        {
            ThreadGroupItem x_item = x_items[i];
            
            if(x_item.isActive())
            {
                if(x_item instanceof Thread)
                {
                    x_result.add(x_item);
                }
                else if(x_item instanceof ThreadGroup)
                {
                    x_result.addAll(getActiveThreads((ThreadGroup)x_item));
                }
            }
        }
        
        Collections.sort(x_result, new TextComparator()); 
        return x_result;
    }
    
    public static List getActiveThreadGroups(ThreadGroup p_threadGroup)
    {
        List x_result = new ArrayList();        
        ThreadGroupItem[] x_items = p_threadGroup.getThreadGroupItems();
        
        for(int i = 0; i < x_items.length; i++)
        {
            ThreadGroupItem x_item = x_items[i];
            
            if(x_item.isActive())
            {
                if(x_item instanceof ThreadGroup)
                {
                    x_result.add(x_item);
                    x_result.addAll(ThreadGroupHelper.getActiveThreadGroups((ThreadGroup)x_item));
                }
            }
        }
        
        Collections.sort(x_result, new TextComparator()); 
        return x_result;
    }
    
    public static List getReminders(ThreadGroup p_threadGroup)
    {
        List x_result = new ArrayList();
        ThreadGroupItem[] x_items = p_threadGroup.getThreadGroupItems();
        
        for(int i = 0; i < x_items.length; i++)
        {
            ThreadGroupItem x_item = x_items[i];
            
            if(x_item.isActive())
            {
                if(x_item instanceof Thread)
                {
                    x_result.addAll(ThreadHelper.getReminders((Thread)x_item));
                }
                else if(x_item instanceof ThreadGroup)
                {
                    x_result.addAll(ThreadGroupHelper.getReminders((ThreadGroup)x_item));
                }
            }
        }
        
        Collections.sort(x_result, new DueDateComparator());        
        return x_result;
    }
}
