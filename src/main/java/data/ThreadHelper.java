package data;

import util.*;

import java.util.*;

public class ThreadHelper
{
    public static List getAllActiveUpdates(Thread p_thread)
    {
        List x_result = new ArrayList();

		x_result.addAll(getActiveUpdates(p_thread));

		ThreadItem[] x_groupItems = p_thread.getThreadItems();
        
        for(int i = 0; i < x_groupItems.length; i++)
        {
            ThreadItem x_groupItem = x_groupItems[i];
            
            if(x_groupItem.isActive())
            {
                if(x_groupItem instanceof Thread)
                {
                    x_result.addAll(ThreadHelper.getAllActiveUpdates((Thread) x_groupItem));
                }
            }
        }

        Collections.sort(x_result, new CreationDateComparator());
        return x_result;
    }
    
    public static List getAllActiveActions(Thread p_thread)
    {
        List x_result = new ArrayList();
		x_result.addAll(getActiveActions(p_thread));

		ThreadItem[] x_groupItems = p_thread.getThreadItems();
        
        for(int i = 0; i < x_groupItems.length; i++)
        {
            ThreadItem x_groupItem = x_groupItems[i];
            
            if(x_groupItem.isActive())
            {
                if(x_groupItem instanceof Thread)
                {
                    x_result.addAll(ThreadHelper.getAllActiveActions((Thread) x_groupItem));
                }
            }
        }
        
        Collections.sort(x_result, new DueDateComparator());        
        return x_result;
    }
    
    public static List getAllActiveThreads(Thread p_thread)
    {
        List x_result = new ArrayList();        
        ThreadItem[] x_groupItems = p_thread.getThreadItems();
        
        for(int i = 0; i < x_groupItems.length; i++)
        {
            ThreadItem x_groupItem = x_groupItems[i];
            
            if(x_groupItem.isActive())
            {
                if(x_groupItem instanceof Thread)
                {
                    x_result.add(x_groupItem);
                    x_result.addAll(ThreadHelper.getAllActiveThreads((Thread) x_groupItem));
                }
            }
        }
        
        Collections.sort(x_result, new TextComparator()); 
        return x_result;
    }
    
    public static List getAllDueActions(Thread p_thread)
    {
        List x_result = new ArrayList();
		x_result.addAll(getDueActions(p_thread));
        ThreadItem[] x_groupItems = p_thread.getThreadItems();

        for(int i = 0; i < x_groupItems.length; i++)
        {
            ThreadItem x_groupItem = x_groupItems[i];

            if(x_groupItem.isActive())
            {
                if(x_groupItem instanceof Thread)
                {
                    x_result.addAll(ThreadHelper.getAllDueActions((Thread) x_groupItem));
                }
            }
        }

        Collections.sort(x_result, new DueDateComparator());
        return x_result;
    }

    public static List getAllDueReminders(Thread p_thread)
    {
        List x_result = new ArrayList();
		x_result.addAll(getDueReminders(p_thread));
        ThreadItem[] x_groupItems = p_thread.getThreadItems();

        for(int i = 0; i < x_groupItems.length; i++)
        {
            ThreadItem x_groupItem = x_groupItems[i];

            if(x_groupItem.isActive())
            {
                if(x_groupItem instanceof Thread)
                {
                    x_result.addAll(ThreadHelper.getAllDueReminders((Thread) x_groupItem));
                }
            }
        }

        Collections.sort(x_result, new DueDateComparator());
        return x_result;
    }

	public static List getActiveUpdates(Thread p_thread)
	{
		List x_updateItems = new ArrayList();
		for(int i = 0; i < p_thread.getThreadItemCount(); i++)
		{
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

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

	public static List getActiveActions(Thread p_thread)
	{
		List x_actionItems = new ArrayList();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++)
		{
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

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

	public static List getDueActions(Thread p_thread)
	{
		List x_actions = new ArrayList();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++)
		{
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive() && x_item.getDueDate() != null)
				{
					if(x_item.getDueDate().before(new Date()))
					{
						x_actions.add(x_item);
					}

				}
			}
		}

		return x_actions;
	}

	public static List getDueReminders(Thread p_thread)
	{
		List x_reminders = new ArrayList();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++)
		{
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive())
				{
					x_reminders.addAll(ItemHelper.getDueReminders(x_item));
				}
			}
		}

		return x_reminders;
	}
}
