package data;

import java.util.Date;

public class Reminder extends Component
{
    private Date o_date;
    
    public Reminder(Item p_item)
    {
        this(new Date(), true, "New Reminder", p_item.getDeadline().getDueDate());
    }
    
    public Reminder(Date p_creationDate, boolean p_active, String p_text, Date p_date)
    {
        super(p_creationDate, p_active, p_text);
        o_date = p_date;
    }
    
    public Deadline getDeadline()
    {
        return (Deadline) getParentComponent();
    }
    
    public Item getItem()
    {
        return (Item) getParentComponent().getParentComponent();
    }
    
    public Date getDate()
    {
        return o_date;
    }
    
    public void setDate(Date p_date)
    {
        o_date = p_date;
        changed();
    }
}
