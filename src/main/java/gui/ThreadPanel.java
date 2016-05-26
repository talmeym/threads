package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.*;

public class ThreadPanel extends JPanel implements TimeUpdateListener, Observer, ComponentInfoChangeListener {
	private final Thread o_thread;
    private final JTabbedPane o_tabs;
    
    public ThreadPanel(Thread p_thread, boolean p_new, int tabIndex, ChangeListener listener) {
        super(new BorderLayout());
        o_thread = p_thread;
        
        o_tabs = new JTabbedPane();
        o_tabs.addTab("Contents", new ThreadContentsPanel(p_thread));
        o_tabs.addTab("Threads", new ThreadThreadPanel(p_thread));
        o_tabs.addTab("Updates", new ThreadUpdatePanel(p_thread));
        o_tabs.addTab("Actions", new ThreadActionPanel(p_thread));
        o_tabs.addTab("Reminders", new ThreadReminderPanel(p_thread));
        o_tabs.addTab("Tree", new ThreadTreePanel(p_thread));

		ComponentInfoPanel componentInfoPanel = new ComponentInfoPanel(p_thread, p_new, this);
		componentInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		add(componentInfoPanel, BorderLayout.NORTH);
        add(o_tabs, BorderLayout.CENTER);

		int index = tabIndex != -1 ? tabIndex : 0;
		o_tabs.setSelectedIndex(index);
		o_tabs.addChangeListener(listener);

        o_thread.addObserver(this);
        TimeUpdater.getInstance().addTimeUpdateListener(this);
        setActionTabBackground();
		setReminderTabBackground();
    }

	public int getTabIndex() {
		return o_tabs.getSelectedIndex();
	}

	public void setTabIndex(int tabIndex) {
		if(tabIndex != -1) {
			ChangeListener[] changeListeners = o_tabs.getChangeListeners();

			for(ChangeListener listener: changeListeners) {
				o_tabs.removeChangeListener(listener);
			}

			o_tabs.setSelectedIndex(tabIndex);

			for(ChangeListener listener: changeListeners) {
				o_tabs.addChangeListener(listener);
			}
		}
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
            o_tabs.setBackgroundAt(3, Color.RED);
        } else {
			o_tabs.setTitleAt(3, "Actions");
            o_tabs.setBackgroundAt(3, o_tabs.getBackgroundAt(0));
        }
    }

    private void setReminderTabBackground() {
        if(LookupHelper.getAllDueReminders(o_thread).size() > 0) {
            o_tabs.setTitleAt(4, "Reminders *");
            o_tabs.setBackgroundAt(4, Color.RED);
        } else {
			o_tabs.setTitleAt(4, "Reminders");
            o_tabs.setBackgroundAt(4, o_tabs.getBackgroundAt(0));
        }
    }

	@Override
	public void componentInfoChanged(boolean saved) {
		// do nothing
	}
}
