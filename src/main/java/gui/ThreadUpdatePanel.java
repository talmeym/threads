package gui;

import data.*;
import data.Thread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ThreadUpdatePanel extends TablePanel {
    private final Thread o_thread;
	private final JButton o_updateButton = new JButton("Update");

	public ThreadUpdatePanel(Thread p_thread) {
        super(new UpdateItemTableModel(p_thread), new CellRenderer(null));
        o_thread = p_thread;
        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);

		o_updateButton.setEnabled(false);
		o_updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				addUpdate();
			}
		});

		JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_buttonPanel.add(o_updateButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	private void addUpdate() {
		int x_index = getSelectedRow();

		if(x_index != -1) {
			Thread x_thread = LookupHelper.getAllActiveUpdates(o_thread).get(x_index).getParentThread();
			String x_text = JOptionPane.showInputDialog(this, "Enter Update Text");

			if(x_text != null) {
				Item x_item = new Item(x_text);
				x_thread.addThreadItem(x_item);
				WindowManager.getInstance().openComponent(x_item, true, 0);
			}
		}
	}

    private void showThread(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);
			Thread x_thread = x_threadItem.getParentThread();

			if(x_thread != o_thread) {
				WindowManager.getInstance().openComponent(x_thread, false, 0);
			}
        }
    }

    private void showItem(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_threadItem, false, 0);
        }
    }

	@Override
	void tableRowClicked(int col, int row) {
		o_updateButton.setEnabled(row != -1);
	}

    void tableRowDoubleClicked(int col, int row) {
        switch(col) {
			case 1: showThread(row); break;
			default: showItem(row); break;
        }
    }
}
