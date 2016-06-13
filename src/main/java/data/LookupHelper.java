package data;

import util.*;

import java.util.*;

public class LookupHelper {
    public static List<Item> getAllActiveUpdates(Thread p_thread) {
        List<Item> x_result = new ArrayList<Item>();
		x_result.addAll(getActiveUpdates(p_thread));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);
            
            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllActiveUpdates((Thread) x_groupItem));
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
            
            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllActiveActions((Thread) x_groupItem));
			}
        }
        
        Collections.sort(x_result, new DueDateComparator());        
        return x_result;
    }

    public static List<Item> getAllActions(Thread p_thread, Date p_referenceDate) {
        List<Item> x_result = new ArrayList<Item>();
		x_result.addAll(getActions(p_thread, p_referenceDate));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);

            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllActions((Thread) x_groupItem, p_referenceDate));
			}
        }

        Collections.sort(x_result, new ActiveAwareDueDateComparator());
        return x_result;
    }

    public static List<Thread> getAllActiveThreads(Thread p_thread) {
        List<Thread> x_result = new ArrayList<Thread>();

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);
            
            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				Thread x_thread = (Thread) x_groupItem;
				x_result.add(x_thread);
				x_result.addAll(getAllActiveThreads(x_thread));
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

            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllDueActions((Thread) x_groupItem));
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

            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllDueReminders((Thread) x_groupItem));
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

	public static List<Item> getActions(Thread p_thread, Date p_date) {
		List<Item> x_actionItems = new ArrayList<Item>();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.getDueDate() != null && DateUtil.isSameDay(x_item.getDueDate(), p_date)) {
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
				Date x_dueDate = x_item.getDueDate();

				if(x_item.isActive() && x_dueDate != null) {
					if(DateUtil.isAllDay(x_dueDate) ? x_dueDate.before(DateUtil.getFirstThingToday()) : x_dueDate.before(new Date())) {
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
					x_reminders.addAll(getDueReminders(x_item));
				}
			}
		}

		return x_reminders;
	}

	public static List<Reminder> getDueReminders(Item p_item) {
		List<Reminder> x_dueActiveReminders = new ArrayList<Reminder>();

		if(p_item.isActive() && p_item.getDueDate() != null) {
			for(int i = 0; i < p_item.getReminderCount(); i++) {
				Reminder x_reminder = p_item.getReminder(i);

				if(x_reminder.isActive() && x_reminder.getDueDate().before(new Date())) {
					x_dueActiveReminders.add(x_reminder);
				}
			}
		}

		return x_dueActiveReminders;
	}
}
