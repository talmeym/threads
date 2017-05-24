package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static util.GoogleUtil.isLinked;
import static util.ImageUtil.getGoogleSmallIcon;
import static util.ImageUtil.getLinkIcon;

class ReminderPanel extends JPanel implements GoogleSyncListener {
	private Reminder o_reminder;
	private JPanel o_parentPanel;

	private final JLabel o_linkLabel = new JLabel(getLinkIcon());

	ReminderPanel(final Reminder p_reminder, JPanel p_parentPanel) {
        super(new BorderLayout());
		o_reminder = p_reminder;
		o_parentPanel = p_parentPanel;

		o_linkLabel.setToolTipText("Link to Google Calendar");
		o_linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (o_linkLabel.isEnabled()) {
                    Actions.linkToGoogle(o_reminder, o_parentPanel);
                }
			}
		});

		ComponentInfoPanel o_compInfoPanel = new ComponentInfoPanel(p_reminder, this, false, o_linkLabel);
		o_compInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel x_remindDatePanel = new JPanel(new BorderLayout());
		x_remindDatePanel.add(new RemindDateSuggestionPanel(p_reminder), BorderLayout.CENTER);
		x_remindDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, BorderLayout.NORTH);
        x_panel.add(x_remindDatePanel, BorderLayout.CENTER);

		add(x_panel, BorderLayout.NORTH);

		GoogleSyncer.getInstance().addActivityListener(this);
    }

	@Override
	public void googleSyncStarted() {
		// do nothing by default
	}

	@Override
	public void googleSynced() {
		o_linkLabel.setIcon(isLinked(o_reminder) ? getGoogleSmallIcon() : getLinkIcon());
	}

	public void googleSynced(List<HasDueDate> p_hasDueDates) {
		o_linkLabel.setIcon(isLinked(o_reminder) ? getGoogleSmallIcon() : getLinkIcon());
	}
}
