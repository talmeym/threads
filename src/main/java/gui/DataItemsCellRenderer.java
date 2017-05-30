package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static gui.ColourConstants.*;
import static java.awt.Color.white;
import static util.DateUtil.*;

abstract class DataItemsCellRenderer<INPUT_COMP extends data.Component, OUTPUT_COMP extends data.Component> extends BaseCellRenderer {
	private List<OUTPUT_COMP> o_list;
	private INPUT_COMP o_component;

	DataItemsCellRenderer(INPUT_COMP p_component) {
		o_list = getDataItems(p_component);
		o_component = p_component;
		p_component.addComponentChangeListener(e -> o_list = getDataItems(p_component));
	}

	public java.awt.Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
		java.awt.Component x_awtComponent = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);

		if(o_list != null) {
			OUTPUT_COMP x_component = o_list.get(p_row);

			if (x_component != null) {
				customSetup(x_component, x_awtComponent, p_isSelected);
			}
		}

		return x_awtComponent;
	}

	abstract List<OUTPUT_COMP> getDataItems(INPUT_COMP p_component);

	void customSetup(OUTPUT_COMP p_component, Component p_awtComponent, boolean p_isSelected) {
		// do nothing by default
	}

	static Color getColourForTime(Date p_dueDate) {
		Date x_now = isAllDay(p_dueDate) ? makeStartOfDay(new Date()) : new Date();

		if(isAllDay(p_dueDate) ? p_dueDate.before(getFirstThing(TODAY)) : p_dueDate.before(x_now)) {
			return s_goneByColour;
		} else if(isToday(p_dueDate)) {
			return s_todayColour;
		} else if(isTomorrow(p_dueDate)) {
			return s_tomorrowColour;
		} else if(isWithin7Days(p_dueDate, true)) {
			return s_thisWeekColour;
		}

		return white;
	}

	void reloadData() {
		o_list = getDataItems(o_component);
	}
}
