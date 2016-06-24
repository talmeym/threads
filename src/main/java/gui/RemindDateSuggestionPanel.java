package gui;

import data.*;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

public class RemindDateSuggestionPanel extends JPanel {
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

	private final Reminder o_reminder;
	private final ComponentInfoChangeListener o_listener;
    private final JTextField o_dueDateField = new JTextField();
    private final JComboBox o_minBox = new JComboBox(s_minItems);
    private final JComboBox o_hourBox = new JComboBox(s_hourItems);
    private final JComboBox o_dayBox = new JComboBox(s_dayItems);
    private final JComboBox o_weekBox = new JComboBox(s_weekItems);
	private boolean o_modified = false;

	public RemindDateSuggestionPanel(Reminder p_reminder, final JPanel p_parentPanel, final ComponentInfoChangeListener p_listener) {
        super(new BorderLayout());
		o_reminder = p_reminder;
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

		o_reminder.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object o) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(getDueDateText(o_reminder.getDueDate()));
				o_dueDateField.getDocument().addDocumentListener(x_listener);
			}
		});

		o_dueDateField.setText(getDueDateText(o_reminder.getDueDate()));
		o_dueDateField.setHorizontalAlignment(JTextField.CENTER);
		o_dueDateField.getDocument().addDocumentListener(x_listener);
		o_dueDateField.setToolTipText("Press enter to set");
		o_dueDateField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.lightGray), BorderFactory.createEmptyBorder(0, 5, 0, 5)));

		o_dueDateField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				setDueDate();
			}
		});

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
				} else if(o_modified) {
					if(JOptionPane.showConfirmDialog(p_parentPanel, "You've made modifications, would you like to keep them ?", "Set date to '" + o_dueDateField.getText() + "' ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon()) == 0) {
						setDueDate();
					} else {
						o_dueDateField.setText(getDueDateText(o_reminder.getDueDate()));
						o_modified = false;
					}
				}
			}

			private void setDueDateText(String textString, Color color) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(textString);
				o_dueDateField.getDocument().addDocumentListener(x_listener);
				o_dueDateField.setForeground(color);
			}
		});

		JButton o_setButton = new JButton("Set");
		o_setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				suggestAndSet();
			}
		});

		JPanel x_labelPanel = new JPanel(new GridLayout(0, 1, 0, 4));
		x_labelPanel.add(new JLabel("Action"));
		x_labelPanel.add(new JLabel("Due Date"));
		x_labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

		JTextField x_actionDueDateField = new JTextField(getDueDateText(((Item)p_reminder.getParentComponent()).getDueDate()));
		x_actionDueDateField.setHorizontalAlignment(JTextField.CENTER);
		x_actionDueDateField.setEnabled(false);
		x_actionDueDateField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.lightGray), BorderFactory.createEmptyBorder(0, 5, 0, 5)));

		JPanel x_fieldPanel = new JPanel(new GridLayout(0, 1, 0, 4));
		x_fieldPanel.add(x_actionDueDateField);
		x_fieldPanel.add(o_dueDateField);
		x_fieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		JPanel x_labelAndFieldPanel = new JPanel(new BorderLayout());
		x_labelAndFieldPanel.add(x_labelPanel, BorderLayout.WEST);
		x_labelAndFieldPanel.add(x_fieldPanel, BorderLayout.CENTER);

		JPanel x_dropdownPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_dropdownPanel.add(o_weekBox);
		x_dropdownPanel.add(o_dayBox);
		x_dropdownPanel.add(o_hourBox);
		x_dropdownPanel.add(o_minBox);

		JPanel x_beforePanel = new JPanel(new BorderLayout());
		x_beforePanel.add(new JLabel("before"), BorderLayout.CENTER);
		x_beforePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		JPanel x_setPanel = new JPanel(new BorderLayout());
		x_setPanel.add(x_beforePanel, BorderLayout.WEST);
		x_setPanel.add(o_setButton, BorderLayout.CENTER);

		JPanel x_borderedPanel = new JPanel(new BorderLayout());
		x_borderedPanel.add(x_dropdownPanel, BorderLayout.CENTER);
		x_borderedPanel.add(x_setPanel, BorderLayout.EAST);
		x_borderedPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5), BorderFactory.createTitledBorder("Quick Set")));

		add(x_labelAndFieldPanel, BorderLayout.CENTER);
		add(x_borderedPanel, BorderLayout.EAST);
    }

	private void suggestAndSet() {
        long x_timeToSubtract = 0;
        x_timeToSubtract += ((DateItem)o_weekBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_dayBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_hourBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_minBox.getSelectedItem()).o_value;
        
        Date x_dueDate = o_reminder.getItem().getDueDate();
        o_dueDateField.setText(getDueDateText(new Date(x_dueDate.getTime() - x_timeToSubtract)));
		setDueDate();
    }

	private void setDueDate() {
		if(o_dueDateField.getText() != null && o_dueDateField.getText().length() > 0) {
			Date x_dueDate = null;

			try {
				x_dueDate = s_dateTimeFormat.parse(o_dueDateField.getText());
			} catch (ParseException e) {
				try {
					x_dueDate = s_dateFormat.parse(o_dueDateField.getText());
				} catch (ParseException pe) { /* do nothing */ }
			}

			if(x_dueDate != null && !x_dueDate.equals(o_reminder.getDueDate())) {
				o_reminder.setDueDate(x_dueDate);
			}
		}

		o_dueDateField.setText(getDueDateText(o_reminder.getDueDate()));
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
