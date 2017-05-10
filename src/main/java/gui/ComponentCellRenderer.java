package gui;

import data.Component;
import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.*;
import java.util.Date;

class ComponentCellRenderer extends DefaultTableCellRenderer {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");

	private final Component o_component;
    
    ComponentCellRenderer(Component p_component) {
        o_component = p_component;
    }

	@Override
	public void setValue(Object p_value) {
		setText("");
		setIcon(null);
		setHorizontalAlignment(JTextField.LEFT);

		if(p_value instanceof Boolean) {
			if((Boolean) p_value) {
				setIcon(ImageUtil.getGoogleSmallIcon());
				setHorizontalAlignment(JTextField.CENTER);
			}
		} else if(p_value instanceof Icon) {
			setIcon((Icon) p_value);
			setHorizontalAlignment(JTextField.CENTER);
		} else if(p_value instanceof Date) {
			setText(s_dateFormat.format((Date)p_value));
		} else {
			setText(String.valueOf(p_value));
		}
	}

    public java.awt.Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
		java.awt.Component x_component = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
		setBorder(noFocusBorder);

		if(o_component instanceof Thread) {
			ThreadItem x_thread = ((Thread)o_component).getThreadItem(p_row);
			x_component.setForeground(x_thread.isActive() ? Color.black : Color.gray);
		} else if(o_component instanceof Item) {
			Reminder x_reminder = ((Item)o_component).getReminder(p_row);
			x_component.setForeground(x_reminder.isActive() ? Color.black : Color.gray);
		} else {
			x_component.setForeground(Color.black);
		}

		x_component.setBackground(p_isSelected ? ColourConstants.s_selectedColour : Color.WHITE);
        return x_component;
    }
}
