package data;

import util.DateUtil;

import java.io.File;
import java.util.*;

import static data.ComponentChangeEvent.Field.DUE_DATE;
import static util.DateUtil.TODAY;

public class Item extends ThreadItem<Reminder> implements HasDueDate {
    private Date o_dueDate;
    
    public Item(String text, Date p_dueDate) {
        this(UUID.randomUUID(), new Date(), new Date(), true, text, p_dueDate, null, null);
    }
    
    public Item(UUID id, Date p_creationDate, Date p_modifledDate, boolean p_active, String p_text, Date p_dueDate, List<Reminder> p_reminders, File p_docFolder) {
        super(id, p_creationDate, p_modifledDate, p_active, p_text, p_reminders, (obj1, obj2) -> obj1.getDueDate().compareTo(obj2.getDueDate()), p_docFolder);
		o_dueDate = p_dueDate;
    }

	public Item(Item p_item, boolean p_addCopyText) {
		this(UUID.randomUUID(), new Date(), new Date(), p_item.isActive(), (p_addCopyText ? "Copy of " : "") + p_item.getText(), p_item.getDueDate(), new ArrayList<>(), p_item.getDocFolder());
		p_item.getReminders().forEach(x_reminder -> addReminder(new Reminder(x_reminder, false)));
	}

	public int getReminderCount() {
		return getComponentCount();
	}

	public Reminder getReminder(int p_index) {
		return getComponent(p_index);
	}

	public List<Reminder> getReminders() {
		return new ArrayList<>(getComponents());
	}

	public void addReminder(Reminder... p_reminder) {
		for(Reminder x_reminder: p_reminder) {
			addComponent(x_reminder);
		}
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
        Date x_oldValue = o_dueDate;
		o_dueDate = p_dueDate;
        changed(DUE_DATE, x_oldValue, p_dueDate);
		modified();
    }

	@Override
	public boolean isDue() {
		return isActive() && o_dueDate != null && (DateUtil.isAllDay(o_dueDate) ? o_dueDate.before(DateUtil.getFirstThing(TODAY)) : o_dueDate.before(new Date()));

	}

	@Override
	public ComponentType getType() {
		return o_dueDate != null ? ComponentType.Action : ComponentType.Update;
	}
}
