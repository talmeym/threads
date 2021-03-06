package threads.gui;

import com.google.api.services.calendar.model.Event;
import threads.data.Configuration;
import threads.data.HasDueDate;
import threads.data.Item;
import threads.data.Reminder;
import threads.util.GoogleAccount;
import threads.util.GoogleSyncer;
import threads.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.List;

import static java.awt.BorderLayout.*;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static javax.swing.BorderFactory.createEmptyBorder;
import static threads.gui.Actions.linkToGoogle;
import static threads.gui.GUIConstants.s_creationDateColumnWidth;
import static threads.gui.GUIConstants.s_dateStatusColumnWidth;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.GoogleUtil.googleAccount;
import static threads.util.ImageUtil.*;
import static threads.util.Settings.Setting.*;

class ItemPanel extends ComponentTablePanel<Item, Reminder> {
    private final Item o_item;
	private final JLabel o_linkLabel;

	ItemPanel(Configuration p_configuration, Item p_item, JPanel p_parentPanel, JFrame p_frame) {
        super(new ItemReminderTableModel(p_item),  new ItemReminderCellRenderer());
        o_item = p_item;
		Settings x_settings = p_configuration.getSettings();

		JLabel x_calendarLabel = createLabel(getCalendarIcon(), "Show in Calendar", o_item, i -> true, e -> {
			x_settings.updateSetting(TABINDEX, 5);
			Calendar x_calendar = Calendar.getInstance();
			x_calendar.setTime(o_item.getDueDate() != null ? o_item.getDueDate() : o_item.getModifiedDate());
			x_settings.updateSetting(Settings.Setting.DATE, x_calendar.get(MONTH) + "_" + x_calendar.get(YEAR));
			x_settings.updateSetting(o_item.getDueDate() != null ? CALENDARACT : CALENDARUP, true);
			WindowManager.getInstance().openComponent(o_item.getParentThread());
		});

		JLabel x_templateItemLabel = createLabel(getTemplateIcon(), "Create Action Template", true);

		x_templateItemLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				JPopupMenu x_popupMenu = new JPopupMenu();

				JMenuItem x_createItem = new JMenuItem("Create");
				x_createItem.setEnabled(o_item.getDueDate() != null && o_item.getReminders().size() > 0);
				x_createItem.addActionListener(e -> {
					new ActionTemplateBuilderDialog(p_configuration, o_item, p_frame);
				});

				JMenuItem x_seeAllItem = new JMenuItem("See All");
				x_seeAllItem.addActionListener(e -> {
					new ShowActionTemplatesDialog(p_configuration, p_frame);
				});

				x_popupMenu.add(x_createItem);
				x_popupMenu.add(x_seeAllItem);

				x_popupMenu.show(x_templateItemLabel, p_me.getX(), p_me.getY());
			}
		});

		o_linkLabel = createLabel(getLinkIcon(), "Link to Google Calendar", o_item, i -> o_item.getDueDate() != null, e -> linkToGoogle(o_item, p_configuration, p_parentPanel));

		fixColumnWidth(1, s_creationDateColumnWidth);
        fixColumnWidth(2, s_dateStatusColumnWidth);
        fixColumnWidth(3, 30);

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, p_parentPanel, true, x_calendarLabel, x_templateItemLabel, o_linkLabel), NORTH);
        x_panel.add(new ItemDateSuggestionPanel(p_configuration, o_item, p_parentPanel), CENTER);
		x_panel.add(new HasDueDateNotesPanel(o_item), SOUTH);
		x_panel.setBorder(createEmptyBorder(0, 0, 5, 0));

		add(x_panel, NORTH);

		GoogleSyncer.getInstance().addActivityListener(this);
		googleSyncFinished();
    }

	@Override
	void showContextMenu(Component p_origin, int p_row, int p_col, Point p_point, Reminder p_selectedObject) {
		// nothing to do here
	}

	@Override
	public void googleSyncFinished() {
		setLinkLabelText();
	}

	@Override
	public void itemsLinked(List<HasDueDate> p_hasDueDatesSynced) {
		setLinkLabelText();
	}

	@Override
	public void googleSynced(List<HasDueDate> p_itemsCreated, List<Event> p_eventsDeleted) {
		setLinkLabelText();
	}

	private void setLinkLabelText() {
		GoogleAccount x_account = googleAccount(o_item);
		o_linkLabel.setIcon(x_account != null ? getGoogleSmallIcon() : getLinkIcon());
		o_linkLabel.setToolTipText(x_account != null ? x_account.getName() : "Link to Google Calendar");
	}

	void selectReminder(Reminder p_reminder) {
		int x_index = ((ItemReminderTableModel)o_table.getModel()).indexOf(p_reminder);
		o_table.getSelectionModel().setSelectionInterval(x_index, x_index);
	}
}