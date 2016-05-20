package gui;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ThreadTreeCellRenderer extends DefaultTreeCellRenderer {
	@Override
	public Component getTreeCellRendererComponent(JTree jTree, Object o, boolean b, boolean b2, boolean b3, int i, boolean b4) {
		Component x_component = super.getTreeCellRendererComponent(jTree, o, b, b2, b3, i, b4);

		if(o instanceof data.Component && !((data.Component)o).isActive()) {
			x_component.setForeground(Color.GRAY);
		}

		return x_component;
	}
}
