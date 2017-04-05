package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.*;

import static gui.Actions.linkToGoogle;

public class ThreadReminderPanel extends ComponentTablePanel<Thread, Reminder> implements Observer {
	private final JMenuItem o_dismissLabel = new JMenuItem("Set Inactive", ImageUtil.getTickIcon());
	private final JMenuItem o_removeLabel = new JMenuItem("Remove", ImageUtil.getMinusIcon());
	private final JMenuItem o_linkLabel = new JMenuItem("Link", ImageUtil.getLinkIcon());
	private final JPanel o_parentPanel;

	ThreadReminderPanel(Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadReminderTableModel(p_thread), new ComponentCellRenderer(null));
		o_parentPanel = p_parentPanel;

		fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(4, GUIConstants.s_googleStatusColumnWidth);

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove Reminder");
		o_removeLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove(getSelectedObject());
			}
		});

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Set Reminder Active/Inactive");
		o_dismissLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dismiss(getSelectedObject());
			}
		});

		o_linkLabel.setToolTipText("Link Reminder to Google Calendar");
		o_linkLabel.setEnabled(false);
		o_linkLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (o_linkLabel.isEnabled()) {
					linkToGoogle(getSelectedObject(), o_parentPanel);
				}
			}
		});

		ThreadReminderTableModel x_tableModel = (ThreadReminderTableModel) o_table.getModel();
		JRadioButton x_showDueRadioButton = new JRadioButton("Due", x_tableModel.onlyDueReminders());
		JRadioButton x_showAllRadioButton = new JRadioButton("All", !x_tableModel.onlyDueReminders());

		x_showDueRadioButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				x_tableModel.setOnlyDueReminders(x_showDueRadioButton.isSelected());
			}
		});

		ButtonGroup x_group = new ButtonGroup();
		x_group.add(x_showDueRadioButton);
		x_group.add(x_showAllRadioButton);

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(x_showDueRadioButton);
		x_buttonPanel.add(x_showAllRadioButton);
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
		if(p_reminder != null) {
			Item x_item = p_reminder.getItem();

			if(JOptionPane.showConfirmDialog(o_parentPanel, "Remove '" + p_reminder.getText() + "' from '" + x_item.getText() + "' ?", "Delete " + p_reminder.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_item.removeReminder(p_reminder);
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
	public void tableRowClicked(int row, int col, Reminder p_reminder) {
		o_dismissLabel.setEnabled(row != -1);
		o_removeLabel.setEnabled(row != -1);
		o_linkLabel.setEnabled(row != -1);
	}

	@Override
	public void tableRowDoubleClicked(int row, int col, Reminder p_reminder) {
        switch(col) {
			case 0: WindowManager.getInstance().openComponent(p_reminder.getItem()); break;
			default: WindowManager.getInstance().openComponent(p_reminder); break;
        }
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1, null);
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
