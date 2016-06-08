package gui;

import data.Component;
import data.*;
import data.Thread;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.*;
import java.util.*;

class CellRenderer extends DefaultTableCellRenderer {
    public static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");
    
    private final Component o_component;
    
    CellRenderer(Component p_component) {
        o_component = p_component;
    }
    
    public java.awt.Component getTableCellRendererComponent(JTable table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
        if(p_value instanceof Date) {
            p_value = s_dateFormat.format((Date)p_value);
        }

        java.awt.Component x_component = super.getTableCellRendererComponent(table, p_value, p_isSelected, p_hasFocus, p_row, p_col);


		if(o_component instanceof Thread) {
			ThreadItem x_thread = ((Thread)o_component).getThreadItem(p_row);
			x_component.setForeground(x_thread.isActive() || p_isSelected ? Color.BLACK : Color.gray);
		}

		if(o_component instanceof Item) {
			Reminder x_reminder = ((Item)o_component).getReminder(p_row);
			x_component.setForeground(x_reminder.isActive() || p_isSelected ? Color.BLACK : Color.gray);
		}

		x_component.setBackground(p_isSelected ? ColourConstants.s_selectedColour : Color.WHITE);
        return x_component;
    }
}
