package threads.gui;

import threads.data.Configuration;
import threads.data.Thread;
import threads.util.Settings;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.createEmptyBorder;
import static threads.data.ComponentType.Thread;
import static threads.gui.Actions.addThread;
import static threads.gui.Actions.linkToGoogle;
import static threads.gui.GUIConstants.s_statsColumnWidth;
import static threads.gui.GUIConstants.s_threadColumnWidth;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.ImageUtil.getPlusIcon;
import static threads.util.Settings.Setting.SEVENDAYS;
import static threads.util.Settings.Setting.TABINDEX;

class ThreadThreadPanel extends ComponentTablePanel<Thread, Thread> {
    private final Thread o_thread;
	private final Settings o_settings;
	private final ContextualPopupMenu o_popupMenu = new ContextualPopupMenu(true, true, Thread);
	private final JLabel o_topLabel = new JLabel("0 Threads");

    ThreadThreadPanel(Configuration p_configuration, Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadThreadTableModel(p_thread), new BaseCellRenderer());
		o_thread = p_thread;
		o_settings = p_configuration.getSettings();
		o_thread.addComponentChangeListener(e -> tableRowClicked(-1, -1, null));

        fixColumnWidth(0, s_threadColumnWidth);
		fixColumnWidth(2, s_statsColumnWidth);
		fixColumnWidth(3, s_statsColumnWidth);
		fixColumnWidth(4, s_statsColumnWidth);

		JLabel x_addLabel = createLabel(getPlusIcon(), "Add Thread", true, e-> addThread(getSelectedObject(), o_thread, p_parentPanel));

		o_topLabel.setHorizontalAlignment(JLabel.CENTER);
		o_topLabel.setText(o_table.getModel().getRowCount() + " Threads");
		o_thread.addComponentChangeListener(l -> o_topLabel.setText(o_table.getModel().getRowCount() + " Threads"));
		o_topLabel.setBorder(createEmptyBorder(0, 5, 5, 5));

		o_popupMenu.setActivateActionListener(e -> Actions.activateComponent(getSelectedObject(), p_parentPanel));
		o_popupMenu.setDeactivateActionListener(e -> Actions.deactivateComponent(getSelectedObject(), p_parentPanel));
		o_popupMenu.setRemoveActionListener(e -> Actions.removeComponent(getSelectedObject(), p_parentPanel, false));
		o_popupMenu.setMoveActionListener(e -> Actions.moveThreadItem(getSelectedObject(), p_parentPanel));
		o_popupMenu.setLinkActionListener(e -> linkToGoogle(getSelectedObject(), p_configuration, p_parentPanel));

		JPanel x_buttonPanel = new JPanel(new FlowLayout(LEFT));
		x_buttonPanel.add(x_addLabel);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(o_topLabel, NORTH);
		add(x_buttonPanel, SOUTH);
	}

	@Override
	void showContextMenu(Component p_origin, int p_row, int p_col, Point p_point, Thread p_selectedObject) {
		if(p_selectedObject != null) {
			o_popupMenu.show(p_point, p_origin);
		}
	}
	@Override
	public void tableRowClicked(int p_row, int p_col, Thread p_thread) {
		boolean x_enabled = p_row != -1;
		o_popupMenu.setStatus(x_enabled, x_enabled, x_enabled, x_enabled, p_thread);
	}

    public void tableRowDoubleClicked(int p_row, int p_col, Thread p_thread) {
		switch(p_col) {
			case 0: WindowManager.getInstance().openComponent(p_thread.getParentComponent()); break;
			default:
				WindowManager.getInstance().openComponent(p_thread);

				if(p_col > 1) {
					if(p_col == 4) {
						o_settings.updateSetting(SEVENDAYS, false);
					}

					o_settings.updateSetting(TABINDEX, "" + (p_col - 1));
				}
		}
    }
}