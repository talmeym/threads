package util;

import data.*;
import data.Component;
import data.Thread;

import java.util.*;
import java.util.List;

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

	private final List<NotificationListener> o_notificationListeners = new ArrayList<NotificationListener>();
	private final List<Component> o_alertedComponents = new ArrayList<Component>();
	private final Thread o_topLevelThread;

	private NotificationUpdater(Thread p_topLevelThread) {
		o_topLevelThread = p_topLevelThread;
		TimeUpdater.getInstance().addTimeUpdateListener(new TimeUpdateListener() {
			@Override
			public void timeUpdate() {
				processAlerts();
			}
		});
	}

	public synchronized void addNotificationListener(NotificationListener p_listener) {
		o_notificationListeners.add(p_listener);
		processAlerts();
	}

	private void processAlerts() {
		List<Component> x_dueComponents = new ArrayList<Component>();
		x_dueComponents.addAll(LookupHelper.getAllActiveReminders(o_topLevelThread, true));
		x_dueComponents.addAll(LookupHelper.getAllDueActions(o_topLevelThread));
		x_dueComponents.removeAll(o_alertedComponents);

		if(x_dueComponents.size() > 0) {
			for(NotificationListener x_listener: o_notificationListeners) {
				x_listener.componentsDue(x_dueComponents);
			}

			o_alertedComponents.addAll(x_dueComponents);

			for(final Component x_component: x_dueComponents) {
				x_component.addComponentChangeListener(new ComponentChangeListener() {
					@Override
					public void componentChanged(ComponentChangeEvent p_cce) {
						if (p_cce.getSource() == x_component && p_cce.getType() == ComponentChangeEvent.s_CHANGE) {
							o_alertedComponents.remove(x_component);
						}
					}
				});
			}
		}
	}
}
