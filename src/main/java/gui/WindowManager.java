package gui;

import data.Component;
import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class WindowManager extends WindowAdapter {
    private static final WindowManager s_INSTANCE = new WindowManager();

	private static final List<WindowListener> listeners = new ArrayList<WindowListener>();

	private static NavigationWindow navigationWindow;

    public static WindowManager getInstance()
    {
        return s_INSTANCE;
    }
    
    private final Map o_componentWindows;

    private final Map o_windowComponents;
    
    private WindowManager()
    {
        o_componentWindows = new HashMap();
        o_windowComponents = new HashMap();
    }

	public void addWindowListener(WindowListener listener) {
		listeners.add(listener);
	}

	private void lastWindowClosing() {
		for(WindowListener listener: listeners) {
			listener.lastWindowClosing();
		}
	}

    public Window openComponentWindow(Component p_component, boolean p_new, int p_tabIndex)
    {
		JFrame x_window;

        if(!o_componentWindows.containsKey(p_component))
        {
            JPanel x_panel = getComponentPanel(p_component, p_new, p_tabIndex);
            x_window = new JFrame();
			ImageUtil.addIconToWindow(x_window);
            sizeWindow(p_component, x_window);
            x_window.setContentPane(x_panel);
            o_componentWindows.put(p_component, x_window);
            o_windowComponents.put(x_window, p_component);
            x_window.addWindowListener(this);
            positionWindow(p_component, x_window);
            renameWindow(p_component);            
            x_window.setVisible(true);
        }
        else
        {
            x_window = (JFrame) o_componentWindows.get(p_component);
            x_window.setVisible(false);
			JPanel panel = (JPanel) x_window.getContentPane();

			if(panel instanceof ThreadPanel && p_tabIndex != -1) {
				((ThreadPanel)panel).setTabIndex(p_tabIndex);
			}

			x_window.setVisible(true);
        }

		closeOtherWindows(p_component);
		navigationWindow.selectComponent(p_component);
		return x_window;
    }

	private void closeOtherWindows(Component p_component) {
		if(p_component instanceof Thread && getAllOtherWindows(p_component).size() > 0) {
			List<Window> x_parentWindows = getAllOtherWindows(p_component);

			for(Window x_parentWindow : x_parentWindows) {
				closeComponentWindow((Component)o_windowComponents.get(x_parentWindow));
			}
		}

		if(p_component instanceof Item && getAllOtherItemAndReminderWindows(p_component).size() > 0) {
			List<Window> x_parentWindows = getAllOtherItemAndReminderWindows(p_component);

			for(Window x_parentWindow : x_parentWindows) {
				closeComponentWindow((Component)o_windowComponents.get(x_parentWindow));
			}
		}

		if(p_component instanceof Reminder && getAllOtherReminderWindows(p_component).size() > 0) {
			List<Window> x_parentWindows = getAllOtherReminderWindows(p_component);

			for(Window x_parentWindow : x_parentWindows) {
				closeComponentWindow((Component)o_windowComponents.get(x_parentWindow));
			}
		}
	}

	public void windowClosing(WindowEvent we)
    {
		Window window = we.getWindow();
		o_componentWindows.remove(o_windowComponents.get(window));
        o_windowComponents.remove(window);
        window.removeWindowListener(this);

		if(o_componentWindows.size() == 0 && !navigationWindow.isVisible()) {
			lastWindowClosing();
		}
    }
    
    public void closeComponentWindow(Component p_component)
    {
        if(o_componentWindows.containsKey(p_component))
        {
            Window x_window = (Window)o_componentWindows.get(p_component);
            o_windowComponents.remove(x_window);
            o_componentWindows.remove(p_component);
            x_window.removeWindowListener(this);
            x_window.setVisible(false);
        }
    }

	public void renameAllWindows() {
		for(Object x_obj: o_componentWindows.keySet()) {
			Component x_component = (Component) x_obj;
			renameWindow(x_component);
		}
	}

    void renameWindow(Component p_component)
    {
        JFrame x_window = (JFrame) o_componentWindows.get(p_component);
        
        StringBuffer x_title = new StringBuffer("Threads: ");
		String x_componentText = p_component.getText();

		List<String> x_parentNames = new ArrayList<String>();

		while(p_component.getParentComponent() != null) {
			x_parentNames.add(p_component.getParentComponent().getText());
			p_component = p_component.getParentComponent();
		}

		for(int i = x_parentNames.size() - 1; i > -1; i--) {
			x_title.append(x_parentNames.get(i));
			x_title.append(" > ");
		}

		x_title.append(x_componentText);

        x_window.setTitle(x_title.toString());
    }
    
    private void sizeWindow(Component p_component, Window p_window)
    {
        if(p_component instanceof Thread)
        {
            p_window.setSize(GUIConstants.s_threadWindowSize);
        }
        else if(p_component instanceof Item)
        {
            p_window.setSize(GUIConstants.s_itemWindowSize);
        }
        else if(p_component instanceof Reminder)
        {
            p_window.setSize(GUIConstants.s_reminderWindowSize);
        }
        else
        {
            throw new IllegalArgumentException("Invalid window component type:" + p_component);
        }

    }
    
    private JPanel getComponentPanel(Component p_component, boolean p_new, int tabIndex)
    {
		if(tabIndex == -1) {
			tabIndex = 0;
		}

        if(p_component instanceof Thread)
        {
            return new ThreadPanel((Thread)p_component, p_new, tabIndex);
        }
        else if(p_component instanceof Item)
        {
            return new ItemPanel((Item)p_component, p_new);
        }
        else if(p_component instanceof Reminder)
        {
            return new ReminderPanel((Reminder)p_component, p_new);
        }
        else
        {
            throw new IllegalArgumentException("Invalid window component type:" + p_component);
        }
    }
    
    void positionWindow(Component p_component, Window p_window)
    {
		List<Window> x_parentWindows = getAllOtherWindows(p_component);
		Window x_parentWindow = x_parentWindows.size() > 0 ? x_parentWindows.get(0) : null;

        if(x_parentWindow != null)
        {
            Point x_location = new Point(x_parentWindow.getX() + (x_parentWindow.getWidth() / 2) - (p_window.getWidth() / 2),
                                         x_parentWindow.getY() + (x_parentWindow.getHeight() / 2) - (p_window.getHeight() / 2));
            p_window.setLocation(x_location);
        }
        
        if(x_parentWindow == null)
        {
            GUIUtil.centreWindow(p_window);
        }
	}

	private List<Window> getAllOtherItemAndReminderWindows(Component p_component) {
		List<Window> x_parentWindows = new ArrayList<Window>();
		Iterator iterator = o_componentWindows.keySet().iterator();

		while(iterator.hasNext()) {
			Component component = (Component) iterator.next();

			if(component != p_component && (component instanceof Item || component instanceof Reminder)) {
				x_parentWindows.add((Window)o_componentWindows.get(component));
			}
		}

		return x_parentWindows;
	}

	private List<Window> getAllOtherReminderWindows(Component p_component) {
		List<Window> x_parentWindows = new ArrayList<Window>();
		Iterator iterator = o_componentWindows.keySet().iterator();

		while(iterator.hasNext()) {
			Component component = (Component) iterator.next();

			if(component != p_component && component instanceof Reminder) {
				x_parentWindows.add((Window)o_componentWindows.get(component));
			}
		}

		return x_parentWindows;
	}

	private List<Window> getAllOtherWindows(Component p_component) {
		List<Window> x_parentWindows = new ArrayList<Window>();
		Iterator iterator = o_componentWindows.keySet().iterator();

		while(iterator.hasNext()) {
			Component component = (Component) iterator.next();

			if(component != p_component) {
				x_parentWindows.add((Window)o_componentWindows.get(component));
			}
		}

		return x_parentWindows;
	}

	public void showNavigationTreeWindow(Thread p_thread) {
		navigationWindow = new NavigationWindow(p_thread);
	}

	public static interface WindowListener {
		void lastWindowClosing();
	}
}
