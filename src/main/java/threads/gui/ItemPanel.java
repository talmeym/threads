package threads.gui;

import threads.data.*;
import threads.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.util.*;
import java.util.List;

import static java.awt.BorderLayout.*;
import static java.util.Calendar.*;
import static javax.swing.BorderFactory.createEmptyBorder;
import static threads.gui.Actions.linkToGoogle;
import static threads.gui.GUIConstants.*;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.GoogleUtil.googleAccount;
import static threads.util.ImageUtil.*;
import static threads.util.Settings.*;

class ItemPanel extends ComponentTablePanel<Item, Reminder> {
    private final Item o_item;
	private final JLabel o_linkLabel;

	ItemPanel(Item p_item, JPanel p_parentPanel, JFrame p_frame) {
        super(new ItemReminderTableModel(p_item),  new ItemReminderCellRenderer());
        o_item = p_item;

		JLabel x_calendarLabel = createLabel(getCalendarIcon(), "Show in Calendar", o_item, i -> i.getDueDate() != null, e -> {
			Calendar x_calendar = Calendar.getInstance();
			x_calendar.setTime(o_item.getDueDate());
			updateSetting(s_TABINDEX, 5);
			updateSetting(s_DATE, x_calendar.get(MONTH) + "_" + x_calendar.get(YEAR));
			updateSetting(s_CALENDARACT, true);
			WindowManager.getInstance().openComponent(o_item.getParentThread());
		});

		JLabel x_templateItemLabel = createLabel(getTemplateIcon(), "Create Action Template", o_item, i -> o_item.getDueDate() != null && o_item.getReminders().size() > 0, e -> new ActionTemplateBuilderDialog(o_item, p_frame));
		o_linkLabel = createLabel(getLinkIcon(), "Link to Google Calendar", o_item, i -> o_item.getDueDate() != null, e -> linkToGoogle(o_item, p_parentPanel));

		fixColumnWidth(1, s_creationDateColumnWidth);
        fixColumnWidth(2, s_dateStatusColumnWidth);
        fixColumnWidth(3, 30);


        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, p_parentPanel, true, x_calendarLabel, x_templateItemLabel, o_linkLabel), NORTH);
        x_panel.add(new ItemDateSuggestionPanel(o_item, p_parentPanel), CENTER);
		x_panel.add(new HasDueDateNotesPanel(o_item), SOUTH);
		x_panel.setBorder(createEmptyBorder(0, 0, 5, 0));

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
		setLinkLabelText();
	}

	@Override
	public void googleSynced(List<HasDueDate> p_hasDueDates) {
		setLinkLabelText();
	}

	public void setLinkLabelText() {
		GoogleAccount x_account = googleAccount(o_item);
		o_linkLabel.setIcon(x_account != null ? getGoogleSmallIcon() : getLinkIcon());
		o_linkLabel.setToolTipText(x_account != null ? x_account.getName() : "Link to Google Calendar");
	}

	void selectReminder(Reminder p_reminder) {
		int x_index = ((ItemReminderTableModel)o_table.getModel()).indexOf(p_reminder);
		o_table.getSelectionModel().setSelectionInterval(x_index, x_index);
	}
}