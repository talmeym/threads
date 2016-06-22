package data;

import util.*;

import java.io.File;
import java.util.*;

public class Item extends ThreadItem<Reminder> implements HasDueDate {
    private Date o_dueDate;
    
    public Item(String text) {
        this(UUID.randomUUID(), new Date(), new Date(), true, text, null, null, null);
    }
    
    public Item(UUID id, Date p_creationDate, Date p_modifledDate, boolean p_active, String p_text, Date p_dueDate, List<Reminder> p_reminders, File p_docFolder) {
        super(id, p_creationDate, p_modifledDate, p_active, p_text, p_reminders, new TextComparator<Reminder>(), p_docFolder);
		o_dueDate = p_dueDate;
    }

	public Item(Item p_item, boolean p_addCopyText) {
		this(UUID.randomUUID(), new Date(), new Date(), p_item.isActive(), (p_addCopyText ? "Copy of " : "") + p_item.getText(), p_item.getDueDate(), new ArrayList<Reminder>(), p_item.getDocFolder());

		for(int i = 0; i < p_item.getReminderCount(); i++) {
			addReminder(new Reminder(p_item.getReminder(i), false));
		}
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

	public void removeAllReminder() {
		removeAllComponents();
	}

	@Override
	public Date getDueDate() {
        return o_dueDate;
    }

	@Override
    public void setDueDate(Date p_dueDate) {
        o_dueDate = p_dueDate;
        changed();
		modified();
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
