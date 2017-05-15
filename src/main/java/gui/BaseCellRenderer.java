package gui;

import data.ComponentType;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.*;
import java.util.Date;

import static gui.ColourConstants.s_selectedColour;
import static java.awt.Color.*;
import static java.lang.String.valueOf;
import static util.ImageUtil.*;

class BaseCellRenderer extends DefaultTableCellRenderer {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");

	@Override
	public void setValue(Object p_value) {
		setText("");
		setIcon(null);
		setHorizontalAlignment(LEFT);

		if(p_value instanceof Boolean) {
			if((Boolean) p_value) {
				setIcon(getGoogleSmallIcon());
				setHorizontalAlignment(CENTER);
			}
		} else if(p_value instanceof ComponentType) {
			setIcon(getIconForType((ComponentType)p_value));
			setHorizontalAlignment(CENTER);
		} else if(p_value instanceof Date) {
			setText(s_dateFormat.format((Date)p_value));
		} else {
			setText(valueOf(p_value));
		}
	}

	public java.awt.Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
		java.awt.Component x_awtComponent = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
		setBorder(noFocusBorder);
		x_awtComponent.setForeground(black);
		x_awtComponent.setBackground(p_isSelected ? s_selectedColour : white);
		return x_awtComponent;
	}
}
