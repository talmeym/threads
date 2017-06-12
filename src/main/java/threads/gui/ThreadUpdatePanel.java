package threads.gui;

import threads.data.Configuration;
import threads.data.Item;
import threads.data.Thread;
import threads.util.TimedUpdater;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.createEmptyBorder;
import static threads.data.ComponentType.Update;
import static threads.gui.Actions.addUpdate;
import static threads.gui.GUIConstants.s_dateStatusColumnWidth;
import static threads.gui.GUIConstants.s_threadColumnWidth;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.ImageUtil.getPlusIcon;

class ThreadUpdatePanel extends ComponentTablePanel<Thread, Item> {
    private final Thread o_thread;
	private JPanel o_parentPanel;
	private final ContextualPopupMenu o_popupMenu = new ContextualPopupMenu(true, false, Update);
	private final JLabel o_topLabel = new JLabel("0 Updates");

	ThreadUpdatePanel(Configuration p_configuration, Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadUpdateTableModel(p_thread), new BaseCellRenderer<>());
        o_thread = p_thread;
		o_parentPanel = p_parentPanel;
		o_thread.addComponentChangeListener(e -> tableRowClicked(-1, -1, null));

        fixColumnWidth(0, s_threadColumnWidth);
        fixColumnWidth(2, s_dateStatusColumnWidth);
        fixColumnWidth(3, s_dateStatusColumnWidth);

		JLabel x_addLabel = createLabel(getPlusIcon(), "Add Update", true, e -> addUpdate(getSelectedObject(), o_thread, p_parentPanel));

		o_topLabel.setHorizontalAlignment(JLabel.CENTER);
		o_topLabel.setText(o_table.getModel().getRowCount() + " Updates");
		o_thread.addComponentChangeListener(l -> o_topLabel.setText(o_table.getModel().getRowCount() + " Updates"));
		o_topLabel.setBorder(createEmptyBorder(0, 5, 5, 5));

		o_popupMenu.setActivateActionListener(e -> Actions.activateComponent(getSelectedObject(), p_parentPanel));
		o_popupMenu.setDeactivateActionListener(e -> Actions.deactivateComponent(getSelectedObject(), p_parentPanel));
		o_popupMenu.setRemoveActionListener(e -> Actions.removeComponent(getSelectedObject(), p_parentPanel, false));
		o_popupMenu.setMoveActionListener(e -> Actions.moveThreadItem(getSelectedObject(), o_parentPanel));

		JPanel x_buttonPanel = new JPanel(new FlowLayout(LEFT));
		x_buttonPanel.add(x_addLabel);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(o_topLabel, NORTH);
		add(x_buttonPanel, SOUTH);

		TimedUpdater.getInstance().addActivityListener(this);
	}

	@Override
	void showContextMenu(Component p_origin, int p_row, int p_col, Point p_point, Item p_item) {
		if(p_item != null) {
			o_popupMenu.show(p_point, p_origin);
		}
	}

	@Override
	public void tableRowClicked(int row, int col, Item p_item) {
		boolean x_enabled = p_item != null;
		o_popupMenu.setStatus(x_enabled, x_enabled, x_enabled, false, p_item);
	}

	@Override
    public void tableRowDoubleClicked(int row, int col, Item p_item) {
        switch(col) {
			case 0: WindowManager.getInstance().openComponent(p_item.getParentThread()); break;
			default: WindowManager.getInstance().openComponent(p_item);
        }
    }
}
