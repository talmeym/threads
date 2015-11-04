package data;

import java.util.*;

abstract class ComponentView
{
    private final Component o_component;
    
    protected ComponentView(Component p_component)
    {
        o_component = p_component;
    }
    
    protected Component getComponent()
    {
        return o_component;
    }
    
    abstract Component[] getComponentSelection();
    
    protected abstract Comparator getSortComparator();
    
    protected int getComponentCount()
    {
        return getComponentSelection().length;
    }
    
    protected Component getComponent(int p_index)
    {
        Component[] x_components = getComponentSelection();
        List x_compList = Arrays.asList(x_components);
        Collections.sort(x_compList);
        return (Component) x_compList.get(p_index);
    }
}
