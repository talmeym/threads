package gui;

import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.util.List;

public class Actions {
	public static void addAction(Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		List<Thread> x_threads = LookupHelper.getAllActiveThreads(p_startingThread);
		x_threads.add(0, p_startingThread);

		if(x_threads.size() > 1) {
			x_thread = (Thread) JOptionPane.showInputDialog(p_enclosingPanel, "Choose a Thread to add it to:", "Add an Action", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
		} else {
			x_thread = p_startingThread;
		}

		if(x_thread != null) {
			addActionToThread(x_thread, p_enclosingPanel);
		}
	}

	static void addAction(Item p_action, Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		if(p_action != null) {
			x_thread = p_action.getParentThread();
		} else {
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(p_startingThread);
			x_threads.add(0, p_startingThread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(p_enclosingPanel, "Choose a Thread to add it to:", "Add an Action ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = p_startingThread;
			}
		}

		if(x_thread != null) {
			addActionToThread(x_thread, p_enclosingPanel);
		}
	}

	private static void addActionToThread(Thread p_thread, JPanel p_enclosingPanel) {
		String x_text = (String) JOptionPane.showInputDialog(p_enclosingPanel, "Enter new Action text:", "Add new Action to '" + p_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Action");

		if(x_text != null) {
			Item x_item = new Item(x_text);
			x_item.setDueDate(DateSuggestionPanel.getDateSuggestion());
			p_thread.addThreadItem(x_item);
			WindowManager.getInstance().openComponent(x_item);
		}
	}

	public static void addUpdate(Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		List<Thread> x_threads = LookupHelper.getAllActiveThreads(p_startingThread);
		x_threads.add(0, p_startingThread);

		if(x_threads.size() > 1) {
			x_thread = (Thread) JOptionPane.showInputDialog(p_enclosingPanel, "Choose a Thread to add it to:", "Add an Update ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
		} else {
			x_thread = p_startingThread;
		}

		if(x_thread != null) {
			addUpdateToThread(x_thread, p_enclosingPanel);
		}
	}

	static void addUpdate(Item p_update, Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		if(p_update != null) {
			x_thread = p_update.getParentThread();
		} else {
			java.util.List<Thread> x_threads = LookupHelper.getAllActiveThreads(p_startingThread);
			x_threads.add(0, p_startingThread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(p_enclosingPanel, "Choose a Thread to add it to:", "Add an Update ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = p_startingThread;
			}
		}

		if(x_thread != null) {
			addUpdateToThread(x_thread, p_enclosingPanel);
		}
	}

	private static void addUpdateToThread(Thread p_thread, JPanel p_enclosingPanel) {
		String x_text = (String) JOptionPane.showInputDialog(p_enclosingPanel, "Enter new Update text:", "Add new Update to '" + p_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Update");

		if(x_text != null) {
			Item x_item = new Item(x_text);
			p_thread.addItem(x_item);

			if(LookupHelper.getActiveUpdates(p_thread).size() == 2 && JOptionPane.showConfirmDialog(p_enclosingPanel, MessagingConstants.s_supersedeUpdatesDesc, MessagingConstants.s_supersedeUpdatesTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
					ThreadItem x_groupItem = p_thread.getThreadItem(i);

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
