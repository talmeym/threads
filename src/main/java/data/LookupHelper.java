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

        Collections.sort(x_result, new ModifiedDateComparator());
        return x_result;
    }

    public static List<Item> getAllActiveActions(Thread p_thread, boolean p_onlyNext7Days) {
        List<Item> x_result = new ArrayList<Item>();
		x_result.addAll(getActiveActions(p_thread, p_onlyNext7Days));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);
            
            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllActiveActions((Thread) x_groupItem, p_onlyNext7Days));
			}
        }
        
        Collections.sort(x_result, new AllDayAwareDueDateComparator());
        return x_result;
    }

    public static List<Item> getAllActiveActions(Thread p_thread) {
        List<Item> x_result = new ArrayList<Item>();
		x_result.addAll(getActiveActions(p_thread, false));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);

            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllActiveActions((Thread) x_groupItem));
			}
        }

        Collections.sort(x_result, new AllDayAwareDueDateComparator());
        return x_result;
    }

    public static List<Component> getAllItems(Thread p_thread, Date p_referenceDate, boolean p_includeActions, boolean p_includeUpdates, boolean p_includeReminders) {
        List<Component> x_result = new ArrayList<Component>();

		if(p_includeActions) {
			x_result.addAll(getActions(p_thread, p_referenceDate));
		}

		if(p_includeUpdates) {
			x_result.addAll(getUpdates(p_thread, p_referenceDate));
		}

		if(p_includeReminders) {
			x_result.addAll(getReminders(p_thread, p_referenceDate));
		}

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);

            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllItems((Thread) x_groupItem, p_referenceDate, p_includeActions, p_includeUpdates, p_includeReminders));
			}
        }

        Collections.sort(x_result, new ActiveAwareUpdateOrHasDueDateComparator());
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

        Collections.sort(x_result, new AllDayAwareDueDateComparator());
        return x_result;
    }

    public static List<Reminder> getAllActiveReminders(Thread p_thread, boolean p_onlyIfDue) {
        List<Reminder> x_result = new ArrayList<Reminder>();
		x_result.addAll(getActiveReminders(p_thread, p_onlyIfDue));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);

            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllActiveReminders((Thread) x_groupItem, p_onlyIfDue));
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

	public static List<Item> getActiveActions(Thread p_thread, boolean p_onlyNext7Days) {
		List<Item> x_actionItems = new ArrayList<Item>();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;
				Date x_dueDate = x_item.getDueDate();

				if(x_item.isActive() && x_dueDate != null) {
					if((!p_onlyNext7Days) || (DateUtil.isbefore7DaysFromNow(x_dueDate))) {
						x_actionItems.add(x_item);
					}
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

	public static List<Item> getUpdates(Thread p_thread, Date p_date) {
		List<Item> x_updateItems = new ArrayList<Item>();
		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.getDueDate() == null && DateUtil.isSameDay(x_item.getModifiedDate(), p_date)) {
					x_updateItems.add(x_item);
				}
			}
		}

		return x_updateItems;
	}

	public static List<Item> getDueActions(Thread p_thread) {
		List<Item> x_actions = new ArrayList<Item>();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive() && x_item.isDue()) {
					x_actions.add(x_item);
				}
			}
		}

		return x_actions;
	}

	public static List<Reminder> getActiveReminders(Thread p_thread, boolean p_onlyIfDue) {
		List<Reminder> x_reminders = new ArrayList<Reminder>();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive()) {
					x_reminders.addAll(getActiveReminders(x_item, p_onlyIfDue));
				}
			}
		}

		return x_reminders;
	}

	public static List<Reminder> getActiveReminders(Item p_item, boolean p_onlyIfDue) {
		List<Reminder> x_dueActiveReminders = new ArrayList<Reminder>();

		if(p_item.isActive() && p_item.getDueDate() != null) {
			for(int i = 0; i < p_item.getReminderCount(); i++) {
				Reminder x_reminder = p_item.getReminder(i);

				if(x_reminder.isActive()) {
					if(!p_onlyIfDue || x_reminder.isDue())
					x_dueActiveReminders.add(x_reminder);
				}
			}
		}

		return x_dueActiveReminders;
	}

	public static List<Reminder> getReminders(Thread p_thread, Date p_date) {
		List<Reminder> x_reminders = new ArrayList<Reminder>();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				x_reminders.addAll(getReminders((Item) x_groupItem, p_date));
			}
		}

		return x_reminders;
	}

	public static List<Reminder> getReminders(Item p_item, Date p_date) {
		List<Reminder> x_reminders = new ArrayList<Reminder>();

		if(p_item.isActive() && p_item.getDueDate() != null) {
			for(int i = 0; i < p_item.getReminderCount(); i++) {
				Reminder x_reminder = p_item.getReminder(i);

				if(DateUtil.isSameDay(x_reminder.getDueDate(), p_date)) {
					x_reminders.add(x_reminder);
				}
			}
		}

		return x_reminders;
	}

	public static int countActiveSyncableComponents(List<Item> p_items) {
		int x_count = p_items.size();

		for(Item x_item: p_items) {
			x_count += x_item.getReminderCount();
		}

		return x_count;
	}
}
