package data;

import java.util.Comparator;

public abstract class ItemView extends ComponentView
{
    protected ItemView(Component p_component)
    {
        super(p_component);
    }
    
    Component[] getComponentSelection()
    {
        return getItemSelection();
    }
    
    protected abstract Item[] getItemSelection();

    public abstract Comparator getSortComparator();
}
