package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MemoryPanel extends JPanel {
	public static final String s_TAB_INDEX = "tabindex";
	public static final String s_MONTH = "month";
	public static final String s_DIVLOC = "divloc";
	public static final String s_NAVDIVLOC = "navdivloc";

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
		if(x_panels != null) {
			for(MemoryPanel x_panel: x_panels) {
				x_panel.memoryChanged(p_memory);
			}
		}
	}

	protected static void setMemoryValue(Class p_clazz, int p_memoryValue) {
		s_memory.put(p_clazz, p_memoryValue);
		updatePanels(s_panels.get(p_clazz), p_memoryValue);
	}

	protected static Integer getMemoryValue(Class p_clazz) {
		return s_memory.get(p_clazz);
	}

	protected void memoryChanged(int p_newMemory) {
		// override me
	}

	public static void applySettingsFromProperties(Properties p_properties) {
		String x_tabIndex = p_properties.getProperty(s_TAB_INDEX);
		String x_month = p_properties.getProperty(s_MONTH);
		String x_itemDivLoc = p_properties.getProperty(s_DIVLOC);
		String x_navDivLoc = p_properties.getProperty(s_NAVDIVLOC);

		if(x_tabIndex != null) {
			setMemoryValue(ThreadPanel.class, Integer.parseInt(x_tabIndex));
		}

		if(x_month != null) {
			setMemoryValue(ThreadCalendarPanel.class, Integer.parseInt(x_month));
		}

		if(x_itemDivLoc != null) {
			setMemoryValue(ItemAndReminderPanel.class, Integer.parseInt(x_itemDivLoc));
		}


		if(x_navDivLoc != null) {
			setMemoryValue(NavigationAndComponentPanel.class, Integer.parseInt(x_navDivLoc));
		}
	}

	public static void saveToProperties(Properties p_properties) {
		Integer x_tabMemory = getMemoryValue(ThreadPanel.class);
		Integer x_monthMemory = getMemoryValue(ThreadCalendarPanel.class);
		Integer x_itemDivMemory = getMemoryValue(ItemAndReminderPanel.class);
		Integer x_navDivMemory = getMemoryValue(NavigationAndComponentPanel.class);

		if(x_tabMemory != null) {
			p_properties.setProperty(s_TAB_INDEX, String.valueOf(x_tabMemory));
		}

		if(x_monthMemory != null) {
			p_properties.setProperty(s_MONTH, String.valueOf(x_monthMemory));
		}

		if(x_itemDivMemory != null) {
			p_properties.setProperty(s_DIVLOC, String.valueOf(x_itemDivMemory));
		}

		if(x_navDivMemory != null) {
			p_properties.setProperty(s_NAVDIVLOC, String.valueOf(x_navDivMemory));
		}
	}
}
