package gui;

import data.Component;
import data.*;
import data.Thread;
import util.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class WindowManager extends WindowAdapter
{
    private static final WindowManager s_INSTANCE = new WindowManager();
    
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
    
    public Window openComponentWindow(Component p_component, boolean p_new, int p_tabIndex)
    {
        if(!o_componentWindows.containsKey(p_component))
        {
            JPanel x_panel = getComponentPanel(p_component, p_new, p_tabIndex);
            JFrame x_window = new JFrame();
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
    
    public void windowClosing(WindowEvent we)
    {
        o_componentWindows.remove(o_windowComponents.get(we.getWindow()));
        o_windowComponents.remove(we.getWindow());
        we.getWindow().removeWindowListener(this);
    }
    
    public void closeComponentWindow(Component p_component)
    {
        if(o_componentWindows.containsKey(p_component))
        {
            Window x_window = (Window)o_componentWindows.get(p_component);
            x_window.removeWindowListener(this);
            o_windowComponents.remove(o_componentWindows.get(p_component));
            o_componentWindows.remove(p_component);
            x_window.setVisible(false);
        }
    }
    
    void renameWindow(Component p_component)
    {
        JFrame x_window = (JFrame) o_componentWindows.get(p_component);
        
        StringBuffer x_title = new StringBuffer("Threads - ");
        
		if(p_component instanceof Item)
        {
            x_title.append("Item - ");
            
        }
        else if(p_component instanceof Reminder)
        {
            x_title.append("Reminder - ");
        }

        x_title.append(p_component.getText());
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
            return new ThreadPanel((Thread)p_component, tabIndex);
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
        
        if(x_parentWindow != null)
        {
            Point x_location = new Point(x_parentWindow.getX() + GUIConstants.s_windowOffset, 
                                         x_parentWindow.getY() + GUIConstants.s_windowOffset);
            p_window.setLocation(x_location);
        }
        
        if(x_parentWindow == null)
        {
            GUIUtil.centreWindow(p_window);
        }        
    }
}
