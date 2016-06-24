package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static util.GuiUtil.setUpButtonLabel;

class ItemPanel extends ComponentTablePanel implements Observer {
    private final Item o_item;
	private final JLabel o_removeReminderLabel = new JLabel(ImageUtil.getMinusIcon());
	private final JLabel o_dismissReminderLabel = new JLabel(ImageUtil.getTickIcon());
	private final JLabel o_linkItemLabel = new JLabel(ImageUtil.getLinkIcon());
	private final JLabel o_linkReminderLabel = new JLabel(ImageUtil.getLinkIcon());

	ItemPanel(Item p_item) {
        super(new ItemReminderTableModel(p_item),  new ComponentCellRenderer(p_item));
        o_item = p_item;
		o_item.addObserver(this);

        fixColumnWidth(1, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(2, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(3, 30);

		o_linkItemLabel.setEnabled(o_item.getDueDate() != null);
		o_linkItemLabel.setToolTipText("Link to Google Calendar");

		final JLabel x_addReminderLabel = new JLabel(ImageUtil.getPlusIcon());
		x_addReminderLabel.setEnabled(o_item.getDueDate() != null);
		o_removeReminderLabel.setEnabled(false);
		o_dismissReminderLabel.setEnabled(false);
		o_linkReminderLabel.setEnabled(false);

		ComponentInfoChangeListener x_listener = new ComponentInfoChangeListener() {
			@Override
			public void componentInfoChanged(boolean saved) {
				if(saved) {
					x_addReminderLabel.setEnabled(o_item.getDueDate() != null);
				}
			}
		};

		o_linkItemLabel.setToolTipText("Link to Google Calendar");
		o_linkItemLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				linkToGoogle();
			}
		});

		x_addReminderLabel.setToolTipText("Add Reminder");
		x_addReminderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				addSomething();
			}
		});

		o_removeReminderLabel.setToolTipText("Remove Reminder");
        o_removeReminderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				removeSomething(getSelectedRow());
			}
		});

		o_dismissReminderLabel.setToolTipText("Make Reminder Active/Inactive");
        o_dismissReminderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dismissSomething(getSelectedRow());
			}
		});

		o_linkReminderLabel.setToolTipText("Link Reminder to Google Calendar");
		o_linkReminderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				linkToGoogle(getSelectedRow());
			}
		});

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, this, x_listener, o_linkItemLabel), BorderLayout.NORTH);
        x_panel.add(new DateSuggestionPanel(o_item, this, x_listener), BorderLayout.SOUTH);
		x_panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(x_addReminderLabel));
        x_buttonPanel.add(setUpButtonLabel(o_removeReminderLabel));
        x_buttonPanel.add(setUpButtonLabel(o_dismissReminderLabel));
        x_buttonPanel.add(setUpButtonLabel(o_linkReminderLabel));
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
				o_removeReminderLabel.setEnabled(false);
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

		if (o_linkItemLabel.isEnabled()) {
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
		o_removeReminderLabel.setEnabled(row != -1);
		o_dismissReminderLabel.setEnabled(row != -1 && o_item.getReminder(row).isActive());
		o_linkReminderLabel.setEnabled(row != -1);
	}

	void tableRowDoubleClicked(int row, int col) {
		if(row != -1) {
			WindowManager.getInstance().openComponent(o_item.getReminder(row));
		}
	}

	@Override
	public void update(Observable observable, Object o) {
		o_linkItemLabel.setEnabled(o_item.getDueDate() != null);
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
			o_linkItemLabel.setIcon(ImageUtil.getGoogleSmallIcon());
		} else {
			o_linkItemLabel.setIcon(ImageUtil.getLinkIcon());
		}
	}
}