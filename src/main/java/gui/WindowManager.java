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
	private WindowSettings o_windowSettings = new WindowSettings();

	private WindowManager(final Thread p_topLevelThread, final String filePath, final Properties p_properties) {
		o_windowSettings.applyProperties(p_properties);

		o_navigationWindow = new NavigationWindow(p_topLevelThread);
		o_navigationWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				setNavigationWindowDetails(o_navigationWindow.getLocation(), o_navigationWindow.getSize());
				TimedSaver.getInstance().stopRunning();
				Saver.saveDocument(p_topLevelThread, filePath, o_windowSettings.getProperties());
				System.exit(0);
			}
		});

		o_navigationWindow.setSize(o_windowSettings.getNavSize());
		o_navigationWindow.setLocation(o_windowSettings.getNavLocation());
		o_navigationWindow.setVisible(true);

		openComponent(p_topLevelThread);
	}

	public void openComponent(final Component p_component) {
		for(JFrame x_frame: o_windows.values()) {
			x_frame.setVisible(false);
		}

		if(!o_windows.containsKey(p_component)) {
			o_windows.put(p_component, makeComponentWindow(p_component));
		}

		JFrame x_window = o_windows.get(p_component);
		x_window.setSize(o_windowSettings.getWindowSize(p_component));
		x_window.setLocation(o_windowSettings.getWindowLocation());
		x_window.setVisible(true);

		o_navigationWindow.selectComponent(p_component);
    }

	private JFrame makeComponentWindow(final Component p_component) {
		JFrame x_window = null;

		if(p_component instanceof Thread) {
			x_window = new ThreadWindow((Thread) p_component);
		}

		if(p_component instanceof Item) {
			x_window = new ItemWindow((Item) p_component);
		}

		if(p_component instanceof Reminder) {
			x_window = new ReminderWindow((Reminder) p_component);
		}

		if (x_window != null) {
			x_window.addWindowListener(new WindowAdapter() {
				@Override
				public void windowDeactivated(WindowEvent windowEvent) {
					Window x_window = windowEvent.getWindow();
					setComponentWindowDetails(p_component, x_window.getLocation(), x_window.getSize());
				}

				@Override
				public void windowClosing(WindowEvent windowEvent) {
					Window x_window = windowEvent.getWindow();
					setComponentWindowDetails(p_component, x_window.getLocation(), x_window.getSize());
				}
			});
		}

		return x_window;
	}

	public void setComponentWindowDetails(Component p_component, Point p_location, Dimension p_size) {
		o_windowSettings.setWindowLocation(p_location);
		o_windowSettings.setWindowSize(p_component, p_size);
	}

	public void setNavigationWindowDetails(Point p_location, Dimension p_size) {
		o_windowSettings.setNavLocation(p_location);
		o_windowSettings.setNavSize(p_size);
	}

	public Properties getSettings() {
		return o_windowSettings.getProperties();
	}
}