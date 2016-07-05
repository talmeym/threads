package gui;

import data.Thread;
import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import static util.GuiUtil.setUpButtonLabel;

class ThreadThreadPanel extends ComponentTablePanel<Thread, Thread> implements Observer {
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
				add(getSelectedObject());
			}
		});

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Make Thread Active/Inactive");
		o_dismissLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dismiss(getSelectedObject());
			}
		});

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove Thread");
		o_removeLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				remove(getSelectedObject());
			}
		});

		o_moveLabel.setEnabled(false);
		o_moveLabel.setToolTipText("Move Thread");
		o_moveLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				move(getSelectedObject());
			}
		});

		o_linkLabel.setEnabled(false);
		o_linkLabel.setToolTipText("Link Thread to Google Calendar");
		o_linkLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				link(getSelectedObject());
			}
		});

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(x_addLabel));
		x_buttonPanel.add(setUpButtonLabel(o_removeLabel));
		x_buttonPanel.add(setUpButtonLabel(o_dismissLabel));
		x_buttonPanel.add(setUpButtonLabel(o_moveLabel));
		x_buttonPanel.add(setUpButtonLabel(o_linkLabel));
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	private void add(Thread p_thread) {
		Thread x_thread = p_thread;

		if(x_thread == null) {
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

	private void dismiss(Thread p_thread) {
		if(p_thread != null) {
			if(JOptionPane.showConfirmDialog(this, "Set '" + p_thread.getText() + "' Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				p_thread.setActive(false);
			}
		}
	}

	private void remove(Thread x_thread) {
		if(x_thread != null) {
			Thread x_parent = x_thread.getParentThread();

			if(JOptionPane.showConfirmDialog(this, "Remove '" + x_thread.getText() + "' from '" + x_thread.getParentThread().getText() + "' ?", "Delete " + x_thread.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_parent.removeThreadItem(x_thread);
			}
		}
	}

	private void move(Thread p_thread) {
		if(p_thread != null) {
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			Thread x_thread = null;

			x_threads.add(0, o_thread);
			x_threads.remove(p_thread.getParentThread());
			x_threads.remove(p_thread);
			x_threads.removeAll(LookupHelper.getAllActiveThreads(p_thread));

			if(x_threads.size() > 0) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose a Thread to move it to:", "Move '" + p_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				JOptionPane.showMessageDialog(this, "This Thread has no child Threads to move this item to. Try moving it from further up the tree.", "Nowhere to go", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon());
			}

			if(x_thread != null) {
				p_thread.getParentThread().removeThreadItem(p_thread);
				x_thread.addThreadItem(p_thread);
			}
		}
	}

	private void link(Thread p_thread) {
		final JPanel x_this = this;
		final List<Item> x_actions = LookupHelper.getAllActions(p_thread);

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

	@Override
	public void tableRowClicked(int p_row, int p_col, Thread p_thread) {
		o_dismissLabel.setEnabled(p_row != -1);
		o_removeLabel.setEnabled(p_row != -1);
		o_moveLabel.setEnabled(p_row != -1);
		o_linkLabel.setEnabled(p_row != -1);
	}

    public void tableRowDoubleClicked(int p_row, int p_col, Thread p_thread) {
		switch(p_col) {
			case 0: WindowManager.getInstance().openComponent(p_thread.getParentComponent()); break;
			default:
				WindowManager.getInstance().openComponent(p_thread);

				if(p_col > 1) {
					ThreadPanel.setTabIndex(p_col - 1);
				}
		}
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1, null);
	}
}