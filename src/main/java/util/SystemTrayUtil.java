package util;

import data.Component;
import data.*;
import data.Thread;
import gui.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static gui.Actions.addAction;
import static gui.Actions.addUpdate;

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

			x_addActionItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					WindowManager.makeThreadsVisible();
					addAction(null, o_topLevelThread, null);
				}
			});

			MenuItem x_addUpdateItem = new MenuItem("Add Update");
			o_popUpMenu.add(x_addUpdateItem);

			x_addUpdateItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					WindowManager.makeThreadsVisible();
					addUpdate(null, o_topLevelThread, null);
				}
			});

			o_trayIcon = new TrayIcon(ImageUtil.getThreadsImage(), "Threads", o_popUpMenu);
			o_trayIcon.setImageAutoSize(true);
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.add(o_trayIcon);

			NotificationUpdater.getInstance().addNotificationListener(new NotificationListener() {
				@Override
				public void componentsDue(List<Component> p_dueComponents) {

					if (p_dueComponents.size() == 1) {
						Component component = p_dueComponents.get(0);
						displayNotification(component instanceof Item ? "Action Overdue" : "Reminder", component.getText());
					} else if (p_dueComponents.size() > 1) {
						displayNotification("Threads", "You have " + p_dueComponents.size() + " new notifications.");
					}

					for (final Component component : p_dueComponents) {
						String menuItemText = (component instanceof Item ? "Action Overdue" : "Reminder") + ": " + component.getText();
						MenuItem menuItem = new MenuItem(menuItemText);

						menuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent actionEvent) {
								WindowManager.makeThreadsVisible();
								if(component.getParentComponent() != null) {
									WindowManager.getInstance().openComponent(component);
								} else {
									displayNotification("Threads", "The Item you've selected no longer exists");
								}
							}
						});

						if(o_popUpMenu.getItemCount() == 1) {
							o_popUpMenu.addSeparator();
						}

						o_popUpMenu.add(menuItem);
					}
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
