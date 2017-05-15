package gui;

import data.*;
import data.Component;
import data.Thread;
import util.*;

import javax.script.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static util.ImageUtil.addIcon;
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

	private final NavigationAndComponentPanel o_navigationAndComponentPanel;

	private WindowManager(Thread p_topLevelThread, WindowListener p_listener) {
		JFrame x_window = new JFrame("Threads");
		addIcon(x_window);
		o_navigationAndComponentPanel = new NavigationAndComponentPanel(p_topLevelThread);
		x_window.setContentPane(o_navigationAndComponentPanel);

		x_window.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateSetting(Settings.s_WINX, "" + new Double(x_window.getLocation().getX()).intValue());
				updateSetting(Settings.s_WINY, "" + new Double(x_window.getLocation().getY()).intValue());
				updateSetting(Settings.s_WINW, "" + new Double(x_window.getSize().getWidth()).intValue());
				updateSetting(Settings.s_WINH, "" + new Double(x_window.getSize().getHeight()).intValue());
			}
		});

		x_window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				p_listener.windowClosing(e);
			}
		});

		String x_firstUuid = registerForSetting(Settings.s_UUID, this, p_topLevelThread.getId().toString());
		Component x_firstComponent = x_firstUuid != null ? p_topLevelThread.search(new Search.Builder().withId(UUID.fromString(x_firstUuid)).build()).get(0) : p_topLevelThread;
		o_navigationAndComponentPanel.showComponent(x_firstComponent);

		x_window.setSize(new Dimension(registerForSetting(Settings.s_WINW, this, GUIConstants.s_windowWidth), registerForSetting(Settings.s_WINH, this, GUIConstants.s_windowHeight)));
		x_window.setLocation(new Point(registerForSetting(Settings.s_WINX, this, GUIConstants.s_windowX), registerForSetting(Settings.s_WINY, this, GUIConstants.s_windowY)));
		x_window.setVisible(true);
	}

	public void openComponent(Component p_component) {
		o_navigationAndComponentPanel.showComponent(p_component);
		updateSetting(Settings.s_UUID, p_component.getId().toString());
	}

	@Override
	public void settingChanged(String p_name, Object p_value) {
		// do nothing
	}
}