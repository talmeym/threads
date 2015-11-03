package data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemHelper
{
    public static List getReminder(Item p_item)
    {
        List x_dueActiveReminders = new ArrayList();
        
        if(p_item.getDeadline() != null)
        {
            Deadline x_deadline = p_item.getDeadline();
            
            for(int i = 0; i < x_deadline.getReminderCount(); i++)
            {
                Reminder x_reminder = x_deadline.getReminder(i);
                
                if(x_reminder.isActive() && x_reminder.getDate().before(new Date()))
                {
                    x_dueActiveReminders.add(x_reminder);
                }
            }
        }
                
        return x_dueActiveReminders;
    }
}
