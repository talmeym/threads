package gui;

import data.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ItemPanel extends TablePanel implements ComponentInfoChangeListener {
    private final Item o_item;
	private final JButton o_removeReminderButton = new JButton("Remove Selected");
    private final JButton o_closeButton = new JButton("Close");

    ItemPanel(Item p_item, boolean p_new, final ActionListener listener) {
        super(new ItemReminderTableModel(p_item),  new CellRenderer(p_item));
        o_item = p_item;

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);

		ComponentInfoPanel o_compInfoPanel = new ComponentInfoPanel(p_item, p_new, this, listener);

		JButton o_addReminderButton = new JButton("Add Reminder");
		o_addReminderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addReminder();
			}
		});

        o_removeReminderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                removeReminder();
            }            
        });

		o_removeReminderButton.setEnabled(false);
        o_closeButton.addActionListener(listener);

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, BorderLayout.NORTH);
        x_panel.add(new DateSuggestionPanel(o_item, this), BorderLayout.CENTER);

        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(o_addReminderButton);
        x_buttonPanel.add(o_removeReminderButton);
        x_buttonPanel.add(o_closeButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

	@Override
	public void componentInfoChanged(boolean saved) {
		o_closeButton.setText(saved ? "Close" : "Cancel");
	}

	private void addReminder() {
        if(o_item.getDueDate() != null) {
            Reminder x_reminder = new Reminder(o_item);
            o_item.addReminder(x_reminder);
            WindowManager.getInstance().openComponent(x_reminder, true, 0);
        }
    }
    
    private void removeReminder() {
        if(o_item.getDueDate() != null) {
            int x_index = getSelectedRow();
            
            if(x_index != -1) {
                Reminder x_reminder = o_item.getReminder(x_index);
                
                if(JOptionPane.showConfirmDialog(null, "Remove Reminder '" + x_reminder.getText() + "' ?", "Remove Reminder?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
                    o_item.removeReminder(x_reminder);
                }
            }
        }
    }  
    
    private void showReminder(int p_index) {
        if(p_index != -1) {
            WindowManager.getInstance().openComponent(o_item.getReminder(p_index), false, 0);
        }
    }

	@Override
	void tableRowClicked(int col, int row) {
		o_removeReminderButton.setEnabled(row != -1);
	}

	void tableRowDoubleClicked(int col, int row) {
		switch(col) {
			default: showReminder(row);
		}
    }
}