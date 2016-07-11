package gui;

import data.*;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import static util.GuiUtil.setUpButtonLabel;

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

	private final JTextField o_dueDateField = new JTextField();
	private final JLabel o_setLabel = new JLabel(ImageUtil.getReturnIcon());
	private final JLabel o_revertLabel = new JLabel(ImageUtil.getCrossIcon());

	private final JComboBox o_minBox = new JComboBox(s_minItems);
    private final JComboBox o_hourBox = new JComboBox(s_hourItems);
    private final JComboBox o_dayBox = new JComboBox(s_dayItems);
    private final JComboBox o_weekBox = new JComboBox(s_weekItems);

	public RemindDateSuggestionPanel(Reminder p_reminder, final JPanel p_parentPanel) {
        super(new BorderLayout());
		o_reminder = p_reminder;

		final DocumentListener x_listener = new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent documentEvent) {
				edited();
			}

			@Override public void removeUpdate(DocumentEvent documentEvent) {
				edited();
			}

			@Override public void changedUpdate(DocumentEvent documentEvent) {
				edited();
			}

			private void edited() {
				o_setLabel.setEnabled(true);
				o_revertLabel.setEnabled(true);
				o_dueDateField.setBackground(ColourConstants.s_editedColour);
			}
		};

		o_reminder.addComponentChangeListener(new ComponentChangeListener() {
			@Override
			public void componentChanged(ComponentChangeEvent p_event) {
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
				}
			}

			private void setDueDateText(String textString, Color color) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(textString);
				o_dueDateField.getDocument().addDocumentListener(x_listener);
				o_dueDateField.setForeground(color);
			}
		});

		o_setLabel.setEnabled(false);
		o_setLabel.setToolTipText("Apply Change");
		o_setLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				setDueDate();
			}
		});

		o_revertLabel.setEnabled(false);
		o_revertLabel.setToolTipText("Revert Change");
		o_revertLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				o_dueDateField.setText(getDueDateText(o_reminder.getDueDate()));
				o_dueDateField.setBackground(Color.white);
				o_setLabel.setEnabled(false);
				o_revertLabel.setEnabled(false);
			}
		});

		JButton o_setButton = new JButton("Set");
		o_setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				suggestAndSet();
			}
		});

		JTextField x_actionDueDateField = new JTextField(getDueDateText(((Item) p_reminder.getParentComponent()).getDueDate()));
		x_actionDueDateField.setHorizontalAlignment(JTextField.CENTER);
		x_actionDueDateField.setEnabled(false);
		x_actionDueDateField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.lightGray), BorderFactory.createEmptyBorder(0, 5, 0, 5)));

		JPanel x_fieldPanel = new JPanel();
		x_fieldPanel.setLayout(new BoxLayout(x_fieldPanel, BoxLayout.Y_AXIS));
		x_fieldPanel.add(Box.createVerticalStrut(13));
		x_fieldPanel.add(o_dueDateField);
		x_fieldPanel.add(Box.createVerticalStrut(14));
		x_fieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		JPanel x_buttonPanel = new JPanel();
		x_buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(o_setLabel));
		x_buttonPanel.add(setUpButtonLabel(o_revertLabel));
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));

		JPanel x_panel = new JPanel(new BorderLayout());
		x_panel.add(new JLabel("Due Date"), BorderLayout.WEST);
		x_panel.add(x_fieldPanel, BorderLayout.CENTER);
		x_panel.add(x_buttonPanel, BorderLayout.EAST);
		x_panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

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

		JPanel x_quickSetPanel = new JPanel(new BorderLayout());
		x_quickSetPanel.add(x_dropdownPanel, BorderLayout.CENTER);
		x_quickSetPanel.add(x_setPanel, BorderLayout.EAST);
		x_quickSetPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5), BorderFactory.createTitledBorder("Quick Set")));

		add(x_panel, BorderLayout.CENTER);
		add(x_quickSetPanel, BorderLayout.EAST);
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
		o_setLabel.setEnabled(false);
		o_revertLabel.setEnabled(false);
		o_dueDateField.setBackground(Color.white);
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
