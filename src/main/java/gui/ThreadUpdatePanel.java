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
				x_thread.addItem(x_item);

				if(LookupHelper.getActiveUpdates(x_thread).size() == 2 && JOptionPane.showConfirmDialog(null, MessagingConstants.s_supersedeUpdatesDesc, MessagingConstants.s_supersedeUpdatesTitle, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					for(int i = 0; i < x_thread.getThreadItemCount(); i++) {
						ThreadItem x_groupItem = x_thread.getThreadItem(i);

						if(x_groupItem instanceof Item)  {
							Item x_otherItem = (Item) x_groupItem;

							if(x_otherItem != x_item && x_otherItem.getDueDate() == null && x_otherItem.isActive()) {
								x_otherItem.setActive(false);
							}
						}
					}
				}
			}
		}
	}

    private void showThread(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);
			Thread x_thread = x_threadItem.getParentThread();
			WindowManager.getInstance().openComponent(x_thread, false, 0);
        }
    }

    private void showItem(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_threadItem, false, 0);
        }
    }

	@Override
	void tableRowClicked(int row, int col) {
		o_updateButton.setEnabled(row != -1);
	}

    void tableRowDoubleClicked(int row, int col) {
        switch(col) {
			case 1: showThread(row); break;
			default: showItem(row); break;
        }
    }
}
