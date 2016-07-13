package gui;

import data.*;
import data.Component;
import data.Thread;
import util.*;
import util.GoogleSyncer;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class WindowManager {
    private static WindowManager s_INSTANCE;

	public static void initialise(Thread p_topLevelThread, File p_dataFile, File p_settingsFile) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise window manager twice");
		}

		s_INSTANCE = new WindowManager(p_topLevelThread, p_dataFile, p_settingsFile);
	}

    public static WindowManager getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Window manager not initialised");
		}

        return s_INSTANCE;
    }

	private final JFrame o_window = new JFrame();
	private final NavigationAndComponentPanel o_navigationAndComponentPanel;
	private WindowSettings o_windowSettings = new WindowSettings();

	private WindowManager(final Thread p_topLevelThread, final File p_dataFilePath, final File p_settingsFilePath) {
		ImageUtil.addIcon(o_window);

		String x_startingUuid = applySettings(p_settingsFilePath);
		Component x_firstComponent = x_startingUuid != null ? p_topLevelThread.findComponent(UUID.fromString(x_startingUuid)) : p_topLevelThread;

		o_navigationAndComponentPanel = new NavigationAndComponentPanel(p_topLevelThread, x_firstComponent);
		o_window.setContentPane(o_navigationAndComponentPanel);

		o_window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				TimedSaver.getInstance().stopRunning();
				GoogleSyncer.getInstance().stopRunning();
				TimeUpdater.getInstance().stopRunning();
				Saver.saveDocument(p_topLevelThread, p_dataFilePath);
				o_windowSettings.setWindowLocation(o_window.getLocation());
				o_windowSettings.setWindowSize(o_window.getSize());
				saveSettings(p_settingsFilePath);
				System.exit(0);
			}
		});

		o_window.setSize(o_windowSettings.getWindowSize());
		o_window.setLocation(o_windowSettings.getWindowLocation());
		o_window.setTitle(x_firstComponent.getType() + " : " + x_firstComponent.getText());
		o_window.setVisible(true);

	}

	private void saveSettings(File p_settingsFile) {
		try {
			Properties x_properties = o_windowSettings.getProperties();
			Integer x_tabMemory = MemoryPanel.getMemoryValue(ThreadPanel.class);
			Integer x_monthMemory = MemoryPanel.getMemoryValue(ThreadCalendarPanel.class);
			Integer x_itemDivMemory = MemoryPanel.getMemoryValue(ItemAndReminderPanel.class);
			Integer x_navDivMemory = MemoryPanel.getMemoryValue(NavigationAndComponentPanel.class);


			if(x_tabMemory != null) {
				x_properties.setProperty(WindowSettings.s_TAB_INDEX, String.valueOf(x_tabMemory));
			}

			if(x_monthMemory != null) {
				x_properties.setProperty(WindowSettings.s_MONTH, String.valueOf(x_monthMemory));
			}

			if(x_itemDivMemory != null) {
				x_properties.setProperty(WindowSettings.s_DIVLOC, String.valueOf(x_itemDivMemory));
			}

			if(x_navDivMemory != null) {
				x_properties.setProperty(WindowSettings.s_NAVDIVLOC, String.valueOf(x_navDivMemory));
			}

			x_properties.setProperty(WindowSettings.s_UUID, o_navigationAndComponentPanel.getComponent().getId().toString());

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
				String x_itemDivLoc = x_properties.getProperty(WindowSettings.s_DIVLOC);
				String x_navDivLoc = x_properties.getProperty(WindowSettings.s_NAVDIVLOC);

				if(x_tabIndex != null) {
					MemoryPanel.setMemoryValue(ThreadPanel.class, Integer.parseInt(x_tabIndex));
				}

				if(x_month != null) {
					MemoryPanel.setMemoryValue(ThreadCalendarPanel.class, Integer.parseInt(x_month));
				}

				if(x_itemDivLoc != null) {
					MemoryPanel.setMemoryValue(ItemAndReminderPanel.class, Integer.parseInt(x_itemDivLoc));
				}


				if(x_navDivLoc != null) {
					MemoryPanel.setMemoryValue(NavigationAndComponentPanel.class, Integer.parseInt(x_navDivLoc));
				}

				return x_properties.getProperty(WindowSettings.s_UUID);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public void openComponent(Component p_component) {
		o_navigationAndComponentPanel.showComponent(p_component);
		o_window.setTitle(p_component.getType() + " : " + p_component.getText());
	}
}