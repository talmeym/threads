package gui;

import data.Item;
import util.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ThreadTreeCellRenderer extends DefaultTreeCellRenderer {

	private final Icon o_actionIcon = ImageUtil.getActionIcon();
	private final Icon o_updateIcon = ImageUtil.getUpdateIcon();

	public ThreadTreeCellRenderer() {
		setClosedIcon(ImageUtil.getThreadIcon());
		setOpenIcon(ImageUtil.getThreadIcon());
	}

	@Override
	public Component getTreeCellRendererComponent(JTree jTree, Object p_value, boolean p_selected, boolean p_expanded, boolean p_leaf, int p_row, boolean p_hasFocus) {
		Component x_component = super.getTreeCellRendererComponent(jTree, p_value, p_selected, p_expanded, p_leaf, p_row, p_hasFocus);

		if(p_value instanceof data.Component && !((data.Component)p_value).isActive()) {
			x_component.setForeground(Color.GRAY);
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
