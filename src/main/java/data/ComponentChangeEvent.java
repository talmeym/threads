package data;

public class ComponentChangeEvent {
    public static final int s_CHANGE = 0;
    public static final int s_ADDED = 1;
    public static final int s_REMOVED = 2;

    static final int s_TEXT = 0;
    static final int s_ACTIVE = 1;
    static final int s_DUE_DATE = 2;

    private final Component o_component;
    private final int o_type;
	private final int o_index;

	public ComponentChangeEvent(Component p_source, int p_type, int p_index) {
        o_component = p_source;
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
