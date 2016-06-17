package gui;

import data.*;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ReminderPanel extends JPanel {
    public ReminderPanel(final Reminder p_reminder) {
        super(new BorderLayout());

		final JButton x_closeButton = new JButton("Close");
        x_closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				WindowManager.getInstance().closeComponent(p_reminder);
			}
		});

		final JButton x_removeButton = new JButton("Remove");
		final JPanel x_this = this;
        x_removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				Item x_item = p_reminder.getItem();
				if (JOptionPane.showConfirmDialog(x_this, "Remove '" + p_reminder.getText() + "' from '" + x_item.getText() + "' ?", "Remove Reminder ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
					x_item.removeReminder(p_reminder);
					WindowManager.getInstance().closeComponent(p_reminder);
					WindowManager.getInstance().openComponent(x_item);
				}
			}
		});

		ComponentInfoChangeListener x_listener = new ComponentInfoChangeListener() {
			@Override
			public void componentInfoChanged(boolean saved) {
				x_closeButton.setText(saved ? "Close" : "Cancel");
			}
		};

		ComponentInfoPanel o_compInfoPanel = new ComponentInfoPanel(p_reminder, x_listener);
		o_compInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel x_remindDatePanel = new JPanel(new BorderLayout());
		x_remindDatePanel.add(new RemindDateSuggestionPanel(p_reminder, x_listener), BorderLayout.CENTER);
		x_remindDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, BorderLayout.NORTH);
        x_panel.add(x_remindDatePanel, BorderLayout.CENTER);


        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(x_closeButton);
        x_buttonPanel.add(x_removeButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }
}
