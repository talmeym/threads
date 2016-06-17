package gui;

import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ThreadUpdatePanel extends ComponentTablePanel implements Observer {
    private final Thread o_thread;
	private final JButton o_dismissButton = new JButton("Dismiss");
	private final JButton o_removeButton = new JButton("Remove");

	public ThreadUpdatePanel(Thread p_thread) {
        super(new ThreadUpdateTableModel(p_thread), new ComponentCellRenderer(null));
        o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(3, GUIConstants.s_lastUpdatedColumnWidth);

		JButton o_addButton = new JButton("Add");
		o_addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				addUpdate(getSelectedRow());
			}
		});

		o_dismissButton.setEnabled(false);
		o_dismissButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				dismissUpdate(getSelectedRow());
			}
		});

		o_removeButton.setEnabled(false);
		o_removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				removeUpdate(getSelectedRow());
			}
		});

		JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_buttonPanel.add(o_addButton);
		x_buttonPanel.add(o_dismissButton);
		x_buttonPanel.add(o_removeButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	private void addUpdate(int p_index) {
		Thread x_thread;

		if(p_index != -1) {
			x_thread = LookupHelper.getAllActiveUpdates(o_thread).get(p_index).getParentThread();
		} else {
			java.util.List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "What would you like to add to ?", "Choose Destination", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(this, "Please enter new Update text", "Add new Update to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Update");

			if(x_text != null) {
				Item x_item = new Item(x_text);
				x_thread.addItem(x_item);

				if(LookupHelper.getActiveUpdates(x_thread).size() == 2 && JOptionPane.showConfirmDialog(this, MessagingConstants.s_supersedeUpdatesDesc, MessagingConstants.s_supersedeUpdatesTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
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

	private void dismissUpdate(int p_index) {
		if(p_index != -1) {
			Item x_update = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);

			if(JOptionPane.showConfirmDialog(this, "Set '" + x_update.getText() + "' inactive ?", "Dismiss Update ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_update.setActive(false);
			}
		}
	}

	private void removeUpdate(int p_index) {
		if(p_index != -1) {
			Item x_update = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);

			if(JOptionPane.showConfirmDialog(this, "Remove '" + x_update.getText() + "' from '" + x_update.getParentThread().getText() + "' ?", "Remove " + x_update.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_update.getParentThread().removeThreadItem(x_update);
			}
		}
	}

	private void showThread(int p_index) {
        if(p_index != -1) {
            Item x_update = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);
			Thread x_thread = x_update.getParentThread();
			WindowManager.getInstance().openComponent(x_thread);
        }
    }

    private void showItem(int p_index) {
        if(p_index != -1) {
            Item x_update = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_update);
        }
    }

	@Override
	void tableRowClicked(int row, int col) {
		o_dismissButton.setEnabled(row != -1);
		o_removeButton.setEnabled(row != -1);
	}

    void tableRowDoubleClicked(int row, int col) {
        switch(col) {
			case 0: showThread(row); break;
			default: showItem(row); break;
        }
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}
