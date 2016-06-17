package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;

public class ThreadPanel extends MemoryPanel implements TimeUpdateListener, Observer, ComponentInfoChangeListener {
	private final Thread o_thread;
	private final JTabbedPane o_tabs;
    
    public ThreadPanel(Thread p_thread) {
        super(new BorderLayout());
        o_thread = p_thread;

		o_tabs = new JTabbedPane();
        o_tabs.addTab("Contents", new ThreadContentsPanel(p_thread));
        o_tabs.addTab("Threads", new ThreadThreadPanel(p_thread));
        o_tabs.addTab("Updates", new ThreadUpdatePanel(p_thread));
        o_tabs.addTab("Actions", new ThreadActionPanel(p_thread));
        o_tabs.addTab("Calendar", new ThreadCalendarPanel(p_thread));
        o_tabs.addTab("Reminders", new ThreadReminderPanel(p_thread));
        o_tabs.addTab("Tree", new ThreadTreePanel(p_thread));

		o_tabs.setForegroundAt(0, Color.lightGray);
		o_tabs.setForegroundAt(5, Color.lightGray);
		o_tabs.setForegroundAt(6, Color.lightGray);

		ComponentInfoPanel componentInfoPanel = new ComponentInfoPanel(p_thread, this);
		componentInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		add(componentInfoPanel, BorderLayout.NORTH);
        add(o_tabs, BorderLayout.CENTER);

		o_tabs.setSelectedIndex(recallValue(0));
		o_tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				rememberValue(o_tabs.getSelectedIndex());
			}
		});

        o_thread.addObserver(this);
        TimeUpdater.getInstance().addTimeUpdateListener(this);
        setActionTabBackground();
		setReminderTabBackground();
    }

	@Override
	protected void memoryChanged(int p_newMemory) {
		o_tabs.setSelectedIndex(p_newMemory);
	}

    public void update(Observable o, Object arg) {
        setActionTabBackground();
        setReminderTabBackground();
    }
    
    public void timeUpdate() {
		setActionTabBackground();
        setReminderTabBackground();
    }

	private void setActionTabBackground() {
        if(LookupHelper.getAllDueActions(o_thread).size() > 0) {
            o_tabs.setTitleAt(3, "Actions *");
            o_tabs.setBackgroundAt(3, Color.red);
        } else {
			o_tabs.setTitleAt(3, "Actions");
            o_tabs.setBackgroundAt(3, o_tabs.getBackgroundAt(1));
        }
    }

    private void setReminderTabBackground() {
        if(LookupHelper.getAllDueReminders(o_thread).size() > 0) {
            o_tabs.setTitleAt(5, "Reminders *");
            o_tabs.setForegroundAt(5, Color.black);
            o_tabs.setBackgroundAt(5, Color.red);
        } else {
			o_tabs.setTitleAt(5, "Reminders");
			o_tabs.setForegroundAt(5, Color.lightGray);
            o_tabs.setBackgroundAt(5, o_tabs.getBackgroundAt(1));
        }
    }

	@Override
	public void componentInfoChanged(boolean saved) {
		// do nothing
	}

	public static void setTabIndex(int p_tabIndex) {
		MemoryPanel.setMemoryValue(ThreadPanel.class, p_tabIndex);
	}
}
