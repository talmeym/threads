package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class ItemWindow extends JDialog implements ActionListener {
	private final Item o_item;

	public ItemWindow(Item p_item, boolean p_new, JFrame parent) {
		o_item = p_item;
		setContentPane(new ItemPanel(p_item, p_new, this));
		setSize(GUIConstants.s_itemWindowSize);
		renameWindow(p_item);
		GUIUtil.centreToWindow(this, parent);
		ImageUtil.addIconToWindow(this);
		setModal(true);
		setVisible(true);
	}

	void renameWindow(Component p_component) {
		StringBuilder x_title = new StringBuilder();
		List<String> x_parentNames = new ArrayList<String>();
		Component x_parent = p_component.getParentComponent();

		while(x_parent != null) {
			x_parentNames.add(x_parent.getText());
			x_parent = x_parent.getParentComponent();
		}

		for(int i = x_parentNames.size() - 1; i > -1; i--) {
			x_title.append(x_parentNames.get(i)).append(" > ");
		}

		x_title.append(p_component.getText());
		setTitle(x_title.toString());
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		setVisible(false);


		if(((JButton)actionEvent.getSource()).getText().equals("Parent")) {
			WindowManager.getInstance().openComponent(o_item.getParentComponent(), false, -1);
		} else {
			Thread x_thread = (Thread) o_item.getParentComponent();

			if(LookupHelper.getActiveUpdates(x_thread).size() > 1 && JOptionPane.showConfirmDialog(null, "Set previous updates inactive ?") == JOptionPane.YES_OPTION) {

				for(int i = 0; i < x_thread.getThreadItemCount(); i++) {
					ThreadItem x_groupItem = x_thread.getThreadItem(i);

					if(x_groupItem instanceof Item)  {
						Item x_item = (Item) x_groupItem;

						if(x_item != o_item && x_item.getDueDate() == null && x_item.isActive()) {
							x_item.setActive(false);
						}
					}
				}
			}
		}
	}
}
