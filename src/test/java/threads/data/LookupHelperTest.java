package threads.data;

import org.junit.Test;

import java.util.*;

import static java.util.Calendar.DAY_OF_MONTH;
import static org.junit.Assert.assertEquals;
import static threads.data.LookupHelper.*;
import static threads.data.View.*;

public class LookupHelperTest {

	@Test
	public void testGetActiveActions() {
		Thread p_thread = new Thread("Test Thread");
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, null, 3));     // active updates
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), 3));         // active actions due
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, get3DaysFromNow(), 3));  // active actions due 3 days from now
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, get10DaysFromNow(), 3)); // active actions due 10 days from now
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, new Date(), 3));        // inactive actions due today

		assertEquals(9, getActiveActions(p_thread, ALL).size());
		assertEquals(6, getActiveActions(p_thread, SEVENDAYS).size());
		assertEquals(3, getActiveActions(p_thread, DUE).size());
	}

	@Test
	public void testGetActionsForDay() {
		Thread p_thread = new Thread("Test Thread");
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, null, 3));               // active updates
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), 3));         // active actions due today
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, get10DaysFromNow(), 3)); // active actions due 10 days from now
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, new Date(), 3));        // inactive actions due today

		assertEquals(6, getActionsForDay(p_thread, new Date()).size());
		assertEquals(3, getActionsForDay(p_thread, get10DaysFromNow()).size());
	}

	@Test
	public void testGetUpdatesForDay() {
		Thread p_thread = new Thread("Test Thread");
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, null, new Date(), 3));               // update made today
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), new Date(), 3));         // actions made today
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, null, get10DaysFromNow(), 3));       // updates made 10 days from now
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), get10DaysFromNow(), 3)); // actions made 10 days from now

		assertEquals(3, getUpdatesForDay(p_thread, new Date()).size());
		assertEquals(3, getUpdatesForDay(p_thread, get10DaysFromNow()).size());
	}

	@Test
	public void testGetActions() {
		Thread p_thread = new Thread("Test Thread");
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, null, 3));               // inactive updates
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, new Date(), 3));         // inactive actions
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, get10DaysFromNow(), 3)); // inactive actions 10 days from now
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), 3));          // active actions

		assertEquals(9, getActions(p_thread).size());
	}

	@Test
	public void testGetActiveReminders_Item() {
		Item item = buildItem(true, new Date());

		item.addReminder(buildReminders(false, item, new Date(), 1));         // inactive, due
		item.addReminder(buildReminders(false, item, get10DaysFromNow(), 1)); // inactive, not due
		item.addReminder(buildReminders(true, item, new Date(), 2));          // active, due
		item.addReminder(buildReminders(true, item, get3DaysFromNow(), 1));  // active, due in 3 days
		item.addReminder(buildReminders(true, item, get10DaysFromNow(), 1));  // active, due in 10 days

		assertEquals(2, getActiveReminders(item, DUE).size());  // all active and due
		assertEquals(3, getActiveReminders(item, SEVENDAYS).size());  // all active and due within 7 days
		assertEquals(4, getActiveReminders(item, ALL).size()); // all active
	}

	@Test
	public void testGetRemindersForDay_item() {
		Item item = buildItem(true, new Date());

		item.addReminder(buildReminders(false, item, new Date(), 3));
		item.addReminder(buildReminders(false, item, get10DaysFromNow(), 2));

		assertEquals(3, getRemindersForDay(item, new Date()).size());
		assertEquals(2, getRemindersForDay(item, get10DaysFromNow()).size());
	}

	@Test
	public void testGetActiveUpdates() {
		Thread p_thread = new Thread("Test Thread");
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, null, 3));               // active updates
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, null, 3));               // inactive updates
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), 3));         // active actions
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, new Date(), 3));        // inactive actions

		assertEquals(3, getActiveUpdates(p_thread).size());
	}

	@Test
	public void testGetRemindersForDay_thread() {
		Thread p_thread = new Thread("Test Thread");

		Item x_item = buildItemWithReminders(true, true, new Date(), 5);
		Item x_item2 = buildItemWithReminders(false, true, new Date(), 5);
		Item x_item3 = buildItemWithReminders(true, true, new Date(), 5);
		Item x_item4 = buildItemWithReminders(true, true, get10DaysFromNow(), 5);
		p_thread.addThreadItem(x_item, x_item2, x_item3, x_item4);

		assertEquals(10, getRemindersForDay(p_thread, new Date()).size());
	}

	@Test
	public void testGetHasDueDates_item() {
		Item p_item = buildItemWithReminders(true, true, new Date(), 5);
		p_item.addReminder(buildReminders(false, p_item, new Date(), 5));
		assertEquals(6, getHasDueDates(p_item, true).size());
		assertEquals(11, getHasDueDates(p_item, false).size());
	}

	@Test
	public void testGetHasDueDates_thread() {
		Thread p_thread = new Thread("Test Thread");

		Item x_item1 = buildItemWithReminders(false, false, new Date(), 5);
		Item x_item2 = buildItemWithReminders(false, true, new Date(), 5);
		Item x_item3 = buildItemWithReminders(true, false, new Date(), 5);
		Item x_item4 = buildItemWithReminders(true, true, new Date(), 5);
		p_thread.addThreadItem(x_item1, x_item2, x_item3, x_item4);

		assertEquals(7, getHasDueDates(p_thread, true).size());
		assertEquals(24, getHasDueDates(p_thread, false).size());
	}

	@Test
	public void testGetHasDueDates_components() {
		Reminder x_reminder = buildReminders(true, new Item("Test Item", new Date()), new Date(), 1)[0];
		Item x_item1 = buildItem(true, null);
		Item x_item2 = buildItem(true, null);
		Item x_item3 = buildItemWithReminders(true, true, new Date(), 5);
		Item x_item4 = buildItemWithReminders(false, true, new Date(), 5);
		Thread x_thread = new Thread("Test Thread");
		Item x_item5 = buildItemWithReminders(true, true, new Date(), 5);
		x_thread.addThreadItem(x_item5);
		List<Component> x_comps = Arrays.asList(x_reminder, x_item1, x_item2, x_item3, x_item4, x_thread);

		assertEquals(8, getHasDueDates(x_comps).size());
	}

	private Item buildItemWithReminders(boolean p_active, boolean p_reminderActive, Date p_reminderDate, int p_reminderCount) {
		Item x_item = new Item("Test Item", new Date());
		x_item.setActive(p_active);
		x_item.addReminder(buildReminders(p_reminderActive, x_item, p_reminderDate, p_reminderCount));
		return x_item;
	}

	private Reminder[] buildReminders(boolean p_active, Item item, Date p_dueDate, int p_numberReminders) {
		Reminder[] x_reminders = new Reminder[p_numberReminders];

		for(int i = 0; i < p_numberReminders; i++) {
			x_reminders[i] = new Reminder("Test Reminder", item.getDueDate());
			x_reminders[i].setActive(p_active);
			x_reminders[i].setDueDate(p_dueDate);
		}

		return x_reminders;
	}

	private Date get3DaysFromNow() {
		return getDate(3);
	}

	private Date get10DaysFromNow() {
		return getDate(10);
	}

	private Date get10DaysAgo() {
		return getDate(-10);
	}

	private Date getDate(int p_daysToAdd) {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.add(DAY_OF_MONTH, p_daysToAdd);
		return x_calendar.getTime();
	}

	private Item[] buildItems(boolean p_active, Date p_dueDate, int p_numberItems) {
		return buildItems(p_active, p_dueDate, new Date(), p_numberItems);
	}

	private Item[] buildItems(boolean p_active, Date p_dueDate, Date p_modifiedDate, int p_numberItems) {
		Item[] x_items = new Item[p_numberItems];

		for(int i = 0; i < p_numberItems; i++) {
			x_items[i] = buildItem(p_active, p_dueDate, p_modifiedDate);
		}

		return x_items;
	}

	private Item buildItem(boolean p_active, Date p_dueDate) {
		return buildItem(p_active, p_dueDate, new Date());
	}

	private Item buildItem(boolean p_active, Date p_dueDate, Date p_modifiedDate) {
		return new Item(UUID.randomUUID(), null, p_modifiedDate, p_active, "Test Item", p_dueDate, null, null, null);
	}
}