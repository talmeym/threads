package util;

import data.Component;
import data.*;
import data.Thread;
import gui.*;

import java.awt.*;

import static gui.Actions.addAction;
import static gui.Actions.addUpdate;
import static gui.DateSuggestionPanel.getDateSuggestion;

public class SystemTrayUtil {
	private static PopupMenu o_popUpMenu;
	private static TrayIcon o_trayIcon;
	private static Thread o_topLevelThread;

	public static void initialise(Thread p_topLevelThread) {
		o_topLevelThread = p_topLevelThread;

		try {
			o_popUpMenu = new PopupMenu();

			MenuItem x_addActionItem = new MenuItem("Add Action");
			o_popUpMenu.add(x_addActionItem);

			x_addActionItem.addActionListener(e -> {
				WindowManager.makeThreadsVisible();
				addAction(null, o_topLevelThread, getDateSuggestion(), null, true);
			});

			MenuItem x_addUpdateItem = new MenuItem("Add Update");
			o_popUpMenu.add(x_addUpdateItem);

			x_addUpdateItem.addActionListener(e -> {
				WindowManager.makeThreadsVisible();
				addUpdate(null, o_topLevelThread, null);
			});

			o_trayIcon = new TrayIcon(ImageUtil.getThreadsImage(), "Threads", o_popUpMenu);
			o_trayIcon.setImageAutoSize(true);
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.add(o_trayIcon);

			NotificationUpdater.getInstance().addNotificationListener(p_dueComponents -> {
				if (p_dueComponents.size() == 1) {
					displayNotification(p_dueComponents.get(0) instanceof Item ? "Action Overdue" : "Reminder", p_dueComponents.get(0).getText());
				} else if (p_dueComponents.size() > 1) {
					displayNotification("Threads", "You have " + p_dueComponents.size() + " new notifications.");
				}

				for (final Component x_component : p_dueComponents) {
					MenuItem x_menuItem = new MenuItem((x_component instanceof Item ? "Action Overdue" : "Reminder") + ": " + x_component.getText());

					x_menuItem.addActionListener(actionEvent -> {
						WindowManager.makeThreadsVisible();
						if(x_component.getParentComponent() != null) {
							WindowManager.getInstance().openComponent(x_component);
						} else {
							displayNotification("Threads", "The Item you've selected no longer exists");
						}
					});

					if(o_popUpMenu.getItemCount() == 2) {
						o_popUpMenu.addSeparator();
					}

					o_popUpMenu.add(x_menuItem);
				}
			});
		} catch (AWTException e) {
			// do nothing for now TODO ??
		}
	}

	private static void displayNotification(String caption, String text) {
		WindowManager.makeThreadsVisible();
		o_trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
	}
}
