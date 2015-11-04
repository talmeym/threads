package gui;

import data.Thread;
import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ThreadPanel extends JPanel implements TimeUpdateListener, Observer
{
	private final Thread o_thread;
    private final JTabbedPane x_tabs;
    
    public ThreadPanel(Thread p_thread, int tabIndex)
    {
        super(new BorderLayout());
        o_thread = p_thread;
        
        x_tabs = new JTabbedPane();
        x_tabs.addTab("Components", new ThreadComponentPanel(p_thread));
        x_tabs.addTab("Threads", new ThreadThreadPanel(p_thread));
        x_tabs.addTab("Updates", new ThreadUpdatePanel(p_thread));
        x_tabs.addTab("Actions", new ThreadActionPanel(p_thread));
        x_tabs.addTab("Reminders", new ThreadReminderPanel(p_thread));
        x_tabs.addTab("Tree", new ThreadTreePanel(p_thread));
        
        add(new ComponentInfoPanel(p_thread), BorderLayout.NORTH);
        add(x_tabs, BorderLayout.CENTER);

		x_tabs.setSelectedIndex(tabIndex);

        o_thread.addObserver(this);
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
        if(ThreadHelper.getAllReminders(o_thread).size() > 0)
        {
            x_tabs.setBackgroundAt(5, Color.RED);
        }
        else
        {
            x_tabs.setBackgroundAt(5, x_tabs.getBackgroundAt(0));
        }
    }
}
