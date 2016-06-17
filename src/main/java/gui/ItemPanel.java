package gui;

import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ItemPanel extends ComponentTablePanel {
    private final Item o_item;
	private final JButton o_removeReminderButton = new JButton("Remove Reminder");

	ItemPanel(Item p_item) {
        super(new ItemReminderTableModel(p_item),  new ComponentCellRenderer(p_item));
        o_item = p_item;

        fixColumnWidth(1, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(2, GUIConstants.s_dateStatusColumnWidth);

		final JButton x_addReminderButton = new JButton("Add Reminder");
		x_addReminderButton.setEnabled(o_item.getDueDate() != null);
		o_removeReminderButton.setEnabled(false);

		final JPanel x_this = this;
		final JButton x_closeButton = new JButton("Close");
		x_closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				WindowManager.getInstance().closeComponent(o_item);
				Thread x_thread = o_item.getParentThread();

				if (o_item.getDueDate() == null && LookupHelper.getActiveUpdates(x_thread).size() == 2 && JOptionPane.showConfirmDialog(x_this, MessagingConstants.s_supersedeUpdatesDesc, MessagingConstants.s_supersedeUpdatesTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
					for (int i = 0; i < x_thread.getThreadItemCount(); i++) {
						ThreadItem x_groupItem = x_thread.getThreadItem(i);

						if (x_groupItem instanceof Item) {
							Item x_otherItem = (Item) x_groupItem;

							if (x_otherItem != o_item && x_otherItem.getDueDate() == null && x_otherItem.isActive()) {
								x_otherItem.setActive(false);
							}
						}
					}
				}
			}
		});

		JButton x_removeButton = new JButton("Remove");
		x_removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				Thread x_thread = o_item.getParentThread();

				if (JOptionPane.showConfirmDialog(x_this, "Remove '" + o_item.getText() + "' from '" + x_thread.getText() + "' ?", "Remove " + o_item.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
					x_thread.removeThreadItem(o_item);
					WindowManager.getInstance().closeComponent(o_item);
					WindowManager.getInstance().openComponent(x_thread);
				}
			}
		});

		ComponentInfoChangeListener x_listener = new ComponentInfoChangeListener() {
			@Override
			public void componentInfoChanged(boolean saved) {
				x_closeButton.setText(saved ? "Close" : "Cancel");

				if(saved) {
					x_addReminderButton.setEnabled(o_item.getDueDate() != null);
				}
			}
		};

		x_addReminderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (o_item.getDueDate() != null) {
					Reminder x_reminder = new Reminder(o_item);
					o_item.addReminder(x_reminder);
					WindowManager.getInstance().openComponent(x_reminder);
				}
			}
		});

        o_removeReminderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
				if(o_item.getDueDate() != null) {
					int x_index = getSelectedRow();

					if(x_index != -1) {
						Reminder x_reminder = o_item.getReminder(x_index);

						if(JOptionPane.showConfirmDialog(x_this, "Remove '" + x_reminder.getText() + "' from '" + o_item.getText() + "' ?", "Remove Reminder?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
							o_item.removeReminder(x_reminder);
						}
					}
				}
			}
        });

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, x_listener), BorderLayout.NORTH);
        x_panel.add(new DateSuggestionPanel(o_item, this, x_listener), BorderLayout.CENTER);

        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(x_addReminderButton);
        x_buttonPanel.add(o_removeReminderButton);
        x_buttonPanel.add(x_closeButton);
        x_buttonPanel.add(x_removeButton);
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
			WindowManager.getInstance().openComponent(o_item.getReminder(row));
		}
	}
}