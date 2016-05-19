package gui;

import com.apple.eawt.Application;
import data.Component;
import data.*;
import data.Thread;
import util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class WindowManager extends WindowAdapter
{
    private static final WindowManager s_INSTANCE = new WindowManager();

	private static final List<WindowListener> listeners = new ArrayList<WindowListener>();

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
        if(!o_componentWindows.containsKey(p_component))
        {
            JPanel x_panel = getComponentPanel(p_component, p_new, p_tabIndex);
            JFrame x_window = new JFrame();
			ImageUtil.addIconToWindow(x_window);
            sizeWindow(p_component, x_window);
            x_window.setContentPane(x_panel);
            o_componentWindows.put(p_component, x_window);
            o_windowComponents.put(x_window, p_component);
            x_window.addWindowListener(this);
            positionWindow(p_component, x_window);
            renameWindow(p_component);            
            x_window.setVisible(true);
            return x_window;
        }
        else
        {
            JFrame x_window = (JFrame) o_componentWindows.get(p_component);
            x_window.setVisible(false);
			JPanel panel = (JPanel) x_window.getContentPane();

			if(panel instanceof ThreadPanel) {
				((ThreadPanel)panel).setTabIndex(p_tabIndex);
			}

			x_window.setVisible(true);
            return x_window;
        }
    }

	private void setIconOnWindow(JFrame x_window) {

	}

	public void windowClosing(WindowEvent we)
    {
		Window window = we.getWindow();
		o_componentWindows.remove(o_windowComponents.get(window));
        o_windowComponents.remove(window);
        window.removeWindowListener(this);

		if(o_componentWindows.size() == 0) {
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
        
        StringBuffer x_title = new StringBuffer();
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
		Window x_parentWindow = getParentWindow(p_component);
        
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

	private Window getParentWindow(Component p_component) {
		Window x_parentWindow = (Window) o_componentWindows.get(p_component.getParentComponent());

		if(x_parentWindow == null && p_component.getParentComponent() != null)
		{
			Component x_tempComp = p_component.getParentComponent();

			while(o_componentWindows.get(x_tempComp) == null)
			{
				x_tempComp = x_tempComp.getParentComponent();
			}

			x_parentWindow = (Window) o_componentWindows.get(x_tempComp);
		}

		return x_parentWindow;
	}

	public static interface WindowListener {
		void lastWindowClosing();
	}
}
