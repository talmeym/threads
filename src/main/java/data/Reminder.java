package data;

import java.util.*;

public class Reminder extends Component implements HasDueDate
{
    private Date o_dueDate;
    
    public Reminder(Item p_item) {
        this(UUID.randomUUID(), new Date(), true, "New Reminder", p_item.getDueDate());
    }
    
    public Reminder(UUID id, Date p_creationDate, boolean p_active, String p_text, Date p_date) {
        super(id, p_creationDate, p_active, p_text);
        o_dueDate = p_date;
    }
    
    public Item getItem() {
        return (Item) getParentComponent();
    }

	@Override
    public Date getDueDate() {
        return o_dueDate;
    }

	@Override
    public void setDueDate(Date p_date) {
        o_dueDate = p_date;
        changed();
    }

	@Override
	public boolean isDue() {
		return o_dueDate != null && o_dueDate.before(new Date());
	}
}
