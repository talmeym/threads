package gui;

import data.*;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import static gui.ColourConstants.s_editedColour;
import static gui.WidgetFactory.createLabel;
import static gui.WidgetFactory.setUpButtonLabel;
import static java.awt.BorderLayout.*;
import static java.awt.Color.*;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.JOptionPane.*;
import static util.DateUtil.isAllDay;
import static util.ImageUtil.*;

public class ItemDateSuggestionPanel extends JPanel implements TimedUpdateListener {
	private static final DateFormat s_dateTimeFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd/MM/yy");
	private static final String s_defaultTextString = "dd/mm/yy [hh:mm]";

	private static final DateItem[] s_timeItems = new DateItem[]{new DateItem("Anytime", 0), new DateItem("9 AM", 9), new DateItem("Midday", 12), new DateItem("C.O.B.", 18)};
    private static final DateItem[] s_weekItems = new DateItem[]{new DateItem("This", 0), new DateItem("Next", 7), new DateItem("A week", 14), new DateItem("2 Weeks", 21), new DateItem("3 Weeks", 28), new DateItem("4 Weeks", 35)};
    private static final DateItem[] s_dayItems = new DateItem[]{new DateItem("Mon", 2), new DateItem("Tues", 3), new DateItem("Wed", 4), new DateItem("Thur", 5), new DateItem("Fri", 6), new DateItem("Sat", 7), new DateItem("Sun", 1)};

    private final JComboBox<DateItem> o_timeBox = new JComboBox<>(s_timeItems);
    private final JComboBox<DateItem> o_weekBox = new JComboBox<>(s_weekItems);
    private final JComboBox<DateItem> o_dayBox = new JComboBox<>(s_dayItems);

	private final JComboBox<String> o_pushBackBox = new JComboBox<>(new String[] {"An Hour", "A Day", "A Week", "A Month", "A Year"});

	private final Item o_item;

	private final JPanel o_parentPanel;
    private final JTextField o_dueDateField = new JTextField();
	private final JLabel o_setLabel = createLabel(getReturnIcon(), "Apply Change", false, e -> setDueDate());
	private final JLabel o_revertLabel = createLabel(getCrossIcon(), "Revert Change", false);

    ItemDateSuggestionPanel(Item p_item, final JPanel p_parentPanel) {
        super(new BorderLayout());
		o_parentPanel = p_parentPanel;
		o_item = p_item;


		final DocumentListener x_listener = new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent p_de) {
				edited();
			}

			@Override public void removeUpdate(DocumentEvent p_de) {
				edited();
			}

			@Override public void changedUpdate(DocumentEvent p_de) {
				edited();
			}

			private void edited() {
				o_dueDateField.setBackground(s_editedColour);
				o_setLabel.setEnabled(true);
				o_revertLabel.setEnabled(true);
			}
		};

		o_item.addComponentChangeListener(e -> {
			if(e.getSource() == o_item && e.isValueChange()) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(getDueDateText(o_item.getDueDate()));
				o_dueDateField.getDocument().addDocumentListener(x_listener);
				o_dueDateField.setForeground(o_item.getDueDate() != null && o_item.isActive() ? black : gray);
			}
		});

		o_dueDateField.setText(getDueDateText(o_item.getDueDate()));
		o_dueDateField.setForeground(o_item.getDueDate() != null && o_item.isActive() ? black : gray);
		o_dueDateField.setToolTipText("Press enter to set");
		o_dueDateField.getDocument().addDocumentListener(x_listener);
		o_dueDateField.setHorizontalAlignment(JTextField.CENTER);
		o_dueDateField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(lightGray), BorderFactory.createEmptyBorder(0, 5, 0, 5)));

		o_dueDateField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent focusEvent) {
				if(o_dueDateField.getText().equals(s_defaultTextString)) {
					setDueDateText("", o_item.isActive() ? black : gray);
				}
			}

			@Override
			public void focusLost(FocusEvent focusEvent) {
				if(o_item.getDueDate() == null && o_dueDateField.getText().length() == 0) {
					setDueDateText(s_defaultTextString, gray);
				}
			}

			private void setDueDateText(String p_text, Color p_foreground) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(p_text);
				o_dueDateField.getDocument().addDocumentListener(x_listener);
				o_dueDateField.setForeground(p_foreground);
			}
		});

		o_revertLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				o_dueDateField.setText(getDueDateText(o_item.getDueDate()));
				o_dueDateField.setBackground(white);
				o_setLabel.setEnabled(false);
				o_revertLabel.setEnabled(false);
			}
		});

		setUpDropDowns();

		JPanel x_fieldPanel = new JPanel();
		x_fieldPanel.setLayout(new BoxLayout(x_fieldPanel, Y_AXIS));
		x_fieldPanel.add(Box.createVerticalStrut(13));
		x_fieldPanel.add(o_dueDateField);
		x_fieldPanel.add(Box.createVerticalStrut(14));
		x_fieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		JPanel x_buttonPanel = new JPanel();
		x_buttonPanel.setLayout(new FlowLayout(LEFT));
		x_buttonPanel.add(o_setLabel);
		x_buttonPanel.add(o_revertLabel);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));

		JPanel x_panel = new JPanel(new BorderLayout());
		x_panel.add(new JLabel("Due Date"), WEST);
		x_panel.add(x_fieldPanel, CENTER);
		x_panel.add(x_buttonPanel, EAST);
		x_panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		o_dueDateField.addActionListener(e -> setDueDate());

		JButton x_pushBackButton = new JButton("Set");
		x_pushBackButton.addActionListener(e -> pushBackAndSet());

		JPanel x_pushbackPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_pushbackPanel.add(o_pushBackBox);
		x_pushbackPanel.add(x_pushBackButton);
		x_pushbackPanel.setBorder(BorderFactory.createTitledBorder("Push Back"));

		JButton x_suggestButton = new JButton("Set");
		x_suggestButton.addActionListener(e -> suggestAndSet());

		JPanel x_quickSetPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_quickSetPanel.add(o_timeBox);
		x_quickSetPanel.add(o_weekBox);
		x_quickSetPanel.add(o_dayBox);
		x_quickSetPanel.add(x_suggestButton);
		x_quickSetPanel.setBorder(BorderFactory.createTitledBorder("Quick Set"));

		JPanel x_panelsPanel = new JPanel(new FlowLayout(LEFT));
		x_panelsPanel.add(x_pushbackPanel);
		x_panelsPanel.add(x_quickSetPanel);

		add(x_panel, CENTER);
		add(x_panelsPanel, EAST);

		TimedUpdater.getInstance().addActivityListener(this);
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

	public static Date getDateSuggestion() {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(Calendar.MINUTE, 0);
		x_calendar.set(Calendar.SECOND, 0);
		x_calendar.set(Calendar.MILLISECOND, 0);

		int x_hour = x_calendar.get(Calendar.HOUR_OF_DAY);

		if(x_hour > 8) {
			x_calendar.set(Calendar.HOUR_OF_DAY, 12);
		}

		if(x_hour > 11) {
			x_calendar.set(Calendar.HOUR_OF_DAY, 18);
		}

		if(x_hour > 17) {
			x_calendar.set(Calendar.HOUR_OF_DAY, 0);
			x_calendar.add(Calendar.DATE, 1);
		}

		return x_calendar.getTime();
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
		o_dueDateField.setForeground(o_item.isActive() ? black : gray);
		setDueDate();
    }

    private void pushBackAndSet() {
    	revertDueDateField();
		String x_text = o_dueDateField.getText();

		if(!StringUtils.isEmpty(x_text)) {
			Date x_date = parseDate(x_text);

			if(x_date != null) {
				Calendar x_calendar = Calendar.getInstance();
				x_calendar.setTime(x_date);
				x_calendar.set(Calendar.SECOND, 0);
				x_calendar.set(Calendar.MILLISECOND, 0);

				switch(o_pushBackBox.getSelectedIndex()) {
					case 0: x_calendar.add(Calendar.HOUR_OF_DAY, 1); break;
					case 1: x_calendar.add(Calendar.DATE, 1); break;
					case 2: x_calendar.add(Calendar.DATE, 7); break;
					case 3: x_calendar.add(Calendar.MONTH, 1); break;
					default: x_calendar.add(Calendar.YEAR, 1); break;
				}

				o_dueDateField.setText(getDueDateText(x_calendar.getTime()));
				o_dueDateField.setForeground(o_item.isActive() ? black : gray);
				setDueDate();
			}
		}
	}

	private void setDueDate() {
		String x_text = o_dueDateField.getText();

		if(!StringUtils.isEmpty(x_text)) {
			Date x_dueDate = parseDate(x_text);

			if(x_dueDate != null && !x_dueDate.equals(o_item.getDueDate())) {
				Date x_currentDate = o_item.getDueDate();
				o_item.setDueDate(x_dueDate);

				if((o_item.getReminderCount() > 0) && (showConfirmDialog(o_parentPanel, "This action has reminders.\nDo you want to keep their relative positions ?", "Keep Reminders Relative ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getThreadsIcon()) == OK_OPTION)) {
					for(Reminder x_reminder: o_item.getReminders()) {
						x_reminder.setDueDate(new Date(x_dueDate.getTime() + (x_reminder.getDueDate().getTime() - x_currentDate.getTime())));
					}
				}
			}
		} else {
			if(showConfirmDialog(o_parentPanel, "Removing Due Date will convert this Action into an Update. Any Reminders will be automatically removed. Continue ?", "Convert to Update ?", OK_CANCEL_OPTION, INFORMATION_MESSAGE, getThreadsIcon()) == 0) {
				o_item.removeAllReminder();
				o_item.setDueDate(null);
			}
		}

		revertDueDateField();
	}

	private void revertDueDateField() {
		o_dueDateField.setText(getDueDateText(o_item.getDueDate()));
		o_dueDateField.setForeground(o_item.getDueDate() != null && o_item.isActive() ? black : gray);
		o_setLabel.setEnabled(false);
		o_revertLabel.setEnabled(false);
		o_dueDateField.setBackground(white);
	}

	private Date parseDate(String p_text) {
		try {
			return s_dateTimeFormat.parse(p_text);
		} catch (ParseException e) {
			try {
				return s_dateFormat.parse(p_text);
			} catch (ParseException pe) {
				return null;
			}
		}
	}

	private String getDueDateText(Date x_dueDate) {
		return x_dueDate != null ? isAllDay(x_dueDate) ? s_dateFormat.format(x_dueDate) : s_dateTimeFormat.format(x_dueDate) : s_defaultTextString;
	}

	@Override
	public void timeUpdate() {
		setUpDropDowns();
	}

	private static class DateItem {
        final String o_display;
        final int o_value;
        
        DateItem(String p_display, int p_value) {
            o_display = p_display;
            o_value = p_value;
        }
        
        public String toString() {
            return o_display;
        }
    }
}
