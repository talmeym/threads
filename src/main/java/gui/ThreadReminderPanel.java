package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.Component;
import java.awt.*;

import static data.ComponentType.Reminder;
import static gui.Actions.linkToGoogle;
import static gui.GUIConstants.*;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEFT;
import static util.Settings.*;

class ThreadReminderPanel extends ComponentTablePanel<Thread, Reminder> implements SettingChangeListener {
	private final JRadioButton o_showDueRadioButton;
	private final JRadioButton o_showAllRadioButton;
	private final ContextualPopupMenu o_popupMenu = new ContextualPopupMenu(false, true, Reminder);

	ThreadReminderPanel(Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadReminderTableModel(p_thread), new ThreadReminderCellRenderer(p_thread));
		p_thread.addComponentChangeListener(e -> tableRowClicked(-1, -1, null));

		fixColumnWidth(0, s_threadColumnWidth);
        fixColumnWidth(2, s_creationDateColumnWidth);
        fixColumnWidth(3, s_dateStatusColumnWidth);
        fixColumnWidth(4, s_googleStatusColumnWidth);

		o_popupMenu.setActivateActionListener(e -> Actions.activate(getSelectedObject(), p_parentPanel));
		o_popupMenu.setDeactivateActionListener(e -> Actions.deactivate(getSelectedObject(), p_parentPanel));
		o_popupMenu.setRemoveActionListener(e -> Actions.remove(getSelectedObject(), p_parentPanel, false));
		o_popupMenu.setLinkActionListener(e -> linkToGoogle(getSelectedObject(), p_parentPanel));

		boolean x_onlyDue = registerForSetting(s_ONLYDUE, this, true);
		ThreadReminderTableModel x_tableModel = (ThreadReminderTableModel) o_table.getModel();
		x_tableModel.setOnlyDueReminders(x_onlyDue);
		((ThreadReminderCellRenderer)o_table.getCellRenderer(0, 0)).setOnlyDueReminders(x_onlyDue);

		o_showDueRadioButton = new JRadioButton("Due", x_tableModel.onlyDueReminders());
		o_showAllRadioButton = new JRadioButton("All", !x_tableModel.onlyDueReminders());

		o_showDueRadioButton.addChangeListener(e -> {
			boolean x_selected = o_showDueRadioButton.isSelected();
			x_tableModel.setOnlyDueReminders(x_selected);
			updateSetting(s_ONLYDUE, x_selected);
		});

		ButtonGroup x_group = new ButtonGroup();
		x_group.add(o_showDueRadioButton);
		x_group.add(o_showAllRadioButton);

		JPanel x_buttonPanel = new JPanel(new FlowLayout(LEFT));
		x_buttonPanel.add(new JLabel("View:"));
		x_buttonPanel.add(o_showDueRadioButton);
		x_buttonPanel.add(o_showAllRadioButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
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

	@Override
	public void settingChanged(String p_name, Object p_value) {
		boolean x_onlyDue = (Boolean) p_value;
		((ThreadReminderTableModel)o_table.getModel()).setOnlyDueReminders(x_onlyDue);
		((ThreadReminderCellRenderer)o_table.getCellRenderer(0, 0)).setOnlyDueReminders(x_onlyDue);
		o_showDueRadioButton.setSelected(x_onlyDue);
		o_showAllRadioButton.setSelected(!x_onlyDue);
	}
}
