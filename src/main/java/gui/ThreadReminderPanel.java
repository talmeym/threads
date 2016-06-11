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
    private final JButton o_dismissButton = new JButton("Dismiss Selected");
    
    ThreadReminderPanel(Thread p_thread) {
        super(new ThreadReminderTableModel(p_thread), new ComponentCellRenderer(null));
		o_thread = p_thread;

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(3, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(4, GUIConstants.s_dateStatusColumnWidth);

		o_dismissButton.setEnabled(false);
		o_dismissButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				dismissReminder(getSelectedRow());
			}
        });
        
        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_dismissButton, BorderLayout.CENTER);
        x_panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_panel, BorderLayout.SOUTH);

		TimeUpdater.getInstance().addTimeUpdateListener(new TimeUpdateListener() {
			@Override
			public void timeUpdate() {
				((ComponentTableModel)o_table.getModel()).fireTableDataChanged();
				tableRowClicked(-1, -1);
			}
		});
    }

	private void dismissReminder(int p_index) {
		if(p_index != -1) {
			Reminder x_reminder = LookupHelper.getAllDueReminders(o_thread).get(p_index);

			if(JOptionPane.showConfirmDialog(this, "Set reminder inactive ?", "Dismiss Reminder ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
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
	void tableRowClicked(int row, int col) {
		o_dismissButton.setEnabled(row != -1);
	}

	void tableRowDoubleClicked(int row, int col) {
        switch(col) {
			case 1: showItem(row); break;
			default: showReminder(row); break;
        }
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}
