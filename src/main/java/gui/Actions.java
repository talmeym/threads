package gui;

import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.util.*;

public class Actions {
	public static void addThread(ThreadItem p_threadItem, Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		if(p_threadItem != null && p_threadItem instanceof Thread) {
			x_thread = (Thread) p_threadItem;
		} else {
			x_thread = chooseThread(p_startingThread, p_enclosingPanel, "Add a Thread ?");
		}

		if(x_thread != null) {
			if(x_thread != null) {
				String x_name = (String) JOptionPane.showInputDialog(p_enclosingPanel, "Enter new Thread name:", "Add new Thread to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Thread");

				if(x_name != null) {
					Thread x_newThread = new Thread(x_name);
					x_thread.addThreadItem(x_newThread);
					WindowManager.getInstance().openComponent(x_newThread);
				}
			}
		}
	}

	public static void addAction(Item p_action, Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		if(p_action != null) {
			x_thread = p_action.getParentThread();
		} else {
			x_thread = chooseThread(p_startingThread, p_enclosingPanel, "Add an Action ?");
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(p_enclosingPanel, "Enter new Action text:", "Add new Action to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Action");

			if(x_text != null) {
				Item x_item = new Item(x_text);
				x_item.setDueDate(DateSuggestionPanel.getDateSuggestion());
				x_thread.addThreadItem(x_item);
				WindowManager.getInstance().openComponent(x_item);
			}
		}
	}

	public static void addUpdate(Item p_update, Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		if(p_update != null) {
			x_thread = p_update.getParentThread();
		} else {
			x_thread = chooseThread(p_startingThread, p_enclosingPanel, "Add an Update ?");
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(p_enclosingPanel, "Enter new Update text:", "Add new Update to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Update");

			if(x_text != null) {
				Item x_item = new Item(x_text);
				x_thread.addItem(x_item);

				if(LookupHelper.getActiveUpdates(x_thread).size() == 2 && JOptionPane.showConfirmDialog(p_enclosingPanel, MessagingConstants.s_supersedeUpdatesDesc, MessagingConstants.s_supersedeUpdatesTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
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

	private static Thread chooseThread(Thread p_startingThread, JPanel p_enclosingPanel, String x_title) {
		Thread x_thread;List<Thread> x_threads = LookupHelper.getAllActiveThreads(p_startingThread);
		x_threads.add(0, p_startingThread);

		if(x_threads.size() > 1) {
			x_thread = (Thread) JOptionPane.showInputDialog(p_enclosingPanel, "Choose a Thread to add it to:", x_title, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
		} else {
			x_thread = p_startingThread;
		}
		return x_thread;
	}

	public static void linkToGoogle(final Item p_item, final JPanel p_enclosingPanel) {
		if (JOptionPane.showConfirmDialog(p_enclosingPanel, "Link '" + p_item.getText() + "' to Google Calendar ?", "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
			GoogleLinkTask x_task = new GoogleLinkTask(Collections.singletonList(p_item), new GoogleProgressWindow(p_enclosingPanel), new ProgressAdapter() {
				@Override
				public void success() {
					JOptionPane.showMessageDialog(p_enclosingPanel, "'" + p_item.getText() + "' was linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
				}

				@Override
				public void error(String errorDesc) {
					JOptionPane.showMessageDialog(p_enclosingPanel, errorDesc, "Error linking to Google Calendar ...", JOptionPane.ERROR_MESSAGE);
				}
			});
			x_task.execute();
		}
	}

	public static void linkToGoogle(Reminder p_reminder, JPanel p_enclosingPanel) {
		if (JOptionPane.showConfirmDialog(p_enclosingPanel, "Link '" + p_reminder.getText() + "' to Google Calendar ?", "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
			GoogleLinkTask x_task = new GoogleLinkTask(p_reminder, new GoogleProgressWindow(p_enclosingPanel), new ProgressAdapter() {
				@Override
				public void success() {
					JOptionPane.showMessageDialog(p_enclosingPanel, "'" + p_reminder.getText() + "' was linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
				}

				@Override
				public void error(String errorDesc) {
					JOptionPane.showMessageDialog(p_enclosingPanel, errorDesc, "Error linking to Google Calendar ...", JOptionPane.ERROR_MESSAGE);
				}
			});
			x_task.execute();
		}
	}

	public static void linkToGoogle(Thread x_thread, JPanel p_enclosingPanel, boolean p_activeOnly) {
		final List<Item> x_actions = p_activeOnly ? LookupHelper.getAllActiveActions(x_thread) : LookupHelper.getAllActions(x_thread);

		if (x_actions.size() > 0) {
			if (JOptionPane.showConfirmDialog(p_enclosingPanel, "Link " + x_actions.size() + (p_activeOnly ? " (Active)" : "") + " Action" + (x_actions.size() > 1 ? "s" : "") + " to Google Calendar ?", "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
				GoogleLinkTask x_task = new GoogleLinkTask(x_actions, new GoogleProgressWindow(p_enclosingPanel), new ProgressAdapter() {
					@Override
					public void success() {
						JOptionPane.showMessageDialog(p_enclosingPanel, x_actions.size() + " Action" + (x_actions.size() > 1 ? "s were" : " was") + " linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
					}

					@Override
					public void error(String errorDesc) {
						JOptionPane.showMessageDialog(p_enclosingPanel, errorDesc, "Error linking to Google Calendar ...", JOptionPane.ERROR_MESSAGE);
					}
				});
				x_task.execute();
			}
		}
	}
}
