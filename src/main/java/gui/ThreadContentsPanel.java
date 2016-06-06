package gui;

import data.*;
import data.Thread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ThreadContentsPanel extends TablePanel implements Observer
{
    private final Thread o_thread;
	private final JButton o_removeButton = new JButton("Remove Selected");

	public ThreadContentsPanel(final Thread p_thread) {
        super(new ComponentListTableModel(p_thread), new CellRenderer(p_thread));
        o_thread = p_thread;
        o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_typeColumnWidth);
        fixColumnWidth(3, GUIConstants.s_statsColumnWidth);
        fixColumnWidth(4, GUIConstants.s_statsColumnWidth);
        fixColumnWidth(5, GUIConstants.s_statsColumnWidth);

		JButton o_addItemButton = new JButton("Add Item");
		o_addItemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addItem();
			}
		});

		JButton o_addThreadButton = new JButton("Add Thread");
		o_addThreadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNewThread();
			}
		});

		o_removeButton.setEnabled(false);
        o_removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                removeComponent();
            }            
        });

        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 0, 0));
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
			o_thread.addThreadItem(x_item);
			WindowManager.getInstance().openComponent(x_item, true, 0);
		}
	}
    
    private void addNewThread() {
        String x_name = JOptionPane.showInputDialog(this, "Enter Thread Name");
        
        if(x_name != null) {
            Thread x_thread = new Thread(x_name);
            o_thread.addThreadItem(x_thread);
            WindowManager.getInstance().openComponent(x_thread, true, 0);
        }
    }
    
    private void removeComponent() {
        int x_index = getSelectedRow();
        
        if(x_index != -1) {
            ThreadItem x_threadItem = o_thread.getThreadItem(x_index);

			if(JOptionPane.showConfirmDialog(null, "Remove " + x_threadItem.getType() + " '" + x_threadItem.getText() + "' ?", "Remove " + x_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
                o_thread.removeThreadItem(x_threadItem);
            }
        }
    }

	@Override
	void tableRowClicked(int row, int col) {
		o_removeButton.setEnabled(row != -1);
	}

	void tableRowDoubleClicked(int row, int col) {
		if(row != -1) {
			WindowManager.getInstance().openComponent(o_thread.getThreadItem(row), false, col > 2 ? col - 2 : 0);
		}
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}