package data;

import util.*;

import java.util.*;
import java.util.stream.Collectors;

public class LookupHelper {
    public static List<Item> getAllActiveUpdates(Thread p_thread) {
        List<Item> x_result = new ArrayList<>();
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
        List<Item> x_result = new ArrayList<>();
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

    public static List<Item> getAllActions(Thread p_thread) {
        List<Item> x_result = new ArrayList<>();
		x_result.addAll(getActions(p_thread));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);

            if(x_groupItem instanceof Thread) {
				x_result.addAll(getAllActions((Thread) x_groupItem));
			}
        }

        Collections.sort(x_result, new AllDayAwareDueDateComparator());
        return x_result;
    }

    public static List<Item> getAllActiveActions(Thread p_thread) {
        List<Item> x_result = new ArrayList<>();
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

    public static List<Component> getAllComponents(Thread p_thread, Date p_referenceDate, boolean p_includeActions, boolean p_includeUpdates, boolean p_includeReminders) {
        List<Component> x_result = new ArrayList<>();

		if(p_includeActions) {
			x_result.addAll(getActionsForDay(p_thread, p_referenceDate));
		}

		if(p_includeUpdates) {
			x_result.addAll(getUpdatesForDay(p_thread, p_referenceDate));
		}

		if(p_includeReminders) {
			x_result.addAll(getRemindersForDay(p_thread, p_referenceDate));
		}

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);

            if(x_groupItem instanceof Thread) {
				x_result.addAll(getAllComponents((Thread) x_groupItem, p_referenceDate, p_includeActions, p_includeUpdates, p_includeReminders));
			}
        }

        Collections.sort(x_result, new ActiveAwareUpdateOrHasDueDateComparator());
        return x_result;
    }

    public static List<Thread> getAllActiveThreads(Thread p_thread) {
        List<Thread> x_result = new ArrayList<>();

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);
            
            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				Thread x_thread = (Thread) x_groupItem;
				x_result.add(x_thread);
				x_result.addAll(getAllActiveThreads(x_thread));
            }
        }
        
        Collections.sort(x_result, new TextComparator<>());
        return x_result;
    }
    
    public static List<Item> getAllActiveDueActions(Thread p_thread) {
        List<Item> x_result = new ArrayList<>();
		x_result.addAll(getActiveDueActions(p_thread));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);

            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllActiveDueActions((Thread) x_groupItem));
			}
        }

        Collections.sort(x_result, new AllDayAwareDueDateComparator());
        return x_result;
    }

    public static List<Reminder> getAllActiveReminders(Thread p_thread, boolean p_onlyIfDue) {
        List<Reminder> x_result = new ArrayList<>();
		x_result.addAll(getActiveReminders(p_thread, p_onlyIfDue));

        for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
            ThreadItem x_groupItem = p_thread.getThreadItem(i);

            if(x_groupItem.isActive() && x_groupItem instanceof Thread) {
				x_result.addAll(getAllActiveReminders((Thread) x_groupItem, p_onlyIfDue));
			}
        }

        Collections.sort(x_result, new DueDateComparator<>());
        return x_result;
    }

	public static List<Item> getActiveUpdates(Thread p_thread) {
		List<Item> x_updateItems = new ArrayList<>();
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

	public static List<Item> getActiveActions(Thread p_thread, boolean p_onlyNext7DaysOrBefore) {
		return p_thread.getThreadItems().stream()
				.filter(t -> t instanceof Item && t.isActive())
				.map(t -> (Item) t)
				.filter(i -> i.getDueDate() != null && (!p_onlyNext7DaysOrBefore || DateUtil.isBefore7DaysFromNow(i.getDueDate())))
				.collect(Collectors.toList());
	}

	static List<Item> getActionsForDay(Thread p_thread, Date p_date) {
		return p_thread.getThreadItems().stream()
				.filter(t -> t instanceof Item)
				.map(t -> (Item) t)
				.filter(i -> i.getDueDate() != null && DateUtil.isSameDay(i.getDueDate(), p_date))
				.collect(Collectors.toList());
	}

	static List<Item> getActions(Thread p_thread) {
		return p_thread.getThreadItems().stream()
				.filter(t -> t instanceof Item)
				.map(t -> (Item) t)
				.filter(i -> i.getDueDate() != null)
				.collect(Collectors.toList());
	}

	static List<Item> getUpdatesForDay(Thread p_thread, Date p_date) {
		return p_thread.getThreadItems().stream()
				.filter(t -> t instanceof Item)
				.map(t -> (Item) t)
				.filter(i -> i.getDueDate() == null && DateUtil.isSameDay(i.getModifiedDate(), p_date))
				.collect(Collectors.toList());
	}

	static List<Item> getActiveDueActions(Thread p_thread) {
		return p_thread.getThreadItems().stream()
				.filter(t -> t instanceof Item)
				.map(t -> (Item) t)
				.filter(i -> i.isActive() && i.isDue())
				.collect(Collectors.toList());
	}

	public static List<Reminder> getActiveReminders(Thread p_thread, boolean p_onlyIfDue) {
		List<Reminder> x_reminders = new ArrayList<>();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item) {
				Item x_item = (Item) x_groupItem;

				if(x_item.isActive() && x_item.getDueDate() != null) {
					x_reminders.addAll(getActiveReminders(x_item, p_onlyIfDue));
				}
			}
		}

		return x_reminders;
	}

	static List<Reminder> getActiveReminders(Item p_item, boolean p_onlyIfDue) {
		return p_item.getReminders().stream()
				.filter(r -> r.isActive() && (!p_onlyIfDue || r.isDue()))
				.collect(Collectors.toList());
	}

	public static List<Reminder> getRemindersForDay(Thread p_thread, Date p_date) {
		List<Reminder> x_reminders = new ArrayList<>();

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_groupItem = p_thread.getThreadItem(i);

			if(x_groupItem instanceof Item && x_groupItem.isActive()) {
				x_reminders.addAll(getRemindersForDay((Item) x_groupItem, p_date));
			}
		}

		return x_reminders;
	}

	static List<Reminder> getRemindersForDay(Item p_item, Date p_date) {
		return p_item.getReminders().stream()
				.filter(r -> DateUtil.isSameDay(r.getDueDate(), p_date))
				.collect(Collectors.toList());
	}

	public static int countActiveSyncableComponents(List<Item> p_items) {
		int x_count = p_items.size();

		for(Item x_item: p_items) {
			x_count += x_item.getReminderCount();
		}

		return x_count;
	}
}
