package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ThreadActionPanel extends ComponentTablePanel implements Observer {
    private final Thread o_thread;
	private final JLabel o_dismissLabel = new JLabel(ImageUtil.getTickIcon());
	private final JLabel o_removeLabel = new JLabel(ImageUtil.getMinusIcon());
	private final JLabel o_moveLabel = new JLabel(ImageUtil.getMoveIcon());

	ThreadActionPanel(Thread p_thread) {
        super(new ThreadActionTableModel(p_thread), new ThreadActionCellRenderer(p_thread));
        o_thread = p_thread;
		o_thread.addObserver(this);

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);

		JLabel x_addLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addLabel.setToolTipText("Add");
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

		TimeUpdater.getInstance().addTimeUpdateListener(new TimeUpdateListener() {
			@Override
			public void timeUpdate() {
				((ComponentTableModel)o_table.getModel()).fireTableDataChanged();
				tableRowClicked(-1, -1);
			}
		});
    }

	protected void addSomething(int p_index) {
		Thread x_thread;

		if(p_index != -1) {
			x_thread = LookupHelper.getAllActiveActions(o_thread).get(p_index).getParentThread();
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
				x_thread.addThreadItem(x_item);
				WindowManager.getInstance().openComponent(x_item);
			}
		}
	}

	private void dismissSomething(int p_index) {
		if(p_index != -1) {
			Item x_action = LookupHelper.getAllActiveActions(o_thread).get(p_index);

			if(JOptionPane.showConfirmDialog(this, "Set '" + x_action.getText() + "' Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_action.setActive(false);
			}
		}
	}

	private void removeSomething(int p_index) {
		if(p_index != -1) {
			Item x_action = LookupHelper.getAllActiveActions(o_thread).get(p_index);

			if(JOptionPane.showConfirmDialog(this, "Remove '" + x_action.getText() + "' from '" + x_action.getParentThread().getText() + "' ?", "Remove " + x_action.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_action.getParentThread().removeThreadItem(x_action);
			}
		}
	}

	private void moveSomething(int p_index) {
		if(p_index != -1) {
			Item x_item = LookupHelper.getAllActiveActions(o_thread).get(p_index);
			Thread x_thread = null;

			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);
			x_threads.remove(x_item.getParentThread());

			if(x_threads.size() > 0) {
				x_thread = (Thread) JOptionPane.showInputDialog(this, "Choose a Thread to move it to:", "Move '" + x_item + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				JOptionPane.showMessageDialog(this, "Nowhere to move to. Sorry");
			}

			if(x_thread != null) {
				x_item.getParentThread().removeThreadItem(x_item);
				x_thread.addThreadItem(x_item);
			}
		}
	}

	private void showThread(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveActions(o_thread).get(p_index);
			Thread x_thread = x_threadItem.getParentThread();

			if(x_thread != o_thread) {
				WindowManager.getInstance().openComponent(x_thread);
			}
        }
    }

    private void showItem(int p_index) {
        if(p_index != -1) {
            Item x_threadItem = LookupHelper.getAllActiveActions(o_thread).get(p_index);
            WindowManager.getInstance().openComponent(x_threadItem);
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
			default: showItem(row);
        }
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}
