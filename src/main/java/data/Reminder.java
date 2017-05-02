package data;

import util.DateUtil;

import java.io.File;
import java.util.*;

import static data.ComponentChangeEvent.s_DUE_DATE;
import static util.DateUtil.TODAY;

public class Reminder extends Component implements HasDueDate
{
    private Date o_dueDate;
    
    public Reminder(Item p_item) {
        this(UUID.randomUUID(), new Date(), new Date(), true, "New Reminder", p_item.getDueDate(), null);
    }
    
    public Reminder(UUID id, Date p_creationDate, Date p_modifiedDate, boolean p_active, String p_text, Date p_dueDate, File p_docFolder) {
        super(id, p_creationDate, p_modifiedDate, p_active, p_text, p_docFolder);
        o_dueDate = p_dueDate;
    }

	public Reminder(Reminder p_reminder, boolean p_addCopyText) {
		this(UUID.randomUUID(), new Date(), new Date(), p_reminder.isActive(), (p_addCopyText ? "Copy of " : "") + p_reminder.getText(), p_reminder.getDueDate(), p_reminder.getDocFolder());
	}

    public Item getParentItem() {
        return (Item) getParentComponent();
    }

	@Override
    public Date getDueDate() {
        return o_dueDate;
    }

	@Override
    public void setDueDate(Date p_date) {
        o_dueDate = p_date;
        changed(s_DUE_DATE);
		modified();
    }

	@Override
	public boolean isDue() {
		if(isActive()) {
			return DateUtil.isAllDay(o_dueDate) ? o_dueDate.before(DateUtil.getFirstThing(TODAY)) : o_dueDate.before(new Date());
		}

		return false;

	}

	@Override
	public ComponentType getType() {
		return ComponentType.Reminder;
	}

	@Override
	public Component findComponent(UUID p_id) {
		if(getId().equals(p_id)) {
			return this;
		}

		return null;
	}
}
