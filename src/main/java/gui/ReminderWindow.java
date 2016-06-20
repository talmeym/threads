package gui;

import data.Reminder;

import java.awt.event.*;

public class ReminderWindow extends ComponentWindow<Reminder> {
	public ReminderWindow(Reminder p_reminder) {
		super(p_reminder);
		setContentPane(new ReminderPanel(p_reminder));
		setSize(GUIConstants.s_reminderWindowSize);
	}
}
