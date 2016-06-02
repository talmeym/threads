package gui;

import data.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

class DateSuggestionPanel extends JPanel implements DocumentListener {
	private static final DateFormat s_dateTimeFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd/MM/yy");
	private static final Dimension s_dueFieldSize = new Dimension(130, 25);
	private static final String s_defaultTextString = "dd/mm/yy hh:mm";

	private static DateItem[] s_timeItems = new DateItem[]{new DateItem("Anytime", 0),
                                                           new DateItem("9 AM", 9),
                                                           new DateItem("Midday", 12),
                                                           new DateItem("C.O.B.", 18)};

    private static DateItem[] s_weekItems = new DateItem[]{new DateItem("This", 0),
                                                           new DateItem("Next", 7),
                                                           new DateItem("A week", 14),
                                                           new DateItem("2 Weeks", 21),
                                                           new DateItem("3 Weeks", 28),
                                                           new DateItem("4 Weeks", 35)};

    private static DateItem[] s_dayItems = new DateItem[]{new DateItem("Mon", 2),
                                                          new DateItem("Tues", 3),
                                                          new DateItem("Wed", 4),
                                                          new DateItem("Thur", 5),
                                                          new DateItem("Fri", 6),
                                                          new DateItem("Sat", 7),
                                                          new DateItem("Sun", 1)};

    private JComboBox o_timeBox = new JComboBox(s_timeItems);
    private JComboBox o_weekBox = new JComboBox(s_weekItems);
    private JComboBox o_dayBox = new JComboBox(s_dayItems);
	private final Item o_item;
	private ComponentInfoChangeListener o_listener;
    private final JTextField o_dueDateField = new JTextField();

    DateSuggestionPanel(Item p_item, ComponentInfoChangeListener p_listener) {
        super(new BorderLayout());
		o_item = p_item;
		o_listener = p_listener;

		if(o_item.getDueDate() != null) {
			o_dueDateField.setText(s_dateTimeFormat.format(o_item.getDueDate()));
			o_dueDateField.setForeground(Color.black);
		} else {
			o_dueDateField.setText(s_defaultTextString);
			o_dueDateField.setForeground(Color.gray);
		}

		o_dueDateField.setPreferredSize(s_dueFieldSize);
		o_dueDateField.getDocument().addDocumentListener(this);
		o_dueDateField.setToolTipText("Press enter to set date");

		final DocumentListener listener = this;

		o_dueDateField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent focusEvent) {
				if(o_dueDateField.getText().equals(s_defaultTextString)) {
					setDueDateText("", Color.black);
				}
			}

			@Override
			public void focusLost(FocusEvent focusEvent) {
				if(o_dueDateField.getText().length() == 0) {
					setDueDateText(s_defaultTextString, Color.gray);
				}
			}

			private void setDueDateText(String textString, Color color) {
				o_dueDateField.getDocument().removeDocumentListener(listener);
				o_dueDateField.setText(textString);
				o_dueDateField.setForeground(color);
				o_dueDateField.getDocument().addDocumentListener(listener);
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

		o_timeBox.setSelectedIndex(3);

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
		o_dayBox.setSelectedIndex(x_dayIndex < 0 ? x_dayIndex + 7 : x_dayIndex);

		int x_hour = x_now.get(Calendar.HOUR_OF_DAY);

		if(x_hour > 9) {
			o_timeBox.setSelectedIndex(1);
		}

		if(x_hour > 12) {
			o_timeBox.setSelectedIndex(2);
		}

		if(x_hour > 18) {
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
        
        x_calendar.set(Calendar.HOUR_OF_DAY, ((DateItem)o_timeBox.getSelectedItem()).o_value);
        x_calendar.set(Calendar.MINUTE, 0);
        x_calendar.set(Calendar.SECOND, 0);
        x_calendar.set(Calendar.MILLISECOND, 0);

        x_calendar.set(Calendar.DAY_OF_WEEK, ((DateItem)o_dayBox.getSelectedItem()).o_value);
        
        int x_daysToAdd = ((DateItem)o_weekBox.getSelectedItem()).o_value;               

        int x_daysLeftInYear = 365 - x_calendar.get(Calendar.DAY_OF_YEAR);
        
        if(x_daysToAdd > x_daysLeftInYear) {
            x_calendar.roll(Calendar.YEAR, true);
        }
        
        int x_daysLeftInMonth = x_calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - x_calendar.get(Calendar.DAY_OF_MONTH);
        
        if(x_daysToAdd > x_daysLeftInMonth) {
            x_calendar.roll(Calendar.MONTH, true);
            x_daysToAdd++;
        }

        x_calendar.roll(Calendar.DATE, x_daysToAdd);
        
        o_dueDateField.setText(s_dateTimeFormat.format(x_calendar.getTime()));
		setDueDate();
		o_dueDateField.setForeground(Color.black);
    }

	private void setDueDate() {
		if(o_dueDateField.getText() != null && o_dueDateField.getText().length() > 0 && !o_dueDateField.getText().equals(s_defaultTextString)) {
			Date x_dueDate = null;

			try {
				x_dueDate = s_dateTimeFormat.parse(o_dueDateField.getText());
			} catch (ParseException e) {
				try {
					x_dueDate = s_dateFormat.parse(o_dueDateField.getText());
				} catch (ParseException pe) { /* do nothing */ }
			}

			if(x_dueDate != null && !x_dueDate.equals(o_item.getDueDate())) {
				Date x_actionDate = o_item.getDueDate();
				o_item.setDueDate(x_dueDate);

				if(o_item.getReminderCount() > 0 && JOptionPane.showConfirmDialog((JPanel) o_listener, MessagingConstants.s_moveRemindersDesc, MessagingConstants.s_moveReminderTitle, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

					for(int i = 0; i < o_item.getReminderCount(); i++) {
						Reminder x_reminder = o_item.getReminder(i);
						x_reminder.setDueDate(new Date(x_dueDate.getTime() + (x_reminder.getDueDate().getTime() - x_actionDate.getTime())));
					}
				}
			}
		} else {
			o_item.setDueDate(null);
		}

		o_dueDateField.setText(o_item.getDueDate() != null ? s_dateTimeFormat.format(o_item.getDueDate()) : "");
		o_listener.componentInfoChanged(true);
	}

	@Override
	public void insertUpdate(DocumentEvent documentEvent) {
		o_listener.componentInfoChanged(false);
	}

	@Override
	public void removeUpdate(DocumentEvent documentEvent) {
		o_listener.componentInfoChanged(false);
	}

	@Override
	public void changedUpdate(DocumentEvent documentEvent) {
		o_listener.componentInfoChanged(false);
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
