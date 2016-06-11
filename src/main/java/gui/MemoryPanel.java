package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MemoryPanel extends JPanel {
	private static Map<Class, Integer> s_memory = new HashMap<Class, Integer>();
	private static Map<Class, List<MemoryPanel>> s_panels = new HashMap<Class, List<MemoryPanel>>();

	public MemoryPanel(LayoutManager p_layoutManager) {
	    super(p_layoutManager);
		Class x_clazz = getClass();

		if(!s_panels.containsKey(x_clazz)) {
			s_panels.put(x_clazz, new ArrayList<MemoryPanel>());
		}

		s_panels.get(x_clazz).add(this);
	}

	public Integer recallValue(int p_defaultValue) {
		Class x_clazz = getClass();
		return s_memory.containsKey(x_clazz) ? s_memory.get(x_clazz) : p_defaultValue;
	}

	public int rememberValue(int p_memory) {
		Class x_clazz = getClass();
		s_memory.put(x_clazz, p_memory);
		updatePanels(s_panels.get(x_clazz), p_memory);
		return p_memory;
	}

	private static void updatePanels(List<MemoryPanel> x_panels, int p_memory) {
		for(MemoryPanel x_panel: x_panels) {
			x_panel.memoryChanged(p_memory);
		}
	}

	protected static void setMemoryValue(Class p_clazz, int p_memoryValue) {
		s_memory.put(p_clazz, p_memoryValue);
		updatePanels(s_panels.get(p_clazz), p_memoryValue);
	}

	protected void memoryChanged(int p_newMemory) {
		// override me
	}
}
