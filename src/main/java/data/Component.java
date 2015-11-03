package data;

import java.util.Date;

public abstract class Component extends ObservableObserver
{
    private final Date o_creationDate;
    
    private boolean o_active;
    
    private String o_text;
    
    private Component o_parentComponent;
    
    Component(Date p_creationDate, boolean p_activeFlag, String p_text)
    {
        o_creationDate = p_creationDate;
        o_active = p_activeFlag;
        o_text = p_text;
    }
    
    public Date getCreationDate()
    {
        return o_creationDate;
    }
    
    public boolean isActive()
    {
        return o_active;
    }
    
    public String getText()
    {
        return o_text;
    }
    
    public Component getParentComponent()
    {
        return o_parentComponent;
    }
    
    public void setActive(boolean p_active)
    {
        o_active = p_active;
        changed();
    }

    public void setText(String p_text)
    {
        o_text = p_text;
        changed();
    }
    
    void setParentComponent(Component p_parentComponent)
    {
        o_parentComponent = p_parentComponent;
    }
    
    public String toString()
    {
        return o_text;
    }
}
