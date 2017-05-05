package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.*;

import static gui.Actions.linkToGoogle;
import static util.Settings.*;

public class ThreadReminderPanel extends ComponentTablePanel<Thread, Reminder> implements ComponentChangeListener, SettingChangeListener {
	private final JMenuItem o_dismissLabel = new JMenuItem("Set Inactive", ImageUtil.getTickIcon());
	private final JMenuItem o_removeLabel = new JMenuItem("Remove", ImageUtil.getMinusIcon());
	private final JMenuItem o_linkLabel = new JMenuItem("Link", ImageUtil.getLinkIcon());
	private final JPanel o_parentPanel;
	private final JRadioButton o_showDueRadioButton;
	private final JRadioButton o_showAllRadioButton;

	ThreadReminderPanel(Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadReminderTableModel(p_thread), new ComponentCellRenderer(null));
		o_parentPanel = p_parentPanel;
		p_thread.addComponentChangeListener(this);

		fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(4, GUIConstants.s_googleStatusColumnWidth);

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove Reminder");
		o_removeLabel.addActionListener(e -> remove(getSelectedObject()));

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Set Reminder Active/Inactive");
		o_dismissLabel.addActionListener(e -> dismiss(getSelectedObject()));

		o_linkLabel.setToolTipText("Link Reminder to Google Calendar");
		o_linkLabel.setEnabled(false);
		o_linkLabel.addActionListener(e -> {
			if (o_linkLabel.isEnabled()) {
				linkToGoogle(getSelectedObject(), o_parentPanel);
			}
		});

		ThreadReminderTableModel x_tableModel = (ThreadReminderTableModel) o_table.getModel();
		x_tableModel.setOnlyDueReminders(registerForSetting(s_ONLYDUE, this, true));

		o_showDueRadioButton = new JRadioButton("Due", x_tableModel.onlyDueReminders());
		o_showAllRadioButton = new JRadioButton("All", !x_tableModel.onlyDueReminders());

		o_showDueRadioButton.addChangeListener(e -> {
			boolean selected = o_showDueRadioButton.isSelected();
			x_tableModel.setOnlyDueReminders(selected);
			updateSetting(Settings.s_ONLYDUE, selected ? 1 : 0);
		});

		ButtonGroup x_group = new ButtonGroup();
		x_group.add(o_showDueRadioButton);
		x_group.add(o_showAllRadioButton);

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(o_showDueRadioButton);
		x_buttonPanel.add(o_showAllRadioButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_buttonPanel, BorderLayout.SOUTH);

		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
    }

	private void dismiss(Reminder p_reminder) {
		if(o_dismissLabel.isEnabled()) {
			boolean x_active = !p_reminder.isActive();

			if(JOptionPane.showConfirmDialog(o_parentPanel, "Set '" + p_reminder.getText() + "' " + (x_active ? "Active" : "Inactive") + " ?", "Set " + (x_active ? "Active" : "Inactive") + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				p_reminder.setActive(false);
			}
		}
	}

	private void remove(Reminder p_reminder) {
		if(o_removeLabel.isEnabled()) {
			if (p_reminder != null) {
				Item x_item = p_reminder.getParentItem();

				if (JOptionPane.showConfirmDialog(o_parentPanel, "Remove '" + p_reminder.getText() + "' from '" + x_item.getText() + "' ?", "Remove " + p_reminder.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
					x_item.removeReminder(p_reminder);
				}
			}
		}
	}

	@Override
	void showContextMenu(int p_row, int p_col, Point p_point, Component p_origin, Reminder p_selectedObject) {
		if(p_selectedObject != null) {
			JPopupMenu x_menu = new JPopupMenu();
			x_menu.add(o_removeLabel);
			x_menu.add(o_dismissLabel);
			x_menu.add(o_linkLabel);
			x_menu.show(p_origin, p_point.x, p_point.y);
		}
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, Reminder p_reminder) {
		o_dismissLabel.setEnabled(p_row != -1);
		o_removeLabel.setEnabled(p_row != -1);
		o_linkLabel.setEnabled(p_row != -1);
	}

	@Override
	public void tableRowDoubleClicked(int p_row, int p_col, Reminder p_reminder) {
        switch(p_col) {
			case 0: WindowManager.getInstance().openComponent(p_reminder.getParentItem()); break;
			default: WindowManager.getInstance().openComponent(p_reminder); break;
        }
    }

	@Override
	public void timeUpdate() {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void googleSynced() {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void componentChanged(ComponentChangeEvent p_cce) {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void settingChanged(String p_name, Object p_value) {
		((ThreadReminderTableModel)o_table.getModel()).setOnlyDueReminders(p_value.equals(1));
		o_showDueRadioButton.setSelected(p_value.equals(1));
		o_showAllRadioButton.setSelected(p_value.equals(0));
	}
}
