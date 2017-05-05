package gui;

import data.Thread;
import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.List;

import static gui.Actions.addThread;
import static gui.Actions.linkToGoogle;
import static util.GuiUtil.setUpButtonLabel;

class ThreadThreadPanel extends ComponentTablePanel<Thread, Thread> implements ComponentChangeListener {
    private final Thread o_thread;
	private JPanel o_parentPanel;
	private final JMenuItem o_dismissLabel = new JMenuItem("Set Inactive", ImageUtil.getTickIcon());
	private final JMenuItem o_removeLabel = new JMenuItem("Remove", ImageUtil.getMinusIcon());
	private final JMenuItem o_moveLabel = new JMenuItem("Move", ImageUtil.getMoveIcon());
	private final JMenuItem o_linkLabel = new JMenuItem("Link", ImageUtil.getLinkIcon());

    ThreadThreadPanel(Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadThreadTableModel(p_thread), new ComponentCellRenderer(null));
		o_thread = p_thread;
		o_parentPanel = p_parentPanel;
		o_thread.addComponentChangeListener(this);

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
		fixColumnWidth(2, GUIConstants.s_statsColumnWidth);
		fixColumnWidth(3, GUIConstants.s_statsColumnWidth);
		fixColumnWidth(4, GUIConstants.s_statsColumnWidth);

		JLabel x_addLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addLabel.setToolTipText("Add Thread");
		x_addLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				addThread(getSelectedObject(), o_thread, p_parentPanel);
			}
		});

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Set Thread Active/Inactive");
		o_dismissLabel.addActionListener(e -> dismiss(getSelectedObject()));

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove Thread");
		o_removeLabel.addActionListener(e -> remove(getSelectedObject()));

		o_moveLabel.setEnabled(false);
		o_moveLabel.setToolTipText("Move Thread");
		o_moveLabel.addActionListener(e -> Actions.move(getSelectedObject(), o_thread, p_parentPanel));

		o_linkLabel.setEnabled(false);
		o_linkLabel.setToolTipText("Link Thread to Google Calendar");
		o_linkLabel.addActionListener(e -> linkToGoogle(getSelectedObject(), p_parentPanel, true));

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(x_addLabel));
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	private void dismiss(Thread p_thread) {
		if(p_thread != null) {
			if(JOptionPane.showConfirmDialog(o_parentPanel, "Set '" + p_thread.getText() + "' Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				p_thread.setActive(false);
			}
		}
	}

	private void remove(Thread x_thread) {
		if(x_thread != null) {
			Thread x_parent = x_thread.getParentThread();

			if(JOptionPane.showConfirmDialog(o_parentPanel, "Remove '" + x_thread.getText() + "' from '" + x_thread.getParentThread().getText() + "' ?", "Remove " + x_thread.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				x_parent.removeThreadItem(x_thread);
			}
		}
	}

	@Override
	void showContextMenu(int p_row, int p_col, Point p_point, Component p_origin, Thread p_selectedObject) {
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
	public void tableRowClicked(int p_row, int p_col, Thread p_thread) {
		o_dismissLabel.setEnabled(p_row != -1);
		o_removeLabel.setEnabled(p_row != -1);
		o_moveLabel.setEnabled(p_row != -1);
		o_linkLabel.setEnabled(p_row != -1);
	}

    public void tableRowDoubleClicked(int p_row, int p_col, Thread p_thread) {
		switch(p_col) {
			case 0: WindowManager.getInstance().openComponent(p_thread.getParentComponent()); break;
			default:
				WindowManager.getInstance().openComponent(p_thread);

				if(p_col > 1) {
					ThreadPanel.setTabIndex(p_col - 1);
				}
		}
    }

	@Override
	public void componentChanged(ComponentChangeEvent p_cce) {
		tableRowClicked(-1, -1, null);
	}
}