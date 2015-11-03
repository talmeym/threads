package data;

import java.util.Date;

public class Item extends Component
{
    private Deadline o_deadline;
    
    public Item()
    {
        this(new Date(), true, "New Item", null);
    }
    
    public Item(Date p_creationDate, boolean p_active, String p_text, Deadline p_deadline)
    {
        super(p_creationDate, p_active, p_text);        
        o_deadline = p_deadline;
        
        if(o_deadline != null)
        {
            startObserve(o_deadline);
            o_deadline.setParentComponent(this);
        }
    }
    
    public Thread getThread()
    {
        return (Thread) getParentComponent();
    }
    
    public Deadline getDeadline()
    {
        return o_deadline;
    }
    
    public void setDeadline(Deadline p_deadline)
    {
        if(p_deadline == null)
        {
            stopObserve(o_deadline);
        }
        
        o_deadline = p_deadline;
        
        if(o_deadline != null)
        {
            o_deadline.setParentComponent(this);
            startObserve(o_deadline);
        }

        changed();
    }
}
