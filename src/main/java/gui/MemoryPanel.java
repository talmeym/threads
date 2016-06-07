package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MemoryPanel extends JPanel {
	private static Map<String, Integer> s_memory = new HashMap<String, Integer>();
	private static Map<String, List<MemoryPanel>> s_panels = new HashMap<String, List<MemoryPanel>>();

	private final String o_category;

	public MemoryPanel(LayoutManager p_layoutManager, String p_category) {
	    super(p_layoutManager);
		o_category = p_category;

		if(!s_panels.containsKey(p_category)) {
			s_panels.put(p_category, new ArrayList<MemoryPanel>());
		}

		s_panels.get(p_category).add(this);
	}

	public Integer getMemoryValue(int p_defaultValue) {
		return s_memory.containsKey(o_category) ? s_memory.get(o_category) : p_defaultValue;
	}

	public int setMemoryValue(int p_memory) {
		s_memory.put(o_category, p_memory);

		List<MemoryPanel> x_panels = s_panels.get(o_category);

		for(MemoryPanel x_panel: x_panels) {
			if(x_panel != this) {
				x_panel.memoryChanged(p_memory);
			}
		}

		return p_memory;
	}

	protected void memoryChanged(int p_newMemory) {
		// do nothing by default
	}
}
