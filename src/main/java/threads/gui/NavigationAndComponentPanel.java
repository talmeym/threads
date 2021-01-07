package threads.gui;

import threads.data.Component;
import threads.data.Thread;
import threads.data.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static threads.util.Settings.Setting.NAVDIVLOC;

class NavigationAndComponentPanel extends JPanel {
    private final Configuration o_configuration;

	private final CardLayout o_cardLayout = new CardLayout();
	private final JPanel o_cardPanel = new JPanel(o_cardLayout);

	private final Map<Component, JPanel> o_componentPanels = new HashMap<>();
	private final NavigationPanel o_navigationPanel;
	private final JFrame o_frame;

	NavigationAndComponentPanel(Configuration p_configuration, JFrame p_frame) {
		super(new BorderLayout());
		o_frame = p_frame;
		o_navigationPanel = new NavigationPanel(p_configuration, p_frame);
		o_configuration = p_configuration;

		JSplitPane x_splitPane = new JSplitPane(HORIZONTAL_SPLIT);
		x_splitPane.setLeftComponent(o_navigationPanel);
		x_splitPane.setRightComponent(o_cardPanel);
		x_splitPane.setDividerLocation(o_configuration.getSettings().getIntSetting(NAVDIVLOC));

		x_splitPane.addPropertyChangeListener(propertyChangeEvent -> {
			if(propertyChangeEvent.getPropertyName().equals("dividerLocation")) {
				o_configuration.getSettings().updateSetting(NAVDIVLOC, "" + propertyChangeEvent.getNewValue());
			}
		});

		add(x_splitPane, BorderLayout.CENTER);
	}

	void showComponent(Component p_component) {
		if(!o_componentPanels.containsKey(p_component)) {
			JPanel x_panel = makeComponentPanel(p_component);
			o_cardPanel.add(x_panel, p_component.getId().toString());
			o_componentPanels.put(p_component, x_panel);
		}

		// TODO better way to select reminder in ItemReminderPanel panel, other than this hack?
		if(p_component instanceof Reminder) {
			((ItemAndReminderPanel)o_componentPanels.get(p_component)).showReminder((Reminder)p_component);
		}

		o_cardLayout.show(o_cardPanel, p_component.getId().toString());
		o_navigationPanel.selectComponent(p_component);
	}

	private JPanel makeComponentPanel(Component p_component) {
		JPanel x_panel = null;

		if(p_component instanceof threads.data.Thread) {
			x_panel = new ThreadPanel(o_configuration, (Thread) p_component, this, o_frame);
		}

		if(p_component instanceof Item) {
			x_panel = new ItemAndReminderPanel(o_configuration, (Item) p_component, this, o_frame);
		}

		if(p_component instanceof Reminder) {
			x_panel = new ItemAndReminderPanel(o_configuration, ((Reminder)p_component).getParentItem(), this, o_frame);
		}

		return x_panel;
	}
}
