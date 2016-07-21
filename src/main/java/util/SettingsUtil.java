package util;

import gui.*;

import java.io.*;
import java.util.Properties;

public class SettingsUtil {
	public static void load(File p_settingsFile) {
		if(p_settingsFile.exists()) {
			try {
				Properties x_properties = new Properties();
				x_properties.load(new FileInputStream(p_settingsFile));
				WindowManager.applySettingsFromProperties(x_properties);
				MemoryPanel.applySettingsFromProperties(x_properties);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void save(File p_settingsFile) {
		try {
			Properties x_properties = new Properties();
			WindowManager.saveToProperties(x_properties);
			MemoryPanel.saveToProperties(x_properties);
			x_properties.store(new FileWriter(p_settingsFile), "Threads settings");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
