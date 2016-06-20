package gui;

import data.*;

import javax.swing.*;
import java.awt.*;

public class ReminderPanel extends JPanel implements ComponentInfoChangeListener {
    public ReminderPanel(final Reminder p_reminder) {
        super(new BorderLayout());

		ComponentInfoPanel o_compInfoPanel = new ComponentInfoPanel(p_reminder, this, this);
		o_compInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel x_remindDatePanel = new JPanel(new BorderLayout());
		x_remindDatePanel.add(new RemindDateSuggestionPanel(p_reminder, this), BorderLayout.CENTER);
		x_remindDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, BorderLayout.NORTH);
        x_panel.add(x_remindDatePanel, BorderLayout.CENTER);

		add(x_panel, BorderLayout.NORTH);
    }

	@Override
	public void componentInfoChanged(boolean saved) {
		// do nothing
	}
}
