package gui;

import data.*;
import data.Thread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ThreadActionPanel extends TablePanel implements Observer {
    private final Thread o_thread;
	private JButton o_dismissButton = new JButton("Dismiss");

	ThreadActionPanel(Thread p_thread) {
        super(new ActionItemTableModel(p_thread), new ActionCellRenderer(p_thread));
        o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(3, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(4, GUIConstants.s_dateStatusColumnWidth);

		o_dismissButton.setEnabled(false);
		o_dismissButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int x_index = getSelectedRow();

				if(x_index != -1) {
					LookupHelper.getAllActiveActions(o_thread).get(x_index).setActive(false);
				}
			}
		});

		JPanel x_panel = new JPanel(new BorderLayout());
		x_panel.add(o_dismissButton, BorderLayout.CENTER);
		x_panel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));

		add(x_panel, BorderLayout.SOUTH);
    }
    
    private void showThread(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveActions(o_thread).get(p_index);
			Thread x_thread = x_threadItem.getParentThread();

			if(x_thread != o_thread) {
				WindowManager.getInstance().openComponent(x_thread, false, 0);
			}
        }
    }

    private void showItem(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveActions(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_threadItem, false, 0);
        }
    }

	@Override
	void tableRowClicked(int col, int row) {
		o_dismissButton.setEnabled(row != -1);
	}

	void tableRowDoubleClicked(int col, int row) {
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
