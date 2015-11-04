package data;

import util.TextComparator;

import java.util.Date;

public class Item extends ThreadGroupItem
{
    private Date o_dueDate;
    
    public Item()
    {
        this(new Date(), true, "New Item", null, null);
    }
    
    public Item(Date p_creationDate, boolean p_active, String p_text, Date p_dueDate, Reminder[] p_reminders)
    {
        super(p_creationDate, p_active, p_text, p_reminders, new TextComparator(), null);
		o_dueDate = p_dueDate;
    }
    
    public ThreadGroup getThreadGroup()
    {
        return (ThreadGroup) getParentComponent();
    }

	public int getReminderCount()
	{
		return getComponentCount();
	}

	public Reminder getReminder(int p_index)
	{
		return (Reminder) getComponent(p_index);
	}

	public void addReminder(Reminder p_reminder)
	{
		addComponent(p_reminder);
	}

	public void removeReminder(Reminder p_reminder)
	{
		removeComponent(p_reminder);
	}

	public Date getDueDate()
    {
        return o_dueDate;
    }
    
    public void setDueDate(Date p_dueDate)
    {
        o_dueDate = p_dueDate;
        changed();
    }

	public String getType() {
		return "Item";
	}
}
