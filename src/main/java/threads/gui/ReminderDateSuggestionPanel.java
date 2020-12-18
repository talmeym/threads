package threads.gui;

import threads.data.Configuration;
import threads.data.Reminder;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.*;
import java.util.Date;

import static java.awt.BorderLayout.*;
import static java.awt.Color.black;
import static java.awt.Color.gray;
import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;
import static threads.data.ComponentChangeEvent.Field.DUE_DATE;

class ReminderDateSuggestionPanel extends DateSuggestionPanel<Reminder> {
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

	ReminderDateSuggestionPanel(Configuration p_configuration, Reminder p_reminder, JPanel p_parentPanel) {
        super(p_configuration, p_reminder, new BorderLayout(), p_parentPanel);

		p_reminder.addComponentChangeListener(e -> {
			if(e.getSource() == p_reminder && e.isValueChange()) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(getDueDateText(p_reminder.getDueDate()));
				o_dueDateField.getDocument().addDocumentListener(x_listener);
				o_dueDateField.setForeground(p_reminder.getDueDate() != null && p_reminder.isActive() ? black : gray);
			}
		});

		o_panelsPanel.add(getQuickSetPanel());
    }

	JPanel getQuickSetPanel() {
		JPanel x_dropdownPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_dropdownPanel.add(o_weekBox);
		x_dropdownPanel.add(o_dayBox);
		x_dropdownPanel.add(o_hourBox);
		x_dropdownPanel.add(o_minBox);

		JPanel x_beforePanel = new JPanel(new BorderLayout());
		x_beforePanel.add(new JLabel("before"), CENTER);
		x_beforePanel.setBorder(createEmptyBorder(0, 5, 0, 5));

		JButton x_suggestButton = new JButton("Set");
		x_suggestButton.addActionListener(e -> suggestAndSet());

		JPanel x_setPanel = new JPanel(new BorderLayout());
		x_setPanel.add(x_beforePanel, WEST);
		x_setPanel.add(x_suggestButton, CENTER);

		JPanel x_quickSetPanel = new JPanel(new BorderLayout());
		x_quickSetPanel.add(x_dropdownPanel, CENTER);
		x_quickSetPanel.add(x_setPanel, EAST);
		x_quickSetPanel.setBorder(createCompoundBorder(createEmptyBorder(0, 0, 0, 5), BorderFactory.createTitledBorder("Quick Set")));

		return x_quickSetPanel;
	}

	private void suggestAndSet() {
		suggest();
		set();
    }

    @Override
	void suggest() {
		long x_timeToSubtract = 0;
		x_timeToSubtract += ((DateItem)o_weekBox.getSelectedItem()).o_value;
		x_timeToSubtract += ((DateItem)o_dayBox.getSelectedItem()).o_value;
		x_timeToSubtract += ((DateItem)o_hourBox.getSelectedItem()).o_value;
		x_timeToSubtract += ((DateItem)o_minBox.getSelectedItem()).o_value;

		Date x_dueDate = o_hasDueDate.getParentItem().getDueDate();
		o_dueDateField.setText(getDueDateText(new Date(x_dueDate.getTime() - x_timeToSubtract)));
	}

	@Override
	void set() {
		String x_text = o_dueDateField.getText();

		if(x_text != null && x_text.length() > 0) {
			Date x_dueDate = parseDate(x_text);

			if(x_dueDate != null && !x_dueDate.equals(o_hasDueDate.getDueDate())) {
				o_hasDueDate.setDueDate(x_dueDate);
			}
		}

		revertDueDateField();
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
