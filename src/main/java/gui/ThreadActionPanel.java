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

public class ThreadActionPanel extends ComponentTablePanel<Thread, Item> implements Observer {
    private final Thread o_thread;
	private final JLabel o_dismissLabel = new JLabel(ImageUtil.getTickIcon());
	private final JLabel o_removeLabel = new JLabel(ImageUtil.getMinusIcon());
	private final JLabel o_moveLabel = new JLabel(ImageUtil.getMoveIcon());
	private final JLabel o_linkLabel = new JLabel(ImageUtil.getLinkIcon());

	ThreadActionPanel(Thread p_thread) {
        super(new ThreadActionTableModel(p_thread), new ThreadActionCellRenderer(p_thread));
        o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(2, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(4, GUIConstants.s_googleStatusColumnWidth);

		JLabel x_addLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addLabel.setToolTipText("Add Action");
		x_addLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				add(getSelectedObject());
			}
		});

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Make Action Active/Inactive");
		o_dismissLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dismiss(getSelectedObject());
			}
		});

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove Action");
		o_removeLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				remove(getSelectedObject());
			}
		});

		o_moveLabel.setEnabled(false);
		o_moveLabel.setToolTipText("Move Action");
		o_moveLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				move(getSelectedObject());
			}
		});

		o_linkLabel.setEnabled(false);
		o_linkLabel.setToolTipText("Link Action to Google Calendar");
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

		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
    }

	protected void add(Item o_action) {
		Thread x_thread;

		if(o_action != null) {
			x_thread = o_action.getParentThread();
		} else {
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose a Thread to add it to:", "Add an Action", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(this, "Enter new Action text:", "Add new Action to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Action");

			if(x_text != null) {
				Item x_item = new Item(x_text);
				x_item.setDueDate(DateSuggestionPanel.getDateSuggestion());
				x_thread.addThreadItem(x_item);
				WindowManager.getInstance().openComponent(x_item);
			}
		}
	}

	private void dismiss(Item o_action) {
		if(o_action != null) {
			if(JOptionPane.showConfirmDialog(this, "Set '" + o_action.getText() + "' Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				o_action.setActive(false);
			}
		}
	}

	private void remove(Item o_action) {
		if(o_action != null) {
			Thread x_thread = o_action.getParentThread();

			if(JOptionPane.showConfirmDialog(this, "Remove '" + o_action.getText() + "' from '" + x_thread.getText() + "' ?", "Delete " + o_action.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_thread.removeThreadItem(o_action);
			}
		}
	}

	private void move(Item o_action) {
		if(o_action != null) {
			Thread x_thread = null;

			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);
			x_threads.remove(o_action.getParentThread());

			if(x_threads.size() > 0) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose a Thread to move it to:", "Move '" + o_action + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				JOptionPane.showMessageDialog(this, "This Thread has no child Threads to move this item to. Try moving it from further up the tree.", "Nowhere to go", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon());
			}

			if(x_thread != null) {
				o_action.getParentThread().removeThreadItem(o_action);
				x_thread.addThreadItem(o_action);
			}
		}
	}

	private void link(final Item p_action) {
		final JPanel x_this = this;

		if (o_linkLabel.isEnabled()) {
			if (JOptionPane.showConfirmDialog(x_this, "Link '" + p_action.getText() + "' to Google Calendar ?", "Link to Google ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
				GoogleLinkTask x_task = new GoogleLinkTask(Arrays.asList(p_action), new GoogleProgressWindow(x_this), new ProgressAdapter() {
					@Override
					public void finished() {
						JOptionPane.showMessageDialog(x_this, "'" + p_action.getText() + "' was linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
					}
				});

				x_task.execute();
			}
		}
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, Item p_item) {
		o_removeLabel.setEnabled(p_item != null);
		o_dismissLabel.setEnabled(p_item != null);
		o_moveLabel.setEnabled(p_item != null);
		o_linkLabel.setEnabled(p_item != null);
	}

	@Override
	public void tableRowDoubleClicked(int p_row, int p_col, Item p_item) {
		switch(p_col) {
			case 0: WindowManager.getInstance().openComponent(p_item.getParentThread()); break;
			default: WindowManager.getInstance().openComponent(p_item);
        }
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void timeUpdate() {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void googleSynced() {
		tableRowClicked(-1, -1, null);
	}
}
