package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

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

		o_tabs.setBackgroundAt(1, Color.gray);
		o_tabs.setBackgroundAt(2, Color.gray);
		o_tabs.setBackgroundAt(3, Color.gray);
		o_tabs.setBackgroundAt(4, Color.gray);
		o_tabs.setBackgroundAt(5, Color.gray);

		o_tabs.setToolTipTextAt(0, "The contents of this Thread");
		o_tabs.setToolTipTextAt(1, "A view of all active child Threads");
		o_tabs.setToolTipTextAt(2, "A view of all active child Updates");
		o_tabs.setToolTipTextAt(3, "A view of all active child Actions");
		o_tabs.setToolTipTextAt(4, "A calendar view of all active child Actions");
		o_tabs.setToolTipTextAt(5, "A view of all due Reminders of active child Actions");

		final JLabel x_linkLabel = new JLabel(ImageUtil.getLinkIcon());
		x_linkLabel.setToolTipText("Link to Google Calendar");
		x_linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				linkToGoogle();
			}
		});

		ComponentInfoPanel componentInfoPanel = new ComponentInfoPanel(p_thread, this, this, x_linkLabel);
		componentInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 3, 0, 3));
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

	private void linkToGoogle() {
		final JPanel x_this = this;
		final List<Item> x_actions = LookupHelper.getAllActions(o_thread);

		if (x_actions.size() > 0) {
			if (JOptionPane.showConfirmDialog(x_this, "Link " + x_actions.size() + " Action" + (x_actions.size() > 1 ? "s" : "") + " to Google Calendar ?", "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
				GoogleLinkTask x_task = new GoogleLinkTask(x_actions, new GoogleProgressWindow(x_this), new ProgressAdapter() {
					@Override
					public void finished() {
						JOptionPane.showMessageDialog(x_this, x_actions.size() + " Action" + (x_actions.size() > 1 ? "s were" : " was") + " linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
					}
				});
				x_task.execute();
			}
		}
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
            o_tabs.setBackgroundAt(5, Color.red);
        } else {
			o_tabs.setTitleAt(5, "Reminders");
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
