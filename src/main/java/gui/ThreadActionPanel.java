package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static gui.Actions.addAction;
import static util.GuiUtil.setUpButtonLabel;
import static util.Settings.*;

public class ThreadActionPanel extends ComponentTablePanel<Thread, Item> implements ComponentChangeListener, SettingChangeListener {
    private final Thread o_thread;
	private final JPanel o_parentPanel;
	private final JMenuItem o_dismissLabel = new JMenuItem("Set Inactive", ImageUtil.getTickIcon());
	private final JMenuItem o_removeLabel = new JMenuItem("Remove", ImageUtil.getMinusIcon());
	private final JMenuItem o_moveLabel = new JMenuItem("Move", ImageUtil.getMoveIcon());
	private final JMenuItem o_linkLabel = new JMenuItem("Link",ImageUtil.getLinkIcon());
	private final JRadioButton o_showNext7DaysRadioButton;
	private final JRadioButton o_showAllRadioButton;

	ThreadActionPanel(Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadActionTableModel(p_thread), new ThreadActionCellRenderer(p_thread));
        o_thread = p_thread;
		o_parentPanel = p_parentPanel;
		o_thread.addComponentChangeListener(this);

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

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Set Action Active/Inactive");
		o_dismissLabel.addActionListener(e -> dismiss(getSelectedObject()));

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove Action");
		o_removeLabel.addActionListener(e -> remove(getSelectedObject()));

		o_moveLabel.setEnabled(false);
		o_moveLabel.setToolTipText("Move Action");
		o_moveLabel.addActionListener(e -> Actions.move(getSelectedObject(), o_thread, o_parentPanel));

		o_linkLabel.setEnabled(false);
		o_linkLabel.setToolTipText("Link Action to Google Calendar");
		o_linkLabel.addActionListener(e -> {
			if (o_linkLabel.isEnabled()) {
				Actions.linkToGoogle(getSelectedObject(), p_parentPanel);
			}
		});

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

	private void dismiss(Item o_action) {
		if(o_action != null) {
			if(JOptionPane.showConfirmDialog(o_parentPanel, "Set '" + o_action.getText() + "' Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				o_action.setActive(false);
			}
		}
	}

	private void remove(Item o_action) {
		if(o_action != null) {
			Thread x_thread = o_action.getParentThread();

			if(JOptionPane.showConfirmDialog(o_parentPanel, "Remove '" + o_action.getText() + "' from '" + x_thread.getText() + "' ?", "Remove " + o_action.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_thread.removeThreadItem(o_action);
			}
		}
	}

	@Override
	void showContextMenu(int p_row, int p_col, Point p_point, Component p_origin, Item p_selectedObject) {
		if(p_selectedObject != null) {
			JPopupMenu x_menu = new JPopupMenu();
			x_menu.add(o_removeLabel);
			x_menu.add(o_dismissLabel);
			x_menu.add(o_moveLabel);
			x_menu.add(o_linkLabel);
			x_menu.show(p_origin, p_point.x, p_point.y);
		}
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, Item p_item) {
		o_removeLabel.setEnabled(p_item != null);
		o_dismissLabel.setEnabled(p_item != null);
		o_moveLabel.setEnabled(p_item != null);
		o_linkLabel.setEnabled(p_item != null);
	}

	@Override
	public void tableRowDoubleClicked(int p_row, int p_col, Item p_item) {
		switch(p_col) {
			case 0: WindowManager.getInstance().openComponent(p_item.getParentThread()); break;
			default: WindowManager.getInstance().openComponent(p_item);
        }
    }

	@Override
	public void componentChanged(ComponentChangeEvent p_cce) {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void timeUpdate() {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void googleSynced() {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void settingChanged(String p_name, Object p_value) {
		boolean x_value = (boolean) p_value;
		((ThreadActionTableModel)o_table.getModel()).setOnlyNext7Days(x_value);
		o_showNext7DaysRadioButton.setSelected(x_value);
		o_showAllRadioButton.setSelected(!x_value);
	}
}
