package gui;

import data.*;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class ItemPanel extends ComponentTablePanel implements Observer {
    private final Item o_item;
	private final JLabel o_removeLabel = new JLabel(ImageUtil.getMinusIcon());
	private final JLabel o_dismissLabel = new JLabel(ImageUtil.getTickIcon());

	ItemPanel(Item p_item) {
        super(new ItemReminderTableModel(p_item),  new ComponentCellRenderer(p_item));
        o_item = p_item;
		o_item.addObserver(this);

        fixColumnWidth(1, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(2, GUIConstants.s_dateStatusColumnWidth);

		final JLabel x_addLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addLabel.setEnabled(o_item.getDueDate() != null);
		o_removeLabel.setEnabled(false);
		o_dismissLabel.setEnabled(false);

		ComponentInfoChangeListener x_listener = new ComponentInfoChangeListener() {
			@Override
			public void componentInfoChanged(boolean saved) {
				if(saved) {
					x_addLabel.setEnabled(o_item.getDueDate() != null);
				}
			}
		};

		x_addLabel.setToolTipText("Add Reminder");
		x_addLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				addSomething();
			}
		});

		o_removeLabel.setToolTipText("Remove");
        o_removeLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				removeSomething(getSelectedRow());
			}
		});

		o_dismissLabel.setToolTipText("Make Active/Inactive");
        o_dismissLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dismissSomething(getSelectedRow());
			}
		});

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, this, x_listener), BorderLayout.NORTH);
        x_panel.add(new DateSuggestionPanel(o_item, this, x_listener), BorderLayout.SOUTH);

        JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        x_buttonPanel.add(x_addLabel);
        x_buttonPanel.add(o_removeLabel);
        x_buttonPanel.add(o_dismissLabel);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

	private void addSomething() {
		if (o_item.getDueDate() != null) {
			String x_text = (String) JOptionPane.showInputDialog(this, "Enter new Reminder text:", "Add new Reminder to '" + o_item + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, "New Reminder");

			if(x_text != null) {
				Reminder x_reminder = new Reminder(o_item);
				x_reminder.setText(x_text);
				o_item.addReminder(x_reminder);
				WindowManager.getInstance().openComponent(x_reminder);
			}
		}
	}

	private void removeSomething(int p_index) {
		if (p_index != -1 && o_item.getDueDate() != null) {
			Reminder x_reminder = o_item.getReminder(p_index);

			if (JOptionPane.showConfirmDialog(this, "Remove '" + x_reminder.getText() + "' from '" + o_item.getText() + "' ?", "Remove Reminder ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
				o_item.removeReminder(x_reminder);
				o_removeLabel.setEnabled(false);
			}
		}
	}

	private void dismissSomething(int p_index) {
		if(p_index != -1) {
			Reminder x_reminder = o_item.getReminder(p_index);
			x_reminder.setActive(!x_reminder.isActive());
		}
	}

	@Override
	void tableRowClicked(int row, int col) {
		o_removeLabel.setEnabled(row != -1);
		o_dismissLabel.setEnabled(row != -1 && o_item.getReminder(row).isActive());
	}

	void tableRowDoubleClicked(int row, int col) {
		if(row != -1) {
			WindowManager.getInstance().openComponent(o_item.getReminder(row));
		}
	}

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}