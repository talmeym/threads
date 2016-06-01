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
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_threadWidth);
		fixColumnWidth(3, GUIConstants.s_statsWidth);
		fixColumnWidth(4, GUIConstants.s_statsWidth);
		fixColumnWidth(5, GUIConstants.s_statsWidth);

		JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_buttonPanel.add(o_addItemButton);
		x_buttonPanel.add(o_addThreadButton);
		x_buttonPanel.add(o_removeButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);

		o_addItemButton.setEnabled(false);
		o_addThreadButton.setEnabled(false);
		o_removeButton.setEnabled(false);

		o_addItemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addItem();
			}
		}
		);

		o_addThreadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNewThread();
			}
		}
		);

		o_removeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				removeComponent();
			}
		}
		);
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
			String x_message = "Remove " + x_threadItem.getType() + " '" + x_threadItem.getText() + "' ?";

			if(JOptionPane.showConfirmDialog(null, x_message) == JOptionPane.YES_OPTION) {
				x_parent.removeThreadItem(x_threadItem);
			}
		}
	}

    private void showParentThread(int p_index) {
        if(p_index != -1) {
            Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_thread.getParentComponent(), false, 0);
        }
    }

    private void showThread(int p_col, int p_row) {
        if(p_row != -1) {
			int x_tab = p_col > 2 ? p_col - 2 : 0;
            Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_row);
            WindowManager.getInstance().openComponent(x_thread, false, x_tab);
        }
    }

	@Override
	void tableRowClicked(int col, int row) {
		o_addItemButton.setEnabled(row != -1);
		o_addThreadButton.setEnabled(row != -1);
		o_removeButton.setEnabled(row != -1);
	}

    void tableRowDoubleClicked(int col, int row) {
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