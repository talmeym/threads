package gui;

import data.*;
import data.Component;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.util.*;

import static util.Settings.registerForSetting;
import static util.Settings.updateSetting;

public class NavigationAndComponentPanel extends JPanel implements SettingChangeListener {
	private final CardLayout o_cardLayout = new CardLayout();
	private final JPanel o_cardPanel = new JPanel(o_cardLayout);

	private final Map<UUID, JPanel> o_componentPanels = new HashMap<UUID, JPanel>();
	private final NavigationPanel o_navigationPanel;

	public NavigationAndComponentPanel(Thread p_topLevelThread) {
		super(new BorderLayout());

		o_navigationPanel = new NavigationPanel(p_topLevelThread);

		JSplitPane x_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		x_splitPane.setLeftComponent(o_navigationPanel);
		x_splitPane.setRightComponent(o_cardPanel);
		x_splitPane.setDividerLocation(registerForSetting(Settings.s_NAVDIVLOC, this, 250));

		x_splitPane.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				if(propertyChangeEvent.getPropertyName().equals("dividerLocation")) {
					updateSetting(Settings.s_NAVDIVLOC, "" + propertyChangeEvent.getNewValue());
				}
			}
		});

		add(x_splitPane, BorderLayout.CENTER);
	}

	public void showComponent(Component p_component) {
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

	@Override
	public void settingChanged(String name, Object value) {
		// do nothing
	}
}
