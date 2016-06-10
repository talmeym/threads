package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ThreadActionPanel extends ComponentTablePanel implements Observer {
    private final Thread o_thread;
	private final JButton o_dismissButton = new JButton("Dismiss Selected");

	ThreadActionPanel(Thread p_thread) {
        super(new ThreadActionTableModel(p_thread), new ThreadActionCellRenderer(p_thread));
        o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(3, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(4, GUIConstants.s_dateStatusColumnWidth);

		JButton o_addButton = new JButton("Add Action");
		o_addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addActionSelected(getSelectedRow());
			}
		});

		o_dismissButton.setEnabled(false);
		o_dismissButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dismissAction(getSelectedRow());
			}
		});

		JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 0, 0));
		x_buttonPanel.add(o_addButton);
		x_buttonPanel.add(o_dismissButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);

		TimeUpdater.getInstance().addTimeUpdateListener(new TimeUpdateListener() {
			@Override
			public void timeUpdate() {
				((ComponentTableModel)o_table.getModel()).fireTableDataChanged();
				tableRowClicked(-1, -1);
			}
		});
    }

	protected void addActionSelected(int p_index) {
		Thread x_thread;

		if(p_index != -1) {
			x_thread = LookupHelper.getAllActiveActions(o_thread).get(p_index).getParentThread();
		} else {
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose thread", "Add new Action ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(this, "Enter action Text", "Add new Action to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Action");

			if(x_text != null) {
				Item x_item = new Item(x_text);
				x_thread.addThreadItem(x_item);
				WindowManager.getInstance().openComponent(x_item, 0);
			}
		}
	}

	private void dismissAction(int p_index) {
		if(p_index != -1) {
			Item x_action = LookupHelper.getAllActiveActions(o_thread).get(p_index);

			if(JOptionPane.showConfirmDialog(this, "Set action inactive ?", "Dismiss Action ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_action.setActive(false);
			}
		}
	}

	private void showThread(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveActions(o_thread).get(p_index);
			Thread x_thread = x_threadItem.getParentThread();

			if(x_thread != o_thread) {
				WindowManager.getInstance().openComponent(x_thread, -1);
			}
        }
    }

    private void showItem(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveActions(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_threadItem, -1);
        }
    }

	@Override
	void tableRowClicked(int row, int col) {
		o_dismissButton.setEnabled(row != -1);
	}

	void tableRowDoubleClicked(int row, int col) {
		switch(col) {
			case 1: showThread(row); break;
			default: showItem(row);
        }
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}
