package threads.util;

import threads.data.Component;
import threads.data.Configuration;
import threads.data.Thread;

import java.util.ArrayList;
import java.util.List;

import static threads.data.ComponentChangeEvent.Field.DUE_DATE;
import static threads.data.LookupHelper.getAllActiveActions;
import static threads.data.LookupHelper.getAllActiveReminders;
import static threads.data.View.DUE;

public class NotificationUpdater {
	private static NotificationUpdater s_INSTANCE = null;

	public static void initialise() {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise notification updater twice");
		}

		s_INSTANCE = new NotificationUpdater();
	}

	public static NotificationUpdater getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Notification updater not initialised");
		}

		return s_INSTANCE;
	}

	private final List<NotificationListener> o_notificationListeners = new ArrayList<>();
	private final List<Component> o_alertedComponents = new ArrayList<>();
	private final List<Configuration> o_configurations;

	private NotificationUpdater() {
		o_configurations = new ArrayList<>();
		TimedUpdater.getInstance().addActivityListener(this::processAlerts);
	}

	public void addConfiguration(Configuration p_configuration) {
		o_configurations.add(p_configuration);
	}

	public void removeConfiguration(Configuration p_configuration) {
		o_configurations.remove(p_configuration);
	}

	synchronized void addNotificationListener(NotificationListener p_listener) {
		o_notificationListeners.add(p_listener);
		processAlerts();
	}

	private void processAlerts() {
		List<Component> x_dueComponents = new ArrayList<>();
		for(Configuration x_configuration: o_configurations) {
			Thread x_topLevelThread = x_configuration.getTopLevelThread();
			x_dueComponents.addAll(getAllActiveReminders(x_topLevelThread, DUE));
			x_dueComponents.addAll(getAllActiveActions(x_topLevelThread, DUE));
			x_dueComponents.removeAll(o_alertedComponents);

			if (x_dueComponents.size() > 0) {
				o_notificationListeners.forEach(x_listener -> x_listener.componentsDue(x_dueComponents));
				o_alertedComponents.addAll(x_dueComponents);

				x_dueComponents.forEach(x_component -> x_component.addComponentChangeListener(e -> {
					if (e.getSource() == x_component && e.getField() == DUE_DATE) {
						o_alertedComponents.remove(x_component);
					}
				}));
			}
		}
	}
}
