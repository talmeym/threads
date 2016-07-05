package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import static util.GuiUtil.setUpButtonLabel;

public class ThreadContentsPanel extends ComponentTablePanel<Thread, ThreadItem> implements Observer
{
    private final Thread o_thread;
	private final JLabel o_removeLabel = new JLabel(ImageUtil.getMinusIcon());
	private final JLabel o_dismissLabel = new JLabel(ImageUtil.getTickIcon());
	private final JLabel o_moveLabel = new JLabel(ImageUtil.getMoveIcon());
	private final JLabel o_linkLabel = new JLabel(ImageUtil.getLinkIcon());

	public ThreadContentsPanel(final Thread p_thread) {
        super(new ThreadContentsTableModel(p_thread), new ComponentCellRenderer(p_thread));
        o_thread = p_thread;
        o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_typeColumnWidth);
        fixColumnWidth(3, GUIConstants.s_threadItemStatusColumnWidth);
        fixColumnWidth(4, GUIConstants.s_googleStatusColumnWidth);

		JLabel x_addItemLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addItemLabel.setToolTipText("Add Item");
		x_addItemLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				add(getSelectedObject());
			}
		});

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove Item");
		o_removeLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				remove(getSelectedObject());
			}
		});

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Make Item Active/Inactive");
        o_dismissLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				dismiss(getSelectedObject());
			}
		});

		o_moveLabel.setToolTipText("Move Item");
		o_moveLabel.setEnabled(false);
		o_moveLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				move(getSelectedObject());
			}
		});

		o_linkLabel.setToolTipText("Link Item to Google Calendar");
		o_linkLabel.setEnabled(false);
		o_linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				link(getSelectedObject());
			}
		});

        JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        x_buttonPanel.add(setUpButtonLabel(x_addItemLabel));
        x_buttonPanel.add(setUpButtonLabel(o_removeLabel));
        x_buttonPanel.add(setUpButtonLabel(o_dismissLabel));
        x_buttonPanel.add(setUpButtonLabel(o_moveLabel));
        x_buttonPanel.add(setUpButtonLabel(o_linkLabel));
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(x_buttonPanel, BorderLayout.SOUTH);

		GoogleSyncer.getInstance().addGoogleSyncListener(this);
    }

	public void add(ThreadItem p_threadItem) {
		String[] x_options = {"Update", "Thread", "Action", "Cancel"};
		int x_selection = JOptionPane.showOptionDialog(this, "What would you like to add ?", "Add Something ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_options, x_options[2]);

		switch(x_selection) {
			case 0:
			case 2: addItem(p_threadItem, x_options[x_selection]); break;
			case 1: addThread(p_threadItem);
		}
	}

	protected void addItem(ThreadItem p_threadItem, String p_type) {
		Thread x_thread;

		if(p_threadItem != null && p_threadItem instanceof Thread) {
			x_thread = (Thread) p_threadItem;
		} else {
			o_table.clearSelection();
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose a Thread to add it to:", "Add an " + p_type + " ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(this, "Enter new " + p_type + " text:", "Add new " + p_type + " to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Item");

			if(x_text != null) {
				Item x_item = new Item(x_text);
				x_thread.addThreadItem(x_item);
				WindowManager.getInstance().openComponent(x_item);
			}
		}
	}

	private void addThread(ThreadItem p_threadItem) {
		Thread x_thread;

		if(p_threadItem != null && p_threadItem instanceof Thread) {
			x_thread = (Thread) p_threadItem;
		} else {
			o_table.clearSelection();
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose a Thread to add it to:", "Add a Thread ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_name = (String) JOptionPane.showInputDialog(this, "Enter new Thread name:", "Add new Thread to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Thread");

			if(x_name != null) {
				Thread x_newThread = new Thread(x_name);
				x_thread.addThreadItem(x_newThread);
				WindowManager.getInstance().openComponent(x_newThread);
			}
		}
	}

	private void dismiss(ThreadItem p_threadItem) {
		if(p_threadItem != null) {
			boolean x_active = !p_threadItem.isActive();

			if(JOptionPane.showConfirmDialog(this, "Set '" + p_threadItem.getText() + "' " + (x_active ? "Active" : "Inactive") + " ?", "Set " + (x_active ? "Active" : "Inactive") + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				p_threadItem.setActive(x_active);
			}
		}
	}

	private void remove(ThreadItem p_threadItem) {
        if(p_threadItem != null) {
			if(JOptionPane.showConfirmDialog(this, "Remove '" + p_threadItem.getText() + "' from '" + p_threadItem.getParentThread().getText() + "' ?", "Delete " + p_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
                o_thread.removeThreadItem(p_threadItem);
            }
        }
    }

	private void move(ThreadItem p_threadItem) {
		if(p_threadItem != null) {
			Thread x_thread = null;

			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);
			x_threads.remove(p_threadItem.getParentThread());

			if(p_threadItem instanceof Thread) {
				x_threads.remove(p_threadItem);
				x_threads.removeAll(LookupHelper.getAllActiveThreads((Thread)p_threadItem));
			}

			if(x_threads.size() > 0) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose a Thread to move it to:", "Move '" + p_threadItem + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				JOptionPane.showMessageDialog(this, "This Thread has no child Threads to move this item to. Try moving it from further up the tree.", "Nowhere to go", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon());
			}

			if(x_thread != null) {
				p_threadItem.getParentThread().removeThreadItem(p_threadItem);
				x_thread.addThreadItem(p_threadItem);
			}
		}
	}

	private void link(ThreadItem p_threadItem) {
		final JPanel x_this = this;

		if (o_linkLabel.isEnabled()) {
			if(p_threadItem instanceof Item) {
				final Item x_action = (Item) p_threadItem;

				if (JOptionPane.showConfirmDialog(x_this, "Link '" + x_action.getText() + "' to Google Calendar ?", "Link to Google ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
					GoogleLinkTask x_task = new GoogleLinkTask(Arrays.asList(x_action), new ProgressAdapter() {
						@Override
						public void finished() {
							JOptionPane.showMessageDialog(x_this, "'" + x_action.getText() + "' was linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
						}
					});

					x_task.execute();
				}
			}

			if(p_threadItem instanceof Thread) {
				final Thread x_thread = (Thread) p_threadItem;
				final List<Item> x_actions = LookupHelper.getAllActions(x_thread);

				if (x_actions.size() > 0) {
					if (JOptionPane.showConfirmDialog(x_this, "Link " + x_actions.size() + " Actions to Google Calendar ?", "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
						GoogleLinkTask x_task = new GoogleLinkTask(x_actions, new GoogleProgressWindow(x_this), new ProgressAdapter() {
							@Override
							public void finished() {
								JOptionPane.showMessageDialog(x_this, x_actions.size() + " Actions were linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
							}
						});

						x_task.execute();
					}
				}
			}
		}
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, ThreadItem p_threadItem) {
		o_dismissLabel.setEnabled(p_threadItem != null && p_threadItem.isActive());
		o_removeLabel.setEnabled(p_threadItem != null);
		o_moveLabel.setEnabled(p_threadItem != null);
		boolean x_linkable = false;

		if(p_threadItem != null && p_threadItem instanceof Item) {
			x_linkable = ((Item)p_threadItem).getDueDate() != null;
		}

		if(p_threadItem != null && p_threadItem instanceof Thread) {
			x_linkable = true;
		}

		o_linkLabel.setEnabled(x_linkable);
	}

	public void tableRowDoubleClicked(int p_row, int p_col, ThreadItem p_threadItem) {
		if(p_threadItem != null) {
			WindowManager.getInstance().openComponent(p_threadItem);
		}
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1, null);
	}
}