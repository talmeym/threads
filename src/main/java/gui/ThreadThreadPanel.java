package gui;

import data.Thread;
import data.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class ThreadThreadPanel extends TablePanel implements Observer {
    private final Thread o_thread;
	private final JButton o_addItemButton = new JButton("Add Item");
	private final JButton o_addThreadButton = new JButton("Add Thread");
	private final JButton o_removeButton = new JButton("Remove Selected");

    ThreadThreadPanel(Thread p_thread) {
        super(new ThreadListTableModel(p_thread), new CellRenderer(null));
		o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_threadColumnWidth);
		fixColumnWidth(3, GUIConstants.s_statsColumnWidth);
		fixColumnWidth(4, GUIConstants.s_statsColumnWidth);
		fixColumnWidth(5, GUIConstants.s_statsColumnWidth);

		o_addItemButton.setEnabled(false);
		o_addThreadButton.setEnabled(false);
		o_removeButton.setEnabled(false);

		o_addItemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addItem();
			}
		});

		o_addThreadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNewThread();
			}
		});

		o_removeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				removeComponent();
			}
		});

		JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_buttonPanel.add(o_addItemButton);
		x_buttonPanel.add(o_addThreadButton);
		x_buttonPanel.add(o_removeButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	protected void addItem() {
		String x_text = JOptionPane.showInputDialog(this, "Enter Item Text");

		if(x_text != null) {
			Item x_item = new Item(x_text);
			Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(getSelectedRow());
			x_thread.addThreadItem(x_item);
			WindowManager.getInstance().openComponent(x_item, true, 0);
		}
	}

	private void addNewThread() {
		String x_name = JOptionPane.showInputDialog(this, "Enter Thread Name");

		if(x_name != null) {
			Thread x_newThread = new Thread(x_name);
			Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(getSelectedRow());
			x_thread.addThreadItem(x_newThread);
			WindowManager.getInstance().openComponent(x_newThread, true, 0);
		}
	}

	private void removeComponent() {
		int x_index = getSelectedRow();

		if(x_index != -1) {
			ThreadItem x_threadItem = (ThreadItem) LookupHelper.getAllActiveThreads(o_thread).get(getSelectedRow());
			Thread x_parent = (Thread) x_threadItem.getParentComponent();

			if(JOptionPane.showConfirmDialog(null, "Remove " + x_threadItem.getType() + " '" + x_threadItem.getText() + "' ?", "Remove " + x_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
				x_parent.removeThreadItem(x_threadItem);
			}
		}
	}

    private void showParentThread(int p_index) {
        if(p_index != -1) {
            Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_thread.getParentComponent(), false, -1);
        }
    }

    private void showThread(int p_col, int p_row) {
        if(p_row != -1) {
			Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_row);
            WindowManager.getInstance().openComponent(x_thread, false, p_col > 2 ? p_col - 2 : -1);
        }
    }

	@Override
	void tableRowClicked(int row, int col) {
		o_addItemButton.setEnabled(row != -1);
		o_addThreadButton.setEnabled(row != -1);
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