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
    
    public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Object x_value = value;
        
        if(x_value instanceof Date) {
            x_value = s_dateFormat.format((Date)x_value);
        }
        
        java.awt.Component x_component = super.getTableCellRendererComponent(table, x_value, isSelected, hasFocus, row, column);

		x_component.setForeground(Color.BLACK);
		x_component.setBackground(Color.WHITE);

        if(o_component instanceof Thread) {
            Thread x_thread = (Thread) o_component;
            ThreadItem x_item = x_thread.getThreadItem(row);
			x_component.setBackground(x_item.isActive() ? Color.WHITE : new Color(220,220,220));
        }
        
        if(o_component instanceof Item) {
            Item x_item = (Item) o_component;
            Reminder x_reminder = x_item.getReminder(row);
			x_component.setBackground(x_reminder.isActive() ? Color.WHITE : Color.LIGHT_GRAY);
        }

		if(isSelected) {
			x_component.setForeground(Color.BLACK);
			x_component.setBackground(Color.CYAN);
		}

        return x_component;
    }
}
