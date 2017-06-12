package threads.gui;

import threads.data.Configuration;
import threads.data.Item;
import threads.data.Thread;
import threads.data.ThreadItem;
import threads.util.GoogleSyncer;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.JOptionPane.*;
import static threads.gui.Actions.*;
import static threads.gui.GUIConstants.*;
import static threads.gui.ItemDateSuggestionPanel.getDateSuggestion;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.ImageUtil.getPlusIcon;
import static threads.util.ImageUtil.getThreadsIcon;

class ThreadContentsPanel extends ComponentTablePanel<Thread, ThreadItem>
{
    private final Thread o_thread;
	private JPanel o_parentPanel;
	private final ContextualPopupMenu o_popupMenu = new ContextualPopupMenu(true, true, null);
	private final JLabel o_topLabel = new JLabel("0 Items");

	ThreadContentsPanel(Configuration p_configuration, final Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadContentsTableModel(p_thread), new ThreadContentsCellRenderer());
        o_thread = p_thread;
		o_parentPanel = p_parentPanel;
		o_thread.addComponentChangeListener(e -> tableRowClicked(-1, -1, null));

        fixColumnWidth(0, s_creationDateColumnWidth);
        fixColumnWidth(1, s_typeColumnWidth);
        fixColumnWidth(3, s_threadItemInfoColumnWidth);
        fixColumnWidth(4, s_googleStatusColumnWidth);

		JLabel x_addItemLabel = createLabel(getPlusIcon(), "Add Item", true, e -> add(getSelectedObject()));

		o_topLabel.setHorizontalAlignment(JLabel.CENTER);
		o_topLabel.setText(o_table.getModel().getRowCount() + " Items");
        o_thread.addComponentChangeListener(l -> o_topLabel.setText(o_table.getModel().getRowCount() + " Items"));
        o_topLabel.setBorder(createEmptyBorder(0, 5, 5, 5));

        o_popupMenu.setActivateActionListener(e -> Actions.activateComponent(getSelectedObject(), p_parentPanel));
        o_popupMenu.setDeactivateActionListener(e -> Actions.deactivateComponent(getSelectedObject(), p_parentPanel));
		o_popupMenu.setRemoveActionListener(e -> Actions.removeComponent(getSelectedObject(), p_parentPanel, false));
		o_popupMenu.setMoveActionListener(e -> Actions.moveThreadItem(getSelectedObject(), p_parentPanel));
		o_popupMenu.setLinkActionListener(e -> link(getSelectedObject(), p_configuration));

        JPanel x_buttonPanel = new JPanel(new FlowLayout(LEFT));
        x_buttonPanel.add(x_addItemLabel);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(o_topLabel, NORTH);
        add(x_buttonPanel, SOUTH);

		GoogleSyncer.getInstance().addActivityListener(this);
    }

	public void add(ThreadItem p_threadItem) {
		String[] x_options = {"Update", "Thread", "Action", "Cancel"};
		int x_selection = showOptionDialog(o_parentPanel, "What would you like to add ?", "Add Something ?", OK_CANCEL_OPTION, INFORMATION_MESSAGE, getThreadsIcon(), x_options, x_options[2]);

		switch(x_selection) {
			case 0: addUpdate(p_threadItem, o_thread, o_parentPanel); break;
			case 1: addThread(p_threadItem, o_thread, o_parentPanel); break;
			case 2: addAction(p_threadItem, o_thread, getDateSuggestion(), o_parentPanel, true); break;
		}
	}

	private void link(ThreadItem p_threadItem, Configuration p_configuration) {
		if(p_threadItem instanceof Item) {
			linkToGoogle((Item) p_threadItem, p_configuration, o_parentPanel);
		}

		if(p_threadItem instanceof Thread) {
			final Thread x_thread = (Thread) p_threadItem;
			linkToGoogle(x_thread, p_configuration, o_parentPanel);
		}
	}

	@Override
	void showContextMenu(Component p_origin, int p_row, int p_col, Point p_point, ThreadItem p_threadItem) {
		if(p_threadItem != null) {
			o_popupMenu.show(p_point, p_origin);
		}
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, ThreadItem p_threadItem) {
		boolean x_enabled = p_threadItem != null;
		boolean x_linkEnabled = x_enabled && !(p_threadItem instanceof Item && ((Item)p_threadItem).getDueDate() == null);
		o_popupMenu.setStatus(x_enabled, x_enabled, x_enabled, x_linkEnabled, p_threadItem);
	}

	public void tableRowDoubleClicked(int p_row, int p_col, ThreadItem p_threadItem) {
		if(p_threadItem != null) {
			WindowManager.getInstance().openComponent(p_threadItem);
		}
    }
}