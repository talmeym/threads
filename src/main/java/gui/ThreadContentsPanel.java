package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ThreadContentsPanel extends ComponentTablePanel implements Observer
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
        fixColumnWidth(3, GUIConstants.s_statsColumnWidth);
        fixColumnWidth(4, GUIConstants.s_statsColumnWidth);
        fixColumnWidth(5, GUIConstants.s_statsColumnWidth);
        fixColumnWidth(6, 30);

		JLabel x_addItemLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addItemLabel.setToolTipText("Add Content");
		x_addItemLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				addSomething(getSelectedRow());
			}
		});

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove");
		o_removeLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				removeSomething(getSelectedRow());
			}
		});

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Make Active/Inactive");
        o_dismissLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				dismissSomething(getSelectedRow());
			}
		});

		o_moveLabel.setToolTipText("Move");
		o_moveLabel.setEnabled(false);
		o_moveLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				moveSomething(getSelectedRow());
			}
		});

		o_linkLabel.setToolTipText("Link to Google Calendar");
		o_linkLabel.setEnabled(false);
		o_linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				linkToGoogle(getSelectedRow());
			}
		});

        JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        x_buttonPanel.add(x_addItemLabel);
        x_buttonPanel.add(o_removeLabel);
        x_buttonPanel.add(o_dismissLabel);
        x_buttonPanel.add(o_moveLabel);
        x_buttonPanel.add(o_linkLabel);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(x_buttonPanel, BorderLayout.SOUTH);

		GoogleSyncer.getInstance().addGoogleSyncListener(this);
    }

	public void addSomething(int p_index) {
		String[] x_options = {"Update", "Thread", "Action", "Cancel"};
		int x_selection = JOptionPane.showOptionDialog(this, "What would you like to add ?", "Add Something ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_options, x_options[2]);

		switch(x_selection) {
			case 0:
			case 2: addItem(p_index, x_options[x_selection]); break;
			case 1: addThread(p_index);
		}
	}

	protected void addItem(int p_index, String p_type) {
		Thread x_thread;

		if(p_index != -1 && o_thread.getThreadItem(p_index) instanceof Thread) {
			x_thread = (Thread) o_thread.getThreadItem(p_index);
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

	private void addThread(int p_index) {
		Thread x_thread;

		if(p_index != -1 && o_thread.getThreadItem(p_index) instanceof Thread) {
			x_thread = (Thread) o_thread.getThreadItem(p_index);
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

	private void dismissSomething(int p_index) {
		if(p_index != -1) {
			ThreadItem x_threadItem = o_thread.getThreadItem(p_index);
			boolean x_active = !x_threadItem.isActive();

			if(JOptionPane.showConfirmDialog(this, "Set '" + x_threadItem.getText() + "' " + (x_active ? "Active" : "Inactive") + " ?", "Set " + (x_active ? "Active" : "Inactive") + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_threadItem.setActive(x_active);
			}
		}
	}

	private void removeSomething(int p_index) {
        if(p_index != -1) {
            ThreadItem x_threadItem = o_thread.getThreadItem(p_index);

			if(JOptionPane.showConfirmDialog(this, "Remove '" + x_threadItem.getText() + "' from '" + x_threadItem.getParentThread().getText() + "' ?", "Remove " + x_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
                o_thread.removeThreadItem(x_threadItem);
            }
        }
    }

	private void moveSomething(int p_index) {
		if(p_index != -1) {
			ThreadItem x_threadItem = o_thread.getThreadItem(p_index);
			Thread x_thread = null;

			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);
			x_threads.remove(x_threadItem.getParentThread());

			if(x_threadItem instanceof Thread) {
				x_threads.remove(x_threadItem);
				x_threads.removeAll(LookupHelper.getAllActiveThreads((Thread)x_threadItem));
			}

			if(x_threads.size() > 0) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose a Thread to move it to:", "Move '" + x_threadItem + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				JOptionPane.showMessageDialog(this, "This Thread has no child Threads to move this item to. Try moving it from further up the tree.", "Nowhere to go", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon());
			}

			if(x_thread != null) {
				x_threadItem.getParentThread().removeThreadItem(x_threadItem);
				x_thread.addThreadItem(x_threadItem);
			}
		}
	}

	private void linkToGoogle(int p_index) {
		final JPanel x_this = this;

		if (o_linkLabel.isEnabled()) {
			ThreadItem x_threadItem = o_thread.getThreadItem(p_index);

			if(x_threadItem instanceof Item) {
				final Item x_action = (Item) x_threadItem;

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

			if(x_threadItem instanceof Thread) {
				final Thread x_thread = (Thread) x_threadItem;
				final List<Item> x_activeActions = LookupHelper.getAllActiveActions(x_thread);

				if (x_activeActions.size() > 0) {
					if (JOptionPane.showConfirmDialog(x_this, "Link " + x_activeActions.size() + " Actions to Google Calendar ?", "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
						GoogleLinkTask x_task = new GoogleLinkTask(x_activeActions, new GoogleProgressWindow(x_this), new ProgressAdapter() {
							@Override
							public void finished() {
								JOptionPane.showMessageDialog(x_this, x_activeActions.size() + " Actions were linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
							}
						});

						x_task.execute();
					}
				}
			}
		}
	}

	@Override
	void tableRowClicked(int p_row, int p_col) {
		o_dismissLabel.setEnabled(p_row != -1 && o_thread.getThreadItem(p_row).isActive());
		o_removeLabel.setEnabled(p_row != -1);
		o_moveLabel.setEnabled(p_row != -1);
		boolean x_linkable = false;

		if(p_row != -1 && o_thread.getThreadItem(p_row) instanceof Item) {
			x_linkable = ((Item)o_thread.getThreadItem(p_row)).getDueDate() != null;
		}

		if(p_row != -1 && o_thread.getThreadItem(p_row) instanceof Thread) {
			x_linkable = true;
		}

		o_linkLabel.setEnabled(x_linkable);
	}

	void tableRowDoubleClicked(int p_row, int p_col) {
		if(p_row != -1) {
			ThreadItem x_threadItem = o_thread.getThreadItem(p_row);
			WindowManager.getInstance().openComponent(x_threadItem);

			if(x_threadItem instanceof Thread && p_col > 2) {
				ThreadPanel.setTabIndex(p_col - 2);
			}
		}
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}