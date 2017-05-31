package threads.gui;

import threads.data.Thread;
import threads.util.SettingChangeListener;
import threads.util.TimedUpdateListener;
import threads.util.TimedUpdater;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.Color.*;
import static threads.data.LookupHelper.*;
import static threads.gui.Actions.linkToGoogle;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.ImageUtil.*;
import static threads.util.Settings.*;

class ThreadPanel extends JPanel implements TimedUpdateListener, SettingChangeListener {
	private final Thread o_thread;
	private final JTabbedPane o_tabs;

	ThreadPanel(Thread p_thread, JPanel p_parentPanel) {
        super(new BorderLayout());
        o_thread = p_thread;

		o_thread.addComponentChangeListener(e -> {
			setActionTabBackground();
			setReminderTabBackground();
		});

		o_tabs = new JTabbedPane();
        o_tabs.addTab("Contents", getFolderSmallIcon(), new ThreadContentsPanel(p_thread, p_parentPanel));
        o_tabs.addTab("Threads", getThreadIcon(), new ThreadThreadPanel(p_thread, p_parentPanel));
        o_tabs.addTab("Updates", getUpdateIcon(), new ThreadUpdatePanel(p_thread, p_parentPanel));
        o_tabs.addTab("Actions", getActionIcon(), new ThreadActionPanel(p_thread, p_parentPanel));
        o_tabs.addTab("Reminders", getReminderIcon(), new ThreadReminderPanel(p_thread, p_parentPanel));
        o_tabs.addTab("Calendar", getCalendarSmallIcon(), new ThreadCalendarPanel(p_thread, p_parentPanel));

		o_tabs.setBackgroundAt(1, gray);
		o_tabs.setBackgroundAt(2, gray);
		o_tabs.setBackgroundAt(3, gray);
		o_tabs.setBackgroundAt(4, gray);
		o_tabs.setBackgroundAt(5, white);

		o_tabs.setToolTipTextAt(0, "The contents of this Thread");
		o_tabs.setToolTipTextAt(1, "A view of all active Threads");
		o_tabs.setToolTipTextAt(2, "A view of all active Updates");
		o_tabs.setToolTipTextAt(3, "A view of all active Actions");
		o_tabs.setToolTipTextAt(4, "A view of all active Reminders");
		o_tabs.setToolTipTextAt(5, "A calendar view of all Items");

		final JLabel x_linkLabel = createLabel(getLinkIcon(), "Link to Google Calendar", true, e -> {
			JPopupMenu x_popupMenu = new JPopupMenu();

			JMenuItem x_linkActive = new JMenuItem("Link active");
			x_linkActive.addActionListener(f -> linkToGoogle(getHasDueDates(o_thread, true), p_parentPanel));

			JMenuItem x_linkAll = new JMenuItem("Link all");
			x_linkAll.addActionListener(f -> linkToGoogle(getHasDueDates(o_thread, false), p_parentPanel));

			x_popupMenu.add(x_linkActive);
			x_popupMenu.add(x_linkAll);

			x_popupMenu.show(this, e.getX(), e.getY());
		});


		ComponentInfoPanel componentInfoPanel = new ComponentInfoPanel(p_thread, p_parentPanel, true, x_linkLabel);
		componentInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 3, 0, 3));
		add(componentInfoPanel, NORTH);
        add(o_tabs, CENTER);

		o_tabs.setSelectedIndex(registerForSetting(s_TABINDEX, this, 0));
		o_tabs.addChangeListener(changeEvent -> updateSetting(s_TABINDEX, "" + o_tabs.getSelectedIndex()));

        TimedUpdater.getInstance().addActivityListener(this);
        setActionTabBackground();
		setReminderTabBackground();
    }

    public void timeUpdate() {
		setActionTabBackground();
        setReminderTabBackground();
    }

	private void setActionTabBackground() {
        if(getAllActiveDueActions(o_thread).size() > 0) {
            o_tabs.setTitleAt(3, "Actions *");
            o_tabs.setBackgroundAt(3, red);
        } else {
			o_tabs.setTitleAt(3, "Actions");
            o_tabs.setBackgroundAt(3, o_tabs.getBackgroundAt(1));
        }
    }

    private void setReminderTabBackground() {
        if(getAllActiveReminders(o_thread, true).size() > 0) {
            o_tabs.setTitleAt(4, "Reminders *");
            o_tabs.setBackgroundAt(4, red);
        } else {
			o_tabs.setTitleAt(4, "Reminders");
            o_tabs.setBackgroundAt(4, o_tabs.getBackgroundAt(1));
        }
    }

	static void setTabIndex(int p_tabIndex) {
		updateSetting(s_TABINDEX, "" + p_tabIndex);
	}

	@Override
	public void settingChanged(String p_name, Object p_value) {
		o_tabs.setSelectedIndex(Integer.parseInt(p_value.toString()));
	}
}
