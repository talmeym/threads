package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import static gui.Actions.addReminder;
import static util.GuiUtil.setUpButtonLabel;
import static util.Settings.*;

class ItemAndReminderPanel extends JPanel implements TableSelectionListener<Reminder>, TimedUpdateListener, GoogleSyncListener, SettingChangeListener {
	private static final String s_none = "none";
	private static final String s_noneSelected = "none selected";

	private final Item o_item;
	private final ItemPanel o_itemPanel;
	private final JPanel o_parentPanel;
	private final JLabel o_addReminderLabel = new JLabel(ImageUtil.getPlusIcon());
	private final CardLayout o_cardLayout = new CardLayout();
	private final JPanel o_cardPanel = new JPanel(o_cardLayout);
	private final Map<UUID, JPanel> o_reminderPanels = new HashMap<>();
	private final JSplitPane o_splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	ItemAndReminderPanel(Item p_item, JPanel p_parentPanel) {
		super(new BorderLayout());
		o_item = p_item;
		o_parentPanel = p_parentPanel;

		o_item.addComponentChangeListener(e -> {
			if (e.getSource() == o_item) {
				if (e.isContentRemoved()) {
					o_cardLayout.show(o_cardPanel, o_item.getReminderCount() > 0 ? s_noneSelected : s_none);
				}

				o_addReminderLabel.setEnabled(o_item.getDueDate() != null);
			}
		});

		o_itemPanel = new ItemPanel(p_item, o_parentPanel);
		o_itemPanel.addTableSelectionListener(this);

		JLabel x_noneLabelLabel = new JLabel("No Reminders");
		x_noneLabelLabel.setHorizontalAlignment(JLabel.CENTER);

		JLabel x_noneSelectedLabel = new JLabel("No Reminder Selected");
		x_noneSelectedLabel.setHorizontalAlignment(JLabel.CENTER);

		JPanel x_nonePanel = new JPanel(new BorderLayout());
		x_nonePanel.add(x_noneLabelLabel, BorderLayout.CENTER);
		x_nonePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createLoweredBevelBorder()));

		JPanel x_noneSelectedPanel = new JPanel(new BorderLayout());
		x_noneSelectedPanel.add(x_noneSelectedLabel, BorderLayout.CENTER);
		x_noneSelectedPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createLoweredBevelBorder()));

	    o_cardPanel.add(x_nonePanel, s_none);
	    o_cardPanel.add(x_noneSelectedPanel, s_noneSelected);
		o_cardLayout.show(o_cardPanel, o_item.getReminderCount() > 0 ? s_noneSelected : s_none);

		o_addReminderLabel.setEnabled(o_item.getDueDate() != null);
		o_addReminderLabel.setToolTipText("Add Reminder");
		o_addReminderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				addReminder(o_item, p_parentPanel, true);
			}
		});

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(o_addReminderLabel));

		JPanel x_bottomPanel = new JPanel(new BorderLayout());
		x_bottomPanel.add(o_cardPanel, BorderLayout.CENTER);
		x_bottomPanel.add(x_buttonPanel, BorderLayout.SOUTH);

		o_splitPane.setDividerLocation(registerForSetting(Settings.s_DIVLOC, this, 350));

		o_splitPane.setTopComponent(o_itemPanel);
		o_splitPane.setBottomComponent(x_bottomPanel);

		o_splitPane.addPropertyChangeListener(propertyChangeEvent -> {
			if(propertyChangeEvent.getPropertyName().equals("dividerLocation")) {
				updateSetting(Settings.s_DIVLOC, "" + propertyChangeEvent.getNewValue());
			}
		});

		add(o_splitPane, BorderLayout.CENTER);

		TimedUpdater.getInstance().addActivityListener(this);
		GoogleSyncer.getInstance().addActivityListener(this);
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, Reminder p_reminder) {
		if(p_reminder != null) {
			showReminder(p_reminder);
		} else {
			o_cardLayout.show(o_cardPanel, o_item.getReminderCount() > 0 ? s_noneSelected : s_none);
		}
	}

	void showReminder(Reminder x_reminder) {
		if(!o_reminderPanels.containsKey(x_reminder.getId())) {
			ReminderPanel x_reminderPanel = new ReminderPanel(x_reminder, o_parentPanel);
			x_reminderPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createLoweredBevelBorder()));
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

	@Override
	public void settingChanged(String p_name, Object p_value) {
		o_splitPane.setDividerLocation(Integer.parseInt(p_value.toString()));
	}
}
