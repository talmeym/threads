package threads.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class Settings {
    public enum Setting {
        TABINDEX("tabindex", "0"),
        DATE("date", getInstance().get(MONTH) + "_" + getInstance().get(YEAR)),
        DIVLOC("divloc", "350"),
        NAVDIVLOC("navdivloc", "250"),
        WINW("winw", "1200"),
        WINH("winh", "800"),
        WINX("winx", "150"),
        WINY("winy", "150"),
        UUID("uuid", null),
        CALENDARACT("calendaract", "true"),
        CALENDARUP("calendarup", "false"),
        CALENDARREM("calendarrem", "false"),
        REMINDERVIEW("remview", "DUE"),
        ACTIONVIEW("actview", "SEVENDAYS"),
        GOOGLE("google", "false"),
        SEARCHNOTES("searchnotes", "false"),
        SEARCHCASE("searchcase", "false");

        private String o_name;
        private String o_defaultValue;

        Setting(String p_name, String p_defaultValue) {
            o_name = p_name;
            o_defaultValue = p_defaultValue;
        }
    }

    private final File o_settingsFile;
    private final Properties o_settings = new Properties();
	private final Map<Setting, List<SettingChangeListener>> interestedParties = new HashMap<>();

    public Settings(File p_settingsFile) {
        o_settingsFile = p_settingsFile;

        if(o_settingsFile.exists()) {
            try {
                o_settings.load(new FileInputStream(o_settingsFile));
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Integer registerForIntSetting(Setting p_setting, SettingChangeListener p_listener) {
        getInterestedParties(p_setting).add(p_listener);
        return getIntSetting(p_setting);
    }

    public Integer getIntSetting(Setting p_setting) {
        return handleDefault(p_setting) ? parseInt((String)o_settings.get(p_setting.o_name)) : null;
    }

    public String registerForStringSetting(Setting p_setting, SettingChangeListener p_listener) {
		getInterestedParties(p_setting).add(p_listener);
		return getStringSetting(p_setting);
	}

    public String getStringSetting(Setting p_setting) {
        return handleDefault(p_setting) ? (String) o_settings.get(p_setting.o_name) : null;
    }

    public Boolean registerForBooleanSetting(Setting p_setting, SettingChangeListener p_listener) {
		getInterestedParties(p_setting).add(p_listener);
		return getBooleanSetting(p_setting);
	}

    public Boolean getBooleanSetting(Setting p_setting) {
        return handleDefault(p_setting) ? parseBoolean((String)o_settings.get(p_setting.o_name)) :  null;
    }

    private boolean handleDefault(Setting p_setting) {
        if(!o_settings.containsKey(p_setting.o_name)) {
            if(p_setting.o_defaultValue != null) {
                o_settings.put(p_setting.o_name, p_setting.o_defaultValue);
            }
        }

        return o_settings.containsKey(p_setting.o_name);
    }

	public void updateSetting(Setting p_setting, Object p_value) {
		o_settings.put(p_setting.o_name, String.valueOf(p_value));
		getInterestedParties(p_setting).forEach(listener -> listener.settingChanged(p_setting, p_value));
	}

	private List<SettingChangeListener> getInterestedParties(Setting p_setting) {
        interestedParties.computeIfAbsent(p_setting, (k) -> new ArrayList<>());
		return interestedParties.get(p_setting);
	}

	public void save() {
		try {
			o_settings.store(new FileWriter(o_settingsFile), "Threads Settings");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
