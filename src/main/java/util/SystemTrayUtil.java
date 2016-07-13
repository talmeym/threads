package util;

import data.Component;
import data.*;
import data.Thread;
import gui.WindowManager;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SystemTrayUtil {
	private static PopupMenu o_popUpMenu;
	private static TrayIcon o_trayIcon;
	private static Thread o_topLevelThread;
	private static List<Component> o_alertedComponents = new ArrayList<Component>();

	public static void initialise(Thread p_topLevelThread) {
		try {
			o_topLevelThread = p_topLevelThread;
			o_popUpMenu = new PopupMenu();
			o_trayIcon = new TrayIcon(ImageUtil.getThreadsImage(), "Threads", o_popUpMenu);
			o_trayIcon.setImageAutoSize(true);
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.add(o_trayIcon);
			processAlerts();
			TimeUpdater.getInstance().addTimeUpdateListener(new TimeUpdateListener() {
				@Override
				public void timeUpdate() {
					processAlerts();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void processAlerts() {
		List<Component> dueAlerts = new ArrayList<Component>();
		dueAlerts.addAll(LookupHelper.getAllDueReminders(o_topLevelThread));
		dueAlerts.addAll(LookupHelper.getAllDueActions(o_topLevelThread));
		dueAlerts.removeAll(o_alertedComponents);

		if(dueAlerts.size() == 1) {
			Component component = dueAlerts.get(0);
			o_trayIcon.displayMessage(component instanceof Item ? "Action Overdue" : "Reminder", component.getText(), TrayIcon.MessageType.INFO);
			haveAlerted(component);
		} else {
			if(dueAlerts.size() > 1) {
				o_trayIcon.displayMessage("Threads", "You have multiple notifications.", TrayIcon.MessageType.INFO);

				for(Component component: dueAlerts) {
					haveAlerted(component);
				}
			}
		}
	}

	private static void haveAlerted(final Component component) {
		o_alertedComponents.add(component);

		component.addComponentChangeListener(new ComponentChangeListener() {
			@Override
			public void componentChanged(ComponentChangeEvent p_event) {
				if (p_event.getSource() == component && p_event.getType() == ComponentChangeEvent.s_CHANGE) {
					o_alertedComponents.remove(component);
				}
			}
		});

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
