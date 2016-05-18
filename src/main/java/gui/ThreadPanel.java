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
    private final JTabbedPane o_tabs;
    
    public ThreadPanel(Thread p_thread, int tabIndex)
    {
        super(new BorderLayout());
        o_thread = p_thread;
        
        o_tabs = new JTabbedPane();
        o_tabs.addTab("Contents", new ThreadContentsPanel(p_thread));
        o_tabs.addTab("Threads", new ThreadThreadPanel(p_thread));
        o_tabs.addTab("Updates", new ThreadUpdatePanel(p_thread));
        o_tabs.addTab("Actions", new ThreadActionPanel(p_thread));
        o_tabs.addTab("Reminders", new ThreadReminderPanel(p_thread));
        o_tabs.addTab("Tree", new ThreadTreePanel(p_thread));
        
        add(new ComponentInfoPanel(p_thread), BorderLayout.NORTH);
        add(o_tabs, BorderLayout.CENTER);

		o_tabs.setSelectedIndex(tabIndex);

        o_thread.addObserver(this);
        TimeUpdater.getInstance().addTimeUpdateListener(this);
        setActionTabBackground();
		setReminderTabBackground();
    }

	public void setTabIndex(int tabIndex) {
		o_tabs.setSelectedIndex(tabIndex);
	}

    public void update(Observable o, Object arg)
    {
        setActionTabBackground();
        setReminderTabBackground();
    }
    
    public void timeUpdate()
    {
		setActionTabBackground();
        setReminderTabBackground();
    }

    private void setActionTabBackground()
    {
        if(ThreadHelper.getAllDueActions(o_thread).size() > 0)
        {
            o_tabs.setTitleAt(3, "Actions *");
            o_tabs.setBackgroundAt(3, Color.RED);
        }
        else
        {
			o_tabs.setTitleAt(3, "Actions");
            o_tabs.setBackgroundAt(3, o_tabs.getBackgroundAt(0));
        }
    }

    private void setReminderTabBackground()
    {
        if(ThreadHelper.getAllDueReminders(o_thread).size() > 0)
        {
            o_tabs.setTitleAt(4, "Reminders *");
            o_tabs.setBackgroundAt(4, Color.RED);
        }
        else
        {
			o_tabs.setTitleAt(4, "Reminders");
            o_tabs.setBackgroundAt(4, o_tabs.getBackgroundAt(0));
        }
    }
}
