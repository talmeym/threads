package gui;

import data.Component;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class WindowManager {
	public static final String s_UUID = "uuid";
	private static WindowManager s_INSTANCE;

	public static void initialise(Thread p_topLevelThread, WindowListener p_listener) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise window manager twice");
		}

		s_INSTANCE = new WindowManager(p_topLevelThread, p_listener);
	}

    public static WindowManager getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Window manager not initialised");
		}

        return s_INSTANCE;
    }

	private static Dimension o_windowSize = GUIConstants.s_windowSize;
	private static Point o_windowLocation = GUIConstants.s_windowLocation;
	private static UUID o_uuid;

	private final JFrame o_window = new JFrame();
	private final NavigationAndComponentPanel o_navigationAndComponentPanel;

	private WindowManager(final Thread p_topLevelThread, final WindowListener p_listener) {
		ImageUtil.addIcon(o_window);
		o_navigationAndComponentPanel = new NavigationAndComponentPanel(p_topLevelThread);
		o_window.setContentPane(o_navigationAndComponentPanel);

		o_window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				o_windowLocation = o_window.getLocation();
				o_windowSize = o_window.getSize();
				p_listener.windowClosing(windowEvent);
			}
		});

		Component x_firstComponent = o_uuid != null ? p_topLevelThread.findComponent(o_uuid) : p_topLevelThread;
		o_navigationAndComponentPanel.showComponent(x_firstComponent);

		o_window.setSize(o_windowSize);
		o_window.setLocation(o_windowLocation);
		o_window.setTitle(x_firstComponent.getType() + " : " + x_firstComponent.getText());
		o_window.setVisible(true);

	}

	public void openComponent(Component p_component) {
		o_navigationAndComponentPanel.showComponent(p_component);
		o_window.setTitle(p_component.getType() + " : " + p_component.getText());
		o_uuid = p_component.getId();
	}

	public static void applySettingsFromProperties(Properties p_properties) {
		if(p_properties.containsKey("winw")) {
			o_windowSize = new Dimension(Integer.parseInt(p_properties.getProperty("winw")), Integer.parseInt(p_properties.getProperty("winh")));
		}

		if(p_properties.containsKey("winx")) {
			o_windowLocation = new Point(Integer.parseInt(p_properties.getProperty("winx")), Integer.parseInt(p_properties.getProperty("winy")));
		}

		if(p_properties.containsKey(s_UUID)) {
			o_uuid = UUID.fromString(p_properties.getProperty(s_UUID));
		}
	}

	public static void saveToProperties(Properties p_properties) {
		if(o_windowSize != null) {
			p_properties.setProperty("winw", String.valueOf((int) o_windowSize.getWidth()));
			p_properties.setProperty("winh", String.valueOf((int) o_windowSize.getHeight()));
		}

		if(o_windowLocation != null) {
			p_properties.setProperty("winx", String.valueOf((int) o_windowLocation.getX()));
			p_properties.setProperty("winy", String.valueOf((int) o_windowLocation.getY()));
		}

		if(o_uuid != null) {
			p_properties.setProperty(s_UUID, o_uuid.toString());
		}
	}
}