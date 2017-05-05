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

public class ThreadContentsPanel extends ComponentTablePanel<Thread, ThreadItem> implements ComponentChangeListener
{
    private final Thread o_thread;
	private JPanel o_parentPanel;
	private final JMenuItem o_removeLabel = new JMenuItem("Remove", ImageUtil.getMinusIcon());
	private final JMenuItem o_dismissLabel = new JMenuItem("Set Inactive", ImageUtil.getTickIcon());
	private final JMenuItem o_moveLabel = new JMenuItem("Move", ImageUtil.getMoveIcon());
	private final JMenuItem o_linkLabel = new JMenuItem("Link", ImageUtil.getLinkIcon());

	public ThreadContentsPanel(final Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadContentsTableModel(p_thread), new ComponentCellRenderer(p_thread));
        o_thread = p_thread;
		o_parentPanel = p_parentPanel;
		o_thread.addComponentChangeListener(this);

        fixColumnWidth(0, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(1, GUIConstants.s_typeColumnWidth);
        fixColumnWidth(3, GUIConstants.s_threadItemInfoColumnWidth);
        fixColumnWidth(4, GUIConstants.s_googleStatusColumnWidth);

		JLabel x_addItemLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addItemLabel.setToolTipText("Add Item");
		x_addItemLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				add(getSelectedObject());
			}
		});

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove Item");
		o_removeLabel.addActionListener(e -> remove(getSelectedObject()));

		o_dismissLabel.setToolTipText("Set Item Active/Inactive");
        o_dismissLabel.addActionListener(e -> dismiss(getSelectedObject()));

		o_moveLabel.setToolTipText("Move Item");
		o_moveLabel.setEnabled(false);
		o_moveLabel.addActionListener(e -> Actions.move(getSelectedObject(), o_thread, o_parentPanel));

		o_linkLabel.setToolTipText("Link Item to Google Calendar");
		o_linkLabel.setEnabled(false);
		o_linkLabel.addActionListener(e -> link(getSelectedObject()));

        JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        x_buttonPanel.add(setUpButtonLabel(x_addItemLabel));
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(x_buttonPanel, BorderLayout.SOUTH);

		GoogleSyncer.getInstance().addGoogleSyncListener(this);
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

	private void dismiss(ThreadItem p_threadItem) {
		if(p_threadItem != null) {
			boolean x_active = !p_threadItem.isActive();

			if(JOptionPane.showConfirmDialog(o_parentPanel, "Set '" + p_threadItem.getText() + "' " + (x_active ? "Active" : "Inactive") + " ?", "Set " + (x_active ? "Active" : "Inactive") + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				p_threadItem.setActive(x_active);
			}
		}
	}

	private void remove(ThreadItem p_threadItem) {
        if(p_threadItem != null) {
			if(JOptionPane.showConfirmDialog(o_parentPanel, "Remove '" + p_threadItem.getText() + "' from '" + p_threadItem.getParentThread().getText() + "' ?", "Remove " + p_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
                o_thread.removeThreadItem(p_threadItem);
            }
        }
    }

	private void link(ThreadItem p_threadItem) {
		if (o_linkLabel.isEnabled()) {
			if(p_threadItem instanceof Item) {
				linkToGoogle((Item) p_threadItem, o_parentPanel);
			}

			if(p_threadItem instanceof Thread) {
				final Thread x_thread = (Thread) p_threadItem;
				linkToGoogle(x_thread, o_parentPanel, false);
			}
		}
	}

	@Override
	void showContextMenu(int p_row, int p_col, Point p_point, Component p_origin, ThreadItem p_selectedObject) {
		if(p_selectedObject != null) {
			o_dismissLabel.setText(p_selectedObject.isActive() ? "Set Inactive" : "Set Active");
			JPopupMenu x_menu = new JPopupMenu();
			x_menu.add(o_removeLabel);
			x_menu.add(o_dismissLabel);
			x_menu.add(o_moveLabel);
			x_menu.add(o_linkLabel);
			x_menu.show(p_origin, p_point.x, p_point.y);
		}
	}

	@Override
	public void tableRowClicked(int p_row, int p_col, ThreadItem p_threadItem) {
		o_removeLabel.setEnabled(p_threadItem != null);
		o_moveLabel.setEnabled(p_threadItem != null);
		o_linkLabel.setEnabled(p_threadItem != null && (p_threadItem instanceof Thread || (p_threadItem instanceof Item && ((Item)p_threadItem).getDueDate() != null)));
	}

	public void tableRowDoubleClicked(int p_row, int p_col, ThreadItem p_threadItem) {
		if(p_threadItem != null) {
			WindowManager.getInstance().openComponent(p_threadItem);
		}
    }

	@Override
	public void componentChanged(ComponentChangeEvent p_cce) {
		tableRowClicked(-1, -1, null);
	}
}