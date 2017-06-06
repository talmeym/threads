package threads.data;

import static threads.data.ComponentChangeEvent.Field.CONTENT;
import static threads.data.ComponentChangeEvent.Field.PARENT;

public class ComponentChangeEvent {

	public enum Field {
		TEXT("Name"),
		ACTIVE("Is Active"),
		DUE_DATE("Due Date"),
		NOTES("Notes"),
		PARENT("NotUsed"),
		CONTENT("NotUsed");

		private String o_displayString;

		Field(String p_displayString) {
			this.o_displayString = p_displayString;
		}

		public String toString() {
			return o_displayString;
		}
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

	private boolean isAddition() {
		return o_oldValue == null && o_newValue != null;
	}

	private boolean isRemoval() {
		return o_oldValue != null && o_newValue == null;
	}

	private boolean isChange() {
		return o_oldValue != null && o_newValue != null;
	}

	public boolean isValueChange() {
		return o_field != CONTENT && o_field != PARENT;
	}

	public boolean isComponentAdded() {
		return o_field == PARENT && isAddition();
	}

	public boolean isContentRemoved() {
		return o_field == CONTENT && isRemoval();
	}

	public boolean isComponentRemoved() {
		return o_field == PARENT && isRemoval();
	}
}
