package threads.data;

import threads.util.ActiveAwareUpdateOrHasDueDateComparator;
import threads.util.AllDayAwareDueDateComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static threads.data.View.ALL;
import static threads.util.DateUtil.isSameDay;

public class LookupHelper {
	public static List<Item> getAllActiveUpdates(Thread p_thread) {
		List<Item> x_result = new ArrayList<>();
		x_result.addAll(getActiveUpdates(p_thread));
		p_thread.getThreadItems().stream()
				.filter(ti -> ti.isActive() && ti instanceof Thread)
				.forEach(ti -> x_result.addAll(getAllActiveUpdates((Thread) ti)));
		Collections.sort(x_result, (obj1, obj2) -> obj2.getModifiedDate().compareTo(obj1.getModifiedDate()));
		return x_result;
	}

	public static List<Item> getAllActiveActions(Thread p_thread, View p_view) {
		List<Item> x_result = new ArrayList<>();
		x_result.addAll(getActiveActions(p_thread, p_view));
		p_thread.getThreadItems().stream()
				.filter(ti -> ti.isActive() && ti instanceof Thread)
				.forEach(ti -> x_result.addAll(getAllActiveActions((Thread) ti, p_view)));
		Collections.sort(x_result, new AllDayAwareDueDateComparator());
		return x_result;
	}

	public static List<Component> getAllComponents(Thread p_thread, Date p_referenceDate, boolean p_includeActions, boolean p_includeUpdates, boolean p_includeReminders) {
		List<Component> x_result = new ArrayList<>();

		if (p_includeActions) {
			x_result.addAll(getActionsForDay(p_thread, p_referenceDate));
		}

		if (p_includeUpdates) {
			x_result.addAll(getUpdatesForDay(p_thread, p_referenceDate));
		}

		if (p_includeReminders) {
			x_result.addAll(getRemindersForDay(p_thread, p_referenceDate));
		}

		p_thread.getThreadItems().stream()
				.filter(ti -> ti instanceof Thread && ti.isActive())
				.forEach(t -> x_result.addAll(getAllComponents((Thread) t, p_referenceDate, p_includeActions, p_includeUpdates, p_includeReminders)));
		Collections.sort(x_result, new ActiveAwareUpdateOrHasDueDateComparator());
		return x_result;
	}

	public static List<Thread> getAllActiveThreads(Thread p_thread) {
		List<Thread> x_result = new ArrayList<>();
		p_thread.getThreadItems().stream()
				.filter(ti -> ti.isActive() && ti instanceof Thread)
				.map(ti -> (Thread) ti)
				.forEach(t -> {
					x_result.add(t);
					x_result.addAll(getAllActiveThreads(t));
				});
		Collections.sort(x_result, (o1, o2) -> o1.getText().compareTo(o2.getText()));
		return x_result;
	}

	public static List<Reminder> getAllActiveReminders(Thread p_thread, View p_view) {
		List<Reminder> x_result = new ArrayList<>();
		x_result.addAll(getActiveReminders(p_thread, p_view));
		p_thread.getThreadItems().stream()
				.filter(ti -> ti.isActive() && ti instanceof Thread)
				.forEach(ti -> x_result.addAll(getAllActiveReminders((Thread) ti, p_view)));
		Collections.sort(x_result, (obj1, obj2) -> obj1.getDueDate().compareTo(obj2.getDueDate()));
		return x_result;
	}

	public static List<Item> getActiveUpdates(Thread p_thread) {
		return p_thread.getThreadItems().stream()
				.filter(ti -> ti instanceof Item)
				.map(ti -> (Item) ti)
				.filter(i -> i.isActive() && i.getDueDate() == null)
				.collect(Collectors.toList());
	}

	public static List<Item> getActiveActions(Thread p_thread, View p_view) {
		return p_thread.getThreadItems().stream()
				.filter(ti -> ti instanceof Item && ti.isActive())
				.map(ti -> (Item) ti)
				.filter(i -> i.getDueDate() != null && p_view.accept(i))
				.collect(Collectors.toList());
	}

	static List<Item> getActionsForDay(Thread p_thread, Date p_date) {
		return p_thread.getThreadItems().stream()
				.filter(ti -> ti instanceof Item)
				.map(ti -> (Item) ti)
				.filter(i -> i.getDueDate() != null && isSameDay(i.getDueDate(), p_date))
				.collect(Collectors.toList());
	}

	static List<Item> getActions(Thread p_thread) {
		return p_thread.getThreadItems().stream()
				.filter(ti -> ti instanceof Item)
				.map(ti -> (Item) ti)
				.filter(i -> i.getDueDate() != null)
				.collect(Collectors.toList());
	}

	static List<Item> getUpdatesForDay(Thread p_thread, Date p_date) {
		return p_thread.getThreadItems().stream()
				.filter(ti -> ti instanceof Item)
				.map(ti -> (Item) ti)
				.filter(i -> i.getDueDate() == null && isSameDay(i.getModifiedDate(), p_date))
				.collect(Collectors.toList());
	}

	private static List<Reminder> getActiveReminders(Thread p_thread, View p_view) {
		List<Reminder> x_reminders = new ArrayList<>();
		p_thread.getThreadItems().stream()
				.filter(ti -> ti instanceof Item)
				.map(ti -> (Item) ti)
				.filter(i -> i.isActive() && i.getDueDate() != null)
				.forEach(i -> x_reminders.addAll(getActiveReminders(i, p_view)));
		return x_reminders;
	}

	static List<Reminder> getActiveReminders(Item p_item, View p_view) {
		return p_item.getReminders().stream()
				.filter(r -> r.isActive() && p_view.accept(r))
				.collect(Collectors.toList());
	}

	public static List<HasDueDate> getHasDueDates(List<Component> p_components) {
		List<HasDueDate> x_hasDueDates = new ArrayList<>();
		p_components.stream()
				.filter(c -> c instanceof HasDueDate)
				.map(c -> (HasDueDate) c)
				.filter(h -> h.getDueDate() != null)
				.forEach(h -> {
					x_hasDueDates.add(h);

					if (h instanceof Item && h.isActive()) {
						x_hasDueDates.addAll(((Item)h).getReminders());
					}
				});
		return x_hasDueDates;
	}

	public static List<HasDueDate> getHasDueDates(Thread p_thread, boolean p_onlyActive) {
		List<HasDueDate> x_hasDueDates = new ArrayList<>();
		p_thread.getThreadItems().stream()
				.filter(ti -> (!p_onlyActive || ti.isActive()))
				.forEach(ti -> {
					if(ti instanceof Item) {
						if(((Item)ti).getDueDate() != null) {
							x_hasDueDates.addAll(getHasDueDates((Item)ti, p_onlyActive));
						}
					} else {
						x_hasDueDates.addAll(getHasDueDates((Thread)ti, p_onlyActive));
					}
				});
		return x_hasDueDates;
	}

	public static List<HasDueDate> getHasDueDates(Item p_item, boolean p_onlyActive) {
		List<HasDueDate> x_hasDueDates = new ArrayList<>();

		if (!p_onlyActive || p_item.isActive()) {
			x_hasDueDates.add(p_item);
		}

		x_hasDueDates.addAll(p_onlyActive ? getActiveReminders(p_item, ALL) : p_item.getReminders());
		return x_hasDueDates;
	}

	static List<Reminder> getRemindersForDay(Thread p_thread, Date p_date) {
		List<Reminder> x_reminders = new ArrayList<>();
		p_thread.getThreadItems().stream()
				.filter(ti -> ti instanceof Item)
				.map(ti -> (Item) ti)
				.filter(i -> i.isActive() && i.getDueDate() != null)
				.forEach(i -> x_reminders.addAll(getRemindersForDay(i, p_date)));
		return x_reminders;
	}

	static List<Reminder> getRemindersForDay(Item p_item, Date p_date) {
		return p_item.getReminders().stream()
				.filter(r -> isSameDay(r.getDueDate(), p_date))
				.collect(Collectors.toList());
	}
}
