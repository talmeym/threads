package data;

import java.io.File;
import java.util.*;

import static data.ComponentChangeEvent.Field.DUE_DATE;
import static java.util.Collections.*;
import static util.DateUtil.*;

public class Reminder extends Component implements HasDueDate
{
    private Date o_dueDate;
    
    public Reminder(String p_text, Date p_dueDate) {
        this(UUID.randomUUID(), new Date(), new Date(), true, p_text, p_dueDate, null);
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
    public void setDueDate(Date p_dueDate) {
        Date x_oldValue = o_dueDate;

		if(!same(x_oldValue, p_dueDate)) {
			o_dueDate = p_dueDate;
			changed(DUE_DATE, x_oldValue, p_dueDate);
		}
    }

	@Override
	public boolean isDue() {
		return isActive() && (isAllDay(o_dueDate) ? o_dueDate.before(getFirstThing(TODAY)) : o_dueDate.before(new Date()));
	}

	@Override
	public ComponentType getType() {
		return ComponentType.Reminder;
	}

	@Override
	public List<Component> search(Search p_search) {
		return p_search.check(this) ? singletonList(this) : emptyList();
	}
}
