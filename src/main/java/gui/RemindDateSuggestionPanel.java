package gui;

import data.*;
import util.StringUtils;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import static data.ComponentChangeEvent.Field.DUE_DATE;
import static gui.ColourConstants.s_editedColour;
import static gui.WidgetFactory.createLabel;
import static gui.WidgetFactory.setUpButtonLabel;
import static java.awt.BorderLayout.WEST;
import static java.awt.Color.*;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.*;
import static util.DateUtil.isAllDay;
import static util.ImageUtil.*;

class RemindDateSuggestionPanel extends JPanel {
    private static final DateFormat s_dateTimeFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd/MM/yy");
	private static final String s_defaultTextString = "dd/mm/yy [hh:mm]";
    private static final DateItem[] s_weekItems = new DateItem[5];
    private static final DateItem[] s_dayItems = new DateItem[7];
    private static final DateItem[] s_hourItems = new DateItem[24];
    private static final DateItem[] s_minItems = new DateItem[12];

    static {
        for(int i = 0; i < s_weekItems.length; i++) {
            s_weekItems[i] = new DateItem(i + " Ws", i * 7 * 24 * 60 * 60 * 1000);
        }

        for(int i = 0; i < s_dayItems.length; i++) {
            s_dayItems[i] = new DateItem(i + " Ds", i * 24 * 60 * 60 * 1000);
        }

        for(int i = 0; i < s_hourItems.length; i++) {
            s_hourItems[i] = new DateItem(i + " Hs", i * 60 * 60 * 1000);
        }

        for(int i = 0; i < s_minItems.length; i++) {
            s_minItems[i] = new DateItem(i * 5 + " Ms", i * 5 * 60 * 1000);
        }
    }

	private final JComboBox<DateItem> o_minBox = new JComboBox<>(s_minItems);
    private final JComboBox<DateItem> o_hourBox = new JComboBox<>(s_hourItems);
    private final JComboBox<DateItem> o_dayBox = new JComboBox<>(s_dayItems);
    private final JComboBox<DateItem> o_weekBox = new JComboBox<>(s_weekItems);

	private final JComboBox<String> o_pushBackBox = new JComboBox<>(new String[] {"An Hour", "A Day", "A Week", "A Month", "A Year"});

	private final Reminder o_reminder;

	private final JTextField o_dueDateField = new JTextField();
	private final JLabel o_setLabel = createLabel(getReturnIcon(), "Apply Change", false, e -> setDueDate());
	private final JLabel o_revertLabel = createLabel(getCrossIcon(), "Revert Change", false);

	RemindDateSuggestionPanel(Reminder p_reminder) {
        super(new BorderLayout());
		o_reminder = p_reminder;

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
				o_setLabel.setEnabled(true);
				o_revertLabel.setEnabled(true);
				o_dueDateField.setBackground(s_editedColour);
			}
		};

		o_reminder.addComponentChangeListener(e -> {
			if(e.getSource() == o_reminder && e.getField() == DUE_DATE) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(getDueDateText(o_reminder.getDueDate()));
				o_dueDateField.getDocument().addDocumentListener(x_listener);
			}
		});

		o_dueDateField.setText(getDueDateText(o_reminder.getDueDate()));
		o_dueDateField.setHorizontalAlignment(JTextField.CENTER);
		o_dueDateField.getDocument().addDocumentListener(x_listener);
		o_dueDateField.setToolTipText("Press enter to set");
		o_dueDateField.setBorder(createCompoundBorder(createLineBorder(lightGray), createEmptyBorder(0, 5, 0, 5)));

		o_dueDateField.addActionListener(e -> setDueDate());
		o_dueDateField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent focusEvent) {
				if(o_dueDateField.getText().equals(s_defaultTextString)) {
					setDueDateText("", black);
				}
			}

			@Override
			public void focusLost(FocusEvent focusEvent) {
				if(o_dueDateField.getText().length() == 0) {
					setDueDateText(s_defaultTextString, gray);
				}
			}

			private void setDueDateText(String textString, Color color) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(textString);
				o_dueDateField.getDocument().addDocumentListener(x_listener);
				o_dueDateField.setForeground(color);
			}
		});

		o_revertLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				o_dueDateField.setText(getDueDateText(o_reminder.getDueDate()));
				o_dueDateField.setBackground(white);
				o_setLabel.setEnabled(false);
				o_revertLabel.setEnabled(false);
			}
		});

		JTextField x_actionDueDateField = new JTextField(getDueDateText(((Item) p_reminder.getParentComponent()).getDueDate()));
		x_actionDueDateField.setHorizontalAlignment(JTextField.CENTER);
		x_actionDueDateField.setEnabled(false);
		x_actionDueDateField.setBorder(createCompoundBorder(createLineBorder(lightGray), createEmptyBorder(0, 5, 0, 5)));

		JPanel x_fieldPanel = new JPanel();
		x_fieldPanel.setLayout(new BoxLayout(x_fieldPanel, BoxLayout.Y_AXIS));
		x_fieldPanel.add(Box.createVerticalStrut(13));
		x_fieldPanel.add(o_dueDateField);
		x_fieldPanel.add(Box.createVerticalStrut(14));
		x_fieldPanel.setBorder(createEmptyBorder(0, 5, 0, 5));

		JPanel x_buttonPanel = new JPanel();
		x_buttonPanel.setLayout(new FlowLayout(LEFT));
		x_buttonPanel.add(o_setLabel);
		x_buttonPanel.add(o_revertLabel);
		x_buttonPanel.setBorder(createEmptyBorder(7, 0, 0, 0));

		JPanel x_panel = new JPanel(new BorderLayout());
		x_panel.add(new JLabel("Due Date"), WEST);
		x_panel.add(x_fieldPanel, BorderLayout.CENTER);
		x_panel.add(x_buttonPanel, BorderLayout.EAST);
		x_panel.setBorder(createEmptyBorder(0, 5, 0, 5));

		JPanel x_dropdownPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_dropdownPanel.add(o_weekBox);
		x_dropdownPanel.add(o_dayBox);
		x_dropdownPanel.add(o_hourBox);
		x_dropdownPanel.add(o_minBox);

		JPanel x_beforePanel = new JPanel(new BorderLayout());
		x_beforePanel.add(new JLabel("before"), BorderLayout.CENTER);
		x_beforePanel.setBorder(createEmptyBorder(0, 5, 0, 5));

		JButton x_suggestButton = new JButton("Set");
		x_suggestButton.addActionListener(e -> suggestAndSet());

		JPanel x_setPanel = new JPanel(new BorderLayout());
		x_setPanel.add(x_beforePanel, WEST);
		x_setPanel.add(x_suggestButton, BorderLayout.CENTER);

		JButton x_pushBackButton = new JButton("Set");
		x_pushBackButton.addActionListener(e -> pushBackAndSet());

		JPanel x_pushbackPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_pushbackPanel.add(o_pushBackBox);
		x_pushbackPanel.add(x_pushBackButton);
		x_pushbackPanel.setBorder(BorderFactory.createTitledBorder("Push Back"));

		JPanel x_quickSetPanel = new JPanel(new BorderLayout());
		x_quickSetPanel.add(x_dropdownPanel, BorderLayout.CENTER);
		x_quickSetPanel.add(x_setPanel, BorderLayout.EAST);
		x_quickSetPanel.setBorder(createCompoundBorder(createEmptyBorder(0, 0, 0, 5), BorderFactory.createTitledBorder("Quick Set")));

		JPanel x_panelsPanel = new JPanel(new FlowLayout(LEFT));
		x_panelsPanel.add(x_pushbackPanel);
		x_panelsPanel.add(x_quickSetPanel);

		add(x_panel, BorderLayout.CENTER);
		add(x_panelsPanel, BorderLayout.EAST);
    }

	private void suggestAndSet() {
        long x_timeToSubtract = 0;
        x_timeToSubtract += ((DateItem)o_weekBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_dayBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_hourBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_minBox.getSelectedItem()).o_value;
        
        Date x_dueDate = o_reminder.getParentItem().getDueDate();
        o_dueDateField.setText(getDueDateText(new Date(x_dueDate.getTime() - x_timeToSubtract)));
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
				o_dueDateField.setForeground(o_reminder.isActive() ? black : gray);
				setDueDate();
			}
		}
	}

	private void setDueDate() {
		String x_text = o_dueDateField.getText();

		if(x_text != null && x_text.length() > 0) {
			Date x_dueDate = parseDate(x_text);

			if(x_dueDate != null && !x_dueDate.equals(o_reminder.getDueDate())) {
				o_reminder.setDueDate(x_dueDate);
			}
		}

		revertDueDateField();
	}

	private void revertDueDateField() {
		o_dueDateField.setText(getDueDateText(o_reminder.getDueDate()));
		o_setLabel.setEnabled(false);
		o_revertLabel.setEnabled(false);
		o_dueDateField.setBackground(white);
	}

	private Date parseDate(String x_text) {
		try {
			return s_dateTimeFormat.parse(x_text);
		} catch (ParseException e) {
			try {
				return s_dateFormat.parse(x_text);
			} catch (ParseException pe) {
				return null;
			}
		}
	}

	private String getDueDateText(Date x_dueDate) {
		return x_dueDate != null ? isAllDay(x_dueDate) ? s_dateFormat.format(x_dueDate) : s_dateTimeFormat.format(x_dueDate) : s_defaultTextString;
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
