package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ReminderPanel extends JPanel implements GoogleSyncListener {
	private Reminder o_reminder;
	private JPanel o_parentPanel;

	private final JLabel o_linkLabel = new JLabel(ImageUtil.getLinkIcon());

	public ReminderPanel(final Reminder p_reminder, JPanel p_parentPanel) {
        super(new BorderLayout());
		o_reminder = p_reminder;
		o_parentPanel = p_parentPanel;

		o_linkLabel.setToolTipText("Link to Google Calendar");
		o_linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				linkToGoogle();
			}
		});

		ComponentInfoPanel o_compInfoPanel = new ComponentInfoPanel(p_reminder, this, false, o_linkLabel);
		o_compInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel x_remindDatePanel = new JPanel(new BorderLayout());
		x_remindDatePanel.add(new RemindDateSuggestionPanel(p_reminder, this), BorderLayout.CENTER);
		x_remindDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, BorderLayout.NORTH);
        x_panel.add(x_remindDatePanel, BorderLayout.CENTER);

		add(x_panel, BorderLayout.NORTH);

		GoogleSyncer.getInstance().addGoogleSyncListener(this);
    }

	private void linkToGoogle() {
		if (o_linkLabel.isEnabled()) {
			if (JOptionPane.showConfirmDialog(o_parentPanel, "Link '" + o_reminder.getText() + "' to Google Calendar ?", "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
				GoogleLinkTask x_task = new GoogleLinkTask(o_reminder, new GoogleProgressWindow(o_parentPanel), new ProgressAdapter() {
					@Override
					public void finished() {
						JOptionPane.showMessageDialog(o_parentPanel, "'" + o_reminder.getText() + "' was linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
						googleSynced();
					}
				});
				x_task.execute();
			}
		}
	}

	@Override
	public void googleSyncStarted() {
		// do nothing by default
	}

	@Override
	public void googleSynced() {
		if(GoogleUtil.isLinked(o_reminder)) {
			o_linkLabel.setIcon(ImageUtil.getGoogleSmallIcon());
		} else {
			o_linkLabel.setIcon(ImageUtil.getLinkIcon());
		}
	}
}
