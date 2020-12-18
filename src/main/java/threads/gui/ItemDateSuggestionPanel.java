package threads.gui;

import threads.data.Configuration;
import threads.data.Item;
import threads.data.Reminder;
import threads.util.DateUtil;
import threads.util.StringUtils;
import threads.util.TimedUpdateListener;
import threads.util.TimedUpdater;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

import static java.awt.Color.black;
import static java.awt.Color.gray;
import static javax.swing.JOptionPane.*;
import static threads.util.DateUtil.TODAY;
import static threads.util.DateUtil.isAllDay;
import static threads.util.ImageUtil.getGoogleIcon;
import static threads.util.ImageUtil.getThreadsIcon;

public class ItemDateSuggestionPanel extends DateSuggestionPanel<Item> implements TimedUpdateListener {
	private static final DateItem[] s_timeItems = new DateItem[]{new DateItem("Anytime", 0), new DateItem("9 AM", 9), new DateItem("Midday", 12), new DateItem("C.O.B.", 18), new DateItem("9 PM", 21)};
    private static final DateItem[] s_weekItems = new DateItem[]{new DateItem("This", 0), new DateItem("Next", 7), new DateItem("A week", 14), new DateItem("2 Weeks", 21), new DateItem("3 Weeks", 28), new DateItem("4 Weeks", 35)};
    private static final DateItem[] s_dayItems = new DateItem[]{new DateItem("Mon", 2), new DateItem("Tues", 3), new DateItem("Wed", 4), new DateItem("Thur", 5), new DateItem("Fri", 6), new DateItem("Sat", 7), new DateItem("Sun", 1)};

    private final JComboBox<DateItem> o_timeBox = new JComboBox<>(s_timeItems);
    private final JComboBox<DateItem> o_weekBox = new JComboBox<>(s_weekItems);
    private final JComboBox<DateItem> o_dayBox = new JComboBox<>(s_dayItems);

    ItemDateSuggestionPanel(Configuration p_configuration, Item p_item, final JPanel p_parentPanel) {
        super(p_configuration, p_item, new BorderLayout(), p_parentPanel);

		p_item.addComponentChangeListener(e -> {
			if(e.getSource() == p_item && e.isValueChange()) {
				o_dueDateField.getDocument().removeDocumentListener(x_listener);
				o_dueDateField.setText(getDueDateText(p_item.getDueDate()));
				o_dueDateField.getDocument().addDocumentListener(x_listener);
				o_dueDateField.setForeground(p_item.getDueDate() != null && p_item.isActive() ? black : gray);
			}
		});

		setUpDropDowns();
		o_panelsPanel.add(getQuickSetPanel());
		TimedUpdater.getInstance().addActivityListener(this);
	}

	JPanel getQuickSetPanel() {
		JButton x_suggestButton = new JButton("Set");
		x_suggestButton.addActionListener(e -> suggestAndSet());

		JPanel x_quickSetPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_quickSetPanel.add(o_timeBox);
		x_quickSetPanel.add(o_weekBox);
		x_quickSetPanel.add(o_dayBox);
		x_quickSetPanel.add(x_suggestButton);
		x_quickSetPanel.setBorder(BorderFactory.createTitledBorder("Quick Set"));

		return x_quickSetPanel;
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
			o_timeBox.setSelectedIndex(4);
		}

		if(x_hour > 21) {
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
			x_calendar.set(Calendar.HOUR_OF_DAY, 21);
		}

		if(x_hour > 21) {
			x_calendar.set(Calendar.HOUR_OF_DAY, 0);
			x_calendar.add(Calendar.DATE, 1);
		}

		return x_calendar.getTime();
	}

	private void suggestAndSet() {
		suggest();
		set();
    }

    @Override
	void suggest() {
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(Calendar.MINUTE, 0);
		x_calendar.set(Calendar.SECOND, 0);
		x_calendar.set(Calendar.MILLISECOND, 0);
		x_calendar.set(Calendar.HOUR_OF_DAY, ((DateItem) o_timeBox.getSelectedItem()).o_value);
		x_calendar.set(Calendar.DAY_OF_WEEK, ((DateItem)o_dayBox.getSelectedItem()).o_value);
		x_calendar.add(Calendar.DATE, ((DateItem)o_weekBox.getSelectedItem()).o_value);
		o_dueDateField.setText(getDueDateText(x_calendar.getTime()));
	}

	@Override
	void set() {
		boolean x_dueDateSet = o_hasDueDate.getDueDate() != null;
		String x_text = o_dueDateField.getText();

		if(!StringUtils.isEmpty(x_text)) {
			if(x_dueDateSet || showConfirmDialog(o_parentPanel, "Setting Due Date will convert this Update into an Action. Continue ?", "Convert to Action ?", OK_CANCEL_OPTION, INFORMATION_MESSAGE, getThreadsIcon()) == OK_OPTION) {
				Date x_dueDate = parseDate(x_text);

				if(x_dueDate != null && !x_dueDate.equals(o_hasDueDate.getDueDate())) {
					Date x_currentDate = o_hasDueDate.getDueDate();
					o_hasDueDate.setDueDate(x_dueDate);

					if((o_hasDueDate.getReminders().size() > 0) && (showConfirmDialog(o_parentPanel, "This action has reminders.\nDo you want to keep their relative positions ?", "Keep Reminders Relative ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getThreadsIcon()) == OK_OPTION)) {
						for(Reminder x_reminder: o_hasDueDate.getReminders()) {
							x_reminder.setDueDate(new Date(x_dueDate.getTime() + (x_reminder.getDueDate().getTime() - x_currentDate.getTime())));
						}
					}

					boolean x_isActive = o_hasDueDate.isActive();
					boolean dueDateInFuture = isAllDay(x_dueDate) ? !x_dueDate.before(DateUtil.getFirstThing(TODAY)) : x_dueDate.after(new Date());

					if(!x_isActive && dueDateInFuture && showConfirmDialog(o_parentPanel, "Your action is in the future. Set it active ?", "Set active ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getGoogleIcon()) == OK_OPTION) {
						o_hasDueDate.setActive(true);
					}

					if(x_isActive && !dueDateInFuture && showConfirmDialog(o_parentPanel, "Your action is in the past. Set it inactive ?", "Set inactive ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getGoogleIcon()) == OK_OPTION) {
						o_hasDueDate.setActive(false);
					}
				}
			}
		} else {
			if(!x_dueDateSet || showConfirmDialog(o_parentPanel, "Removing Due Date will convert this Action into an Update. Any Reminders will be automatically removed. Continue ?", "Convert to Update ?", OK_CANCEL_OPTION, INFORMATION_MESSAGE, getThreadsIcon()) == OK_OPTION) {
				o_hasDueDate.removeAllReminder();
				o_hasDueDate.setDueDate(null);
			}
		}

		revertDueDateField();
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
