package threads.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Settings {
	public static final String s_TABINDEX = "tabindex";
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
	public static final String s_GOOGLE = "google";

	private static Map<String, List<SettingChangeListener>> interestedParties = new HashMap<>();
	private static Properties settings = new Properties();

	public static int registerForSetting(String name, SettingChangeListener listener, int defaultValue) {
		if(!settings.containsKey(name)) {
			settings.put(name, String.valueOf(defaultValue));
		}

		getInterestedParties(name).add(listener);
		return Integer.parseInt((String)settings.get(name));
	}

	public static String registerForSetting(String name, SettingChangeListener listener, String defaultValue) {
		if(!settings.containsKey(name)) {
			settings.put(name, defaultValue);
		}

		getInterestedParties(name).add(listener);
		return (String) settings.get(name);
	}

	public static Boolean registerForSetting(String name, SettingChangeListener listener, boolean defaultValue) {
		if(!settings.containsKey(name)) {
			settings.put(name, String.valueOf(defaultValue));
		}

		getInterestedParties(name).add(listener);
		return Boolean.parseBoolean((String)settings.get(name));
	}


	public static void updateSetting(String name, Object value) {
		settings.put(name, String.valueOf(value));
		getInterestedParties(name).forEach(listener -> listener.settingChanged(name, value));
	}

	private static List<SettingChangeListener> getInterestedParties(String name) {
		if(!interestedParties.containsKey(name)) {
			interestedParties.put(name, new ArrayList<>());
		}

		return interestedParties.get(name);
	}

	public static void load(File file) {
		if(file.exists()) {
			try {
				settings.load(new FileInputStream(file));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void save(File file) {
		try {
			settings.store(new FileWriter(file), "threads.Threads settings");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
