package gui;

import data.*;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ItemPanel extends ComponentTablePanel {
    private final Item o_item;
	private final JButton o_addReminderButton = new JButton("Add Reminder");
	private final JButton o_removeReminderButton = new JButton("Remove Reminder");
    private final JButton o_closeButton = new JButton("Close");

    ItemPanel(Item p_item, ActionListener p_listener) {
        super(new ItemReminderTableModel(p_item),  new ComponentCellRenderer(p_item));
        o_item = p_item;

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);

		o_addReminderButton.setEnabled(o_item.getDueDate() != null);
		o_removeReminderButton.setEnabled(false);
		o_closeButton.addActionListener(p_listener);

		ComponentInfoChangeListener x_listener = new ComponentInfoChangeListener() {
			@Override
			public void componentInfoChanged(boolean saved) {
				o_closeButton.setText(saved ? "Close" : "Cancel");

				if(saved) {
					o_addReminderButton.setEnabled(o_item.getDueDate() != null);
				}
			}
		};

		o_addReminderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(o_item.getDueDate() != null) {
					Reminder x_reminder = new Reminder(o_item);
					o_item.addReminder(x_reminder);
					WindowManager.getInstance().openComponent(x_reminder, 0);
				}
			}
		});

		final JPanel x_this = this;

        o_removeReminderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
				if(o_item.getDueDate() != null) {
					int x_index = getSelectedRow();

					if(x_index != -1) {
						Reminder x_reminder = o_item.getReminder(x_index);

						if(JOptionPane.showConfirmDialog(x_this, "Remove Reminder '" + x_reminder.getText() + "' ?", "Remove Reminder?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
							o_item.removeReminder(x_reminder);
						}
					}
				}
			}
        });

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, x_listener), BorderLayout.NORTH);
        x_panel.add(new DateSuggestionPanel(o_item, x_listener), BorderLayout.CENTER);

        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(o_addReminderButton);
        x_buttonPanel.add(o_removeReminderButton);
        x_buttonPanel.add(o_closeButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

	@Override
	void tableRowClicked(int row, int col) {
		o_removeReminderButton.setEnabled(row != -1);
	}

	void tableRowDoubleClicked(int row, int col) {
		if(row != -1) {
			WindowManager.getInstance().openComponent(o_item.getReminder(row), -1);
		}
	}
}