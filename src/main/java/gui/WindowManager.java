package gui;

import data.*;
import data.Component;
import data.Thread;
import util.*;
import util.GoogleSyncer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class WindowManager {
    private static WindowManager s_INSTANCE;

	public static void initialise(Thread p_topLevelThread, String p_dataFilePath, String p_settingsFilePath) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise window manager twice");
		}

		s_INSTANCE = new WindowManager(p_topLevelThread, new File(p_dataFilePath), new File(p_settingsFilePath));
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

	private WindowManager(final Thread p_topLevelThread, final File p_dataFilePath, final File p_settingsFilePath) {

		o_navigationWindow = new NavigationWindow(p_topLevelThread);
		o_navigationWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				setNavigationWindowDetails(o_navigationWindow.getLocation(), o_navigationWindow.getSize());
				TimedSaver.getInstance().stopRunning();
				GoogleSyncer.getInstance().stopRunning();
				Saver.saveDocument(p_topLevelThread, p_dataFilePath);
				saveSettings(p_settingsFilePath);
				System.exit(0);
			}
		});

		String x_startingUuid = applySettings(p_settingsFilePath);
		Component x_component = x_startingUuid != null ? p_topLevelThread.findComponent(UUID.fromString(x_startingUuid)) : null;

		o_navigationWindow.setSize(o_windowSettings.getNavSize());
		o_navigationWindow.setLocation(o_windowSettings.getNavLocation());
		o_navigationWindow.setVisible(true);

		openComponent(x_component != null ? x_component : p_topLevelThread);
	}

	private void saveSettings(File p_settingsFile) {
		try {
			Properties x_properties = o_windowSettings.getProperties();
			Integer x_tabMemory = MemoryPanel.getMemoryValue(ThreadPanel.class);
			Integer x_monthMemory = MemoryPanel.getMemoryValue(ThreadCalendarPanel.class);

			if(x_tabMemory != null) {
				x_properties.setProperty(WindowSettings.s_TAB_INDEX, String.valueOf(x_tabMemory));
			}

			if(x_monthMemory != null) {
				x_properties.setProperty(WindowSettings.s_MONTH, String.valueOf(x_monthMemory));
			}

			for(Component x_component: o_windows.keySet()) {
				if(o_windows.get(x_component).isVisible()) {
					x_properties.setProperty(WindowSettings.s_UUID, x_component.getId().toString());
				}
			}

			x_properties.store(new FileWriter(p_settingsFile), "Threads settings");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String applySettings(File p_settingsFile) {
		if(p_settingsFile.exists()) {
			try {
				Properties x_properties = new Properties();
				x_properties.load(new FileInputStream(p_settingsFile));
				o_windowSettings.applyProperties(x_properties);
				String x_tabIndex = x_properties.getProperty(WindowSettings.s_TAB_INDEX);
				String x_month = x_properties.getProperty(WindowSettings.s_MONTH);

				if(x_tabIndex != null) {
					MemoryPanel.setMemoryValue(ThreadPanel.class, Integer.parseInt(x_tabIndex));
				}

				if(x_month != null) {
					MemoryPanel.setMemoryValue(ThreadCalendarPanel.class, Integer.parseInt(x_month));
				}

				return x_properties.getProperty(WindowSettings.s_UUID);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
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

	public void closeComponent(final Component p_component) {
		o_windows.get(p_component).setVisible(false);
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
}