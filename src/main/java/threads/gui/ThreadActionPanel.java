package threads.gui;

import threads.data.*;
import threads.data.Thread;
import threads.util.*;
import threads.util.Settings.Setting;

import javax.swing.*;
import java.awt.Component;
import java.awt.*;

import static java.awt.BorderLayout.*;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.SwingConstants.VERTICAL;
import static threads.data.ComponentType.Action;
import static threads.gui.Actions.*;
import static threads.gui.GUIConstants.*;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.ImageUtil.*;
import static threads.util.Settings.Setting.*;

class ThreadActionPanel extends ComponentTablePanel<Thread, Item> implements SettingChangeListener {
    private final Thread o_thread;
	private final ContextualPopupMenu o_popupMenu = new ContextualPopupMenu(true, true, Action);
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

		boolean x_sevenDays = x_settings.registerForBooleanSetting(SEVENDAYS, this);
		ThreadActionTableModel x_tableModel = (ThreadActionTableModel) o_table.getModel();
		x_tableModel.setOnlyNext7Days(x_sevenDays);

		o_showNext7DaysRadioButton = new JRadioButton("7 Days", x_tableModel.onlyNext7Days());
		o_showAllRadioButton = new JRadioButton("All", !x_tableModel.onlyNext7Days());

		o_showNext7DaysRadioButton.addChangeListener(e -> {
			x_tableModel.setOnlyNext7Days(o_showNext7DaysRadioButton.isSelected());
			x_settings.updateSetting(SEVENDAYS, o_showNext7DaysRadioButton.isSelected());
            o_topLabel.setText(o_table.getModel().getRowCount() + " Actions");
		});

		ButtonGroup x_group = new ButtonGroup();
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
		x_buttonPanel.add(o_showNext7DaysRadioButton);
		x_buttonPanel.add(o_showAllRadioButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(o_topLabel, NORTH);
        add(x_buttonPanel, SOUTH);

		TimedUpdater.getInstance().addActivityListener(this);
		GoogleSyncer.getInstance().addActivityListener(this);
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

	@Override
	public void settingChanged(Setting p_setting, Object p_value) {
		boolean x_sevenDays = (Boolean) p_value;
		((ThreadActionTableModel)o_table.getModel()).setOnlyNext7Days(x_sevenDays);
		o_showNext7DaysRadioButton.setSelected(x_sevenDays);
		o_showAllRadioButton.setSelected(!x_sevenDays);
	}
}