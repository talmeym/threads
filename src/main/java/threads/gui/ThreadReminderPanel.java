package threads.gui;

import threads.data.Configuration;
import threads.data.Reminder;
import threads.data.Thread;
import threads.util.GoogleSyncer;
import threads.util.Settings;
import threads.util.TimedUpdater;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.createEmptyBorder;
import static threads.data.ComponentType.Reminder;
import static threads.gui.Actions.linkToGoogle;
import static threads.gui.GUIConstants.*;
import static threads.util.Settings.Setting.ONLYDUE;

class ThreadReminderPanel extends ComponentTablePanel<Thread, Reminder> {
	private final JRadioButton o_showDueRadioButton;
	private final JRadioButton o_showAllRadioButton;
	private final ContextualPopupMenu o_popupMenu = new ContextualPopupMenu(false, true, Reminder);
	private final JLabel o_topLabel = new JLabel("0 Reminders");

	ThreadReminderPanel(Configuration p_configuration, Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadReminderTableModel(p_thread), new ThreadReminderCellRenderer());
		p_thread.addComponentChangeListener(e -> tableRowClicked(-1, -1, null));

		Settings x_settings = p_configuration.getSettings();

		fixColumnWidth(0, s_threadColumnWidth);
        fixColumnWidth(2, s_dateStatusColumnWidth);
        fixColumnWidth(3, s_dateStatusColumnWidth);
        fixColumnWidth(4, s_googleStatusColumnWidth);

		o_popupMenu.setActivateActionListener(e -> Actions.activateComponent(getSelectedObject(), p_parentPanel));
		o_popupMenu.setDeactivateActionListener(e -> Actions.deactivateComponent(getSelectedObject(), p_parentPanel));
		o_popupMenu.setRemoveActionListener(e -> Actions.removeComponent(getSelectedObject(), p_parentPanel, false));
		o_popupMenu.setLinkActionListener(e -> linkToGoogle(getSelectedObject(), p_configuration, p_parentPanel));

		ThreadReminderTableModel x_tableModel = (ThreadReminderTableModel) o_table.getModel();
		o_showDueRadioButton = new JRadioButton("Due", x_tableModel.onlyDueReminders());
		o_showAllRadioButton = new JRadioButton("All", !x_tableModel.onlyDueReminders());

		boolean x_onlyDue = x_settings.registerForBooleanSetting(ONLYDUE, (k, v) -> {
			((ThreadReminderTableModel)o_table.getModel()).setOnlyDueReminders((Boolean) v);
			o_showDueRadioButton.setSelected((boolean)v);
			o_showAllRadioButton.setSelected(!(boolean)v);
		});

		x_tableModel.setOnlyDueReminders(x_onlyDue);

		o_showDueRadioButton.addChangeListener(e -> {
			boolean x_selected = o_showDueRadioButton.isSelected();
			x_tableModel.setOnlyDueReminders(x_selected);
			x_settings.updateSetting(ONLYDUE, x_selected);
            o_topLabel.setText(o_table.getModel().getRowCount() + " Reminders");
        });

		ButtonGroup x_group = new ButtonGroup();
		x_group.add(o_showDueRadioButton);
		x_group.add(o_showAllRadioButton);

		o_topLabel.setHorizontalAlignment(JLabel.CENTER);
		o_topLabel.setText(o_table.getModel().getRowCount() + " Reminders");
		p_thread.addComponentChangeListener(l -> o_topLabel.setText(o_table.getModel().getRowCount() + " Reminders"));
		o_topLabel.setBorder(createEmptyBorder(0, 5, 5, 5));

		JPanel x_buttonPanel = new JPanel(new FlowLayout(LEFT));
		x_buttonPanel.add(new JLabel("View:"));
		x_buttonPanel.add(o_showDueRadioButton);
		x_buttonPanel.add(o_showAllRadioButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(o_topLabel, NORTH);
        add(x_buttonPanel, SOUTH);

		TimedUpdater.getInstance().addActivityListener(this);
		GoogleSyncer.getInstance().addActivityListener(this);
    }

	@Override
	void showContextMenu(Component p_origin, int p_row, int p_col, Point p_point, Reminder p_selectedObject) {
		if(p_selectedObject != null) {
			o_popupMenu.show(p_point, p_origin);
		}
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, Reminder p_reminder) {
		boolean x_enabled = p_row != -1;
		o_popupMenu.setStatus(x_enabled, x_enabled, false, x_enabled, p_reminder);
	}

	@Override
	public void tableRowDoubleClicked(int p_row, int p_col, Reminder p_reminder) {
        switch(p_col) {
			case 0: WindowManager.getInstance().openComponent(p_reminder.getParentItem()); break;
			default: WindowManager.getInstance().openComponent(p_reminder); break;
        }
    }
}
