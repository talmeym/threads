package util;

import data.Component;

import java.util.List;

interface NotificationListener {
	void componentsDue(List<Component> p_dueComponents);
}
