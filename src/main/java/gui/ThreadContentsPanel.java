package gui;

import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ThreadContentsPanel extends ComponentTablePanel implements Observer
{
    private final Thread o_thread;
	private final JButton o_dismissButton = new JButton("Dismiss");
	private final JButton o_removeButton = new JButton("Remove");

	public ThreadContentsPanel(final Thread p_thread) {
        super(new ThreadContentsTableModel(p_thread), new ComponentCellRenderer(p_thread));
        o_thread = p_thread;
        o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_typeColumnWidth);
        fixColumnWidth(3, GUIConstants.s_statsColumnWidth);
        fixColumnWidth(4, GUIConstants.s_statsColumnWidth);
        fixColumnWidth(5, GUIConstants.s_statsColumnWidth);

		JButton x_addItemButton = new JButton("Add");
		x_addItemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				add(getSelectedRow());
			}
		});

		o_dismissButton.setEnabled(false);
        o_dismissButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                dismissThread(getSelectedRow());
            }            
        });

		o_removeButton.setEnabled(p_thread.getParentThread() != null);
        o_removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                removeComponent(getSelectedRow());
            }
        });

        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 0, 0));
        x_buttonPanel.add(x_addItemButton);
        x_buttonPanel.add(o_dismissButton);
        x_buttonPanel.add(o_removeButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

	public void add(int p_index) {
		String[] x_options = {"Update", "Thread", "Action", "Cancel"};
		int x_selection = JOptionPane.showOptionDialog(this, "What would you like to add ?", "Choose Addition", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_options, x_options[2]);

		switch(x_selection) {
			case 0:
			case 2: addItem(p_index, x_options[x_selection]); break;
			case 1: addThread(p_index);
		}
	}

	protected void addItem(int p_index, String p_type) {
		Thread x_thread;

		if(p_index != -1 && o_thread.getThreadItem(p_index) instanceof Thread) {
			x_thread = (Thread) o_thread.getThreadItem(p_index);
		} else {
			o_table.clearSelection();
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "What would you like to add it to ?", "Choose Destination", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(this, "Please enter new " + p_type + " text", "Add new " + p_type + " to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Item");

			if(x_text != null) {
				Item x_item = new Item(x_text);
				x_thread.addThreadItem(x_item);
				WindowManager.getInstance().openComponent(x_item);
			}
		}
	}

	private void addThread(int p_index) {
		Thread x_thread;

		if(p_index != -1 && o_thread.getThreadItem(p_index) instanceof Thread) {
			x_thread = (Thread) o_thread.getThreadItem(p_index);
		} else {
			o_table.clearSelection();
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
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
			ThreadItem x_threadItem = o_thread.getThreadItem(p_index);

			if(JOptionPane.showConfirmDialog(this, "Set '" + x_threadItem.getText() + "' inactive ?", "Dismiss Thread ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_threadItem.setActive(false);
			}
		}
	}

	private void removeComponent(int p_index) {
        if(p_index != -1) {
            ThreadItem x_threadItem = o_thread.getThreadItem(p_index);

			if(JOptionPane.showConfirmDialog(this, "Remove '" + x_threadItem.getText() + "' from '" + x_threadItem.getParentThread().getText() + "' ?", "Remove " + x_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
                o_thread.removeThreadItem(x_threadItem);
            }
        } else {
			if(JOptionPane.showConfirmDialog(this, "Remove '" + o_thread.getText() + "' from '" + o_thread.getParentThread().getText() + "' ?", "Remove " + o_thread.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				o_thread.getParentThread().removeThreadItem(o_thread);
			}
		}
    }

	@Override
	void tableRowClicked(int p_row, int p_col) {
		o_dismissButton.setEnabled(p_row != -1);
		o_removeButton.setEnabled(p_row != -1 || o_thread.getParentThread() != null);
	}

	void tableRowDoubleClicked(int p_row, int p_col) {
		if(p_row != -1) {
			ThreadItem x_threadItem = o_thread.getThreadItem(p_row);
			WindowManager.getInstance().openComponent(x_threadItem);

			if(x_threadItem instanceof Thread && p_col > 2) {
				ThreadPanel.setTabIndex(p_col - 2);
			}
		}
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}