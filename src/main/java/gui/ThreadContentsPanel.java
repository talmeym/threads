package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.*;

import static gui.Actions.*;
import static util.GuiUtil.setUpButtonLabel;

class ThreadContentsPanel extends ComponentTablePanel<Thread, ThreadItem>
{
    private final Thread o_thread;
	private JPanel o_parentPanel;
	private final ContextualPopupMenu o_popupMenu = new ContextualPopupMenu(true, true, null);

	ThreadContentsPanel(final Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadContentsTableModel(p_thread), new ContentsCellRenderer(p_thread));
        o_thread = p_thread;
		o_parentPanel = p_parentPanel;
		o_thread.addComponentChangeListener(e -> tableRowClicked(-1, -1, null));

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_typeColumnWidth);
        fixColumnWidth(3, GUIConstants.s_threadItemInfoColumnWidth);
        fixColumnWidth(4, GUIConstants.s_googleStatusColumnWidth);

		JLabel x_addItemLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addItemLabel.setToolTipText("Add Item");
		x_addItemLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				add(getSelectedObject());
			}
		});

        o_popupMenu.setActivateActionListener(e -> Actions.activate(getSelectedObject(), p_parentPanel));
        o_popupMenu.setDeactivateActionListener(e -> Actions.deactivate(getSelectedObject(), p_parentPanel));
		o_popupMenu.setRemoveActionListener(e -> Actions.remove(getSelectedObject(), p_parentPanel));
		o_popupMenu.setMoveActionListener(e -> Actions.move(getSelectedObject(), o_thread, p_parentPanel));
		o_popupMenu.setLinkActionListener(e -> link(getSelectedObject()));

        JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        x_buttonPanel.add(setUpButtonLabel(x_addItemLabel));
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(x_buttonPanel, BorderLayout.SOUTH);

		TimedUpdater.getInstance().addActivityListener(this);
		GoogleSyncer.getInstance().addActivityListener(this);
    }

	public void add(ThreadItem p_threadItem) {
		String[] x_options = {"Update", "Thread", "Action", "Cancel"};
		int x_selection = JOptionPane.showOptionDialog(o_parentPanel, "What would you like to add ?", "Add Something ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_options, x_options[2]);

		switch(x_selection) {
			case 0: addUpdate(p_threadItem, o_thread, o_parentPanel); break;
			case 1: addThread(p_threadItem, o_thread, o_parentPanel); break;
			case 2: addAction(p_threadItem, o_thread, DateSuggestionPanel.getDateSuggestion(), o_parentPanel, true); break;
		}
	}

	private void link(ThreadItem p_threadItem) {
		if(p_threadItem instanceof Item) {
			linkToGoogle((Item) p_threadItem, o_parentPanel);
		}

		if(p_threadItem instanceof Thread) {
			final Thread x_thread = (Thread) p_threadItem;
			linkToGoogle(x_thread, o_parentPanel);
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