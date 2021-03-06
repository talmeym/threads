package threads.gui;

import threads.data.Thread;
import threads.data.*;
import threads.util.GoogleAccount;
import threads.util.GoogleLinkTask;
import threads.util.ProgressAdapter;

import javax.swing.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static javax.swing.JOptionPane.*;
import static threads.data.LookupHelper.*;
import static threads.util.DateUtil.deriveDate;
import static threads.util.DateUtil.deriveReminderDate;
import static threads.util.GoogleUtil.getGoogleAccounts;
import static threads.util.GoogleUtil.isLinked;
import static threads.util.ImageUtil.getGoogleIcon;
import static threads.util.ImageUtil.getThreadsIcon;
import static threads.util.Settings.Setting.GOOGLE;

public class Actions {
	static Item addActionFromTemplate(Configuration p_configuration, ThreadItem p_threadItem, Thread p_startingThread, Date p_date, JPanel p_enclosingPanel, boolean p_openAfter) {
		List<ActionTemplate> x_actionTemplates = p_configuration.getActionTemplates();
		ActionTemplate x_template = (ActionTemplate) showInputDialog(p_enclosingPanel, "Choose a Template:", "Add From Template ?", INFORMATION_MESSAGE, getThreadsIcon(), x_actionTemplates.toArray(new Object[x_actionTemplates.size()]), x_actionTemplates.get(0));

		if(x_template != null) {
			Thread x_thread;

			if (p_threadItem instanceof Thread) {
				x_thread = (Thread) p_threadItem;
			} else if (p_threadItem instanceof Item) {
				x_thread = p_threadItem.getParentThread();
			} else {
				x_thread = chooseThread(p_startingThread, p_enclosingPanel, "Add new " + x_template.getName() + " ?");
			}

			if (x_thread != null) {
				String x_text = null;
				boolean x_proceed = true;

				if(x_template.getTokenPrompt() != null) {
					x_text = (String) showInputDialog(p_enclosingPanel, x_template.getTokenPrompt(), "Add new " + x_template.getName() + " to '" + x_thread + "' ?", INFORMATION_MESSAGE, getThreadsIcon(), null, x_template.getTokenDefault());
					x_proceed = x_text != null;
				}

				if (x_proceed) {
					Date x_derivedDate = deriveDate(x_text, p_date);
					Item x_action = x_template.buildAction(x_derivedDate != null ? x_text.substring(x_text.indexOf(" ") + 1) : x_text, x_derivedDate != null ? x_derivedDate : p_date);
					x_thread.addThreadItem(x_action);

					if (p_openAfter) {
						WindowManager.getInstance().openComponent(x_action);
					}

					return x_action;
				}
			}
		}

		return null;
	}

	static Thread addThread(ThreadItem p_threadItem, Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		if (p_threadItem != null && p_threadItem instanceof Thread) {
			x_thread = (Thread) p_threadItem;
		} else {
			x_thread = chooseThread(p_startingThread, p_enclosingPanel, "Add a Thread ?");
		}

		if (x_thread != null) {
			String x_name = (String) showInputDialog(p_enclosingPanel, "Enter new Thread:", "Add new Thread to '" + x_thread + "' ?", INFORMATION_MESSAGE, getThreadsIcon(), null, "New Thread");

			if (x_name != null) {
				Thread x_newThread = new Thread(x_name);
				x_thread.addThreadItem(x_newThread);
				WindowManager.getInstance().openComponent(x_newThread);
				return x_thread;
			}
		}

		return null;
	}

	public static Item addAction(ThreadItem p_threadItem, Thread p_startingThread, Date p_date, JPanel p_enclosingPanel, boolean p_openAfter) {
		Thread x_thread;

		if (p_threadItem instanceof Thread) {
			x_thread = (Thread) p_threadItem;
		} else if (p_threadItem instanceof Item) {
			x_thread = p_threadItem.getParentThread();
		} else {
			x_thread = chooseThread(p_startingThread, p_enclosingPanel, "Add an Action ?");
		}

		if (x_thread != null) {
			String x_text = (String) showInputDialog(p_enclosingPanel, "Enter new Action:", "Add new Action to '" + x_thread + "' ?", INFORMATION_MESSAGE, getThreadsIcon(), null, "New Action");

			if (x_text != null) {
				Date x_derivedDate = deriveDate(x_text, p_date);
				Item x_action = new Item(x_derivedDate != null ? x_text.substring(x_text.indexOf(" ") + 1) : x_text, x_derivedDate != null ? x_derivedDate : p_date);
				x_thread.addThreadItem(x_action);

				if (p_openAfter) {
					WindowManager.getInstance().openComponent(x_action);
				}

				return x_action;
			}
		}

		return null;
	}

	public static Item addUpdate(ThreadItem p_threadItem, Thread p_startingThread, JPanel p_enclosingPanel) {
		Thread x_thread;

		if (p_threadItem instanceof Thread) {
			x_thread = (Thread) p_threadItem;
		} else if (p_threadItem instanceof Item) {
			x_thread = p_threadItem.getParentThread();
		} else {
			x_thread = chooseThread(p_startingThread, p_enclosingPanel, "Add an Update ?");
		}

		if (x_thread != null) {
			String x_text = (String) showInputDialog(p_enclosingPanel, "Enter new Update:", "Add new Update to '" + x_thread + "' ?", INFORMATION_MESSAGE, getThreadsIcon(), null, "New Update");

			if (x_text != null) {
				Item x_update = new Item(x_text, null);
				x_thread.addThreadItem(x_update);

				if (getActiveUpdates(x_thread).size() == 2 && showConfirmDialog(p_enclosingPanel, "Set previous updates inactive ?", "Supersede Previous Updates ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getThreadsIcon()) == OK_OPTION) {
					x_thread.getThreadItems().stream()
							.filter(ti -> ti instanceof Item)
							.map(ti -> (Item) ti)
							.filter(i -> i != x_update && i.getDueDate() == null && i.isActive())
							.forEach(i -> i.setActive(false));
				}

				return x_update;
			}
		}

		return null;
	}

	static Reminder addReminder(Item p_item, JPanel p_enclosingPanel, boolean p_openAfter) {
		if (p_item.getDueDate() != null) {
			String x_text = (String) showInputDialog(p_enclosingPanel, "Enter new Reminder:", "Add new Reminder to '" + p_item + "' ?", INFORMATION_MESSAGE, getThreadsIcon(), null, "New Reminder");

			if(x_text != null) {
				Date x_derivedDate = deriveReminderDate(x_text, p_item.getDueDate());
				Reminder x_reminder = new Reminder(x_derivedDate != null ? x_text.substring(x_text.indexOf(" ") + 1) : x_text, x_derivedDate != null ? x_derivedDate : p_item.getDueDate());
				p_item.addReminder(x_reminder);

				if(p_openAfter) {
					WindowManager.getInstance().openComponent(x_reminder);
				}

				return x_reminder;
			}
		}

		return null;
	}

	private static Thread chooseThread(Thread p_startingThread, JPanel p_enclosingPanel, String x_title) {
		List<Thread> x_threads = getAllActiveThreads(p_startingThread);
		x_threads.add(0, p_startingThread);
		return x_threads.size() > 1 ? (Thread) showInputDialog(p_enclosingPanel, "Choose a Thread to add it to:", x_title, INFORMATION_MESSAGE, getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0)) : p_startingThread;
	}

	static void linkToGoogle(Thread x_thread, Configuration p_configuration, JPanel p_enclosingPanel) {
		linkToGoogle(getHasDueDates(x_thread, false), p_configuration, p_enclosingPanel);
	}

	static void linkToGoogle(Item p_item, Configuration p_configuration, final JPanel p_enclosingPanel) {
		linkToGoogle(getHasDueDates(p_item, false), p_configuration, p_enclosingPanel);
	}

	static void linkToGoogle(HasDueDate p_hasDueDate, Configuration p_configuration, JPanel p_enclosingPanel) {
		linkToGoogle(singletonList(p_hasDueDate), p_configuration, p_enclosingPanel);
	}

	static void linkToGoogle(List<HasDueDate> p_hasDueDates, Configuration p_configuration, final JPanel p_enclosingPanel) {
		if (checkGoogle(p_configuration, p_enclosingPanel) || p_hasDueDates.size() == 0) {
			return;
		}

		List<HasDueDate> x_unlinkedHasDueDates = p_hasDueDates.stream().filter(h -> !isLinked(((Component) h))).collect(Collectors.toList());

		if (x_unlinkedHasDueDates.size() > 0) {
			List<GoogleAccount> x_googleAccounts = getGoogleAccounts();
			GoogleAccount x_googleAccount;

			if(x_googleAccounts.size() == 0) {
				showMessageDialog(p_enclosingPanel, "No Google accounts found.", "No Can Do", INFORMATION_MESSAGE, getGoogleIcon());
				return;
			} else if(x_googleAccounts.size() == 1) {
				x_googleAccount = x_googleAccounts.get(0);
			} else {
				x_googleAccount = (GoogleAccount) showInputDialog(p_enclosingPanel, "Choose a Google Account:", "Link to Google Calendar ?", INFORMATION_MESSAGE, getGoogleIcon(), x_googleAccounts.toArray(new Object[x_googleAccounts.size()]), x_googleAccounts.get(0));
			}

			if(x_googleAccount != null) {
				String x_deltaString = x_unlinkedHasDueDates.size() != p_hasDueDates.size()  && x_unlinkedHasDueDates.size() > 1 ? " (of " + p_hasDueDates.size() + ")" : "";
				String x_confirmMessage = x_unlinkedHasDueDates.size() > 1 ? "Link " + x_unlinkedHasDueDates.size() + " item" + (x_unlinkedHasDueDates.size() > 1 ? "s" : "") + x_deltaString + " to '" + x_googleAccount + "' ?" : "Link '" + x_unlinkedHasDueDates.get(0) + "'" + x_deltaString + " to '" + x_googleAccount + "' ?";
				String x_successMessage = x_unlinkedHasDueDates.size() > 1 ? x_unlinkedHasDueDates.size() + " Item" + (x_unlinkedHasDueDates.size() > 1 ? "s were" : " was") + " linked to '" + x_googleAccount + "'" : "'" + x_unlinkedHasDueDates.get(0).getText() + "' was linked to '" + x_googleAccount + "'";

				if (showConfirmDialog(p_enclosingPanel, x_confirmMessage, "Link to Google Calendar ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getGoogleIcon()) == OK_OPTION) {
					GoogleLinkTask x_task = new GoogleLinkTask(x_googleAccount, x_unlinkedHasDueDates, new GoogleProgressWindow(p_enclosingPanel), new ProgressAdapter() {
						@Override
						public void success() {
							showMessageDialog(p_enclosingPanel, x_successMessage, "All Good", WARNING_MESSAGE, getGoogleIcon());
						}

						@Override
						public void error(String errorDesc) {
							showMessageDialog(p_enclosingPanel, "Error linking to Google Calendar: " + errorDesc, "No Can Do", ERROR_MESSAGE, getGoogleIcon());
						}
					});

					x_task.execute();
				}
			}
		} else {
			showMessageDialog(p_enclosingPanel, "All Items already linked.", "All Good", INFORMATION_MESSAGE, getGoogleIcon());
		}
	}

	private static boolean checkGoogle(Configuration p_configuration, JPanel p_enclosingPanel) {
		if(!p_configuration.getSettings().getBooleanSetting(GOOGLE)) {
			if(showConfirmDialog(p_enclosingPanel, "Google is disabled for '" + p_configuration.getXmlFile().getName() + "'. Would you like to enable it ?", "Enable Google ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getGoogleIcon()) == OK_OPTION) {
				p_configuration.getSettings().updateSetting(GOOGLE, true);
				return false;
			}

			return true;
		}

		return false;
	}

	static void moveThreadItem(ThreadItem p_threadItem, JPanel o_enclosingPanel) {
		if (p_threadItem != null) {
			Thread x_thread = null;

			Thread x_topThread = (Thread) p_threadItem.getHierarchy().get(0);
			List<Thread> x_threads = getAllActiveThreads(x_topThread);
			x_threads.add(0, x_topThread);
			x_threads.remove(p_threadItem.getParentThread());

			if (p_threadItem instanceof Thread) {
				x_threads.remove(p_threadItem);
				x_threads.removeAll(getAllActiveThreads((Thread) p_threadItem));
			}

			if (x_threads.size() > 0) {
				x_thread = (Thread) showInputDialog(o_enclosingPanel, "Choose a Thread to move it to:", "Move '" + p_threadItem + "' ?", INFORMATION_MESSAGE, getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				showMessageDialog(o_enclosingPanel, "This is no other Thread to move this " + p_threadItem.getType() + " to. Try creating another Thread.", "No Can Do", INFORMATION_MESSAGE, getThreadsIcon());
			}

			if (x_thread != null) {
				p_threadItem.moveTo(x_thread);
			}
		}
	}

	static void activateComponent(Component p_component, JPanel p_enclosingPanel) {
		if (p_component != null) {
			if (showConfirmDialog(p_enclosingPanel, "Set '" + p_component.getText() + "' active ?", "Set active ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getThreadsIcon()) == OK_OPTION) {
				p_component.setActive(true);
			}
		}
	}

	static void deactivateComponent(Component p_component, JPanel p_enclosingPanel) {
		if (p_component != null) {
			if (showConfirmDialog(p_enclosingPanel, "Set '" + p_component.getText() + "' inactive ?", "Set inactive ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getThreadsIcon()) == OK_OPTION) {
				p_component.setActive(false);
			}
		}
	}

	public static void removeComponent(Component p_component, JPanel p_enclosingPanel, boolean p_openParentAfter) {
		if (p_component != null) {
			Component x_parentComponent = p_component.getParentComponent();

			if(x_parentComponent != null) {
				if (showConfirmDialog(p_enclosingPanel, "Remove '" + p_component.getText() + "' from '" + x_parentComponent.getText() + "' ?", "Remove " + p_component.getType() + " ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getThreadsIcon()) == OK_OPTION) {
					if (p_component instanceof ThreadItem) {
						ThreadItem x_threadItem = (ThreadItem) p_component;
						x_threadItem.getParentThread().removeThreadItem(x_threadItem);
					} else {
						Item x_item = (Item) x_parentComponent;
						x_item.removeReminder((Reminder) p_component);
					}

					if (p_openParentAfter) {
						WindowManager.getInstance().openComponent(x_parentComponent);
					}
				}
			} else {
				showMessageDialog(p_enclosingPanel, "The top Thread cannot be removed.", "No can do", WARNING_MESSAGE, getThreadsIcon());
			}
		}
	}
}
