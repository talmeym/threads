package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.util.*;

import static data.LookupHelper.getHasDueDates;

public class Actions {
	static void addThread(ThreadItem p_threadItem, Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		if(p_threadItem != null && p_threadItem instanceof Thread) {
			x_thread = (Thread) p_threadItem;
		} else {
			x_thread = chooseThread(p_startingThread, p_enclosingPanel, "Add a Thread ?");
		}

		if(x_thread != null) {
			String x_name = (String) JOptionPane.showInputDialog(p_enclosingPanel, "Enter new Thread name:", "Add new Thread to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Thread");

			if(x_name != null) {
				Thread x_newThread = new Thread(x_name);
				x_thread.addThreadItem(x_newThread);
				WindowManager.getInstance().openComponent(x_newThread);
			}
		}
	}

	public static Item addAction(ThreadItem p_threadItem, Thread p_startingThread, Date p_date, JPanel p_enclosingPanel, boolean p_openAfter) {
		Thread x_thread;

		if(p_threadItem instanceof Thread) {
			x_thread = (Thread) p_threadItem;
		} else if(p_threadItem instanceof Item) {
			x_thread = p_threadItem.getParentThread();
		} else {
			x_thread = chooseThread(p_startingThread, p_enclosingPanel, "Add an Action ?");
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(p_enclosingPanel, "Enter new Action text:", "Add new Action to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Action");

			if(x_text != null) {
				Item x_item = new Item(x_text, p_date);
				x_thread.addThreadItem(x_item);

				if(p_openAfter) {
					WindowManager.getInstance().openComponent(x_item);
				}

				return x_item;
			}
		}

		return null;
	}

	public static void addUpdate(ThreadItem p_threadItem, Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		if(p_threadItem instanceof Thread) {
			x_thread = (Thread) p_threadItem;
		} else if(p_threadItem instanceof Item) {
			x_thread = p_threadItem.getParentThread();
		} else {
			x_thread = chooseThread(p_startingThread, p_enclosingPanel, "Add an Update ?");
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(p_enclosingPanel, "Enter new Update text:", "Add new Update to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Update");

			if(x_text != null) {
				Item x_item = new Item(x_text, null);
				x_thread.addThreadItem(x_item);

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
		List<Thread> x_threads = LookupHelper.getAllActiveThreads(p_startingThread);
		x_threads.add(0, p_startingThread);

		if(x_threads.size() > 1) {
			return (Thread) JOptionPane.showInputDialog(p_enclosingPanel, "Choose a Thread to add it to:", x_title, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
		} else {
			return p_startingThread;
		}
	}

	static void linkToGoogle(Thread x_thread, JPanel p_enclosingPanel, boolean p_activeOnly) {
		if(checkGoogle(p_enclosingPanel)) {
			return;
		}

		linkToGoogle(getHasDueDates(x_thread, p_activeOnly), p_enclosingPanel);
	}

	static void linkToGoogle(final Item p_item, final JPanel p_enclosingPanel) {
		if(checkGoogle(p_enclosingPanel)) {
			return;
		}

		linkToGoogle(getHasDueDates(p_item, false), p_enclosingPanel);
	}

	static void linkToGoogle(Reminder p_reminder, JPanel p_enclosingPanel) {
		if(checkGoogle(p_enclosingPanel)) {
			return;
		}

		linkToGoogle(Collections.singletonList(p_reminder), p_enclosingPanel);
	}

	static void linkToGoogle(final List<HasDueDate> p_hasDueDates, final JPanel p_enclosingPanel) {
		if(checkGoogle(p_enclosingPanel) || p_hasDueDates.size() == 0) {
			return;
		}

		String x_confirmMessage = p_hasDueDates.size() > 1 ? "Link " + p_hasDueDates.size() + " item" + (p_hasDueDates.size() > 1 ? "s" : "") + " to Google Calendar ?" : "Link '" + p_hasDueDates.get(0) + "' to Google Calendar ?";
		String x_successMessage = p_hasDueDates.size() > 1 ? p_hasDueDates.size() + " Item" + (p_hasDueDates.size() > 1 ? "s were" : " was") + " linked to Google Calendar" : "'" + p_hasDueDates.get(0).getText() + "' was linked to Google Calendar";

		if (JOptionPane.showConfirmDialog(p_enclosingPanel, x_confirmMessage, "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
			GoogleLinkTask x_task = new GoogleLinkTask(p_hasDueDates, new GoogleProgressWindow(p_enclosingPanel), new ProgressAdapter() {
				@Override
				public void success() {
					JOptionPane.showMessageDialog(p_enclosingPanel, x_successMessage, "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
				}

				@Override
				public void error(String errorDesc) {
					JOptionPane.showMessageDialog(p_enclosingPanel, errorDesc, "Error linking to Google Calendar ...", JOptionPane.ERROR_MESSAGE);
				}
			});

			x_task.execute();
		}
	}

	private static boolean checkGoogle(JPanel p_enclosingPanel) {
		if(!Settings.registerForSetting(Settings.s_GOOGLE_ENABLED, (p_name, p_value) -> { }, false)) {
			JOptionPane.showMessageDialog(p_enclosingPanel, "Google is disabled", "No Google", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}

		return false;
	}

	static void move(ThreadItem p_threadItem, Thread o_startingThread, JPanel o_enclosingPanel) {
		if(p_threadItem != null) {
			Thread x_thread = null;

			Thread x_topThread = (Thread) o_startingThread.getHierarchy().get(0);
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(x_topThread);

			x_threads.add(0, x_topThread);
			x_threads.remove(p_threadItem.getParentThread());

			if(p_threadItem instanceof Thread) {
				x_threads.remove(p_threadItem);
				x_threads.removeAll(LookupHelper.getAllActiveThreads((Thread) p_threadItem));
			}

			if(x_threads.size() > 0) {
				x_thread = (Thread) JOptionPane.showInputDialog(o_enclosingPanel, "Choose a Thread to move it to:", "Move '" + p_threadItem + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				JOptionPane.showMessageDialog(o_enclosingPanel, "This is no other Thread to move this " + p_threadItem.getType() + " to. Try creating another Thread.", "Nowhere to go", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon());
			}

			if(x_thread != null) {
				p_threadItem.getParentThread().removeThreadItem(p_threadItem);
				x_thread.addThreadItem(p_threadItem);
			}
		}
	}

	static void activate(Component p_component, JPanel p_parentPanel) {
		if(p_component != null) {
			if(JOptionPane.showConfirmDialog(p_parentPanel, "Set '" + p_component.getText() + "' Active ?", "Set Active ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				p_component.setActive(true);
			}
		}
	}

	static void deactivate(Component p_component, JPanel p_parentPanel) {
		if(p_component != null) {
			if(JOptionPane.showConfirmDialog(p_parentPanel, "Set '" + p_component.getText() + "' Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				p_component.setActive(false);
			}
		}
	}

	public static void remove(ThreadItem p_threadItem, JPanel p_parentPanel) {
		if(p_threadItem != null) {
			Thread x_thread = p_threadItem.getParentThread();

			if(JOptionPane.showConfirmDialog(p_parentPanel, "Remove '" + p_threadItem.getText() + "' from '" + x_thread.getText() + "' ?", "Remove " + p_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_thread.removeThreadItem(p_threadItem);
			}
		}
	}

	static void remove(Reminder p_reminder, JPanel p_parentPanel) {
		if (p_reminder != null) {
			Item x_item = p_reminder.getParentItem();

			if (JOptionPane.showConfirmDialog(p_parentPanel, "Remove '" + p_reminder.getText() + "' from '" + x_item.getText() + "' ?", "Remove " + p_reminder.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_item.removeReminder(p_reminder);
			}
		}
	}
}
