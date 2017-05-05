package data;

import org.junit.Test;
import util.TextComparator;

import java.util.*;

import static data.ComponentType.Thread;
import static org.junit.Assert.assertEquals;

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
			super(UUID.randomUUID(), new Date(), new Date(), true, p_text, null, new TextComparator<>(), null);
		}

		@Override
		public ComponentType getType() {
			return Thread;
		}

		@Override
		public Component findComponent(UUID p_id) {
			return null;
		}
	}
}
