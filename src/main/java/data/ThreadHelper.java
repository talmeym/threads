package data;

import util.*;

import java.util.*;

public class ThreadHelper {
    public static List<Item> getAllActiveUpdates(Thread p_thread) {
        List<Item> x_result = new ArrayList<Item>();
		x_result.addAll(getActiveUpdates(p_thread));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);
            
            if(x_groupItem.isActive()) {
                if(x_groupItem instanceof Thread) {
                    x_result.addAll(ThreadHelper.getAllActiveUpdates((Thread) x_groupItem));
                }
            }
        }

        Collections.sort(x_result, new CreationDateComparator());
        return x_result;
    }
    
    public static List<Item> getAllActiveActions(Thread p_thread) {
        List<Item> x_result = new ArrayList<Item>();
		x_result.addAll(getActiveActions(p_thread));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);
            
            if(x_groupItem.isActive()) {
                if(x_groupItem instanceof Thread) {
                    x_result.addAll(ThreadHelper.getAllActiveActions((Thread) x_groupItem));
                }
            }
        }
        
        Collections.sort(x_result, new DueDateComparator());        
        return x_result;
    }
    
    public static List<Thread> getAllActiveThreads(Thread p_thread) {
        List<Thread> x_result = new ArrayList<Thread>();

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);
            
            if(x_groupItem.isActive()) {
                if(x_groupItem instanceof Thread) {
					Thread x_thread = (Thread) x_groupItem;
                    x_result.add(x_thread);
                    x_result.addAll(ThreadHelper.getAllActiveThreads(x_thread));
                }
            }
        }
        
        Collections.sort(x_result, new TextComparator<Thread>());
        return x_result;
    }
    
    public static List<Item> getAllDueActions(Thread p_thread) {
        List<Item> x_result = new ArrayList<Item>();
		x_result.addAll(getDueActions(p_thread));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);

            if(x_groupItem.isActive()) {
                if(x_groupItem instanceof Thread) {
                    x_result.addAll(ThreadHelper.getAllDueActions((Thread) x_groupItem));
                }
            }
        }

        Collections.sort(x_result, new DueDateComparator());
        return x_result;
    }

    public static List<Reminder> getAllDueReminders(Thread p_thread) {
        List<Reminder> x_result = new ArrayList<Reminder>();
		x_result.addAll(getDueReminders(p_thread));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);

            if(x_groupItem.isActive()) {
                if(x_groupItem instanceof Thread) {
                    x_result.addAll(ThreadHelper.getAllDueReminders((Thread) x_groupItem));
                }
            }
        }

        Collections.sort(x_result, new DueDateComparator());
        return x_result;
    }

	public static List<Item> getActiveUpdates(Thread p_thread) {
		List<Item> x_updateItems = new ArrayList<Item>();
		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive() && x_item.getDueDate() == null) {
					x_updateItems.add(x_item);
				}
			}
		}

		return x_updateItems;
	}

	public static List<Item> getActiveActions(Thread p_thread) {
		List<Item> x_actionItems = new ArrayList<Item>();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive() && x_item.getDueDate() != null) {
					x_actionItems.add(x_item);
				}
			}
		}

		return x_actionItems;
	}

	public static List<Item> getDueActions(Thread p_thread) {
		List<Item> x_actions = new ArrayList<Item>();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive() && x_item.getDueDate() != null) {
					if(x_item.getDueDate().before(new Date())) {
						x_actions.add(x_item);
					}
				}
			}
		}

		return x_actions;
	}

	public static List<Reminder> getDueReminders(Thread p_thread) {
		List<Reminder> x_reminders = new ArrayList<Reminder>();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive()) {
					x_reminders.addAll(ItemHelper.getDueReminders(x_item));
				}
			}
		}

		return x_reminders;
	}
}
