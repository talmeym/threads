package data;

public class ComponentMoveEvent {
	private Component o_component;

	ComponentMoveEvent(Component p_component) {
		o_component = p_component;
	}

	public Component getSource() {
		return o_component;
	}
}
