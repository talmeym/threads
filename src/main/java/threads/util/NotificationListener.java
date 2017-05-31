package threads.util;

import threads.data.Component;

import java.util.List;

interface NotificationListener {
	void componentsDue(List<Component> p_dueComponents);
}
