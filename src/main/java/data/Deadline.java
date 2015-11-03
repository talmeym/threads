package data;

import java.util.Date;

import util.DueDateComparator;

public class Deadline extends CollectionComponent
{
    private Date o_dueDate;
    
    public Deadline(Date p_creationDate, boolean p_active, String p_text, Date p_dueDate, Reminder[] p_reminders)
    {
        super(p_creationDate, p_active, p_text, p_reminders, new DueDateComparator());
        o_dueDate = p_dueDate;
    }
    
    public Date getDueDate()
    {
        return o_dueDate;
    }
    
    public int getReminderCount()
    {
        return getComponentCount();
    }

    public Reminder getReminder(int p_index)
    {
        return (Reminder) getComponent(p_index);
    }
    
    public Reminder[] getReminders()
    {
        return (Reminder[]) getComponents().toArray(new Reminder[0]);
    }
    
    public void setDueDate(Date p_dueDate)
    {
        o_dueDate = p_dueDate;
        changed();
    }
    
    public void addReminder(Reminder p_reminder)
    {   
        addComponent(p_reminder);
    }
    
    public void removeReminder(Reminder p_reminder)
    {
        removeComponent(p_reminder);
    }
}
