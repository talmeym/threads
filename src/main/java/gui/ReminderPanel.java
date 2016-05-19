package gui;

import data.Reminder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ReminderPanel extends JPanel implements ChangeListener, ActionListener
{

    private final Reminder o_reminder;
    private final boolean o_new;
    
    private final ComponentInfoPanel o_compInfoPanel;

    private final JButton o_closeButton = new JButton("Close");
    
    public ReminderPanel(Reminder p_reminder, boolean p_new)
    {
        super(new BorderLayout());
        o_reminder = p_reminder;
        o_new = p_new;
        
        o_compInfoPanel = new ComponentInfoPanel(p_reminder, p_new);

        JPanel x_remindDatePanel = new JPanel(new BorderLayout());
		x_remindDatePanel.add(new RemindDateSuggestionPanel(o_reminder, this), BorderLayout.CENTER);
		x_remindDatePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, BorderLayout.NORTH);
        x_panel.add(x_remindDatePanel, BorderLayout.CENTER);
        x_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        o_closeButton.addActionListener(this);
        
        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(o_closeButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e)
    {
			WindowManager.getInstance().closeComponentWindow(o_reminder);
    }

	@Override
	public void changed(boolean saved) {
		o_closeButton.setText(saved ? "Close" : "Cancel");
	}
}
