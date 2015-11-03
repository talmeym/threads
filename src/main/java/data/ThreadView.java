package data;

import java.util.Comparator;

public abstract class ThreadView extends ComponentView
{
    protected ThreadView(Component p_component)
    {
        super(p_component);
    }
    
    Component[] getComponentSelection()
    {
        return getThreadSelection();
    }
    
    protected abstract Thread[] getThreadSelection();

    protected abstract Comparator getSortComparator();
}
