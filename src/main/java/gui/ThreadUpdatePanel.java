package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.List;

import static gui.Actions.addUpdate;
import static util.GuiUtil.setUpButtonLabel;

public class ThreadUpdatePanel extends ComponentTablePanel<Thread, Item> implements ComponentChangeListener {
    private final Thread o_thread;
	private JPanel o_parentPanel;
	private final JMenuItem o_dismissLabel = new JMenuItem("Set Inactive", ImageUtil.getTickIcon());
	private final JMenuItem o_removeLabel = new JMenuItem("Remove", ImageUtil.getMinusIcon());
	private final JMenuItem o_moveLabel = new JMenuItem("Move", ImageUtil.getMoveIcon());

	public ThreadUpdatePanel(Thread p_thread, JPanel p_parentPanel) {
        super(new ThreadUpdateTableModel(p_thread), new ComponentCellRenderer(null));
        o_thread = p_thread;
		o_parentPanel = p_parentPanel;
		o_thread.addComponentChangeListener(this);

        fixColumnWidth(0, GUIConstants.s_threadColumnWidth);
        fixColumnWidth(2, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusColumnWidth);

		JLabel x_addLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addLabel.setToolTipText("Add Update");
		x_addLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				addUpdate(getSelectedObject(), o_thread, p_parentPanel);
			}
		});

		o_dismissLabel.setEnabled(false);
		o_dismissLabel.setToolTipText("Set Update Active/Inactive");
		o_dismissLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dismiss(getSelectedObject());
			}
		});

		o_removeLabel.setEnabled(false);
		o_removeLabel.setToolTipText("Remove Update");
		o_removeLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove(getSelectedObject());
			}
		});

		o_moveLabel.setEnabled(false);
		o_moveLabel.setToolTipText("Move Update");
		o_moveLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				move(getSelectedObject());
			}
		});

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(x_addLabel));
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	private void dismiss(Item p_update) {
		if(p_update != null) {
			if(JOptionPane.showConfirmDialog(o_parentPanel, "Set '" + p_update.getText() + "' Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				p_update.setActive(false);
			}
		}
	}

	private void remove(Item p_update) {
		if(p_update != null) {
			if(JOptionPane.showConfirmDialog(o_parentPanel, "Remove '" + p_update.getText() + "' from '" + p_update.getParentThread().getText() + "' ?", "Delete " + p_update.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				p_update.getParentThread().removeThreadItem(p_update);
			}
		}
	}

	private void move(Item p_update) {
		if(p_update != null) {
			Thread x_thread = null;

			Thread x_topThread = (Thread) o_thread.getHierarchy().get(0);
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(x_topThread);
			x_threads.add(0, x_topThread);
			x_threads.remove(p_update.getParentThread());

			if(x_threads.size() > 0) {
				x_thread = (Thread) JOptionPane.showInputDialog(o_parentPanel, "Choose a Thread to move it to:", "Move '" + p_update + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				JOptionPane.showMessageDialog(o_parentPanel, "This is no other Thread to move this Update to. Try creating another Thread.", "Nowhere to go", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon());
			}

			if(x_thread != null) {
				p_update.getParentThread().removeThreadItem(p_update);
				x_thread.addThreadItem(p_update);
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
			x_menu.show(p_origin, p_point.x, p_point.y);
		}
	}

	@Override
	public void tableRowClicked(int row, int col, Item p_item) {
		o_removeLabel.setEnabled(p_item != null);
		o_dismissLabel.setEnabled(p_item != null);
		o_moveLabel.setEnabled(p_item != null);
	}

	@Override
    public void tableRowDoubleClicked(int row, int col, Item p_item) {
        switch(col) {
			case 0: WindowManager.getInstance().openComponent(p_item.getParentThread()); break;
			default: WindowManager.getInstance().openComponent(p_item);
        }
    }

	@Override
	public void componentChanged(ComponentChangeEvent p_event) {
		tableRowClicked(-1, -1, null);
	}
}
