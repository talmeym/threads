package threads.gui;

import threads.data.Component;
import threads.data.Item;
import threads.data.Reminder;
import threads.data.Thread;
import threads.util.SettingChangeListener;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static threads.util.Settings.*;

class NavigationAndComponentPanel extends JPanel implements SettingChangeListener {
	private final CardLayout o_cardLayout = new CardLayout();
	private final JPanel o_cardPanel = new JPanel(o_cardLayout);

	private final Map<UUID, JPanel> o_componentPanels = new HashMap<>();
	private final NavigationPanel o_navigationPanel;
	private final JFrame o_frame;

	NavigationAndComponentPanel(Thread p_topLevelThread, JFrame p_frame) {
		super(new BorderLayout());
		o_frame = p_frame;

		o_navigationPanel = new NavigationPanel(p_topLevelThread);

		JSplitPane x_splitPane = new JSplitPane(HORIZONTAL_SPLIT);
		x_splitPane.setLeftComponent(o_navigationPanel);
		x_splitPane.setRightComponent(o_cardPanel);
		x_splitPane.setDividerLocation(registerForSetting(s_NAVDIVLOC, this, 250));

		x_splitPane.addPropertyChangeListener(propertyChangeEvent -> {
			if(propertyChangeEvent.getPropertyName().equals("dividerLocation")) {
				updateSetting(s_NAVDIVLOC, "" + propertyChangeEvent.getNewValue());
			}
		});

		add(x_splitPane, BorderLayout.CENTER);
	}

	void showComponent(Component p_component) {
		if(!o_componentPanels.containsKey(p_component.getId())) {
			JPanel x_panel = makeComponentPanel(p_component);
			o_cardPanel.add(x_panel, p_component.getId().toString());
			o_componentPanels.put(p_component.getId(), x_panel);
		}

		// TODO better way to select reminder in ItemReminderPanel panel, other than this hack?
		if(p_component instanceof Reminder) {
			((ItemAndReminderPanel)o_componentPanels.get(p_component.getId())).showReminder((Reminder)p_component);
		}

		o_cardLayout.show(o_cardPanel, p_component.getId().toString());
		o_navigationPanel.selectComponent(p_component);
	}

	private JPanel makeComponentPanel(Component p_component) {
		JPanel x_panel = null;

		if(p_component instanceof threads.data.Thread) {
			x_panel = new ThreadPanel((Thread) p_component, this);
		}

		if(p_component instanceof Item) {
			x_panel = new ItemAndReminderPanel((Item) p_component, this, o_frame);
		}

		if(p_component instanceof Reminder) {
			x_panel = new ItemAndReminderPanel(((Reminder)p_component).getParentItem(), this, o_frame);
		}

		return x_panel;
	}

	@Override
	public void settingChanged(String p_name, Object p_value) {
		// do nothing
	}
}
