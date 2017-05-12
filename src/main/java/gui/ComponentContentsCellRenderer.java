package gui;

import data.*;
import data.Thread;

import java.util.*;

class ComponentContentsCellRenderer extends DataItemsCellRenderer<Component, Component> {
    ComponentContentsCellRenderer(Component p_component) {
        super(p_component);
    }

	@Override
	List<Component> getDataItems(Component p_component) {
		List<Component> x_components = new ArrayList<>();

		if(p_component instanceof Thread) {
			x_components.addAll(((Thread)p_component).getThreadItems());
		} else if(p_component instanceof Item) {
			x_components.addAll(((Item)p_component).getReminders());
		}

		return x_components;
	}
}
