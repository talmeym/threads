package util;

import data.Component;
import data.*;
import data.Thread;
import gui.WindowManager;

import java.awt.*;
import java.util.*;

import static data.ComponentChangeEvent.Field.TEXT;
import static gui.Actions.*;
import static gui.DateSuggestionPanel.getDateSuggestion;
import static util.ImageUtil.getThreadsImage;

public class SystemTrayUtil {
	private static PopupMenu o_popUpMenu;
	private static TrayIcon o_trayIcon;
	private static Thread o_topLevelThread;

	private static Map<Component, MenuItem> o_menuItems = new HashMap<>();

	public static void initialise(Thread p_topLevelThread) {
		o_topLevelThread = p_topLevelThread;

		try {
			o_popUpMenu = new PopupMenu();

			MenuItem x_addActionItem = new MenuItem("Add Action");
			o_popUpMenu.add(x_addActionItem);

			x_addActionItem.addActionListener(e -> addAction(null, o_topLevelThread, getDateSuggestion(), null, true));

			MenuItem x_addUpdateItem = new MenuItem("Add Update");
			o_popUpMenu.add(x_addUpdateItem);

			x_addUpdateItem.addActionListener(e -> addUpdate(null, o_topLevelThread, null));

			o_trayIcon = new TrayIcon(getThreadsImage(), "Threads", o_popUpMenu);
			o_trayIcon.setImageAutoSize(true);
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.add(o_trayIcon);

			NotificationUpdater.getInstance().addNotificationListener(p_dueComponents -> {
				if (p_dueComponents.size() == 1) {
					Component x_component = p_dueComponents.get(0);
					displayNotification(x_component instanceof Item ? "Action Overdue" : "Reminder", x_component.getText());
				} else if (p_dueComponents.size() > 1) {
					displayNotification("Threads", "You have " + p_dueComponents.size() + " new notifications.");
				}

				for (final Component x_component : p_dueComponents) {
					if(!o_menuItems.containsKey(x_component)) {
						MenuItem x_menuItem = new MenuItem((getMenuItemText(x_component)));
						o_menuItems.put(x_component, x_menuItem);

						x_menuItem.addActionListener(e -> WindowManager.getInstance().openComponent(x_component));

						x_component.addComponentChangeListener(e -> {
							if(e.getSource() == x_component) {
								if(e.isComponentRemoved()) {
									o_popUpMenu.remove(o_menuItems.get(x_component));
								} else if(e.getField() == TEXT) {
									o_menuItems.get(x_component).setLabel(getMenuItemText(x_component));
								}
							}
						});

						if(o_popUpMenu.getItemCount() == 2) {
							o_popUpMenu.addSeparator();
						}

						o_popUpMenu.add(x_menuItem);
					}
				}
			});
		} catch (AWTException e) {
			// do nothing for now TODO ??
		}
	}

	private static String getMenuItemText(Component x_component) {
		return (x_component instanceof Item ? "Action Overdue" : "Reminder") + ": " + x_component.getText();
	}

	private static void displayNotification(String caption, String text) {
		o_trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
	}
}
