package threads.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static threads.data.ComponentChangeEvent.Field.DUE_DATE;
import static threads.data.ComponentChangeEvent.Field.NOTES;
import static threads.util.DateUtil.*;

public class Item extends ThreadItem<Reminder> implements HasDueDate {
    private Date o_dueDate;
	private String o_notes;

    public Item(String text, Date p_dueDate) {
        this(UUID.randomUUID(), new Date(), new Date(), true, text, p_dueDate, null, null, null);
    }
    
    public Item(UUID id, Date p_creationDate, Date p_modifiedDate, boolean p_active, String p_text, Date p_dueDate, String p_notes, List<Reminder> p_reminders, File p_docFolder) {
        super(id, p_creationDate, p_modifiedDate, p_active, p_text, p_reminders, (obj1, obj2) -> obj1.getDueDate().compareTo(obj2.getDueDate()), p_docFolder);
		o_dueDate = p_dueDate;
		o_notes = p_notes;
    }

	public Item(Item p_item, boolean p_addCopyText) {
		this(UUID.randomUUID(), new Date(), new Date(), p_item.isActive(), (p_addCopyText ? "Copy of " : "") + p_item.getText(), p_item.getDueDate(), p_item.getNotes(), new ArrayList<>(), p_item.getDocFolder());
		p_item.getReminders().forEach(x_reminder -> addReminder(new Reminder(x_reminder, false)));
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

		if(!same(x_oldValue, p_dueDate)) {
			o_dueDate = p_dueDate;
			changed(DUE_DATE, x_oldValue, p_dueDate);
		}
    }

	public String getNotes() {
		return o_notes;
	}

	public void setNotes(String p_notes) {
		String x_oldValue = o_notes;

		if(!same(x_oldValue, p_notes)) {
			o_notes = p_notes;
			changed(NOTES, x_oldValue, p_notes);
		}
	}

	@Override
	public boolean isDue() {
		return isActive() && o_dueDate != null && (isAllDay(o_dueDate) ? o_dueDate.before(getFirstThing(TODAY)) : o_dueDate.before(new Date()));

	}

	@Override
	public ComponentType getType() {
		return o_dueDate != null ? ComponentType.Action : ComponentType.Update;
	}

	@Override
	public Component duplicate(boolean p_addCopyText) {
    	Item x_item = new Item(this, p_addCopyText);
    	getParentThread().addThreadItem(x_item);
		return x_item;
	}
}
