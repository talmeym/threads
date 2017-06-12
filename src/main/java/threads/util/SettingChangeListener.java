package threads.util;

import threads.util.Settings.Setting;

public interface SettingChangeListener {
	void settingChanged(Setting p_setting, Object p_value);
}
