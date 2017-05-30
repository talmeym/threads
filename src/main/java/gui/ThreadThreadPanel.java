package gui;

import data.Thread;
import util.TimedUpdater;

import javax.swing.*;
import java.awt.*;

import static data.ComponentType.Thread;
import static gui.Actions.*;
import static gui.GUIConstants.*;
import static gui.WidgetFactory.createLabel;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEFT;
import static util.ImageUtil.getPlusIcon;
import static util.Settings.*;

class ThreadThreadPanel extends ComponentTablePanel<Thread, Thread> {
    private final Thread o_thread;
	private final ContextualPopupMenu o_popupMenu = new ContextualPopupMenu(true, true, Thread);

    ThreadThreadPanel(Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadThreadTableModel(p_thread), new BaseCellRenderer());
		o_thread = p_thread;
		o_thread.addComponentChangeListener(e -> tableRowClicked(-1, -1, null));

        fixColumnWidth(0, s_threadColumnWidth);
		fixColumnWidth(2, s_statsColumnWidth);
		fixColumnWidth(3, s_statsColumnWidth);
		fixColumnWidth(4, s_statsColumnWidth);

		JLabel x_addLabel = createLabel(getPlusIcon(), "Add Thread", true, e-> addThread(getSelectedObject(), o_thread, p_parentPanel));

		o_popupMenu.setActivateActionListener(e -> Actions.activate(getSelectedObject(), p_parentPanel));
		o_popupMenu.setDeactivateActionListener(e -> Actions.deactivate(getSelectedObject(), p_parentPanel));
		o_popupMenu.setRemoveActionListener(e -> Actions.remove(getSelectedObject(), p_parentPanel, false));
		o_popupMenu.setMoveActionListener(e -> Actions.move(getSelectedObject(), o_thread, p_parentPanel));
		o_popupMenu.setLinkActionListener(e -> linkToGoogle(getSelectedObject(), p_parentPanel));

		JPanel x_buttonPanel = new JPanel(new FlowLayout(LEFT));
		x_buttonPanel.add(x_addLabel);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, SOUTH);

		TimedUpdater.getInstance().addActivityListener(this);
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
						updateSetting(s_SEVENDAYS, false);
					}

					ThreadPanel.setTabIndex(p_col - 1);
				}
		}
    }
}