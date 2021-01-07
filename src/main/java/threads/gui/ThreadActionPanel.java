package threads.gui;

import threads.data.Configuration;
import threads.data.Item;
import threads.data.Thread;
import threads.data.View;
import threads.util.GoogleSyncer;
import threads.util.Settings;
import threads.util.TimedUpdater;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.SwingConstants.VERTICAL;
import static threads.data.ComponentType.Action;
import static threads.data.View.*;
import static threads.gui.Actions.*;
import static threads.gui.GUIConstants.*;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.ImageUtil.getPlusIcon;
import static threads.util.ImageUtil.getTemplateIcon;
import static threads.util.Settings.Setting.ACTIONVIEW;

class ThreadActionPanel extends ComponentTablePanel<Thread, Item> {
    private final Thread o_thread;
	private final ContextualPopupMenu o_popupMenu = new ContextualPopupMenu(true, true, Action);
	private final JRadioButton o_showDueRadioButton;
	private final JRadioButton o_showNext7DaysRadioButton;
	private final JRadioButton o_showAllRadioButton;
	private final JLabel o_topLabel = new JLabel("0 Actions");

	ThreadActionPanel(Configuration p_configuration, Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadActionTableModel(p_thread), new ThreadActionCellRenderer());
        o_thread = p_thread;
		o_thread.addComponentChangeListener(e -> tableRowClicked(-1, -1, null));

		Settings x_settings = p_configuration.getSettings();

        fixColumnWidth(0, s_threadColumnWidth);
        fixColumnWidth(2, s_dateStatusColumnWidth);
        fixColumnWidth(3, s_dateStatusColumnWidth);
        fixColumnWidth(4, s_googleStatusColumnWidth);

		JLabel x_addLabel = createLabel(getPlusIcon(), "Add Action", true, e -> addAction(getSelectedObject(), o_thread, ItemDateSuggestionPanel.getDateSuggestion(), p_parentPanel, true));
		JLabel x_addTemplateLabel = createLabel(getTemplateIcon(), "Add From Template", true, e -> addActionFromTemplate(p_configuration, getSelectedObject(), o_thread, ItemDateSuggestionPanel.getDateSuggestion(), p_parentPanel, true));

		o_popupMenu.setActivateActionListener(e -> activateComponent(getSelectedObject(), p_parentPanel));
		o_popupMenu.setDeactivateActionListener(e -> deactivateComponent(getSelectedObject(), p_parentPanel));
		o_popupMenu.setRemoveActionListener(e -> removeComponent(getSelectedObject(), p_parentPanel, false));
		o_popupMenu.setMoveActionListener(e -> moveThreadItem(getSelectedObject(), p_parentPanel));
		o_popupMenu.setLinkActionListener(e -> linkToGoogle(getSelectedObject(), p_configuration, p_parentPanel));

		View x_view = View.valueOf(x_settings.getStringSetting(ACTIONVIEW));
		ThreadActionTableModel x_tableModel = (ThreadActionTableModel) o_table.getModel();
		x_tableModel.setView(x_view);

		o_showDueRadioButton = new JRadioButton("Due", x_view == DUE);
		o_showNext7DaysRadioButton = new JRadioButton("7 Days", x_view == SEVENDAYS);
		o_showAllRadioButton = new JRadioButton("All", x_view == ALL);

		x_settings.registerForStringSetting(ACTIONVIEW, (p_setting, p_value) -> {
			View x_newView = (View) p_value;
			if(x_newView != x_tableModel.getView()) {
				x_tableModel.setView(x_newView);
				switch (x_newView) {
					case DUE: o_showDueRadioButton.setSelected(true); break;
					case SEVENDAYS: o_showNext7DaysRadioButton.setSelected(true); break;
					case ALL: o_showAllRadioButton.setSelected(true); break;
				}
			}
		});

		o_showDueRadioButton.addChangeListener(e -> radioButtonChanged(o_showDueRadioButton, DUE, x_settings, x_tableModel));
		o_showNext7DaysRadioButton.addChangeListener(e -> radioButtonChanged(o_showNext7DaysRadioButton, SEVENDAYS, x_settings, x_tableModel));
		o_showAllRadioButton.addChangeListener(e -> radioButtonChanged(o_showAllRadioButton, ALL, x_settings, x_tableModel));

		ButtonGroup x_group = new ButtonGroup();
		x_group.add(o_showDueRadioButton);
		x_group.add(o_showNext7DaysRadioButton);
		x_group.add(o_showAllRadioButton);

        o_topLabel.setHorizontalAlignment(JLabel.CENTER);
        o_topLabel.setText(o_table.getModel().getRowCount() + " Actions");
        o_thread.addComponentChangeListener(l -> o_topLabel.setText(o_table.getModel().getRowCount() + " Actions"));
        o_topLabel.setBorder(createEmptyBorder(0, 5, 5, 5));

        JPanel x_buttonPanel = new JPanel(new FlowLayout(LEFT));
		x_buttonPanel.add(x_addLabel);
		x_buttonPanel.add(x_addTemplateLabel);
		x_buttonPanel.add(new JSeparator(VERTICAL));
		x_buttonPanel.add(new JLabel("View:"));
		x_buttonPanel.add(o_showDueRadioButton);
		x_buttonPanel.add(o_showNext7DaysRadioButton);
		x_buttonPanel.add(o_showAllRadioButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(o_topLabel, NORTH);
        add(x_buttonPanel, SOUTH);

		TimedUpdater.getInstance().addActivityListener(this);
		GoogleSyncer.getInstance().addActivityListener(this);
    }

	private void radioButtonChanged(JRadioButton p_radioButton, View p_newView, Settings p_settings, ThreadActionTableModel p_tableModel) {
		if(p_radioButton.isSelected() && p_newView != p_tableModel.getView()) {
			p_tableModel.setView(p_newView);
			p_settings.updateSetting(ACTIONVIEW, p_newView);
			o_topLabel.setText(o_table.getModel().getRowCount() + " Actions");
		}
	}

	@Override
	void showContextMenu(Component p_origin, int p_row, int p_col, Point p_point, Item p_item) {
		if(p_item != null) {
			o_popupMenu.show(p_point, p_origin);
		}
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, Item p_item) {
		boolean x_enabled = p_item != null;
		o_popupMenu.setStatus(x_enabled, x_enabled, x_enabled, x_enabled, p_item);
	}

	@Override
	public void tableRowDoubleClicked(int p_row, int p_col, Item p_item) {
		switch(p_col) {
			case 0: WindowManager.getInstance().openComponent(p_item.getParentThread()); break;
			default: WindowManager.getInstance().openComponent(p_item);
        }
    }
}
