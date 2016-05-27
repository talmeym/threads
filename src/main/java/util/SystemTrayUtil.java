package util;

import data.Component;
import data.*;
import data.Thread;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SystemTrayUtil {
	private static TrayIcon o_trayIcon;
	private static Thread o_topLevelThread;

	private static List<Component> alertedComponents = new ArrayList<Component>();

	public static void initialise(Thread p_topLevelThread) {
		try {
			o_trayIcon = new TrayIcon(ImageUtil.getThreadsImage());
			o_topLevelThread = p_topLevelThread;
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.add(o_trayIcon);
			alertMultipleActionsReminders();
			TimeUpdater.getInstance().addTimeUpdateListener(new TimeUpdateListener() {
				@Override
				public void timeUpdate() {
					alertIndividualActionsReminders();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void alertMultipleActionsReminders() {
		List<Component> currentAlerts = new ArrayList<Component>();
		currentAlerts.addAll(LookupHelper.getAllDueReminders(o_topLevelThread));
		currentAlerts.addAll(LookupHelper.getAllDueActions(o_topLevelThread));

		if(currentAlerts.size() == 1) {
			alertIndividualActionsReminders();
		} else {
			if(currentAlerts.size() > 1) {
				o_trayIcon.displayMessage("Threads", "You have multiple notifications.", TrayIcon.MessageType.INFO);

				for(Component alert: currentAlerts) {
					haveAlerted(alert);
				}
			}
		}
	}

	private static void alertIndividualActionsReminders() {
		List dueReminders = LookupHelper.getAllDueReminders(o_topLevelThread);

		if(dueReminders.size() > 0) {
			for(Object obj: dueReminders) {
				if(alertComponent((Component)obj, "Reminder")){
					return;
				}
			}
		}

		List dueActions = LookupHelper.getAllDueActions(o_topLevelThread);

		if(dueActions.size() > 0) {
			for(Object obj: dueActions) {
				if(alertComponent((Component)obj, "Action Overdue")){
					return;
				}
			}
		}
	}

	public synchronized static boolean alertComponent(Component component, String type) {
		if(!alertedComponents.contains(component)) {
			o_trayIcon.displayMessage(type, component.getText(), TrayIcon.MessageType.INFO);
			haveAlerted(component);
			return true;
		}

		return false;
	}

	private static void haveAlerted(Component component) {
		alertedComponents.add(component);

		component.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object o) {
				ObservableChangeEvent event = (ObservableChangeEvent) o;

				if(event.getType() == ObservableChangeEvent.s_CHANGE) {
					Component component = (Component) observable;
					alertedComponents.remove(component);
					component.deleteObserver(this);
				}
			}
		});
	}
}
