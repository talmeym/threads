package gui;

import data.ThreadGroup;
import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ThreadGroupPanel extends JPanel implements TimeUpdateListener, Observer
{
    private final ThreadGroup o_threadGroup;
        
    private final JTabbedPane x_tabs;
    
    public ThreadGroupPanel(ThreadGroup p_threadGroup)
    {
        super(new BorderLayout());
        o_threadGroup = p_threadGroup;
        
        x_tabs = new JTabbedPane();
        x_tabs.addTab("Components", new ThreadGroupComponentPanel(p_threadGroup));
        x_tabs.addTab("Threads", new ThreadGroupThreadPanel(p_threadGroup));
        x_tabs.addTab("Thread Groups", new ThreadGroupThreadGroupPanel(p_threadGroup));
        x_tabs.addTab("Updates", new ThreadGroupUpdatePanel(p_threadGroup));        
        x_tabs.addTab("Actions", new ThreadGroupActionPanel(p_threadGroup)); 
        x_tabs.addTab("Reminders", new ThreadGroupReminderPanel(p_threadGroup)); 
        x_tabs.addTab("Tree", new ThreadGroupTreePanel(p_threadGroup));
        
        add(new ComponentInfoPanel(p_threadGroup), BorderLayout.NORTH);
        add(x_tabs, BorderLayout.CENTER);

        o_threadGroup.addObserver(this);
        TimeUpdater.getInstance().addTimeUpdateListener(this);
        setReminderTabBackground();
    }

    public void update(Observable o, Object arg)
    {
        setReminderTabBackground();
    }
    
    public void timeUpdate()
    {
        setReminderTabBackground();
    }
    
    private void setReminderTabBackground()
    {
        if(ThreadGroupHelper.getReminders(o_threadGroup).size() > 0)
        {
            x_tabs.setBackgroundAt(5, Color.RED);
        }
        else
        {
            x_tabs.setBackgroundAt(5, x_tabs.getBackgroundAt(0));
        }
    }
}
