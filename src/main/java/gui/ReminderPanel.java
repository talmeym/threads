package gui;

import data.Reminder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ReminderPanel extends JPanel implements ComponentInfoChangeListener, ActionListener {

    private final Reminder o_reminder;
	private final JButton o_closeButton = new JButton("Close");
    
    public ReminderPanel(Reminder p_reminder, boolean p_new) {
        super(new BorderLayout());
        o_reminder = p_reminder;

		ComponentInfoPanel o_compInfoPanel = new ComponentInfoPanel(p_reminder, p_new, this);
		o_compInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel x_remindDatePanel = new JPanel(new BorderLayout());
		x_remindDatePanel.add(new RemindDateSuggestionPanel(o_reminder, this), BorderLayout.CENTER);
		x_remindDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, BorderLayout.NORTH);
        x_panel.add(x_remindDatePanel, BorderLayout.CENTER);

        o_closeButton.addActionListener(this);
        
        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(o_closeButton);

        add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
		WindowManager.getInstance().closeComponent(o_reminder);
    }

	@Override
	public void componentInfoChanged(boolean saved) {
		o_closeButton.setText(saved ? "Close" : "Cancel");
	}
}
