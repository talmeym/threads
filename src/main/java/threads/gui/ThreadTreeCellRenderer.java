package threads.gui;

import threads.data.Item;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

import static java.awt.Color.*;
import static threads.gui.ColourConstants.s_selectedColour;
import static threads.util.ImageUtil.*;

class ThreadTreeCellRenderer extends DefaultTreeCellRenderer {

	private final Icon o_actionIcon = getActionIcon();
	private final Icon o_updateIcon = getUpdateIcon();

	ThreadTreeCellRenderer() {
		setClosedIcon(getThreadIcon());
		setOpenIcon(getThreadIcon());

		setBackgroundNonSelectionColor(white);
		setBackgroundSelectionColor(s_selectedColour);
		setBorderSelectionColor(s_selectedColour);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree jTree, Object p_value, boolean p_selected, boolean p_expanded, boolean p_leaf, int p_row, boolean p_hasFocus) {
		Component x_component = super.getTreeCellRendererComponent(jTree, p_value, p_selected, p_expanded, p_leaf, p_row, p_hasFocus);
		x_component.setForeground(black);

		if(p_value instanceof threads.data.Component && !((threads.data.Component)p_value).isActive()) {
			x_component.setForeground(gray);
		}

		if(p_value instanceof Item) {
			Item x_item = (Item) p_value;

			if(x_item.getDueDate() != null) {
				setIcon(o_actionIcon);
			} else {
				setIcon(o_updateIcon);
			}
		} else {
			setIcon(getClosedIcon());
		}

		return x_component;
	}
}
