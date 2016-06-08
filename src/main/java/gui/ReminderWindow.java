package gui;

import data.Reminder;

import java.awt.event.*;

public class ReminderWindow extends ComponentWindow<Reminder> implements ActionListener {
	public ReminderWindow(Reminder p_reminder) {
		super(p_reminder);
		setContentPane(new ReminderPanel(p_reminder, this));
		setSize(GUIConstants.s_reminderWindowSize);
		renameWindow(p_reminder);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		setVisible(false);
	}
}
