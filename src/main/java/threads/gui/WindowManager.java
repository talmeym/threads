package threads.gui;

import threads.data.Component;
import threads.data.Configuration;
import threads.data.Thread;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import static com.apple.eawt.Application.getApplication;
import static threads.util.FontUtil.standardiseFontSizes;
import static threads.util.ImageUtil.getThreadsImage;

public class WindowManager {
	private static WindowManager s_INSTANCE;

	public static void initialise() {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise window manager twice");
		}

		s_INSTANCE = new WindowManager();
	}

    public static WindowManager getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Window manager not initialised");
		}

        return s_INSTANCE;
    }

	private Map<Component, ThreadsWindow> o_threadsWindows = new HashMap<>();
    private Map<ThreadsWindow, WindowListener> o_windowListeners = new HashMap<>();

	private WindowManager() {
		standardiseFontSizes();
        getApplication().setDockIconImage(getThreadsImage());
	}

	public void openConfiguration(Configuration p_configuration, WindowListener p_listener) {
        ThreadsWindow x_threadWindow = new ThreadsWindow(p_configuration);
        o_threadsWindows.put(p_configuration.getTopLevelThread(), x_threadWindow);
        o_windowListeners.put(x_threadWindow, p_listener);

        x_threadWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                p_listener.windowClosing(e);
            }
        });

        x_threadWindow.setVisible(true);
    }

    public void closeConfiguration(Configuration p_configuration) {
        Thread x_topLevelThread = p_configuration.getTopLevelThread();
        ThreadsWindow x_threadWindow = o_threadsWindows.get(x_topLevelThread);
        x_threadWindow.setVisible(false);
        o_threadsWindows.remove(x_topLevelThread);
        o_windowListeners.get(x_threadWindow).windowClosing(null);
        o_windowListeners.remove(x_threadWindow);
    }

    public void closeAllWindows() {
        o_windowListeners.keySet().forEach(w -> {
            w.setVisible(false);
            o_windowListeners.get(w).windowClosing(null);
        });
        o_threadsWindows.clear();
        o_windowListeners.clear();
    }

    public void openComponent(Component p_component) {
        o_threadsWindows.get(p_component.getHierarchy().get(0)).openComponent(p_component);
    }
}