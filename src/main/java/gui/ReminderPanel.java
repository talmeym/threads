package gui;

import data.Reminder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ReminderPanel extends JPanel {
    public ReminderPanel(Reminder p_reminder, final ActionListener p_listener) {
        super(new BorderLayout());

		final JButton o_closeButton = new JButton("Close");
        o_closeButton.addActionListener(p_listener);

		ComponentInfoChangeListener x_listener = new ComponentInfoChangeListener() {
			@Override
			public void componentInfoChanged(boolean saved) {
				o_closeButton.setText(saved ? "Close" : "Cancel");
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
        x_buttonPanel.add(o_closeButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }
}
