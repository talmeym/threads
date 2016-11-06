package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import static gui.Actions.linkToGoogle;
import static util.Settings.*;

public class ThreadPanel extends JPanel implements TimeUpdateListener, ComponentChangeListener, SettingChangeListener {
	private final Thread o_thread;
	private final JTabbedPane o_tabs;

	public ThreadPanel(Thread p_thread, JPanel p_parentPanel) {
        super(new BorderLayout());
        o_thread = p_thread;

		o_tabs = new JTabbedPane();
        o_tabs.addTab("Contents", ImageUtil.getFolderSmallIcon(), new ThreadContentsPanel(p_thread, p_parentPanel));
        o_tabs.addTab("Threads", ImageUtil.getThreadIcon(), new ThreadThreadPanel(p_thread, p_parentPanel));
        o_tabs.addTab("Updates", ImageUtil.getUpdateIcon(), new ThreadUpdatePanel(p_thread, p_parentPanel));
        o_tabs.addTab("Actions", ImageUtil.getActionIcon(), new ThreadActionPanel(p_thread, p_parentPanel));
        o_tabs.addTab("Reminders", ImageUtil.getReminderIcon(), new ThreadReminderPanel(p_thread, p_parentPanel));
        o_tabs.addTab("Calendar", ImageUtil.getCalendarSmallIcon(), new ThreadCalendarPanel(p_thread, p_parentPanel));

		o_tabs.setBackgroundAt(1, Color.gray);
		o_tabs.setBackgroundAt(2, Color.gray);
		o_tabs.setBackgroundAt(3, Color.gray);
		o_tabs.setBackgroundAt(4, Color.gray);
		o_tabs.setBackgroundAt(4, Color.white);

		o_tabs.setToolTipTextAt(0, "The contents of this Thread");
		o_tabs.setToolTipTextAt(1, "A view of all active Threads");
		o_tabs.setToolTipTextAt(2, "A view of all active Updates");
		o_tabs.setToolTipTextAt(3, "A view of all active Actions");
		o_tabs.setToolTipTextAt(4, "A view of all active Reminders");
		o_tabs.setToolTipTextAt(5, "A calendar view of all Items");

		final JLabel x_linkLabel = new JLabel(ImageUtil.getLinkIcon());
		x_linkLabel.setToolTipText("Link to Google Calendar");
		x_linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				linkToGoogle(o_thread, p_parentPanel);
			}
		});

		ComponentInfoPanel componentInfoPanel = new ComponentInfoPanel(p_thread, p_parentPanel, true, x_linkLabel);
		componentInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 3, 0, 3));
		add(componentInfoPanel, BorderLayout.NORTH);
        add(o_tabs, BorderLayout.CENTER);

		o_tabs.setSelectedIndex(registerForSetting(Settings.s_TAB_INDEX, this, 0));
		o_tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				updateSetting(Settings.s_TAB_INDEX, "" + o_tabs.getSelectedIndex());
			}
		});

        o_thread.addComponentChangeListener(this);
        TimeUpdater.getInstance().addTimeUpdateListener(this);
        setActionTabBackground();
		setReminderTabBackground();
    }

	@Override
	public void componentChanged(ComponentChangeEvent p_event) {
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
        if(LookupHelper.getAllActiveReminders(o_thread, true).size() > 0) {
            o_tabs.setTitleAt(4, "Reminders *");
            o_tabs.setBackgroundAt(4, Color.red);
        } else {
			o_tabs.setTitleAt(4, "Reminders");
            o_tabs.setBackgroundAt(4, o_tabs.getBackgroundAt(1));
        }
    }

	public static void setTabIndex(int p_tabIndex) {
		updateSetting(Settings.s_TAB_INDEX, "" + p_tabIndex);
	}

	@Override
	public void settingChanged(String name, Object value) {
		o_tabs.setSelectedIndex(Integer.parseInt(value.toString()));
	}
}
