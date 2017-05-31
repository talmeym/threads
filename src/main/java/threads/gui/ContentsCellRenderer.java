package threads.gui;

import threads.data.Component;
import threads.data.Item;
import threads.data.Thread;

import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.black;
import static java.awt.Color.gray;

class ContentsCellRenderer extends DataItemsCellRenderer<Component, Component> {
    ContentsCellRenderer(Component p_component) {
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

	@Override
	void customSetup(Component p_component, java.awt.Component p_awtComponent, boolean p_isSelected) {
		p_awtComponent.setForeground(p_component.isActive() ? black : gray);
	}
}
