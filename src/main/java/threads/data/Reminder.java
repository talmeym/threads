package threads.data;

import threads.util.DateUtil;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static threads.data.ComponentChangeEvent.Field.DUE_DATE;
import static threads.data.ComponentChangeEvent.Field.NOTES;

public class Reminder extends Component implements HasDueDate
{
    private Date o_dueDate;
	private String o_notes;

	public Reminder(String p_text, Date p_dueDate) {
        this(UUID.randomUUID(), new Date(), new Date(), true, p_text, p_dueDate, null, null);
    }
    
    public Reminder(UUID id, Date p_creationDate, Date p_modifiedDate, boolean p_active, String p_text, Date p_dueDate, String p_notes, File p_docFolder) {
        super(id, p_creationDate, p_modifiedDate, p_active, p_text, p_docFolder);
        o_dueDate = p_dueDate;
		o_notes = p_notes;
    }

	public Reminder(Reminder p_reminder, boolean p_addCopyText) {
		this(UUID.randomUUID(), new Date(), new Date(), p_reminder.isActive(), (p_addCopyText ? "Copy of " : "") + p_reminder.getText(), p_reminder.getDueDate(), p_reminder.getNotes(), p_reminder.getDocFolder());
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
		return isActive() && DateUtil.isDue(o_dueDate);
	}

	@Override
	public ComponentType getType() {
		return ComponentType.Reminder;
	}

	@Override
	public List<Component> search(Search p_search) {
		return p_search.check(this) ? singletonList(this) : emptyList();
	}

	@Override
	public Component duplicate(boolean p_addCopyText) {
		Reminder x_reminder = new Reminder(this, p_addCopyText);
		getParentItem().addReminder(x_reminder);
		return x_reminder;
	}

	@Override
	public Component component() {
		return this;
	}
}
