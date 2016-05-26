package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.util.*;

public class ItemWindow extends JFrame {
	public ItemWindow(Item p_item, boolean p_new, JFrame parent) {
		setContentPane(new ItemPanel(p_item, p_new));
		setSize(GUIConstants.s_itemWindowSize);
		renameWindow(p_item);
		GUIUtil.centreToWindow(this, parent);
		ImageUtil.addIconToWindow(this);
		setVisible(true);
	}

	void renameWindow(Component p_component) {
		StringBuilder x_title = new StringBuilder("Threads: ");
		List<String> x_parentNames = new ArrayList<String>();

		while(p_component.getParentComponent() != null) {
			x_parentNames.add(p_component.getParentComponent().getText());
			p_component = p_component.getParentComponent();
		}

		for(int i = x_parentNames.size() - 1; i > -1; i--) {
			x_title.append(x_parentNames.get(i)).append(" > ");
		}

		x_title.append(p_component.getText());
		setTitle(x_title.toString());
	}

	public void close() {
		setVisible(false);
	}
}
