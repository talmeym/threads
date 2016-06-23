package gui;

import data.*;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

class DateSuggestionPanel extends JPanel {
	private static final DateFormat s_dateTimeFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd/MM/yy");
	private static final String s_defaultTextString = "dd/mm/yy [hh:mm]";

	private static DateItem[] s_timeItems = new DateItem[]{new DateItem("Anytime", 0), new DateItem("9 AM", 9), new DateItem("Midday", 12), new DateItem("C.O.B.", 18)};
    private static DateItem[] s_weekItems = new DateItem[]{new DateItem("This", 0), new DateItem("Next", 7), new DateItem("A week", 14), new DateItem("2 Weeks", 21), new DateItem("3 Weeks", 28), new DateItem("4 Weeks", 35)};
    private static DateItem[] s_dayItems = new DateItem[]{new DateItem("Mon", 2), new DateItem("Tues", 3), new DateItem("Wed", 4), new DateItem("Thur", 5), new DateItem("Fri", 6), new DateItem("Sat", 7), new DateItem("Sun", 1)};

    private JComboBox o_timeBox = new JComboBox(s_timeItems);
    private JComboBox o_weekBox = new JComboBox(s_weekItems);
    private JComboBox o_dayBox = new JComboBox(s_dayItems);

	private final Item o_item;
	private JPanel o_parentPanel;
	private final ComponentInfoChangeListener o_listener;
    private final JTextField o_dueDateField = new JTextField();
	private boolean o_modified = false;

    DateSuggestionPanel(Item p_item, final JPanel p_parentPanel, final ComponentInfoChangeListener p_listener) {
        super(new BorderLayout());
		o_parentPanel = p_parentPanel;
		o_item = p_item;
		o_listener = p_listener;

		final DocumentListener x_listener = new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent documentEvent) {
				p_listener.componentInfoChanged(false);
				o_modified = true;
			}

			@Override public void removeUpdate(DocumentEvent documentEvent) {
				p_listener.componentInfoChanged(false);
				o_modified = true;
			}

			@Override public void changedUpdate(DocumentEvent documentEvent) {
				p_listener.componentInfoChanged(false);
				o_modified = true;
			}
		};

		o_item.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object o) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(getDueDateText(o_item.getDueDate()));
				o_dueDateField.getDocument().addDocumentListener(x_listener);
				o_dueDateField.setForeground(o_item.getDueDate() != null && o_item.isActive() ? Color.black : Color.gray);
			}
		});

		o_dueDateField.setText(getDueDateText(o_item.getDueDate()));
		o_dueDateField.setForeground(o_item.getDueDate() != null && o_item.isActive() ? Color.black : Color.gray);
		o_dueDateField.setToolTipText("Press enter to set");
		o_dueDateField.getDocument().addDocumentListener(x_listener);
		o_dueDateField.setHorizontalAlignment(JTextField.CENTER);

		o_dueDateField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent focusEvent) {
				if(o_dueDateField.getText().equals(s_defaultTextString)) {
					setDueDateText("", o_item.isActive() ? Color.black : Color.gray);
				}
			}

			@Override
			public void focusLost(FocusEvent focusEvent) {
				if(o_item.getDueDate() == null && o_dueDateField.getText().length() == 0) {
					setDueDateText(s_defaultTextString, Color.gray);
				} else if(o_modified) {
					if(JOptionPane.showConfirmDialog(p_parentPanel, "You've made modifications, would you like to keep them ?", "Set date to '" + o_dueDateField.getText() + "' ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
						setDueDate();
					} else {
						o_dueDateField.setText(getDueDateText(o_item.getDueDate()));
						o_modified = false;
					}
				}
			}

			private void setDueDateText(String p_text, Color p_foreground) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(p_text);
				o_dueDateField.getDocument().addDocumentListener(x_listener);
				o_dueDateField.setForeground(p_foreground);
			}
		});

		JButton o_SetButton = new JButton("Set");
		o_SetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				suggestAndSet();
			}
		});

		o_dueDateField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				setDueDate();
			}
		});

		setUpDropDowns();

		JPanel x_labelPanel = new JPanel(new BorderLayout());
		x_labelPanel.add(new JLabel("Due Date"), BorderLayout.CENTER);
		x_labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_buttonPanel.add(o_timeBox);
		x_buttonPanel.add(o_weekBox);
		x_buttonPanel.add(o_dayBox);
		x_buttonPanel.add(o_SetButton);
		x_buttonPanel.setBorder(BorderFactory.createTitledBorder("Quick Set"));

		JPanel x_fieldPanel = new JPanel();
		x_fieldPanel.setLayout(new BoxLayout(x_fieldPanel, BoxLayout.Y_AXIS));
		x_fieldPanel.add(Box.createVerticalStrut(12));
		x_fieldPanel.add(o_dueDateField);
		x_fieldPanel.add(Box.createVerticalStrut(12));
		x_fieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

		add(x_labelPanel, BorderLayout.WEST);
		add(x_fieldPanel, BorderLayout.CENTER);
		add(x_buttonPanel, BorderLayout.EAST);
	}

	private void setUpDropDowns() {
		Calendar x_now = Calendar.getInstance();
		int x_dayIndex = x_now.get(Calendar.DAY_OF_WEEK) - 2;
		int x_hour = x_now.get(Calendar.HOUR_OF_DAY);

		o_dayBox.setSelectedIndex(x_dayIndex < 0 ? x_dayIndex + 7 : x_dayIndex);
		o_timeBox.setSelectedIndex(1);

		if(x_hour > 8) {
			o_timeBox.setSelectedIndex(2);
		}

		if(x_hour > 11) {
			o_timeBox.setSelectedIndex(3);
		}

		if(x_hour > 17) {
			o_timeBox.setSelectedIndex(0);

			if(o_dayBox.getSelectedIndex() == 6) {
				o_weekBox.setSelectedIndex(1);
				o_dayBox.setSelectedIndex(0);
			} else {
				o_dayBox.setSelectedIndex(o_dayBox.getSelectedIndex() + 1);
			}
		}
	}

	private void suggestAndSet() {
        Calendar x_calendar = Calendar.getInstance();
        x_calendar.set(Calendar.MINUTE, 0);
        x_calendar.set(Calendar.SECOND, 0);
        x_calendar.set(Calendar.MILLISECOND, 0);
        x_calendar.set(Calendar.HOUR_OF_DAY, ((DateItem) o_timeBox.getSelectedItem()).o_value);
        x_calendar.set(Calendar.DAY_OF_WEEK, ((DateItem)o_dayBox.getSelectedItem()).o_value);
		x_calendar.add(Calendar.DATE, ((DateItem)o_weekBox.getSelectedItem()).o_value);
		o_dueDateField.setText(getDueDateText(x_calendar.getTime()));
		o_dueDateField.setForeground(o_item.isActive() ? Color.black : Color.gray);
		setDueDate();
    }

	private void setDueDate() {
		String x_text = o_dueDateField.getText();

		if(!StringUtils.isEmpty(x_text)) {
			Date x_dueDate = null;

			try {
				x_dueDate = s_dateTimeFormat.parse(x_text);
			} catch (ParseException e) {
				try {
					x_dueDate = s_dateFormat.parse(x_text);
				} catch (ParseException pe) { /* do nothing */ }
			}

			if(x_dueDate != null && !x_dueDate.equals(o_item.getDueDate())) {
				Date x_currentDate = o_item.getDueDate();
				o_item.setDueDate(x_dueDate);

				if(o_item.getReminderCount() > 0 && JOptionPane.showConfirmDialog(o_parentPanel, MessagingConstants.s_moveRemindersDesc, MessagingConstants.s_moveReminderTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
					for(int i = 0; i < o_item.getReminderCount(); i++) {
						Reminder x_reminder = o_item.getReminder(i);
						x_reminder.setDueDate(new Date(x_dueDate.getTime() + (x_reminder.getDueDate().getTime() - x_currentDate.getTime())));
					}
				}
			}
		} else {
			if(JOptionPane.showConfirmDialog(o_parentPanel, "Removing Due Date will convert this Action into an Update. Any Reminders will be automatically removed. Continue ?", "Convert to Update ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon()) == 0) {
				o_item.removeAllReminder();
				o_item.setDueDate(null);
			}
		}

		o_dueDateField.setText(getDueDateText(o_item.getDueDate()));
		o_dueDateField.setForeground(o_item.getDueDate() != null && o_item.isActive() ? Color.black : Color.gray);
		o_listener.componentInfoChanged(true);
		o_modified = false;
	}

	private String getDueDateText(Date x_dueDate) {
		return x_dueDate != null ? DateUtil.isAllDay(x_dueDate) ? s_dateFormat.format(x_dueDate) : s_dateTimeFormat.format(x_dueDate) : s_defaultTextString;
	}

	private static class DateItem {
        public final String o_display;
        public final int o_value;
        
        public DateItem(String p_display, int p_value) {
            o_display = p_display;
            o_value = p_value;
        }
        
        public String toString() {
            return o_display;
        }
    }
}
