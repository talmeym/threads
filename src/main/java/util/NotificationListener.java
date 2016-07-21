package util;

import data.Component;

import java.util.List;

public interface NotificationListener {
	public void componentsDue(List<Component> p_dueComponents);
}
