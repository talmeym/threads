package threads.gui;

import threads.data.*;
import threads.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.BorderLayout.*;
import static threads.gui.Actions.linkToGoogle;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.GoogleUtil.googleAccount;
import static threads.util.ImageUtil.*;

class ReminderPanel extends JPanel implements GoogleSyncListener {
	private final Reminder o_reminder;
	private final JPanel o_parentPanel;
	private final JLabel o_linkLabel;

	ReminderPanel(final Reminder p_reminder, JPanel p_parentPanel) {
        super(new BorderLayout());
		o_reminder = p_reminder;
		o_parentPanel = p_parentPanel;

		o_linkLabel = createLabel(getLinkIcon(), "Link to Google Calendar", true, e -> linkToGoogle(o_reminder, o_parentPanel));

		ComponentInfoPanel o_compInfoPanel = new ComponentInfoPanel(p_reminder, this, false, o_linkLabel);
		o_compInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel x_remindDatePanel = new JPanel(new BorderLayout());
		x_remindDatePanel.add(new ReminderDateSuggestionPanel(p_reminder), CENTER);
		x_remindDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, NORTH);
        x_panel.add(x_remindDatePanel, CENTER);

		add(x_panel, NORTH);

		GoogleSyncer.getInstance().addActivityListener(this);
		googleSynced();
	}

	@Override
	public void googleSyncStarted() {
		// do nothing by default
	}

	@Override
	public void googleSynced() {
		setLinkLabelText();
	}

	public void googleSynced(List<HasDueDate> p_hasDueDates) {
		setLinkLabelText();
	}

	public void setLinkLabelText() {
		GoogleAccount x_account = googleAccount(o_reminder);
		o_linkLabel.setIcon(x_account != null ? getGoogleSmallIcon() : getLinkIcon());
		o_linkLabel.setToolTipText(x_account != null ? x_account.getName() : "Link to Google Calendar");
	}
}
