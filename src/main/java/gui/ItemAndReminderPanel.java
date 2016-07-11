package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import static util.GuiUtil.setUpButtonLabel;

public class ItemAndReminderPanel extends MemoryPanel implements TableSelectionListener<Reminder>, TimeUpdateListener, GoogleSyncListener {
	private static final String s_none = "none";
	private static final String s_noneSelected = "none selected";

	private final Item o_item;
	private final JLabel o_addReminderLabel = new JLabel(ImageUtil.getPlusIcon());
	private final CardLayout o_cardLayout = new CardLayout();
	private final JPanel o_cardPanel = new JPanel(o_cardLayout);
	private final Map<UUID, JPanel> o_reminderPanels = new HashMap<UUID, JPanel>();

	public ItemAndReminderPanel(Item p_item) {
		this(p_item, null);
	}

	public ItemAndReminderPanel(Reminder p_reminder) {
		this(p_reminder.getItem(), p_reminder);
	}

	public ItemAndReminderPanel(Item p_item, Reminder p_reminder) {
		super(new BorderLayout());
		o_item = p_item;

		o_item.addComponentChangeListener(new ComponentChangeListener() {
			@Override
			public void componentChanged(ComponentChangeEvent p_event) {
				if (p_event.getSource() == o_item) {
					if (p_event.getType() == ComponentChangeEvent.s_REMOVED) {
						o_cardLayout.show(o_cardPanel, o_item.getReminderCount() > 0 ? s_noneSelected : s_none);
					}

					o_addReminderLabel.setEnabled(o_item.getDueDate() != null);
				}
			}
		});

		ItemPanel x_itemPanel = new ItemPanel(p_item);
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
				addSomething();
			}
		});

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(o_addReminderLabel));

		JPanel x_bottomPanel = new JPanel(new BorderLayout());
		x_bottomPanel.add(o_cardPanel, BorderLayout.CENTER);
		x_bottomPanel.add(x_buttonPanel, BorderLayout.SOUTH);

		JSplitPane x_splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		x_splitPane.setDividerLocation(recallValue(350));

		x_splitPane.setTopComponent(x_itemPanel);
		x_splitPane.setBottomComponent(x_bottomPanel);

		x_splitPane.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				if(propertyChangeEvent.getPropertyName().equals("dividerLocation")) {
					rememberValue((Integer)propertyChangeEvent.getNewValue());
				}
			}
		});

		add(x_splitPane, BorderLayout.CENTER);

		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);

		if(p_reminder != null) {
			showReminder(p_reminder);
		}
	}

	private void addSomething() {
		if (o_item.getDueDate() != null) {
			String x_text = (String) JOptionPane.showInputDialog(this, "Enter new Reminder text:", "Add new Reminder to '" + o_item + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Reminder");

			if(x_text != null) {
				Reminder x_reminder = new Reminder(o_item);
				x_reminder.setText(x_text);
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
			ReminderPanel x_reminderPanel = new ReminderPanel(x_reminder);
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
	public void googleSynced() {
		tableRowClicked(-1, -1, null);
	}
}
