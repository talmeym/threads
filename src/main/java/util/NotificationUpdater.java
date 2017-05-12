package util;

import data.*;
import data.Component;
import data.Thread;

import java.util.*;
import java.util.List;

import static data.ComponentChangeEvent.s_CHANGED;
import static data.ComponentChangeEvent.s_DUE_DATE;

public class NotificationUpdater {
	private static NotificationUpdater s_INSTANCE = null;

	public static void initialise(Thread p_topLevelThread) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise notification updater twice");
		}

		s_INSTANCE = new NotificationUpdater(p_topLevelThread);
	}

	public static NotificationUpdater getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Notification updater not initialised");
		}

		return s_INSTANCE;
	}

	private final List<NotificationListener> o_notificationListeners = new ArrayList<>();
	private final List<Component> o_alertedComponents = new ArrayList<>();
	private final Thread o_topLevelThread;

	private NotificationUpdater(Thread p_topLevelThread) {
		o_topLevelThread = p_topLevelThread;
		TimedUpdater.getInstance().addActivityListener(this::processAlerts);
	}

	synchronized void addNotificationListener(NotificationListener p_listener) {
		o_notificationListeners.add(p_listener);
		processAlerts();
	}

	private void processAlerts() {
		List<Component> x_dueComponents = new ArrayList<>();
		x_dueComponents.addAll(LookupHelper.getAllActiveReminders(o_topLevelThread, true));
		x_dueComponents.addAll(LookupHelper.getAllActiveDueActions(o_topLevelThread));
		x_dueComponents.removeAll(o_alertedComponents);

		if(x_dueComponents.size() > 0) {
			o_notificationListeners.forEach(x_listener -> x_listener.componentsDue(x_dueComponents));
			o_alertedComponents.addAll(x_dueComponents);

			x_dueComponents.forEach(x_component -> x_component.addComponentChangeListener(e -> {
				if (e.getSource() == x_component && e.getType() == s_CHANGED && e.getIndex() == s_DUE_DATE) {
					o_alertedComponents.remove(x_component);
				}
			}));
		}
	}
}
