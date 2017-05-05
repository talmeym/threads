package data;

import org.junit.*;

import java.util.*;

import static java.util.Calendar.DAY_OF_MONTH;
import static org.junit.Assert.assertEquals;

public class LookupHelperTest {

	@Test
	public void testGetActiveActions() {
		Thread p_thread = new Thread("Test Thread");
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, null, 3));               // active updates
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), 3));         // active actions due today
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, get10DaysFromNow(), 3)); // active actions due 10 days from now
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, new Date(), 3));        // inactive actions due today

		assertEquals(6, LookupHelper.getActiveActions(p_thread, false).size());
		assertEquals(3, LookupHelper.getActiveActions(p_thread, true).size());
	}

	@Test
	public void testGetActionsForDay() {
		Thread p_thread = new Thread("Test Thread");
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, null, 3));               // active updates
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), 3));         // active actions due today
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, get10DaysFromNow(), 3)); // active actions due 10 days from now
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, new Date(), 3));        // inactive actions due today

		assertEquals(6, LookupHelper.getActionsForDay(p_thread, new Date()).size());
		assertEquals(3, LookupHelper.getActionsForDay(p_thread, get10DaysFromNow()).size());
	}

	@Test
	public void testGetUpdatesForDay() {
		Thread p_thread = new Thread("Test Thread");
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, null, new Date(), 3));               // update made today
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), new Date(), 3));         // actions made today
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, null, get10DaysFromNow(), 3));       // updates made 10 days from now
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), get10DaysFromNow(), 3)); // actions made 10 days from now

		assertEquals(3, LookupHelper.getUpdatesForDay(p_thread, new Date()).size());
		assertEquals(3, LookupHelper.getUpdatesForDay(p_thread, get10DaysFromNow()).size());
	}

	@Test
	public void testGetActions() {
		Thread p_thread = new Thread("Test Thread");
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, null, 3));               // inactive updates
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, new Date(), 3));         // inactive actions
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, get10DaysFromNow(), 3)); // inactive actions 10 days from now
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), 3));          // active actions

		assertEquals(9, LookupHelper.getActions(p_thread).size());
	}

	@Test
	public void testGetActiveDueActions() {
		Thread p_thread = new Thread("Test Thread");
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, null, 3));               // active updates
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, new Date(), 3));         // active due actions
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, get10DaysFromNow(), 3)); // active not due actions
		p_thread.addThreadItem((ThreadItem[]) buildItems(true, get10DaysAgo(), 3));     // active due actions
		p_thread.addThreadItem((ThreadItem[]) buildItems(false, new Date(), 3));        // inactive due actions

		assertEquals(6, LookupHelper.getActiveDueActions(p_thread).size());
	}

	@Test
	public void testGetActiveReminders_Item() {
		Item item = buildItem(true, new Date());

		item.addReminder(buildReminders(false, item, new Date(), 1));         // inactive, due
		item.addReminder(buildReminders(true, item, new Date(), 2));          // active, due
		item.addReminder(buildReminders(true, item, get10DaysFromNow(), 1));  // active, not due
		item.addReminder(buildReminders(false, item, get10DaysFromNow(), 1)); // inactive, not due

		assertEquals(3, LookupHelper.getActiveReminders(item, false).size()); // all active
		assertEquals(2, LookupHelper.getActiveReminders(item, true).size());  // all active and due
	}

	@Test
	public void testGetRemindersForDay() {
		Item item = buildItem(true, new Date());

		item.addReminder(buildReminders(false, item, new Date(), 3));
		item.addReminder(buildReminders(false, item, get10DaysFromNow(), 2));

		assertEquals(3, LookupHelper.getRemindersForDay(item, new Date()).size());
		assertEquals(2, LookupHelper.getRemindersForDay(item, get10DaysFromNow()).size());
	}

	private Reminder[] buildReminders(boolean p_active, Item item, Date p_dueDate, int p_numberReminders) {
		Reminder[] x_reminders = new Reminder[p_numberReminders];

		for(int i = 0; i < p_numberReminders; i++) {
			x_reminders[i] = new Reminder("Test Reminder", item);
			x_reminders[i].setActive(p_active);
			x_reminders[i].setDueDate(p_dueDate);
		}

		return x_reminders;
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
		return new Item(UUID.randomUUID(), null, p_modifiedDate, p_active, "Test Item", p_dueDate, null, null);
	}
}