package data;

import static data.ComponentChangeEvent.Field.CONTENT;
import static data.ComponentChangeEvent.Field.PARENT;

public class ComponentChangeEvent {

	public enum Field {
		TEXT,
		ACTIVE,
		DUE_DATE,
		PARENT,
		CONTENT
	}

    private final Component o_component;
	private final Field o_field;
	private final Object o_oldValue;
	private final Object o_newValue;

	public ComponentChangeEvent(Component p_source, Field p_field, Object p_oldValue, Object p_newValue) {
        o_component = p_source;
		o_field = p_field;
		this.o_oldValue = p_oldValue;
		this.o_newValue = p_newValue;
	}
    
    public Component getSource() {
        return o_component;
    }

	public Field getField() {
		return o_field;
	}

	public Object getOldValue() {
		return o_oldValue;
	}

	public Object getNewValue() {
		return o_newValue;
	}

	public boolean isAddition() {
		return o_oldValue == null && o_newValue != null;
	}

	public boolean isRemoval() {
		return o_oldValue != null && o_newValue == null;
	}

	public boolean isChange() {
		return o_oldValue != null && o_newValue != null;
	}

	public boolean isValueChange() {
		return o_field != CONTENT && o_field != PARENT && isChange();
	}

	public boolean isComponentMove() {
		return o_field == PARENT && isAddition();
	}

	public boolean isContentRemoved() {
		return o_field == CONTENT && isRemoval();
	}

	public boolean isComponentRemoved() {
		return o_field == PARENT && isRemoval();
	}
}
