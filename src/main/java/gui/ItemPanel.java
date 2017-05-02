package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.Calendar;

import static gui.Actions.linkToGoogle;
import static util.Settings.updateSetting;

class ItemPanel extends ComponentTablePanel<Item, Reminder> implements ComponentChangeListener {
    private final Item o_item;
	private final JLabel o_calendarLabel = new JLabel(ImageUtil.getCalendarIcon());
	private final JLabel o_linkItemLabel = new JLabel(ImageUtil.getLinkIcon());

	ItemPanel(Item p_item, final JPanel p_parentPanel) {
        super(new ItemReminderTableModel(p_item),  new ComponentCellRenderer(p_item));
        o_item = p_item;
		o_item.addComponentChangeListener(this);

        fixColumnWidth(1, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(2, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(3, 30);

		o_calendarLabel.setEnabled(o_item.getDueDate() != null);
		o_calendarLabel.setToolTipText("Show in Calendar");
		o_calendarLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (o_calendarLabel.isEnabled()) {
					Calendar x_calendar = Calendar.getInstance();
					x_calendar.setTime(o_item.getDueDate());
					updateSetting(Settings.s_TAB_INDEX, 5);
					updateSetting(Settings.s_DATE, x_calendar.get(Calendar.MONTH) + "_" + x_calendar.get(Calendar.YEAR));
					WindowManager.getInstance().openComponent(o_item.getParentThread());
				}
			}
		});

		o_linkItemLabel.setEnabled(o_item.getDueDate() != null);
		o_linkItemLabel.setToolTipText("Link to Google Calendar");
		o_linkItemLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (o_linkItemLabel.isEnabled()) {
					linkToGoogle(o_item, p_parentPanel);
				}
			}
		});

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, p_parentPanel, true, o_calendarLabel, o_linkItemLabel), BorderLayout.NORTH);
        x_panel.add(new DateSuggestionPanel(o_item, p_parentPanel), BorderLayout.SOUTH);
		x_panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		add(x_panel, BorderLayout.NORTH);

		GoogleSyncer.getInstance().addGoogleSyncListener(this);
    }

	@Override
	public void componentChanged(ComponentChangeEvent p_cce) {
		if(p_cce.getSource() == o_item) {
			o_calendarLabel.setEnabled(o_item.getDueDate() != null);
			o_linkItemLabel.setEnabled(o_item.getDueDate() != null);
		}
	}

	@Override
	void showContextMenu(int p_row, int p_col, Point p_point, Component p_origin, Reminder p_selectedObject) {
		// nothing to do here
	}

	@Override
	public void googleSynced() {
		if(GoogleUtil.isLinked(o_item)) {
			o_linkItemLabel.setIcon(ImageUtil.getGoogleSmallIcon());
		} else {
			o_linkItemLabel.setIcon(ImageUtil.getLinkIcon());
		}
	}
}