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
	private final JButton o_removeButton = new JButton("Remove Selected");

    ThreadThreadPanel(Thread p_thread) {
        super(new ThreadThreadTableModel(p_thread), new ComponentCellRenderer(null));
		o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_threadColumnWidth);
		fixColumnWidth(3, GUIConstants.s_statsColumnWidth);
		fixColumnWidth(4, GUIConstants.s_statsColumnWidth);
		fixColumnWidth(5, GUIConstants.s_statsColumnWidth);

		JButton o_addItemButton = new JButton("Add Item");
		o_addItemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addItem(getSelectedRow());
			}
		});

		JButton o_addThreadButton = new JButton("Add Thread");
		o_addThreadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addThread(getSelectedRow());
			}
		});

		o_removeButton.setEnabled(false);
		o_removeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				removeComponent(getSelectedRow());
			}
		});

		JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_buttonPanel.add(o_addItemButton);
		x_buttonPanel.add(o_addThreadButton);
		x_buttonPanel.add(o_removeButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	protected void addItem(int p_index) {
		List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
		Thread x_thread;

		if(p_index != -1) {
			x_thread = x_threads.get(p_index);
		} else {
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose thread", "Add new Item ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(this, "Enter Item Text", "Add new Item to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Item");

			if(x_text != null) {
				Item x_item = new Item(x_text);
				x_thread.addThreadItem(x_item);
				WindowManager.getInstance().openComponent(x_item);
			}
		}
	}

	private void addThread(int p_index) {
		List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
		Thread x_thread;

		if(p_index != -1) {
			x_thread = x_threads.get(p_index);
		} else {
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose thread", "Add new Thread ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_name = (String) JOptionPane.showInputDialog(this, "Enter Thread Name", "Add new Thread to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Thread");

			if(x_name != null) {
				Thread x_newThread = new Thread(x_name);
				x_thread.addThreadItem(x_newThread);
				WindowManager.getInstance().openComponent(x_newThread);
			}
		}
	}

	private void removeComponent(int p_index) {
		if(p_index != -1) {
			ThreadItem x_threadItem = (ThreadItem) LookupHelper.getAllActiveThreads(o_thread).get(p_index);
			Thread x_parent = (Thread) x_threadItem.getParentComponent();

			if(JOptionPane.showConfirmDialog(this, "Remove " + x_threadItem.getType() + " '" + x_threadItem.getText() + "' ?", "Remove " + x_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
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

			if(p_col > 2) {
				ThreadPanel.setTabIndex(p_col - 2);
			}
		}
    }

	@Override
	void tableRowClicked(int row, int col) {
		o_removeButton.setEnabled(row != -1);
	}

    void tableRowDoubleClicked(int row, int col) {
		switch(col) {
			case 1: showParentThread(row); break;
			default: showThread(col, row);
		}
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}