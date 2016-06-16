package gui;

import data.*;
import data.Thread;
import util.DateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.Component;
import java.text.*;
import java.util.*;

public class ThreadActionCellRenderer extends DefaultTableCellRenderer {
	public static final DateFormat s_createDateFormat = new SimpleDateFormat("dd MMM yy HH:mm");

    private final Thread o_thread;

    ThreadActionCellRenderer(Thread p_thread) {
        o_thread = p_thread;
    }

    public Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
        if(p_value instanceof Date) {
			p_value = p_col == 0 ? s_createDateFormat.format((Date) p_value) : DateUtil.getFormattedDate((Date) p_value);
        }

        java.awt.Component x_component = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
		x_component.setForeground(Color.black);

		Item x_item = LookupHelper.getAllActiveActions(o_thread).get(p_row);
		x_component.setBackground(DateUtil.getColourForTime(x_item.getDueDate()));

		if(p_isSelected) {
			x_component.setBackground(ColourConstants.s_selectedColour);
		}

        return x_component;
    }
}