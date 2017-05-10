package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static util.GuiUtil.setUpButtonLabel;
import static util.Settings.*;

public class ItemAndReminderPanel extends JPanel implements TableSelectionListener<Reminder>, TimeUpdateListener, GoogleSyncListener, SettingChangeListener {
	private static final String s_none = "none";
	private static final String s_noneSelected = "none selected";

	private final Item o_item;
	private final JPanel o_parentPanel;
	private final JLabel o_addReminderLabel = new JLabel(ImageUtil.getPlusIcon());
	private final CardLayout o_cardLayout = new CardLayout();
	private final JPanel o_cardPanel = new JPanel(o_cardLayout);
	private final Map<UUID, JPanel> o_reminderPanels = new HashMap<UUID, JPanel>();
	private final JSplitPane o_splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	public ItemAndReminderPanel(Item p_item, JPanel p_parentPanel) {
		this(p_item, null, p_parentPanel);
	}

	public ItemAndReminderPanel(Reminder p_reminder, JPanel p_parentPanel) {
		this(p_reminder.getParentItem(), p_reminder, p_parentPanel);
	}

	public ItemAndReminderPanel(Item p_item, Reminder p_reminder, JPanel p_parentPanel) {
		super(new BorderLayout());
		o_item = p_item;
		o_parentPanel = p_parentPanel;

		o_item.addComponentChangeListener(p_cce -> {
			if (p_cce.getSource() == o_item) {
				if (p_cce.getType() == ComponentChangeEvent.s_REMOVED) {
					o_cardLayout.show(o_cardPanel, o_item.getReminderCount() > 0 ? s_noneSelected : s_none);
				}

				o_addReminderLabel.setEnabled(o_item.getDueDate() != null);
			}
		});

		ItemPanel x_itemPanel = new ItemPanel(p_item, o_parentPanel);
		x_itemPanel.addTableSelectionListener(this);

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
			public void mouseClicked(MouseEvent e) {
				addReminder();
			}
		});

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(o_addReminderLabel));

		JPanel x_bottomPanel = new JPanel(new BorderLayout());
		x_bottomPanel.add(o_cardPanel, BorderLayout.CENTER);
		x_bottomPanel.add(x_buttonPanel, BorderLayout.SOUTH);

		o_splitPane.setDividerLocation(registerForSetting(Settings.s_DIVLOC, this, 350));

		o_splitPane.setTopComponent(x_itemPanel);
		o_splitPane.setBottomComponent(x_bottomPanel);

		o_splitPane.addPropertyChangeListener(propertyChangeEvent -> {
			if(propertyChangeEvent.getPropertyName().equals("dividerLocation")) {
				updateSetting(Settings.s_DIVLOC, "" + propertyChangeEvent.getNewValue());
			}
		});

		add(o_splitPane, BorderLayout.CENTER);

		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);

		if(p_reminder != null) {
			showReminder(p_reminder);
		}
	}

	private void addReminder() {
		if (o_item.getDueDate() != null) {
			String x_text = (String) JOptionPane.showInputDialog(o_parentPanel, "Enter new Reminder text:", "Add new Reminder to '" + o_item + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Reminder");

			if(x_text != null) {
				Reminder x_reminder = new Reminder(x_text, o_item);
				o_item.addReminder(x_reminder);
				showReminder(x_reminder);
			}
		}
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, Reminder p_reminder) {
		if(p_reminder != null) {
			showReminder(p_reminder);
		} else {
			o_cardLayout.show(o_cardPanel, o_item.getReminderCount() > 0 ? s_noneSelected : s_none);
		}
	}

	public void showReminder(Reminder x_reminder) {
		if(!o_reminderPanels.containsKey(x_reminder.getId())) {
			ReminderPanel x_reminderPanel = new ReminderPanel(x_reminder, o_parentPanel);
			x_reminderPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createLoweredBevelBorder()));
			o_cardPanel.add(x_reminderPanel, x_reminder.getId().toString());
			o_reminderPanels.put(x_reminder.getId(), x_reminderPanel);
		}

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
	public void settingChanged(String p_name, Object p_value) {
		o_splitPane.setDividerLocation(Integer.parseInt(p_value.toString()));
	}
}
