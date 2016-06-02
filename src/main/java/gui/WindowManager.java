package gui;

import data.*;
import data.Component;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
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

	private Map<Class, Dimension> o_windowDimensions = new HashMap<Class, Dimension>();
	private Point o_windowLocation = null;

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

		ImageUtil.addIconToWindow(o_navigationWindow);
		o_navigationWindow.setLocation(250, 200);
		o_navigationWindow.setVisible(true);
	}

    public void openComponent(final Component p_component, boolean p_new, int p_tabIndex) {
		for(JFrame x_frame: o_windows.values()) {
			x_frame.setVisible(false);
		}

		int x_index = p_tabIndex != -1 ? p_tabIndex : o_tabIndex != -1 ? o_tabIndex : 0;

		if(!o_windows.containsKey(p_component)) {
			o_windows.put(p_component, makeComponentWindow(p_component, p_new, x_index));
		} else if (p_component instanceof Thread) {
			((ThreadPanel)o_windows.get(p_component).getContentPane()).setTabIndex(x_index);
		}

		JFrame x_window = o_windows.get(p_component);
		x_window.setSize(getWindowDimension(p_component));
		x_window.setLocation(getWindowLocation());
		x_window.setVisible(true);

		o_navigationWindow.selectComponent(p_component);
    }

	private Point getWindowLocation() {
		if(o_windowLocation == null) {
			o_windowLocation = new Point(o_navigationWindow.getX() + o_navigationWindow.getWidth() + 20, o_navigationWindow.getY());
		}

		return o_windowLocation;
	}

	private Dimension getWindowDimension(Component p_component) {
		Class<? extends Component> x_clazz = p_component.getClass();

		if(!o_windowDimensions.containsKey(x_clazz)) {
			o_windowDimensions.put(x_clazz, GUIConstants.dimensionFor(p_component));
		}

		return o_windowDimensions.get(x_clazz);
	}

	private JFrame makeComponentWindow(final Component p_component, boolean p_new, int p_tabIndex) {
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

		ImageUtil.addIconToWindow(x_window);
		return x_window;
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

	public void setComponentWindowDetails(Class<? extends Component> p_clazz, Point p_location, Dimension p_size) {
		o_windowLocation = p_location;
		o_windowDimensions.put(p_clazz, p_size);
	}
}