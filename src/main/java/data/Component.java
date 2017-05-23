package data;

import java.io.File;
import java.util.*;

import static data.ComponentChangeEvent.Field;
import static data.ComponentChangeEvent.Field.*;

public abstract class Component {
	private List<ComponentChangeListener> o_changeListeners = new ArrayList<>();

	private final UUID o_id;
	private final Date o_creationDate;
	private Date o_modifiedDate;
    private boolean o_active;
    private String o_text;
    private Component o_parentComponent;
	private File o_docFolder;

	Component(UUID p_id, Date p_creationDate, Date p_modifiedDate, boolean p_activeFlag, String p_text, File p_docFolder) {
		o_id = p_id;
		o_creationDate = p_creationDate;
		o_modifiedDate = p_modifiedDate;
        o_active = p_activeFlag;
        o_text = p_text;
		o_docFolder = p_docFolder;
    }

	public UUID getId() {
		return o_id;
	}

    public Date getCreationDate() {
        return o_creationDate;
    }
    
	public Date getModifiedDate() {
		return o_modifiedDate;
	}

	void modified() {
		o_modifiedDate = new Date();
	}

    public boolean isActive() {
        return o_active;
    }

    public String getText() {
        return o_text;
    }

    public Component getParentComponent() {
        return o_parentComponent;
    }

    public void setActive(boolean p_active) {
        boolean x_oldValue = o_active;

		if(!same(x_oldValue, p_active)) {
			o_active = p_active;
			changed(ACTIVE, x_oldValue, p_active);
		}
    }

    public void setText(String p_text) {
        String x_oldValue = o_text;

		if(!same(x_oldValue, p_text)) {
			o_text = p_text;
			changed(TEXT, x_oldValue, p_text);
		}
    }

	void unsetParentComponent() {
		Component x_oldValue = o_parentComponent;
		o_parentComponent = null;
		changed(new ComponentChangeEvent(this, PARENT, x_oldValue, null));
	}

    void setParentComponent(Component p_parentComponent) {
        o_parentComponent = p_parentComponent;
		changed(new ComponentChangeEvent(this, PARENT, null, p_parentComponent));
    }

	public File getDocFolder() {
		return o_docFolder;
	}

	public void setDocFolder(File p_docFolder) {
		o_docFolder = p_docFolder;
	}

    public String toString() {
        return o_text;
    }

	public abstract ComponentType getType();

	public abstract List<Component> search(Search p_search);

	public void addComponentChangeListener(ComponentChangeListener p_listener) {
		o_changeListeners.add(p_listener);
	}

	void removeComponentChangeListener(ComponentChangeListener p_listener) {
		o_changeListeners.remove(p_listener);
	}

	void changed(Field p_field, Object p_oldValue, Object p_newValue) {
		modified();
		changed(new ComponentChangeEvent(this, p_field, p_oldValue, p_newValue));
	}

	void changed(ComponentChangeEvent p_event) {
		o_changeListeners.forEach(x_listener -> x_listener.componentChanged(p_event));
	}

	public List<Component> getHierarchy() {
		if(o_parentComponent == null) {
			return new ArrayList<>(Collections.singletonList(this));
		}

		List<Component> x_parents = o_parentComponent.getHierarchy();
		x_parents.add(this);
		return x_parents;
	}

	static boolean same(Object obj1, Object obj2) {
		return (obj1 == null) == (obj2 == null) && (obj1 == null || obj1.equals(obj2));
	}
}
