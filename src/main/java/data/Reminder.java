package data;

import java.util.Date;

public class Reminder extends Component implements HasDueDate
{
    private Date o_date;
    
    public Reminder(Item p_item)
    {
        this(new Date(), true, "New Reminder", p_item.getDueDate());
    }
    
    public Reminder(Date p_creationDate, boolean p_active, String p_text, Date p_date)
    {
        super(p_creationDate, p_active, p_text);
        o_date = p_date;
    }
    
    public Item getItem()
    {
        return (Item) getParentComponent();
    }

	@Override
    public Date getDueDate()
    {
        return o_date;
    }

	@Override
    public void setDueDate(Date p_date)
    {
        o_date = p_date;
        changed();
    }
}
