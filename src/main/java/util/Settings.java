package util;

import java.io.*;
import java.util.*;

public class Settings {
	public static final String s_TAB_INDEX = "tabindex";
	public static final String s_DATE = "date";
	public static final String s_DIVLOC = "divloc";
	public static final String s_NAVDIVLOC = "navdivloc";
	public static final String s_WINW = "winw";
	public static final String s_WINH = "winh";
	public static final String s_WINX = "winx";
	public static final String s_WINY = "winy";
	public static final String s_UUID = "uuid";
	public static final String s_CALENDARACT = "calendaract";
	public static final String s_CALENDARUP = "calendarup";
	public static final String s_CALENDARREM = "calendarrem";
	public static final String s_ONLYDUE = "onlydue";
	public static final String s_SEVENDAYS = "sevendays";
	public static final String s_GOOGLE_ENABLED = "google";

	private static Map<String, List<SettingChangeListener>> interestedParties = new HashMap<>();
	private static Properties settings = new Properties();

	public static int registerForSetting(String name, SettingChangeListener listener, int defaultValue) {
		if(!interestedParties.containsKey(name)) {
			interestedParties.put(name, new ArrayList<>());
		}

		interestedParties.get(name).add(listener);

		if(!settings.containsKey(name)) {
			settings.put(name, String.valueOf(defaultValue));
			return defaultValue;
		}

		Object value = settings.get(name);
		return Integer.parseInt((String) value);
	}

	public static String registerForSetting(String name, SettingChangeListener listener, String defaultValue) {
		if(!interestedParties.containsKey(name)) {
			interestedParties.put(name, new ArrayList<>());
		}

		interestedParties.get(name).add(listener);

		if(!settings.containsKey(name) && defaultValue != null) {
			settings.put(name, defaultValue);
		}

		Object value = settings.get(name);
		return (String) value;
	}

	public static void updateSetting(String name, Object value) {
		settings.put(name, String.valueOf(value));

		List<SettingChangeListener> listeners = interestedParties.get(name);

		if(interestedParties != null) {
			for(SettingChangeListener listener: listeners) {
				listener.settingChanged(name, value);
			}
		}
	}

	public static boolean load(File file) {
		if(file.exists()) {
			try {
				settings.load(new FileInputStream(file));
				return "true".equals(settings.get(Settings.s_GOOGLE_ENABLED));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return false;
	}

	public static void save(File file) {
		try {
			settings.store(new FileWriter(file), "Threads settings");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
