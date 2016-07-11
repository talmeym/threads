package data;

public class ComponentChangeEvent {
    public static final int s_CHANGE = 0;
    public static final int s_ADDED = 1;
    public static final int s_REMOVED = 2;

    private final Component o_component;
    private final int o_type;
	private final int o_index;

	public ComponentChangeEvent(Component p_component) {
		o_component = p_component;
		o_type = s_CHANGE;
		o_index = -1;
	}

	public ComponentChangeEvent(Component p_component, int p_type, int p_index) {
        o_component = p_component;
        o_type = p_type;
		this.o_index = p_index;
	}
    
    public Component getSource() {
        return o_component;
    }
    
    public int getType() {
        return o_type;
    }

	public int getIndex() {
		return o_index;
	}
}
