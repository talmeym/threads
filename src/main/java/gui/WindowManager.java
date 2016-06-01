package gui;

import data.*;
import data.Component;
import data.Thread;
import util.TimedSaver;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;

public class WindowManager implements ChangeListener {
    private static WindowManager s_INSTANCE;

	public static void initialise(Thread p_topLevelThread, String filePath) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise window manager twice");
		}

		s_INSTANCE = new WindowManager(p_topLevelThread, filePath);
	}

    public static WindowManager getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Window manager not initialised");
		}

        return s_INSTANCE;
    }

	private NavigationWindow o_navigationWindow;

	private Map<Component, JFrame> o_windows = new HashMap<Component, JFrame>();

	private int o_tabIndex = -1;

    private WindowManager(final Thread p_topLevelThread, final String filePath) {
		o_navigationWindow = new NavigationWindow(p_topLevelThread);

		o_navigationWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				TimedSaver.getInstance().stopRunning();
				Saver.saveDocument(p_topLevelThread, filePath);
				System.exit(0);
			}
		});

		o_navigationWindow.setLocation(250, 200);
		o_navigationWindow.setVisible(true);
		openComponent(p_topLevelThread, false, -1);
	}

    public void openComponent(Component p_component, boolean p_new, int p_tabIndex) {
		for(JFrame x_frame: o_windows.values()) {
			x_frame.setVisible(false);
		}

		int x_index = p_tabIndex != -1 ? p_tabIndex : o_tabIndex != -1 ? o_tabIndex : 0;

		if(!o_windows.containsKey(p_component)) {
			o_windows.put(p_component, makeComponentWindow(p_component, p_new, x_index));
		} else if (p_component instanceof Thread) {
			((ThreadPanel)o_windows.get(p_component).getContentPane()).setTabIndex(x_index);
		}

		showFrame(o_windows.get(p_component));
		o_navigationWindow.selectComponent(p_component);
    }

	private JFrame makeComponentWindow(Component p_component, boolean p_new, int p_tabIndex) {
		JFrame x_window = null;

		if(p_component instanceof Thread) {
			x_window = new ThreadWindow((Thread) p_component, p_new, p_tabIndex, this);
		}

		if(p_component instanceof Item) {
			x_window = new ItemWindow((Item) p_component, p_new);
		}

		if(p_component instanceof Reminder) {
			x_window = new ReminderWindow((Reminder) p_component, p_new);
		}
		return x_window;
	}

	private void showFrame(JFrame x_window) {
		x_window.setLocation(o_navigationWindow.getX() + o_navigationWindow.getWidth() + 20, o_navigationWindow.getY());
		x_window.setVisible(true);
	}

	@Override
	public void stateChanged(ChangeEvent changeEvent) {
		o_tabIndex = ((JTabbedPane)changeEvent.getSource()).getSelectedIndex();

		for(Component x_component: o_windows.keySet()) {
			if(x_component instanceof Thread) {
				((ThreadPanel)o_windows.get(x_component).getContentPane()).setTabIndex(o_tabIndex);
			}
		}
	}
}