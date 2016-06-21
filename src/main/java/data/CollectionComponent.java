package data;

import java.io.File;
import java.util.*;

public abstract class CollectionComponent <CONTENTS extends Component> extends Component {
    private final List<CONTENTS> o_components = new ArrayList<CONTENTS>();
    private final Comparator<CONTENTS> o_comparator;
    
    CollectionComponent(UUID id, Date p_creationDate, boolean p_active, String p_text, List<CONTENTS> p_components, Comparator<CONTENTS> p_comparator, File p_docFolder) {
        super(id, p_creationDate, p_active, p_text, p_docFolder);
        o_comparator = p_comparator;
        
        if(p_components != null) {
            for(CONTENTS x_component: p_components) {
                addComponent(x_component);
            }
        }
    }
    
    protected int getComponentCount() {
        return o_components.size();
    }
    
    protected CONTENTS getComponent(int p_index) {
		return o_components.get(p_index);
    }
    
    protected void addComponent(CONTENTS p_component) {
        p_component.setParentComponent(this);
        observe(p_component);
        o_components.add(p_component);
        Collections.sort(o_components, o_comparator);
		int index = o_components.indexOf(p_component);
		changed(new ObservableChangeEvent(this, ObservableChangeEvent.s_ADDED, index));
    }

    protected void removeComponent(CONTENTS p_component) {
		p_component.setParentComponent(null);
        unobserve(p_component);
		int index = o_components.indexOf(p_component);
        o_components.remove(p_component);
        Collections.sort(o_components, o_comparator);
		changed(new ObservableChangeEvent(this, ObservableChangeEvent.s_REMOVED, index));
    }
}
