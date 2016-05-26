package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.util.*;

public class ReminderWindow extends JFrame {
	public ReminderWindow(Reminder p_reminder, boolean p_new, JFrame parent) {
		setContentPane(new ReminderPanel(p_reminder, p_new));
		setSize(GUIConstants.s_reminderWindowSize);
		renameWindow(p_reminder);
		GUIUtil.centreToWindow(this, parent);
		ImageUtil.addIconToWindow(this);
		setVisible(true);
	}

	void renameWindow(Component p_component) {
		StringBuilder x_title = new StringBuilder("Threads: ");
		List<String> x_parentNames = new ArrayList<String>();

		while(p_component.getParentComponent() != null) {
			x_parentNames.add(p_component.getParentComponent().getText());
			p_component = p_component.getParentComponent();
		}

		for(int i = x_parentNames.size() - 1; i > -1; i--) {
			x_title.append(x_parentNames.get(i)).append(" > ");
		}

		x_title.append(p_component.getText());
		setTitle(x_title.toString());
	}

	public void close() {
		setVisible(false);
	}
}
