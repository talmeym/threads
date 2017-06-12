package threads.gui;

import threads.data.Configuration;
import threads.data.HasDueDate;
import threads.data.Item;
import threads.data.Reminder;
import threads.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEFT;
import static java.lang.Integer.parseInt;
import static javax.swing.BorderFactory.*;
import static javax.swing.JSplitPane.VERTICAL_SPLIT;
import static threads.gui.Actions.addReminder;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.ImageUtil.getPlusIcon;
import static threads.util.Settings.Setting.DIVLOC;

class ItemAndReminderPanel extends JPanel implements TableSelectionListener<Reminder>, TimedUpdateListener, GoogleSyncListener {
	private static final String s_none = "none";
	private static final String s_noneSelected = "none selected";

	private final Item o_item;
	private final Configuration o_configuration;
	private final JPanel o_parentPanel;

	private final ItemPanel o_itemPanel;
	private final CardLayout o_cardLayout = new CardLayout();
	private final JPanel o_cardPanel = new JPanel(o_cardLayout);
	private final Map<UUID, JPanel> o_reminderPanels = new HashMap<>();
	private final JSplitPane o_splitPane = new JSplitPane(VERTICAL_SPLIT);

	ItemAndReminderPanel(Configuration p_configuration, Item p_item, JPanel p_parentPanel, JFrame p_frame) {
		super(new BorderLayout());
		o_item = p_item;
		o_configuration = p_configuration;
		o_parentPanel = p_parentPanel;
		Settings x_settings = p_configuration.getSettings();

		o_item.addComponentChangeListener(e -> {
			if (e.getSource() == o_item && e.isContentRemoved()) {
				o_cardLayout.show(o_cardPanel, o_item.getReminders().size() > 0 ? s_noneSelected : s_none);
			}
		});

		o_itemPanel = new ItemPanel(p_configuration, p_item, o_parentPanel, p_frame);
		o_itemPanel.addTableSelectionListener(this);

		JLabel x_noneLabelLabel = new JLabel("No Reminders");
		x_noneLabelLabel.setHorizontalAlignment(JLabel.CENTER);

		JLabel x_noneSelectedLabel = new JLabel("No Reminder Selected");
		x_noneSelectedLabel.setHorizontalAlignment(JLabel.CENTER);

		JPanel x_nonePanel = new JPanel(new BorderLayout());
		x_nonePanel.add(x_noneLabelLabel, CENTER);
		x_nonePanel.setBorder(createCompoundBorder(createEmptyBorder(5, 5, 5, 5), createLoweredBevelBorder()));

		JPanel x_noneSelectedPanel = new JPanel(new BorderLayout());
		x_noneSelectedPanel.add(x_noneSelectedLabel, CENTER);
		x_noneSelectedPanel.setBorder(createCompoundBorder(createEmptyBorder(5, 5, 5, 5), createLoweredBevelBorder()));

	    o_cardPanel.add(x_nonePanel, s_none);
	    o_cardPanel.add(x_noneSelectedPanel, s_noneSelected);
		o_cardLayout.show(o_cardPanel, o_item.getReminders().size() > 0 ? s_noneSelected : s_none);

		JLabel o_addReminderLabel = createLabel(getPlusIcon(), "Add Reminder", o_item, i -> i.getDueDate() != null, e -> addReminder(o_item, p_parentPanel, true));

		JPanel x_buttonPanel = new JPanel(new FlowLayout(LEFT));
		x_buttonPanel.add(o_addReminderLabel);

		JPanel x_bottomPanel = new JPanel(new BorderLayout());
		x_bottomPanel.add(o_cardPanel, CENTER);
		x_bottomPanel.add(x_buttonPanel, SOUTH);

		o_splitPane.setDividerLocation(x_settings.registerForIntSetting(DIVLOC, (k, v) -> o_splitPane.setDividerLocation(parseInt(v.toString()))));

		o_splitPane.setTopComponent(o_itemPanel);
		o_splitPane.setBottomComponent(x_bottomPanel);

		o_splitPane.addPropertyChangeListener(propertyChangeEvent -> {
			if(propertyChangeEvent.getPropertyName().equals("dividerLocation")) {
				x_settings.updateSetting(DIVLOC, "" + propertyChangeEvent.getNewValue());
			}
		});

		add(o_splitPane, CENTER);

		TimedUpdater.getInstance().addActivityListener(this);
		GoogleSyncer.getInstance().addActivityListener(this);
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, Reminder p_reminder) {
		if(p_reminder != null) {
			showReminder(p_reminder);
		} else {
			o_cardLayout.show(o_cardPanel, o_item.getReminders().size() > 0 ? s_noneSelected : s_none);
		}
	}

	void showReminder(Reminder x_reminder) {
		if(!o_reminderPanels.containsKey(x_reminder.getId())) {
			ReminderPanel x_reminderPanel = new ReminderPanel(o_configuration, x_reminder, o_parentPanel);
			x_reminderPanel.setBorder(createCompoundBorder(createEmptyBorder(5, 5, 5, 5), createLoweredBevelBorder()));
			o_cardPanel.add(x_reminderPanel, x_reminder.getId().toString());
			o_reminderPanels.put(x_reminder.getId(), x_reminderPanel);
		}

		o_itemPanel.selectReminder(x_reminder);
		o_cardLayout.show(o_cardPanel, x_reminder.getId().toString());
	}

	@Override
	public void tableRowDoubleClicked(int p_row, int p_col, Reminder p_reminder) {
		// do nothing
	}

	@Override
	public void timeUpdate() {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void googleSyncStarted() {
		// do nothing by default
	}

	@Override
	public void googleSynced() {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void googleSynced(List<HasDueDate> p_hasDueDates) {
		tableRowClicked(-1, -1, null);
	}
}
