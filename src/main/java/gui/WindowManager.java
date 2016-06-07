package gui;

import data.*;
import data.Component;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class WindowManager {
    private static WindowManager s_INSTANCE;

	public static void initialise(Thread p_topLevelThread, String p_filePath, Properties p_settings) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise window manager twice");
		}

		s_INSTANCE = new WindowManager(p_topLevelThread, p_filePath, p_settings);
	}

    public static WindowManager getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Window manager not initialised");
		}

        return s_INSTANCE;
    }

	private NavigationWindow o_navigationWindow;
	private Map<Component, JFrame> o_windows = new HashMap<Component, JFrame>();

	private Map<Class, Dimension> o_windowDimensions = new HashMap<Class, Dimension>();
	private Point o_windowLocation = null;
	private Dimension o_navSize = GUIConstants.s_navWindowSize;
	private Point o_navLocation = new Point(250, 200);

	private WindowManager(final Thread p_topLevelThread, final String filePath, final Properties p_settings) {
		applySettings(p_settings);

		o_navigationWindow = new NavigationWindow(p_topLevelThread);
		o_navigationWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				TimedSaver.getInstance().stopRunning();
				Saver.saveDocument(p_topLevelThread, filePath, getSettings());
				System.exit(0);
			}
		});

		ImageUtil.addIconToWindow(o_navigationWindow);
		o_navigationWindow.setSize(o_navSize);
		o_navigationWindow.setLocation(o_navLocation);
		o_navigationWindow.setVisible(true);
	}

	public Properties getSettings() {
		Properties x_properties = new Properties();

		for(Class clazz: o_windowDimensions.keySet()) {
			x_properties.setProperty("winw_" + clazz.getName(), String.valueOf((int)o_windowDimensions.get(clazz).getWidth()));
			x_properties.setProperty("winh_" + clazz.getName(), String.valueOf((int)o_windowDimensions.get(clazz).getHeight()));
		}

		if(o_windowLocation != null) {
			x_properties.setProperty("winx", String.valueOf((int)o_windowLocation.getX()));
			x_properties.setProperty("winy", String.valueOf((int)o_windowLocation.getY()));
		}

		x_properties.setProperty("navx", String.valueOf((int)o_navigationWindow.getLocation().getX()));
		x_properties.setProperty("navy", String.valueOf((int)o_navigationWindow.getLocation().getY()));

		x_properties.setProperty("navw", String.valueOf((int)o_navigationWindow.getSize().getWidth()));
		x_properties.setProperty("navh", String.valueOf((int)o_navigationWindow.getSize().getHeight()));

		return x_properties;
	}

	public void applySettings(Properties p_properties) {
		Enumeration x_enumeration = p_properties.propertyNames();

		while(x_enumeration.hasMoreElements()) {
			String x_propertyName = (String) x_enumeration.nextElement();

			if(x_propertyName.startsWith("winw_")) {
				try {
					Class x_clazz = Class.forName(x_propertyName.substring(5));
					String x_widthStr = p_properties.getProperty(x_propertyName);
					String x_heightStr = p_properties.getProperty("winh_" + x_clazz.getName());
					int x_width = Integer.parseInt(x_widthStr);
					int x_height = Integer.parseInt(x_heightStr);
					o_windowDimensions.put(x_clazz, new Dimension(x_width, x_height));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		if(p_properties.containsKey("winx")) {
			o_windowLocation = new Point(Integer.parseInt(p_properties.getProperty("winx")), Integer.parseInt(p_properties.getProperty("winy")));
		}

		if(p_properties.containsKey("navw")) {
			o_navSize = new Dimension(Integer.parseInt(p_properties.getProperty("navw")), Integer.parseInt(p_properties.getProperty("navh")));
		}

		if(p_properties.containsKey("navx")) {
			o_navLocation = new Point(Integer.parseInt(p_properties.getProperty("navx")), Integer.parseInt(p_properties.getProperty("navy")));
		}

		// TODO remove if on navs soon
	}

	public void openComponent(final Component p_component, boolean p_new, int p_tabIndex) {
		for(JFrame x_frame: o_windows.values()) {
			x_frame.setVisible(false);
		}

		if(!o_windows.containsKey(p_component)) {
			o_windows.put(p_component, makeComponentWindow(p_component, p_new, p_tabIndex));
		}

		JFrame x_window = o_windows.get(p_component);
		x_window.setSize(getWindowDimension(p_component));
		x_window.setLocation(getWindowLocation());
		x_window.setVisible(true);

		o_navigationWindow.selectComponent(p_component);
    }

	private Point getWindowLocation() {
		if(o_windowLocation == null) {
			o_windowLocation = new Point(o_navigationWindow.getX() + o_navigationWindow.getWidth() + 20, o_navigationWindow.getY());
		}

		return o_windowLocation;
	}

	private Dimension getWindowDimension(Component p_component) {
		Class<? extends Component> x_clazz = p_component.getClass();

		if(!o_windowDimensions.containsKey(x_clazz)) {
			o_windowDimensions.put(x_clazz, GUIConstants.dimensionFor(p_component));
		}

		return o_windowDimensions.get(x_clazz);
	}

	private JFrame makeComponentWindow(final Component p_component, boolean p_new, int p_tabIndex) {
		JFrame x_window = null;

		if(p_component instanceof Thread) {
			x_window = new ThreadWindow((Thread) p_component, p_new, p_tabIndex);
		}

		if(p_component instanceof Item) {
			x_window = new ItemWindow((Item) p_component, p_new);
		}

		if(p_component instanceof Reminder) {
			x_window = new ReminderWindow((Reminder) p_component, p_new);
		}

		ImageUtil.addIconToWindow(x_window);
		return x_window;
	}

	public void setComponentWindowDetails(Class<? extends Component> p_clazz, Point p_location, Dimension p_size) {
		o_windowLocation = p_location;
		o_windowDimensions.put(p_clazz, p_size);
	}
}