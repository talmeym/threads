package gui;

import data.Component;
import data.Thread;
import util.*;

import javax.script.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static util.Settings.registerForSetting;
import static util.Settings.updateSetting;

public class WindowManager implements SettingChangeListener {
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

    public static void makeThreadsVisible() {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("AppleScript");

		try
		{
			if(engine != null) {
				engine.eval("tell me to activate");
			}
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}
	}

	private static Dimension o_windowSize;
	private static Point o_windowLocation;
	private static UUID o_uuid;

	private final JFrame o_window = new JFrame();
	private final NavigationAndComponentPanel o_navigationAndComponentPanel;

	private WindowManager(final Thread p_topLevelThread, final WindowListener p_listener) {
		doSettings(p_topLevelThread.getId());
		ImageUtil.addIcon(o_window);
		o_navigationAndComponentPanel = new NavigationAndComponentPanel(p_topLevelThread);
		o_window.setContentPane(o_navigationAndComponentPanel);

		o_window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				updateSetting(Settings.s_WINX, "" + new Double(o_window.getLocation().getX()).intValue());
				updateSetting(Settings.s_WINY, "" + new Double(o_window.getLocation().getY()).intValue());
				updateSetting(Settings.s_WINW, "" + new Double(o_window.getSize().getWidth()).intValue());
				updateSetting(Settings.s_WINH, "" + new Double(o_window.getSize().getHeight()).intValue());
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
		updateSetting(Settings.s_UUID, p_component.getId().toString());
	}

	private void doSettings(UUID p_topLevelUuid) {
		o_windowSize = new Dimension(registerForSetting(Settings.s_WINW, this, GUIConstants.s_windowWidth), registerForSetting(Settings.s_WINH, this, GUIConstants.s_windowHeight));
		o_windowLocation = new Point(registerForSetting(Settings.s_WINX, this, GUIConstants.s_windowX), registerForSetting(Settings.s_WINY, this, GUIConstants.s_windowY));
		o_uuid = UUID.fromString(registerForSetting(Settings.s_UUID, this, p_topLevelUuid.toString()));
	}

	@Override
	public void settingChanged(String p_name, Object p_value) {
		// do nothing
	}
}