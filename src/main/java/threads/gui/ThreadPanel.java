package threads.gui;

import threads.data.Configuration;
import threads.data.Thread;
import threads.util.Settings;
import threads.util.TimedUpdateListener;
import threads.util.TimedUpdater;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.Color.*;
import static java.lang.Integer.parseInt;
import static javax.swing.BorderFactory.createEmptyBorder;
import static threads.data.LookupHelper.*;
import static threads.gui.Actions.linkToGoogle;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.ImageUtil.*;
import static threads.util.Settings.Setting.TABINDEX;

class ThreadPanel extends JPanel implements TimedUpdateListener {
	private final Thread o_thread;
	private final JTabbedPane o_tabs = new JTabbedPane();

	ThreadPanel(Configuration p_configuration, Thread p_thread, JPanel p_parentPanel) {
        super(new BorderLayout());
        o_thread = p_thread;


		o_thread.addComponentChangeListener(e -> {
			setActionTabBackground();
			setReminderTabBackground();
		});

		addTab("Contents", "The contents of this Thread", getFolderSmallIcon(), new ThreadContentsPanel(p_configuration, p_thread, p_parentPanel), white);
        addTab("Threads", "A view of all active Threads", getThreadIcon(), new ThreadThreadPanel(p_configuration, p_thread, p_parentPanel), gray);
        addTab("Updates", "A view of all active Updates", getUpdateIcon(), new ThreadUpdatePanel(p_configuration, p_thread, p_parentPanel), gray);
        addTab("Actions", "A view of all active Actions", getActionIcon(), new ThreadActionPanel(p_configuration, p_thread, p_parentPanel), gray);
        addTab("Reminders", "A view of all active Reminders", getReminderIcon(), new ThreadReminderPanel(p_configuration, p_thread, p_parentPanel), gray);
        addTab("Calendar", "A calendar view of all Items", getCalendarSmallIcon(), new ThreadCalendarPanel(p_configuration, p_thread, p_parentPanel), white);

        Settings x_settings = p_configuration.getSettings();
		JLabel x_linkLabel = createLabel(getLinkIcon(), "Link to Google Calendar", true);

		x_linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JPopupMenu x_popupMenu = new JPopupMenu();

				JMenuItem x_linkActive = new JMenuItem("Link active");
				x_linkActive.addActionListener(f -> linkToGoogle(getHasDueDates(o_thread, true), p_configuration, p_parentPanel));

				JMenuItem x_linkAll = new JMenuItem("Link all");
				x_linkAll.addActionListener(f -> linkToGoogle(getHasDueDates(o_thread, false), p_configuration, p_parentPanel));

				x_popupMenu.add(x_linkActive);
				x_popupMenu.add(x_linkAll);

				x_popupMenu.show(x_linkLabel, e.getX(), e.getY());
			}
		});

		ComponentInfoPanel componentInfoPanel = new ComponentInfoPanel(p_thread, p_parentPanel, true, x_linkLabel);
		componentInfoPanel.setBorder(createEmptyBorder(5, 3, 0, 3));
		add(componentInfoPanel, NORTH);
        add(o_tabs, CENTER);

		o_tabs.setSelectedIndex(x_settings.registerForIntSetting(TABINDEX, (k, v) -> o_tabs.setSelectedIndex(parseInt(v.toString()))));
		o_tabs.addChangeListener(e -> x_settings.updateSetting(TABINDEX, "" + o_tabs.getSelectedIndex()));

        TimedUpdater.getInstance().addActivityListener(this);
        setActionTabBackground();
		setReminderTabBackground();
    }

    private void addTab(String p_title, String p_toolTipText, Icon p_icon, JPanel p_panel, Color p_color) {
        o_tabs.addTab(p_title, p_icon, p_panel, p_toolTipText);
        o_tabs.setBackgroundAt(o_tabs.getTabCount() - 1, p_color);

    }

    public void timeUpdate() {
		setActionTabBackground();
        setReminderTabBackground();
    }

	private void setActionTabBackground() {
        boolean x_dueActions = getAllActiveDueActions(o_thread).size() > 0;
        o_tabs.setTitleAt(3, x_dueActions ? "Actions *" : "Actions");
        o_tabs.setBackgroundAt(3, x_dueActions ? red : o_tabs.getBackgroundAt(1));
    }

    private void setReminderTabBackground() {
        boolean x_dueReminders = getAllActiveReminders(o_thread, true).size() > 0;
        o_tabs.setTitleAt(4, x_dueReminders ? "Reminders *" : "Reminders");
        o_tabs.setBackgroundAt(4, x_dueReminders ? red : o_tabs.getBackgroundAt(1));
    }
}
