package gui;

import data.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class ReminderWindow extends ComponentWindow<Reminder> implements ActionListener {
	public ReminderWindow(Reminder p_reminder, boolean p_new) {
		super(p_reminder);
		setContentPane(new ReminderPanel(p_reminder, p_new, this));
		setSize(GUIConstants.s_reminderWindowSize);
		renameWindow(p_reminder);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		super.actionPerformed(actionEvent);

		if(!((JButton)actionEvent.getSource()).getText().equals("Parent")) {
			setVisible(false);
		}
	}
}
