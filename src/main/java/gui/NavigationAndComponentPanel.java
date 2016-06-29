package gui;

import data.*;
import data.Component;
import data.Thread;

import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.util.*;

public class NavigationAndComponentPanel extends MemoryPanel {
	private final CardLayout o_cardLayout = new CardLayout();
	private final JPanel o_cardPanel = new JPanel(o_cardLayout);

	private final Map<UUID, JPanel> o_componentPanels = new HashMap<UUID, JPanel>();
	private final NavigationPanel o_navigationPanel;
	private Component o_component;

	public NavigationAndComponentPanel(Thread p_topLevelThread, Component p_firstComponent) {
		super(new BorderLayout());

		o_navigationPanel = new NavigationPanel(p_topLevelThread);

		JSplitPane x_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		x_splitPane.setLeftComponent(o_navigationPanel);
		x_splitPane.setRightComponent(o_cardPanel);
		x_splitPane.setDividerLocation(recallValue(250));

		x_splitPane.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				if(propertyChangeEvent.getPropertyName().equals("dividerLocation")) {
					rememberValue((Integer)propertyChangeEvent.getNewValue());
				}
			}
		});

		add(x_splitPane, BorderLayout.CENTER);

		showComponent(p_firstComponent);
	}

	public void showComponent(Component p_component) {
		o_component = p_component;

		if(!o_componentPanels.containsKey(p_component.getId())) {
			JPanel x_panel = makeComponentPanel(p_component);
			o_cardPanel.add(x_panel, p_component.getId().toString());
			o_componentPanels.put(p_component.getId(), x_panel);
		}

		o_cardLayout.show(o_cardPanel, p_component.getId().toString());

		o_navigationPanel.selectComponent(p_component);
	}

	public JPanel makeComponentPanel(Component p_component) {
		JPanel x_panel = null;

		if(p_component instanceof data.Thread) {
			x_panel = new ThreadPanel((Thread) p_component);
		}

		if(p_component instanceof Item) {
			x_panel = new ItemAndReminderPanel((Item) p_component);
		}

		if(p_component instanceof Reminder) {
			x_panel = new ItemAndReminderPanel((Reminder) p_component);
		}

		return x_panel;
	}

	public Component getComponent() {
		return o_component;
	}
}
