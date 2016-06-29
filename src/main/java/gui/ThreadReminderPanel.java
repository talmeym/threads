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
	private final JLabel o_removeLabel = new JLabel(ImageUtil.getMinusIcon());
	private final JLabel o_linkLabel = new JLabel(ImageUtil.getLinkIcon());

    ThreadReminderPanel(Thread p_thread) {
        super(new ThreadReminderTableModel(p_thread), new ComponentCellRenderer(null));
		o_thread = p_thread;

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(4, GUIConstants.s_googleStatusColumnWidth);

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove Reminder");
		o_removeLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				removeSomething(getSelectedRow());
			}
		});

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Make Reminder Active/Inactive");
		o_dismissLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				dismissReminder(getSelectedRow());
			}
		});

		o_linkLabel.setToolTipText("Link Reminder to Google Calendar");
		o_linkLabel.setEnabled(false);
		o_linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				linkToGoogle(getSelectedRow());
			}
		});
        
        JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(o_dismissLabel, BorderLayout.CENTER);
		x_buttonPanel.add(o_removeLabel, BorderLayout.CENTER);
		x_buttonPanel.add(o_linkLabel, BorderLayout.CENTER);
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

	private void removeSomething(int p_index) {
		if(p_index != -1) {
			Reminder x_reminder = LookupHelper.getAllDueReminders(o_thread).get(p_index);
			Item x_item = x_reminder.getItem();

			if(JOptionPane.showConfirmDialog(this, "Remove '" + x_reminder.getText() + "' from '" + x_item.getText() + "' ?", "Delete " + x_reminder.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_item.removeReminder(x_reminder);
			}
		}
	}

	private void linkToGoogle(int p_index) {
		final JPanel x_this = this;

		if (o_linkLabel.isEnabled()) {
			final Reminder x_reminder = LookupHelper.getAllDueReminders(o_thread).get(p_index);

			if (JOptionPane.showConfirmDialog(x_this, "Link '" + x_reminder.getText() + "' to Google Calendar ?", "Link to Google ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
				GoogleLinkTask x_task = new GoogleLinkTask(x_reminder, new GoogleProgressWindow(x_this), new ProgressAdapter() {
					@Override
					public void finished() {
						JOptionPane.showMessageDialog(x_this, "'" + x_reminder.getText() + "' was linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
					}
				});

				x_task.execute();
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
		o_removeLabel.setEnabled(row != -1);
		o_linkLabel.setEnabled(row != -1);
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
