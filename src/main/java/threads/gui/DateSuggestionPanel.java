package threads.gui;

import threads.data.HasDueDate;
import threads.util.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.awt.BorderLayout.*;
import static java.awt.Color.*;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.*;
import static javax.swing.BoxLayout.Y_AXIS;
import static threads.gui.ColourConstants.s_editedColour;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.DateUtil.isAllDay;
import static threads.util.ImageUtil.getCrossIcon;
import static threads.util.ImageUtil.getReturnIcon;

abstract class DateSuggestionPanel<TYPE extends HasDueDate> extends JPanel {
	private static final DateFormat s_dateTimeFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd/MM/yy");
	private static final String s_defaultTextString = "dd/mm/yy [hh:mm]";

	final TYPE o_hasDueDate;

	final JTextField o_dueDateField = new JTextField();

	private final JComboBox<String> o_pushBackBox = new JComboBox<>(new String[] {"1 Hour", "1 Day", "1 Week", "2 Weeks", "1 Month", "3 months", "6 months", "A Year"});
	private final JCheckBox o_pushBackDupeBox = new JCheckBox("Leave Duplicate");
	private final JLabel o_setLabel = createLabel(getReturnIcon(), "Apply Change", false, e -> set());
	private final JLabel o_revertLabel = createLabel(getCrossIcon(), "Revert Change", false);
	final JPanel o_panelsPanel;

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

	DateSuggestionPanel(TYPE p_hasDueDate, LayoutManager p_layout) {
		super(p_layout);
		this.o_hasDueDate = p_hasDueDate;

		o_dueDateField.setText(getDueDateText(o_hasDueDate.getDueDate()));
		o_dueDateField.setForeground(s_defaultTextString.equals(o_dueDateField.getText()) ? gray : black);
		o_dueDateField.setHorizontalAlignment(JTextField.CENTER);
		o_dueDateField.getDocument().addDocumentListener(x_listener);
		o_dueDateField.setToolTipText("Press enter to set");
		o_dueDateField.setBorder(createCompoundBorder(createLineBorder(lightGray), createEmptyBorder(0, 5, 0, 5)));

		o_dueDateField.addActionListener(e -> set());
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
				o_dueDateField.setText(getDueDateText(o_hasDueDate.getDueDate()));
				o_dueDateField.setBackground(white);
				o_setLabel.setEnabled(false);
				o_revertLabel.setEnabled(false);
			}
		});

		JPanel x_fieldPanel = new JPanel();
		x_fieldPanel.setLayout(new BoxLayout(x_fieldPanel, Y_AXIS));
		x_fieldPanel.add(Box.createVerticalStrut(14));
		x_fieldPanel.add(o_dueDateField);
		x_fieldPanel.add(Box.createVerticalStrut(15));
		x_fieldPanel.setBorder(createEmptyBorder(0, 5, 0, 5));

		o_setLabel.setVerticalAlignment(JLabel.CENTER);
		o_revertLabel.setVerticalAlignment(JLabel.CENTER);

		JPanel x_buttonPanel = new JPanel();
		x_buttonPanel.setLayout(new FlowLayout(LEFT));
		x_buttonPanel.add(o_setLabel);
		x_buttonPanel.add(o_revertLabel);
		x_buttonPanel.setBorder(createEmptyBorder(7, 0, 0, 0));

		JPanel x_dueDatePanel = new JPanel(new BorderLayout());
		x_dueDatePanel.add(new JLabel("Due Date"), WEST);
		x_dueDatePanel.add(x_fieldPanel, CENTER);
		x_dueDatePanel.add(x_buttonPanel, EAST);
		x_dueDatePanel.setBorder(createEmptyBorder(0, 5, 0, 5));

		JButton x_pushBackButton = new JButton("Set");
		x_pushBackButton.addActionListener(e -> pushBackAndSet());

		JPanel x_pushbackPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_pushbackPanel.add(o_pushBackBox);
		x_pushbackPanel.add(o_pushBackDupeBox);
		x_pushbackPanel.add(x_pushBackButton);
		x_pushbackPanel.setBorder(createTitledBorder("Push Back"));

		o_panelsPanel = new JPanel(new FlowLayout(LEFT));
		o_panelsPanel.add(x_pushbackPanel);

		add(x_dueDatePanel, CENTER);
		add(o_panelsPanel, EAST);
	}

	abstract JPanel getQuickSetPanel();
	abstract void suggest();
	abstract void set();

	private void pushBackAndSet() {
		revertDueDateField();
		String x_text = o_dueDateField.getText();

		if(!StringUtils.isEmpty(x_text)) {
			Date x_date = parseDate(x_text);

			if(x_date != null) {
			    if(o_pushBackDupeBox.isSelected()) {
                    o_hasDueDate.duplicate(false);
                }

				Calendar x_calendar = Calendar.getInstance();
				x_calendar.setTime(x_date);
				x_calendar.set(Calendar.SECOND, 0);
				x_calendar.set(Calendar.MILLISECOND, 0);

				switch(o_pushBackBox.getSelectedIndex()) {
					case 0: x_calendar.add(Calendar.HOUR_OF_DAY, 1); break;
					case 1: x_calendar.add(Calendar.DATE, 1); break;
					case 2: x_calendar.add(Calendar.DATE, 7); break;
					case 3: x_calendar.add(Calendar.DATE, 14); break;
					case 4: x_calendar.add(Calendar.MONTH, 1); break;
					case 5: x_calendar.add(Calendar.MONTH, 3); break;
					case 6: x_calendar.add(Calendar.MONTH, 6); break;
					default: x_calendar.add(Calendar.YEAR, 1); break;
				}

				o_dueDateField.setText(getDueDateText(x_calendar.getTime()));
				o_dueDateField.setForeground(o_hasDueDate.isActive() ? black : gray);
				set();
			}
		}
	}

	String getDueDateText(Date x_dueDate) {
		return x_dueDate != null ? isAllDay(x_dueDate) ? s_dateFormat.format(x_dueDate) : s_dateTimeFormat.format(x_dueDate) : s_defaultTextString;
	}

	Date parseDate(String p_text) {
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

	void revertDueDateField() {
		o_dueDateField.setText(getDueDateText(o_hasDueDate.getDueDate()));
		o_dueDateField.setForeground(o_hasDueDate.getDueDate() != null && o_hasDueDate.isActive() ? black : gray);
		o_setLabel.setEnabled(false);
		o_revertLabel.setEnabled(false);
		o_dueDateField.setBackground(white);
	}
}
