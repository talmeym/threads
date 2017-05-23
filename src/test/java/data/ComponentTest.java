package data;

import data.ComponentChangeEvent.Field;
import org.junit.Test;

import java.util.*;

import static data.ComponentChangeEvent.Field.*;
import static data.ComponentType.Thread;
import static org.junit.Assert.*;

public class ComponentTest {
	@Test
	public void testGetHierarchy() {
		MyComponent comp1 = new MyComponent("top");
		MyComponent comp2 = new MyComponent("middle");
		MyComponent comp3 = new MyComponent("bottom");

		comp1.addComponent(comp2);
		comp2.addComponent(comp3);

		List<Component> parents = comp3.getHierarchy();

		assertEquals(3, parents.size());
		assertEquals(comp1, parents.get(0));
		assertEquals(comp2, parents.get(1));
		assertEquals(comp3, parents.get(2));
	}

	private static class MyComponent extends CollectionComponent<MyComponent> {
		MyComponent(String p_text) {
			super(UUID.randomUUID(), new Date(), new Date(), true, p_text, null, (o1, o2) -> o1.getText().compareTo(o2.getText()), null);
		}

		@Override
		public ComponentType getType() {
			return Thread;
		}

		@Override
		public List<Component> search(Search p_search) {
			return null;
		}
	}

	@Test
	public void testAddRemoveComponent() {
		ChangeAsserter x_changeAsserter = new ChangeAsserter();

		MyComponent comp1 = new MyComponent("C1");
		x_changeAsserter.addListener(comp1);

		MyComponent comp2 = new MyComponent("C2");
		x_changeAsserter.addListener(comp2);

		comp1.addComponent(comp2);
		comp1.removeComponent(comp2);

		x_changeAsserter.assertUpdate(comp2, comp2, PARENT, null, comp1);
		x_changeAsserter.assertUpdate(comp1, comp1, CONTENT, null, 0);
		x_changeAsserter.assertUpdate(comp2, comp2, PARENT, comp1, null);
		x_changeAsserter.assertUpdate(comp1, comp2, PARENT, comp1, null);
		x_changeAsserter.assertUpdate(comp1, comp1, CONTENT, 0, null);
		x_changeAsserter.assertDone();
	}

	@Test
	public void testRemoveComponent() {
		ChangeAsserter x_changeAsserter = new ChangeAsserter();

		MyComponent comp1 = new MyComponent("C1");
		MyComponent comp2 = new MyComponent("C2");
		comp1.addComponent(comp2);

		x_changeAsserter.addListener(comp1);
		x_changeAsserter.addListener(comp2);

		comp1.removeComponent(comp2);

		x_changeAsserter.assertUpdate(comp1, comp2, PARENT, comp1, null);
		x_changeAsserter.assertUpdate(comp2, comp2, PARENT, comp1, null);
		x_changeAsserter.assertUpdate(comp1, comp1, CONTENT, 0, null);
		x_changeAsserter.assertDone();
	}

	private class ChangeAsserter {
		private List<Component> o_components = new ArrayList<>();
		private List<ComponentChangeEvent> o_events = new ArrayList<>();
		private int o_index;

		void addListener(Component o_component) {
			o_component.addComponentChangeListener(e -> {
				o_components.add(o_component);
				o_events.add(e);
			});
		}

		void assertUpdate(Component p_updated, Component p_source, Field p_field, Object p_oldValue, Object p_newValue) {
			int x_index = o_index++;
			Component x_component = o_components.get(x_index);
			ComponentChangeEvent x_event = o_events.get(x_index);
			assertTrue("[" + x_index + "] updated component", x_component == p_updated);
			assertTrue("[" + x_index + "] source component", p_source == x_event.getSource());
			assertEquals("[" + x_index + "] field", p_field, x_event.getField());
			assertEquals("[" + x_index + "] old val", p_oldValue, x_event.getOldValue());
			assertEquals("[" + x_index + "] new val", p_newValue, x_event.getNewValue());
		}

		void assertDone() {
			assertEquals(o_index, o_components.size());
			assertEquals(o_index, o_events.size());
		}
	}
}
