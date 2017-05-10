package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.*;

import static data.ComponentType.Action;
import static gui.Actions.addAction;
import static util.GuiUtil.setUpButtonLabel;
import static util.Settings.*;

class ThreadActionPanel extends ComponentTablePanel<Thread, Item> implements SettingChangeListener {
    private final Thread o_thread;
	private final ContextualPopupMenu o_popupMenu = new ContextualPopupMenu(true, true, Action);
	private final JRadioButton o_showNext7DaysRadioButton;
	private final JRadioButton o_showAllRadioButton;

	ThreadActionPanel(Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadActionTableModel(p_thread), new ThreadActionCellRenderer(p_thread));
        o_thread = p_thread;
		o_thread.addComponentChangeListener(e -> tableRowClicked(-1, -1, null));

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(2, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(4, GUIConstants.s_googleStatusColumnWidth);

		JLabel x_addLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addLabel.setToolTipText("Add Action");
		x_addLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				addAction(getSelectedObject(), o_thread, DateSuggestionPanel.getDateSuggestion(), p_parentPanel, true);
			}
		});

		o_popupMenu.setActivateActionListener(e -> Actions.activate(getSelectedObject(), p_parentPanel));
		o_popupMenu.setDeactivateActionListener(e -> Actions.deactivate(getSelectedObject(), p_parentPanel));
		o_popupMenu.setRemoveActionListener(e -> Actions.remove(getSelectedObject(), p_parentPanel));
		o_popupMenu.setMoveActionListener(e -> Actions.move(getSelectedObject(), o_thread, p_parentPanel));
		o_popupMenu.setLinkActionListener(e -> Actions.linkToGoogle(getSelectedObject(), p_parentPanel));

		ThreadActionTableModel x_tableModel = (ThreadActionTableModel) o_table.getModel();
		x_tableModel.setOnlyNext7Days(registerForSetting(s_SEVENDAYS, this, true));

		o_showNext7DaysRadioButton = new JRadioButton("7 Days", x_tableModel.onlyNext7Days());
		o_showAllRadioButton = new JRadioButton("All", !x_tableModel.onlyNext7Days());

		o_showNext7DaysRadioButton.addChangeListener(e -> {
			x_tableModel.setOnlyNext7Days(o_showNext7DaysRadioButton.isSelected());
			updateSetting(Settings.s_SEVENDAYS, o_showNext7DaysRadioButton.isSelected());
		});

		ButtonGroup x_group = new ButtonGroup();
		x_group.add(o_showNext7DaysRadioButton);
		x_group.add(o_showAllRadioButton);

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(x_addLabel));
		x_buttonPanel.add(o_showNext7DaysRadioButton);
		x_buttonPanel.add(o_showAllRadioButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);

		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
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
	public void settingChanged(String p_name, Object p_value) {
		boolean x_value = (boolean) p_value;
		((ThreadActionTableModel)o_table.getModel()).setOnlyNext7Days(x_value);
		o_showNext7DaysRadioButton.setSelected(x_value);
		o_showAllRadioButton.setSelected(!x_value);
	}
}
