package gui;

import data.*;

public class ItemAndReminderWindow extends ComponentWindow {
	public ItemAndReminderWindow(Item p_item) {
		super(p_item);
		setContentPane(new ItemAndReminderPanel(p_item));
		setSize(GUIConstants.s_itemWindowSize);
	}

	public ItemAndReminderWindow(Reminder p_reminder) {
		super(p_reminder);
		setContentPane(new ItemAndReminderPanel(p_reminder));
		setSize(GUIConstants.s_itemWindowSize);
	}
}
