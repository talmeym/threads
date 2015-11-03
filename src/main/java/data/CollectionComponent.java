package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CollectionComponent extends Component
{
    private final List o_components;
    private final Comparator o_comparator;
    
    CollectionComponent(Date p_creationDate, boolean p_active, String p_text, Component[] p_components, Comparator p_comparator)
    {
        super(p_creationDate, p_active, p_text);
        o_components = new ArrayList(); 
        o_comparator = p_comparator;
        
        if(p_components != null)
        {
            o_components.addAll(Arrays.asList(p_components));
            
            for(int i = 0; i < p_components.length; i++)
            {
                p_components[i].setParentComponent(this);
                startObserve(p_components[i]);
            }
            
            Collections.sort(o_components, o_comparator);
        }
    }
    
    protected List getComponents()
    {
        return o_components;
    }
    
    protected int getComponentCount()
    {
        return o_components.size();
    }
    
    protected Component getComponent(int p_index)
    {
        if(p_index > -1 && p_index < o_components.size())
        {
            return (Component) o_components.get(p_index);
        }
        
        throw new IllegalArgumentException("Invalid index: " + p_index);
    }
    
    protected void addComponent(Component p_component)
    {
        p_component.setParentComponent(this);
        startObserve(p_component);
        o_components.add(p_component);
        Collections.sort(o_components, o_comparator);
        changed(new ObservableChangeEvent(this, ObservableChangeEvent.s_ADDED));
    }

    protected void removeComponent(Component p_component)
    {        
        p_component.setParentComponent(null);
        stopObserve(p_component);
        o_components.remove(p_component);
        Collections.sort(o_components, o_comparator);
        changed(new ObservableChangeEvent(this, ObservableChangeEvent.s_REMOVED));
    }
}
