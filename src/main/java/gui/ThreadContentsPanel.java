package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.List;

import static gui.Actions.addThread;
import static gui.Actions.linkToGoogle;
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
		o_removeLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove(getSelectedObject());
			}
		});

		o_dismissLabel.setToolTipText("Set Item Active/Inactive");
        o_dismissLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dismiss(getSelectedObject());
			}
		});

		o_moveLabel.setToolTipText("Move Item");
		o_moveLabel.setEnabled(false);
		o_moveLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				move(getSelectedObject());
			}
		});

		o_linkLabel.setToolTipText("Link Item to Google Calendar");
		o_linkLabel.setEnabled(false);
		o_linkLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				link(getSelectedObject());
			}
		});

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
			case 0:
			case 2: addItem(p_threadItem, x_options[x_selection]); break;
			case 1: addThread(p_threadItem, o_thread, o_parentPanel);
		}
	}

	protected void addItem(ThreadItem p_threadItem, String p_type) {
		Thread x_thread;

		if(p_threadItem != null && p_threadItem instanceof Thread) {
			x_thread = (Thread) p_threadItem;
		} else {
			o_table.clearSelection();
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
			x_threads.add(0, o_thread);

			if(x_threads.size() > 1) {
				x_thread = (Thread) JOptionPane.showInputDialog(o_parentPanel, "Choose a Thread to add it to:", "Add an " + p_type + " ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				x_thread = o_thread;
			}
		}

		if(x_thread != null) {
			String x_text = (String) JOptionPane.showInputDialog(o_parentPanel, "Enter new " + p_type + " text:", "Add new " + p_type + " to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New " + p_type);

			if(x_text != null) {
				Item x_item = new Item(x_text);

				if(p_type.equals("Action")) {
					x_item.setDueDate(DateSuggestionPanel.getDateSuggestion());
				}

				x_thread.addThreadItem(x_item);
				WindowManager.getInstance().openComponent(x_item);
			}
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
			if(JOptionPane.showConfirmDialog(o_parentPanel, "Remove '" + p_threadItem.getText() + "' from '" + p_threadItem.getParentThread().getText() + "' ?", "Delete " + p_threadItem.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
                o_thread.removeThreadItem(p_threadItem);
            }
        }
    }

	private void move(ThreadItem p_threadItem) {
		if(p_threadItem != null) {
			Thread x_thread = null;

			Thread x_topThread = (Thread) o_thread.getHierarchy().get(0);
			List<Thread> x_threads = LookupHelper.getAllActiveThreads(x_topThread);
			x_threads.add(0, x_topThread);
			x_threads.remove(p_threadItem.getParentThread());

			if(p_threadItem instanceof Thread) {
				x_threads.remove(p_threadItem);
				x_threads.removeAll(LookupHelper.getAllActiveThreads((Thread)p_threadItem));
			}

			if(x_threads.size() > 0) {
				x_thread = (Thread) JOptionPane.showInputDialog(o_parentPanel, "Choose a Thread to move it to:", "Move '" + p_threadItem + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
			} else {
				JOptionPane.showMessageDialog(o_parentPanel, "This is no other Thread to move this Item to. Try creating another Thread.", "Nowhere to go", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon());
			}

			if(x_thread != null) {
				p_threadItem.getParentThread().removeThreadItem(p_threadItem);
				x_thread.addThreadItem(p_threadItem);
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
//		o_dismissLabel.setEnabled(p_threadItem != null && p_threadItem.isActive());
		o_removeLabel.setEnabled(p_threadItem != null);
		o_moveLabel.setEnabled(p_threadItem != null);
		boolean x_linkable = false;

		if(p_threadItem != null && p_threadItem instanceof Item) {
			x_linkable = ((Item)p_threadItem).getDueDate() != null;
		}

		if(p_threadItem != null && p_threadItem instanceof Thread) {
			x_linkable = true;
		}

		o_linkLabel.setEnabled(x_linkable);
	}

	public void tableRowDoubleClicked(int p_row, int p_col, ThreadItem p_threadItem) {
		if(p_threadItem != null) {
			WindowManager.getInstance().openComponent(p_threadItem);
		}
    }

	@Override
	public void componentChanged(ComponentChangeEvent p_event) {
		tableRowClicked(-1, -1, null);
	}
}