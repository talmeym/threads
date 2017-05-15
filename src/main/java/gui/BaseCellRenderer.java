package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.*;
import java.util.Date;

import static gui.ColourConstants.s_selectedColour;
import static java.awt.Color.*;
import static util.ImageUtil.getGoogleSmallIcon;

class BaseCellRenderer extends DefaultTableCellRenderer {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");

	@Override
	public void setValue(Object p_value) {
		setText("");
		setIcon(null);
		setHorizontalAlignment(JTextField.LEFT);

		if(p_value instanceof Boolean) {
			if((Boolean) p_value) {
				setIcon(getGoogleSmallIcon());
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
		java.awt.Component x_awtComponent = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);

		setBorder(noFocusBorder);

		x_awtComponent.setForeground(black);
		x_awtComponent.setBackground(white);

		if(p_isSelected) {
			x_awtComponent.setBackground(s_selectedColour);
		}

		return x_awtComponent;
	}
}
