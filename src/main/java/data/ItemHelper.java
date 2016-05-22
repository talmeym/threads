package data;

import java.util.*;

public class ItemHelper {
	public static List<Reminder> getDueReminders(Item p_item) {
        List<Reminder> x_dueActiveReminders = new ArrayList<Reminder>();

        if(p_item.isActive() && p_item.getDueDate() != null) {
            for(int i = 0; i < p_item.getReminderCount(); i++) {
                Reminder x_reminder = p_item.getReminder(i);

                if(x_reminder.isActive() && x_reminder.getDueDate().before(new Date())) {
                    x_dueActiveReminders.add(x_reminder);
                }
            }
        }

        return x_dueActiveReminders;
    }
}
