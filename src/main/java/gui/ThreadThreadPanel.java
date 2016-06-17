package gui;

import data.Thread;
import data.*;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class ThreadThreadPanel extends ComponentTablePanel implements Observer {
    private final Thread o_thread;
	private final JButton o_dismissButton = new JButton("Dismiss");
	private final JButton o_removeButton = new JButton("Remove");

    ThreadThreadPanel(Thread p_thread) {
        super(new ThreadThreadTableModel(p_thread), new ComponentCellRenderer(null));
		o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
		fixColumnWidth(2, GUIConstants.s_statsColumnWidth);
		fixColumnWidth(3, GUIConstants.s_statsColumnWidth);
		fixColumnWidth(4, GUIConstants.s_statsColumnWidth);

		JButton o_addButton = new JButton("Add");
		o_addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addThread(getSelectedRow());
			}
		});

		o_dismissButton.setEnabled(false);
		o_dismissButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dismissThread(getSelectedRow());
			}
		});

		o_removeButton.setEnabled(false);
		o_removeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				removeComponent(getSelectedRow());
			}
		});

		JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_buttonPanel.add(o_addButton);
		x_buttonPanel.add(o_dismissButton);
		x_buttonPanel.add(o_removeButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	private void addThread(int p_index) {
		List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
		Thread x_thread;

		if(p_index != -1) {
			x_thread = x_threads.get(p_index);
		} else {
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "What would you like to add to ?", "Choose Destination", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_name = (String) JOptionPane.showInputDialog(this, "Please enter new Thread name", "Add new Thread to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Thread");

			if(x_name != null) {
				Thread x_newThread = new Thread(x_name);
				x_thread.addThreadItem(x_newThread);
				WindowManager.getInstance().openComponent(x_newThread);
			}
		}
	}

	private void dismissThread(int p_index) {
		if(p_index != -1) {
			Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_index);

			if(JOptionPane.showConfirmDialog(this, "Set '" + x_thread.getText() + "' inactive ?", "Dismiss Thread ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_thread.setActive(false);
			}
		}
	}

	private void removeComponent(int p_index) {
		if(p_index != -1) {
			ThreadItem x_threadItem = (ThreadItem) LookupHelper.getAllActiveThreads(o_thread).get(p_index);
			Thread x_parent = (Thread) x_threadItem.getParentComponent();

			if(JOptionPane.showConfirmDialog(this, "Remove '" + x_threadItem.getText() + "' from '" + x_threadItem.getParentThread().getText() + "' ?", "Remove " + x_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_parent.removeThreadItem(x_threadItem);
			}
		}
	}

    private void showParentThread(int p_index) {
        if(p_index != -1) {
            Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_thread.getParentComponent());
        }
    }

    private void showThread(int p_col, int p_row) {
        if(p_row != -1) {
			Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_row);
			WindowManager.getInstance().openComponent(x_thread);

			if(p_col > 1) {
				ThreadPanel.setTabIndex(p_col - 1);
			}
		}
    }

	@Override
	void tableRowClicked(int p_row, int p_col) {
		o_dismissButton.setEnabled(p_row != -1);
		o_removeButton.setEnabled(p_row != -1);
	}

    void tableRowDoubleClicked(int p_row, int p_col) {
		switch(p_col) {
			case 0: showParentThread(p_row); break;
			default: showThread(p_col, p_row);
		}
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}