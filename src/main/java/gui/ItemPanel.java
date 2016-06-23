package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class ItemPanel extends ComponentTablePanel implements Observer {
    private final Item o_item;
	private final JLabel o_removeLabel = new JLabel(ImageUtil.getMinusIcon());
	private final JLabel o_dismissLabel = new JLabel(ImageUtil.getTickIcon());
	private final JLabel o_linkLabel = new JLabel(ImageUtil.getLinkIcon());
	private final JLabel o_linkReminderLabel = new JLabel(ImageUtil.getLinkIcon());

	ItemPanel(Item p_item) {
        super(new ItemReminderTableModel(p_item),  new ComponentCellRenderer(p_item));
        o_item = p_item;
		o_item.addObserver(this);

        fixColumnWidth(1, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(2, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(3, 30);

		final JLabel x_addLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addLabel.setEnabled(o_item.getDueDate() != null);
		o_removeLabel.setEnabled(false);
		o_dismissLabel.setEnabled(false);
		o_linkLabel.setEnabled(o_item.getDueDate() != null && o_item.isActive());
		o_linkReminderLabel.setEnabled(false);

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
			@Override
			public void mouseClicked(MouseEvent e) {
				addSomething();
			}
		});

		o_removeLabel.setToolTipText("Remove");
        o_removeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				removeSomething(getSelectedRow());
			}
		});

		o_dismissLabel.setToolTipText("Make Active/Inactive");
        o_dismissLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dismissSomething(getSelectedRow());
			}
		});

		o_linkLabel.setToolTipText("Link to Google Calendar");
		o_linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				linkToGoogle();
			}
		});

		o_linkReminderLabel.setToolTipText("Link to Google Calendar");
		o_linkReminderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				linkToGoogle(getSelectedRow());
			}
		});

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, this, x_listener, o_linkLabel), BorderLayout.NORTH);
        x_panel.add(new DateSuggestionPanel(o_item, this, x_listener), BorderLayout.SOUTH);

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(x_addLabel);
        x_buttonPanel.add(o_removeLabel);
        x_buttonPanel.add(o_dismissLabel);
        x_buttonPanel.add(o_linkReminderLabel);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);

		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
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

	private void linkToGoogle() {
		final JPanel x_this = this;

		if (o_linkLabel.isEnabled()) {
			if (JOptionPane.showConfirmDialog(x_this, "Link '" + o_item.getText() + "' to Google Calendar ?", "Link to Google ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
				GoogleLinkTask x_task = new GoogleLinkTask(Arrays.asList(o_item), new GoogleProgressWindow(x_this), new ProgressAdapter() {
					@Override
					public void finished() {
						JOptionPane.showMessageDialog(x_this, "'" + o_item.getText() + "' was linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
					}
				});
				x_task.execute();
			}
		}
	}

	private void linkToGoogle(int p_index) {
		final JPanel x_this = this;

		if (o_linkReminderLabel.isEnabled() && o_item.getDueDate() != null) {
			final Reminder x_reminder = o_item.getReminder(p_index);

			if (JOptionPane.showConfirmDialog(x_this, "Link '" + x_reminder.getText() + "' to Google Calendar ?", "Link to Google ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
				GoogleLinkTask x_task = new GoogleLinkTask(x_reminder, new GoogleProgressWindow(x_this), new ProgressAdapter() {
					@Override
					public void finished() {
						JOptionPane.showMessageDialog(x_this, "'" + x_reminder.getText() + "' was linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
					}
				});
				x_task.execute();
			}
		}
	}

	@Override
	void tableRowClicked(int row, int col) {
		o_removeLabel.setEnabled(row != -1);
		o_dismissLabel.setEnabled(row != -1 && o_item.getReminder(row).isActive());
		o_linkReminderLabel.setEnabled(row != -1 && o_item.getReminder(row).isActive());
	}

	void tableRowDoubleClicked(int row, int col) {
		if(row != -1) {
			WindowManager.getInstance().openComponent(o_item.getReminder(row));
		}
	}

	@Override
	public void update(Observable observable, Object o) {
		o_linkLabel.setEnabled(o_item.getDueDate() != null && o_item.isActive());
		tableRowClicked(-1, -1);
	}

	@Override
	public void timeUpdate() {
		((ComponentTableModel)o_table.getModel()).fireTableDataChanged();
		tableRowClicked(-1, -1);
	}

	@Override
	public void googleSynced() {
		((ComponentTableModel)o_table.getModel()).fireTableDataChanged();
		tableRowClicked(-1, -1);

		if(GoogleUtil.isLinked(o_item)) {
			o_linkLabel.setIcon(ImageUtil.getGoogleSmallIcon());
		} else {
			o_linkLabel.setIcon(ImageUtil.getLinkIcon());
		}
	}
}