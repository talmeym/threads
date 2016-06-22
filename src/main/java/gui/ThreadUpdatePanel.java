package gui;

import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ThreadUpdatePanel extends ComponentTablePanel implements Observer {
    private final Thread o_thread;
	private final JLabel o_dismissLabel = new JLabel(ImageUtil.getTickIcon());
	private final JLabel o_removeLabel = new JLabel(ImageUtil.getMinusIcon());
	private final JLabel o_moveLabel = new JLabel(ImageUtil.getMoveIcon());

	public ThreadUpdatePanel(Thread p_thread) {
        super(new ThreadUpdateTableModel(p_thread), new ComponentCellRenderer(null));
        o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(3, GUIConstants.s_lastUpdatedColumnWidth);

		JLabel x_addLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addLabel.setToolTipText("Add Update");
		x_addLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				addSomething(getSelectedRow());
			}
		});

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Make Active/Inactive");
		o_dismissLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dismissSomething(getSelectedRow());
			}
		});

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove");
		o_removeLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				removeSomething(getSelectedRow());
			}
		});

		o_moveLabel.setEnabled(false);
		o_moveLabel.setToolTipText("Move");
		o_moveLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				moveSomething(getSelectedRow());
			}
		});

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(x_addLabel);
		x_buttonPanel.add(o_removeLabel);
		x_buttonPanel.add(o_dismissLabel);
		x_buttonPanel.add(o_moveLabel);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	private void addSomething(int p_index) {
		Thread x_thread;

		if(p_index != -1) {
			x_thread = LookupHelper.getAllActiveUpdates(o_thread).get(p_index).getParentThread();
		} else {
			java.util.List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose a Thread to add it to:", "Add an Update ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(this, "Enter new Update text:", "Add new Update to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Update");

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

	private void dismissSomething(int p_index) {
		if(p_index != -1) {
			Item x_update = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);

			if(JOptionPane.showConfirmDialog(this, "Set '" + x_update.getText() + "' Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_update.setActive(false);
			}
		}
	}

	private void removeSomething(int p_index) {
		if(p_index != -1) {
			Item x_update = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);

			if(JOptionPane.showConfirmDialog(this, "Remove '" + x_update.getText() + "' from '" + x_update.getParentThread().getText() + "' ?", "Remove " + x_update.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_update.getParentThread().removeThreadItem(x_update);
			}
		}
	}

	private void moveSomething(int p_index) {
		if(p_index != -1) {
			Item x_item = LookupHelper.getAllActiveUpdates(o_thread).get(p_index);
			Thread x_thread = null;

			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);
			x_threads.remove(x_item.getParentThread());

			if(x_threads.size() > 0) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose a Thread to move it to:", "Move '" + x_item + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				JOptionPane.showMessageDialog(this, "This Thread has no child Threads to move this item to. Try moving it from further up the tree.", "Nowhere to go", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon());
			}

			if(x_thread != null) {
				x_item.getParentThread().removeThreadItem(x_item);
				x_thread.addThreadItem(x_item);
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
		o_removeLabel.setEnabled(row != -1);
		o_dismissLabel.setEnabled(row != -1);
		o_moveLabel.setEnabled(row != -1);
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
