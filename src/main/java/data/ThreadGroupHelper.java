package data;

import util.*;

import java.util.*;

public class ThreadGroupHelper
{
    public static List getAllActiveUpdates(ThreadGroup p_threadGroup)
    {
        List x_result = new ArrayList();

		Item latestUpdate = getLatestUpdate(p_threadGroup);

		if(latestUpdate != null) {
			x_result.add(latestUpdate);
		}

		ThreadGroupItem[] x_groupItems = p_threadGroup.getThreadGroupItems();
        
        for(int i = 0; i < x_groupItems.length; i++)
        {
            ThreadGroupItem x_groupItem = x_groupItems[i];
            
            if(x_groupItem.isActive())
            {
                if(x_groupItem instanceof ThreadGroup)
                {
                    x_result.addAll(ThreadGroupHelper.getAllActiveUpdates((ThreadGroup) x_groupItem));
                }
            }
        }

        Collections.sort(x_result, new CreationDateComparator());        
        return x_result;
    }
    
    public static List getAllActiveActions(ThreadGroup p_threadGroup)
    {
        List x_result = new ArrayList();
		x_result.addAll(getActionItems(p_threadGroup));

		ThreadGroupItem[] x_groupItems = p_threadGroup.getThreadGroupItems();
        
        for(int i = 0; i < x_groupItems.length; i++)
        {
            ThreadGroupItem x_groupItem = x_groupItems[i];
            
            if(x_groupItem.isActive())
            {
                if(x_groupItem instanceof ThreadGroup)
                {
                    x_result.addAll(ThreadGroupHelper.getAllActiveActions((ThreadGroup) x_groupItem));
                }
            }
        }
        
        Collections.sort(x_result, new DueDateComparator());        
        return x_result;
    }
    
    public static List getAllActiveThreadGroups(ThreadGroup p_threadGroup)
    {
        List x_result = new ArrayList();        
        ThreadGroupItem[] x_groupItems = p_threadGroup.getThreadGroupItems();
        
        for(int i = 0; i < x_groupItems.length; i++)
        {
            ThreadGroupItem x_groupItem = x_groupItems[i];
            
            if(x_groupItem.isActive())
            {
                if(x_groupItem instanceof ThreadGroup)
                {
                    x_result.add(x_groupItem);
                    x_result.addAll(ThreadGroupHelper.getAllActiveThreadGroups((ThreadGroup) x_groupItem));
                }
            }
        }
        
        Collections.sort(x_result, new TextComparator()); 
        return x_result;
    }
    
    public static List getAllReminders(ThreadGroup p_threadGroup)
    {
        List x_result = new ArrayList();
		x_result.addAll(getReminders(p_threadGroup));
        ThreadGroupItem[] x_groupItems = p_threadGroup.getThreadGroupItems();
        
        for(int i = 0; i < x_groupItems.length; i++)
        {
            ThreadGroupItem x_groupItem = x_groupItems[i];
            
            if(x_groupItem.isActive())
            {
                if(x_groupItem instanceof ThreadGroup)
                {
                    x_result.addAll(ThreadGroupHelper.getAllReminders((ThreadGroup) x_groupItem));
                }
            }
        }
        
        Collections.sort(x_result, new DueDateComparator());        
        return x_result;
    }

	static Item getLatestUpdate(ThreadGroup p_threadGroup)
	{
		for(int i = 0; i < p_threadGroup.getThreadGroupItemCount(); i++)
		{
			ThreadGroupItem x_groupItem = p_threadGroup.getThreadGroupItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive() && x_item.getDueDate() == null)
				{
					return x_item;
				}
			}
		}

		return null;
	}

	public static List getUpdateItems(ThreadGroup p_threadGroup)
	{
		List x_updateItems = new ArrayList();
		for(int i = 0; i < p_threadGroup.getThreadGroupItemCount(); i++)
		{
			ThreadGroupItem x_groupItem = p_threadGroup.getThreadGroupItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive() && x_item.getDueDate() == null)
				{
					x_updateItems.add(x_item);
				}
			}
		}

		return x_updateItems;
	}

	public static List getActionItems(ThreadGroup p_threadGroup)
	{
		List x_actionItems = new ArrayList();

		for(int i = 0; i < p_threadGroup.getThreadGroupItemCount(); i++)
		{
			ThreadGroupItem x_groupItem = p_threadGroup.getThreadGroupItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive() && x_item.getDueDate() != null)
				{
					x_actionItems.add(x_item);
				}
			}
		}


		return x_actionItems;
	}

	public static List getReminders(ThreadGroup p_threadGroup)
	{
		List x_reminders = new ArrayList();

		for(int i = 0; i < p_threadGroup.getThreadGroupItemCount(); i++)
		{
			ThreadGroupItem x_groupItem = p_threadGroup.getThreadGroupItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive())
				{
					x_reminders.addAll(ItemHelper.getReminder(x_item));
				}
			}
		}

		return x_reminders;
	}
}
