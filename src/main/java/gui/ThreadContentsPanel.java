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
    private final JButton o_openDocFolderButton = new JButton("Open Doc Folder");

	public ThreadContentsPanel(final Thread p_thread)
    {
        super(new ComponentListTableModel(p_thread),
              new CellRenderer(p_thread));
        o_thread = p_thread;
        o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_typeWidth);
        fixColumnWidth(3, GUIConstants.s_statsWidth);
        fixColumnWidth(4, GUIConstants.s_statsWidth);
        fixColumnWidth(5, GUIConstants.s_statsWidth);

		o_removeButton.setEnabled(false);

		JButton o_addItemButton = new JButton("Add Item");
		o_addItemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addItem();
			}
		}
		);

		JButton o_addThreadButton = new JButton("Add Thread");
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

        o_openDocFolderButton.setEnabled(o_thread.getDocFolder() != null);
        o_openDocFolderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                FolderManager.openDocFolder(o_thread);
            }            
        });

		JButton o_setDocFolderButton = new JButton("Set Doc Folder");
		o_setDocFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FolderManager.setDocFolder(o_thread);
				o_openDocFolderButton.setEnabled(o_thread.getDocFolder() != null);
			}
		});
        
        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(o_addItemButton);
        x_buttonPanel.add(o_addThreadButton);
        x_buttonPanel.add(o_removeButton);
        x_buttonPanel.add(o_openDocFolderButton);
        x_buttonPanel.add(o_setDocFolderButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

	protected void addItem() {
		String x_text = JOptionPane.showInputDialog(this, "Enter Item Text");

		if(x_text != null) {
			Item x_item = new Item(x_text);
			o_thread.addThreadItem(x_item);
			WindowManager.getInstance().openComponentWindow(x_item, true, 0);
		}
	}
    
    private void addNewThread() {
        String x_name = JOptionPane.showInputDialog(this, "Enter Thread Name");
        
        if(x_name != null) {
            Thread x_thread = new Thread(x_name);
            o_thread.addThreadItem(x_thread);
            WindowManager.getInstance().openComponentWindow(x_thread, true, 0);
        }
    }
    
    private void removeComponent() {
        int x_index = getSelectedRow();
        
        if(x_index != -1) {
            ThreadItem x_threadItem = o_thread.getThreadItem(x_index);
            String x_message = "Remove " + x_threadItem.getType() + " '" + x_threadItem.getText() + "' ?";
            
            if(JOptionPane.showConfirmDialog(null, x_message) == JOptionPane.YES_OPTION) {
                WindowManager.getInstance().closeComponentWindow(x_threadItem);
                o_thread.removeThreadItem(x_threadItem);
            }
        }
    }
    
    private void showComponent(int p_col, int p_row) {
        if(p_row != -1) {
			WindowManager.getInstance().openComponentWindow(o_thread.getThreadItem(p_row), false, p_col > 2 ? p_col - 2 : 0);
        }
    }

	@Override
	void tableRowClicked(int col, int row) {
		o_removeButton.setEnabled(row != -1);
	}

	void tableRowDoubleClicked(int col, int row) {
		switch (col) {
			default: showComponent(col, row);
		}
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}