package threads.gui;

import threads.data.Configuration;
import threads.data.HasDueDate;
import threads.data.Reminder;
import threads.util.GoogleAccount;
import threads.util.GoogleSyncListener;
import threads.util.GoogleSyncer;
import threads.util.Settings;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.List;

import static java.awt.BorderLayout.*;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static threads.gui.Actions.linkToGoogle;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.GoogleUtil.googleAccount;
import static threads.util.ImageUtil.*;
import static threads.util.Settings.Setting.*;

class ReminderPanel extends JPanel implements GoogleSyncListener {
	private final Reminder o_reminder;
	private final JPanel o_parentPanel;
	private final JLabel o_linkLabel;

	ReminderPanel(Configuration p_configuration, final Reminder p_reminder, JPanel p_parentPanel) {
        super(new BorderLayout());
		o_reminder = p_reminder;
		o_parentPanel = p_parentPanel;
		Settings x_settings = p_configuration.getSettings();

		JLabel x_calendarLabel = createLabel(getCalendarIcon(), "Show in Calendar", o_reminder, i -> true, e -> {
			Calendar x_calendar = Calendar.getInstance();
			x_calendar.setTime(o_reminder.getDueDate());
			x_settings.updateSetting(TABINDEX, 5);
			x_settings.updateSetting(DATE, x_calendar.get(MONTH) + "_" + x_calendar.get(YEAR));
			x_settings.updateSetting(CALENDARREM, true);
			WindowManager.getInstance().openComponent(o_reminder.getParentItem().getParentThread());
		});

		o_linkLabel = createLabel(getLinkIcon(), "Link to Google Calendar", true, e -> linkToGoogle(o_reminder, p_configuration, o_parentPanel));

		ComponentInfoPanel o_compInfoPanel = new ComponentInfoPanel(p_reminder, this, false, x_calendarLabel, o_linkLabel);
		o_compInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel x_remindDatePanel = new JPanel(new BorderLayout());
		x_remindDatePanel.add(new ReminderDateSuggestionPanel(p_configuration, p_reminder, p_parentPanel), CENTER);
		x_remindDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, NORTH);
        x_panel.add(x_remindDatePanel, CENTER);
		x_panel.add(new HasDueDateNotesPanel(o_reminder), SOUTH);

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

	public void googleSynced(List<HasDueDate> p_hasDueDates, boolean p_import) {
		setLinkLabelText();
	}

	private void setLinkLabelText() {
		GoogleAccount x_account = googleAccount(o_reminder);
		o_linkLabel.setIcon(x_account != null ? getGoogleSmallIcon() : getLinkIcon());
		o_linkLabel.setToolTipText(x_account != null ? x_account.getName() : "Link to Google Calendar");
	}
}
