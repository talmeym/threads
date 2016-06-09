package gui;

import data.*;
import data.Thread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ThreadActionPanel extends ComponentTablePanel implements Observer {
    private final Thread o_thread;
	private JButton o_addButton = new JButton("Add Action");
	private JButton o_dismissButton = new JButton("Dismiss");

	ThreadActionPanel(Thread p_thread) {
        super(new ActionItemTableModel(p_thread), new ActionCellRenderer(p_thread));
        o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(3, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(4, GUIConstants.s_dateStatusColumnWidth);

		o_addButton.setEnabled(false);
		o_addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addAction(getSelectedRow());
			}
		});

		o_dismissButton.setEnabled(false);
		o_dismissButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int x_index = getSelectedRow();

				if(x_index != -1) {
					LookupHelper.getAllActiveActions(o_thread).get(getSelectedRow()).setActive(false);
				}
			}
		});

		JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 0, 0));
		x_buttonPanel.add(o_addButton);
		x_buttonPanel.add(o_dismissButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
    }

	protected void addAction(int p_index) {
		if(p_index != -1) {
			String x_text = JOptionPane.showInputDialog(this, "Enter Action Text");

			if(x_text != null) {
				Item x_threadItem = LookupHelper.getAllActiveActions(o_thread).get(p_index);
				Thread x_thread = x_threadItem.getParentThread();
				Item x_item = new Item(x_text);
				x_thread.addThreadItem(x_item);
				WindowManager.getInstance().openComponent(x_item, 0);
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
		o_addButton.setEnabled(row != -1);
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
