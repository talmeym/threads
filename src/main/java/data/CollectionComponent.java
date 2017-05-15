package data;

import java.io.File;
import java.util.*;

import static data.ComponentChangeEvent.*;
import static java.util.Collections.sort;

abstract class CollectionComponent <CONTENTS extends Component> extends Component implements ComponentChangeListener {
    private final List<CONTENTS> o_components = new ArrayList<>();
    private final Comparator<CONTENTS> o_comparator;
    
    CollectionComponent(UUID id, Date p_creationDate, Date p_modifiedDate, boolean p_active, String p_text, List<CONTENTS> p_components, Comparator<CONTENTS> p_comparator, File p_docFolder) {
        super(id, p_creationDate, p_modifiedDate, p_active, p_text, p_docFolder);
        o_comparator = p_comparator;
        
        if(p_components != null) {
			p_components.forEach(this::addComponent);
        }
    }
    
    int getComponentCount() {
        return o_components.size();
    }
    
    protected CONTENTS getComponent(int p_index) {
		return o_components.get(p_index);
    }

    List<CONTENTS> getComponents() {
    	// defensive copy
    	return new ArrayList<>(o_components);
	}

    void addComponent(CONTENTS p_component) {
        p_component.setParentComponent(this);
        p_component.addComponentChangeListener(this);
        o_components.add(p_component);
        sort(o_components, o_comparator);
		int index = o_components.indexOf(p_component);
		changed(new ComponentChangeEvent(this, s_CONTENT_ADDED, index));
    }

    void removeComponent(CONTENTS p_component) {
		p_component.unsetParentComponent();
        p_component.removeComponentChangeListener(this);
		int index = o_components.indexOf(p_component);
        o_components.remove(p_component);
		changed(new ComponentChangeEvent(this, s_CONTENT_REMOVED, index));
    }

    void removeAllComponents() {
		Iterator<CONTENTS> iterator = o_components.iterator();
		int index = 0;

		while(iterator.hasNext()) {
			Component x_component = iterator.next();
			x_component.unsetParentComponent();
			x_component.removeComponentChangeListener(this);
			iterator.remove();
			changed(new ComponentChangeEvent(this, s_CONTENT_REMOVED, index++));
		}
    }

	public List<Component> search(Search p_search) {
		p_search.check(this);

		for(int i = 0; i < getComponentCount(); i++) {
			getComponent(i).search(p_search);
		}

		return p_search.getResults();
	}

	@Override
	public void componentChanged(ComponentChangeEvent p_cce) {
		changed(p_cce);
		Component x_source = p_cce.getSource();

		if(o_components.contains(x_source) && p_cce.getType() == s_CHANGED) {
			int p_beforeIndex = o_components.indexOf(x_source);
			sort(o_components, o_comparator);
			int p_afterIndex = o_components.indexOf(x_source);

			if(p_afterIndex != p_beforeIndex) {
				changed(new ComponentChangeEvent(this, s_CONTENT_REMOVED, p_beforeIndex));
				changed(new ComponentChangeEvent(this, s_CONTENT_ADDED, p_afterIndex));
			}
		}
	}
}
