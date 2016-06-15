package data;

import util.*;

import java.util.*;

public class Item extends ThreadItem<Reminder> implements HasDueDate {
    private Date o_dueDate;
    
    public Item(String text) {
        this(UUID.randomUUID(), new Date(), true, text, null, null);
    }
    
    public Item(UUID id, Date p_creationDate, boolean p_active, String p_text, Date p_dueDate, List<Reminder> p_reminders) {
        super(id, p_creationDate, p_active, p_text, p_reminders, new TextComparator<Reminder>(), null);
		o_dueDate = p_dueDate;
    }
    
    public Thread getParentThread() {
        return (Thread) getParentComponent();
    }

	public int getReminderCount() {
		return getComponentCount();
	}

	public Reminder getReminder(int p_index) {
		return getComponent(p_index);
	}

	public void addReminder(Reminder p_reminder) {
		addComponent(p_reminder);
	}

	public void removeReminder(Reminder p_reminder) {
		removeComponent(p_reminder);
	}

	@Override
	public Date getDueDate() {
        return o_dueDate;
    }

	@Override
    public void setDueDate(Date p_dueDate) {
        o_dueDate = p_dueDate;
        changed();
    }

	@Override
	public boolean isDue() {
		if(isActive() && o_dueDate != null) {
			return DateUtil.isAllDay(o_dueDate) ? o_dueDate.before(DateUtil.getFirstThingToday()) : o_dueDate.before(new Date());
		}

		return false;
	}

	public String getType() {
		return o_dueDate != null ? "Action" : "Update";
	}
}
