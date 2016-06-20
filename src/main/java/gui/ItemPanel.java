package gui;

import data.*;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ItemPanel extends ComponentTablePanel {
    private final Item o_item;
	private final JLabel o_removeReminderLabel = new JLabel(ImageUtil.getMinusIcon());

	ItemPanel(Item p_item) {
        super(new ItemReminderTableModel(p_item),  new ComponentCellRenderer(p_item));
        o_item = p_item;

        fixColumnWidth(1, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(2, GUIConstants.s_dateStatusColumnWidth);

		final JLabel x_addReminderLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addReminderLabel.setEnabled(o_item.getDueDate() != null);
		o_removeReminderLabel.setEnabled(false);

		final JPanel x_this = this;
//		final JButton x_closeButton = new JButton("Close");
//		x_closeButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent actionEvent) {
//				WindowManager.getInstance().closeComponent(o_item);
//				Thread x_thread = o_item.getParentThread();
//
//				if (o_item.getDueDate() == null && LookupHelper.getActiveUpdates(x_thread).size() == 2 && JOptionPane.showConfirmDialog(x_this, MessagingConstants.s_supersedeUpdatesDesc, MessagingConstants.s_supersedeUpdatesTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
//					for (int i = 0; i < x_thread.getThreadItemCount(); i++) {
//						ThreadItem x_groupItem = x_thread.getThreadItem(i);
//
//						if (x_groupItem instanceof Item) {
//							Item x_otherItem = (Item) x_groupItem;
//
//							if (x_otherItem != o_item && x_otherItem.getDueDate() == null && x_otherItem.isActive()) {
//								x_otherItem.setActive(false);
//							}
//						}
//					}
//				}
//			}
//		});

		ComponentInfoChangeListener x_listener = new ComponentInfoChangeListener() {
			@Override
			public void componentInfoChanged(boolean saved) {
				if(saved) {
					x_addReminderLabel.setEnabled(o_item.getDueDate() != null);
				}
			}
		};

		x_addReminderLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (o_item.getDueDate() != null) {
					String x_text = (String) JOptionPane.showInputDialog(x_this, "Enter new Reminder text:", "Add new Reminder to '" + o_item + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Reminder");

					if(x_text != null) {
						Reminder x_reminder = new Reminder(o_item);
						x_reminder.setText(x_text);
						o_item.addReminder(x_reminder);
						WindowManager.getInstance().openComponent(x_reminder);
					}
				}
			}
		});

        o_removeReminderLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (o_item.getDueDate() != null) {
					int x_index = getSelectedRow();

					if (x_index != -1) {
						Reminder x_reminder = o_item.getReminder(x_index);

						if (JOptionPane.showConfirmDialog(x_this, "Remove '" + x_reminder.getText() + "' from '" + o_item.getText() + "' ?", "Remove Reminder?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
							o_item.removeReminder(x_reminder);
							o_removeReminderLabel.setEnabled(false);
						}
					}
				}
			}
		});

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, this, x_listener), BorderLayout.CENTER);
        x_panel.add(new DateSuggestionPanel(o_item, this, x_listener), BorderLayout.SOUTH);

        JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        x_buttonPanel.add(x_addReminderLabel);
        x_buttonPanel.add(o_removeReminderLabel);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

	@Override
	void tableRowClicked(int row, int col) {
		o_removeReminderLabel.setEnabled(row != -1);
	}

	void tableRowDoubleClicked(int row, int col) {
		if(row != -1) {
			WindowManager.getInstance().openComponent(o_item.getReminder(row));
		}
	}
}