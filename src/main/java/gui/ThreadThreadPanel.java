package gui;

import data.Thread;
import data.*;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class ThreadThreadPanel extends ComponentTablePanel implements Observer {
    private final Thread o_thread;
	private final JLabel o_dismissLabel = new JLabel(ImageUtil.getTickIcon());
	private final JLabel o_removeLabel = new JLabel(ImageUtil.getMinusIcon());
	private final JLabel o_moveLabel = new JLabel(ImageUtil.getMoveIcon());
	private final JLabel o_linkLabel = new JLabel(ImageUtil.getLinkIcon());

    ThreadThreadPanel(Thread p_thread) {
        super(new ThreadThreadTableModel(p_thread), new ComponentCellRenderer(null));
		o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
		fixColumnWidth(2, GUIConstants.s_statsColumnWidth);
		fixColumnWidth(3, GUIConstants.s_statsColumnWidth);
		fixColumnWidth(4, GUIConstants.s_statsColumnWidth);

		JLabel x_addLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addLabel.setToolTipText("Add Thread");
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

		o_linkLabel.setEnabled(false);
		o_linkLabel.setToolTipText("Link to Google Calendar");
		o_linkLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				linkToGoogle(getSelectedRow());
			}
		});

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(x_addLabel);
		x_buttonPanel.add(o_removeLabel);
		x_buttonPanel.add(o_dismissLabel);
		x_buttonPanel.add(o_moveLabel);
		x_buttonPanel.add(o_linkLabel);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	private void addSomething(int p_index) {
		List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
		Thread x_thread;

		if(p_index != -1) {
			x_thread = x_threads.get(p_index);
		} else {
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
			Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_index);

			if(JOptionPane.showConfirmDialog(this, "Set '" + x_thread.getText() + "' Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_thread.setActive(false);
			}
		}
	}

	private void removeSomething(int p_index) {
		if(p_index != -1) {
			ThreadItem x_threadItem = (ThreadItem) LookupHelper.getAllActiveThreads(o_thread).get(p_index);
			Thread x_parent = (Thread) x_threadItem.getParentComponent();

			if(JOptionPane.showConfirmDialog(this, "Remove '" + x_threadItem.getText() + "' from '" + x_threadItem.getParentThread().getText() + "' ?", "Remove " + x_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_parent.removeThreadItem(x_threadItem);
			}
		}
	}

	private void moveSomething(int p_index) {
		if(p_index != -1) {
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			ThreadItem x_threadItem = x_threads.get(p_index);
			Thread x_thread = null;

			x_threads.add(0, o_thread);
			x_threads.remove(x_threadItem.getParentThread());
			x_threads.remove(x_threadItem);
			x_threads.removeAll(LookupHelper.getAllActiveThreads((Thread) x_threadItem));

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
		Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_index);
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

    private void showParentThread(int p_index) {
        if(p_index != -1) {
            Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_thread.getParentComponent());
        }
    }

    private void showThread(int p_col, int p_row) {
        if(p_row != -1) {
			Thread x_thread = LookupHelper.getAllActiveThreads(o_thread).get(p_row);
			WindowManager.getInstance().openComponent(x_thread);

			if(p_col > 1) {
				ThreadPanel.setTabIndex(p_col - 1);
			}
		}
    }

	@Override
	void tableRowClicked(int p_row, int p_col) {
		o_dismissLabel.setEnabled(p_row != -1);
		o_removeLabel.setEnabled(p_row != -1);
		o_moveLabel.setEnabled(p_row != -1);
		o_linkLabel.setEnabled(p_row != -1);
	}

    void tableRowDoubleClicked(int p_row, int p_col) {
		switch(p_col) {
			case 0: showParentThread(p_row); break;
			default: showThread(p_col, p_row);
		}
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}