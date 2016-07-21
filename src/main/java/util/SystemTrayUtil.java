package util;

import data.Component;
import data.*;
import gui.WindowManager;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SystemTrayUtil {
	private static PopupMenu o_popUpMenu;
	private static TrayIcon o_trayIcon;

	public static void initialise() {
		try {
			o_popUpMenu = new PopupMenu();
			o_trayIcon = new TrayIcon(ImageUtil.getThreadsImage(), "Threads", o_popUpMenu);
			o_trayIcon.setImageAutoSize(true);
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.add(o_trayIcon);

			NotificationUpdater.getInstance().addNotificationListener(new NotificationListener() {
				@Override
				public void componentsDue(List<Component> p_dueComponents) {

					if (p_dueComponents.size() == 1) {
						Component component = p_dueComponents.get(0);
						o_trayIcon.displayMessage(component instanceof Item ? "Action Overdue" : "Reminder", component.getText(), TrayIcon.MessageType.INFO);
					} else if (p_dueComponents.size() > 1) {
						o_trayIcon.displayMessage("Threads", "You have " + p_dueComponents.size() + " new notifications.", TrayIcon.MessageType.INFO);
					}

					for (final Component component : p_dueComponents) {
						String menuItemText = (component instanceof Item ? "Action Overdue" : "Reminder") + ": " + component.getText();
						MenuItem menuItem = new MenuItem(menuItemText);

						menuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent actionEvent) {
								if(component.getParentComponent() != null) {
									WindowManager.getInstance().openComponent(component);
								} else {
									o_trayIcon.displayMessage("Threads", "The Item you've selected no longer exists", TrayIcon.MessageType.INFO);
								}
							}
						});

						o_popUpMenu.add(menuItem);
					}
				}
			});
		} catch (AWTException e) {
			// do nothing for now TODO ??
		}
	}
}
