package gui;

import data.*;
import util.GoogleSyncer;

import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.util.*;
import java.util.List;

import static gui.Actions.linkToGoogle;
import static gui.GUIConstants.*;
import static gui.WidgetFactory.createLabel;
import static java.awt.BorderLayout.*;
import static java.util.Calendar.*;
import static util.GoogleUtil.isLinked;
import static util.ImageUtil.*;
import static util.Settings.*;

class ItemPanel extends ComponentTablePanel<Item, Reminder> {
    private final Item o_item;
	private final JLabel o_linkItemLabel;

	ItemPanel(Item p_item, JPanel p_parentPanel, JFrame p_frame) {
        super(new ItemReminderTableModel(p_item),  new ContentsCellRenderer(p_item));
        o_item = p_item;

		JLabel o_calendarLabel = createLabel(getCalendarIcon(), "Show in Calendar", o_item, i -> i.getDueDate() != null, e -> {
			Calendar x_calendar = Calendar.getInstance();
			x_calendar.setTime(o_item.getDueDate());
			updateSetting(s_TABINDEX, 5);
			updateSetting(s_DATE, x_calendar.get(MONTH) + "_" + x_calendar.get(YEAR));
			WindowManager.getInstance().openComponent(o_item.getParentThread());
		});

		JLabel o_templateItemLabel = createLabel(getTemplateIcon(), "Create Action Template", o_item, i -> o_item.getDueDate() != null && o_item.getReminders().size() > 0, e -> new ActionTemplateBuilderDialog(o_item, p_frame));
		o_linkItemLabel = createLabel(getLinkIcon(), "Link to Google Calendar", o_item, i -> o_item.getDueDate() != null, e -> linkToGoogle(o_item, p_parentPanel));

		fixColumnWidth(1, s_creationDateColumnWidth);
        fixColumnWidth(2, s_dateStatusColumnWidth);
        fixColumnWidth(3, 30);

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, p_parentPanel, true, o_calendarLabel, o_templateItemLabel, o_linkItemLabel), NORTH);
        x_panel.add(new DateSuggestionPanel(o_item, p_parentPanel), SOUTH);
		x_panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		add(x_panel, NORTH);

		GoogleSyncer.getInstance().addActivityListener(this);
		googleSynced();
    }

	@Override
	void showContextMenu(Component p_origin, int p_row, int p_col, Point p_point, Reminder p_selectedObject) {
		// nothing to do here
	}

	@Override
	public void googleSynced() {
		o_linkItemLabel.setIcon(isLinked(o_item) ? getGoogleSmallIcon() : getLinkIcon());
	}

	@Override
	public void googleSynced(List<HasDueDate> p_hasDueDates) {
		o_linkItemLabel.setIcon(isLinked(o_item) ? getGoogleSmallIcon() : getLinkIcon());
	}

	void selectReminder(Reminder p_reminder) {
		int x_index = ((ItemReminderTableModel)o_table.getModel()).indexOf(p_reminder);
		o_table.getSelectionModel().setSelectionInterval(x_index, x_index);
	}
}