package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ThreadReminderPanel extends ComponentTablePanel implements Observer {
    private final Thread o_thread;
    private final JLabel o_dismissLabel = new JLabel(ImageUtil.getTickIcon());

    ThreadReminderPanel(Thread p_thread) {
        super(new ThreadReminderTableModel(p_thread), new ComponentCellRenderer(null));
		o_thread = p_thread;

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(4, GUIConstants.s_googleStatusColumnWidth);

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dismissReminder(getSelectedRow());
			}
		});
        
        JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(o_dismissLabel, BorderLayout.CENTER);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_buttonPanel, BorderLayout.SOUTH);

		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
    }

	private void dismissReminder(int p_index) {
		if(o_dismissLabel.isEnabled()) {
			Reminder x_reminder = LookupHelper.getAllDueReminders(o_thread).get(p_index);
			boolean x_active = !x_reminder.isActive();

			if(JOptionPane.showConfirmDialog(this, "Set '" + x_reminder.getText() + "' " + (x_active ? "Active" : "Inactive") + " ?", "Set " + (x_active ? "Active" : "Inactive") + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_reminder.setActive(false);
			}
		}
	}

	private void showReminder(int p_index) {
        if(p_index != -1) {
            Reminder x_reminder = LookupHelper.getAllDueReminders(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_reminder);
        }
    }

    private void showItem(int p_index) {
        if(p_index != -1) {
            Reminder x_reminder = LookupHelper.getAllDueReminders(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_reminder.getItem());
        }
    }

	@Override
	public void tableRowClicked(int row, int col) {
		o_dismissLabel.setEnabled(row != -1);
	}

	public void tableRowDoubleClicked(int row, int col) {
        switch(col) {
			case 0: showItem(row); break;
			default: showReminder(row); break;
        }
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}

	@Override
	public void timeUpdate() {
		((ComponentTableModel) o_table.getModel()).fireTableDataChanged();
		tableRowClicked(-1, -1);
	}

	@Override
	public void googleSynced() {
		((ComponentTableModel) o_table.getModel()).fireTableDataChanged();
		tableRowClicked(-1, -1);
	}
}
