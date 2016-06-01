package gui;

import data.*;
import data.Thread;

import javax.swing.*;
import java.awt.event.*;

public class ItemWindow extends ComponentWindow<Item> implements ActionListener {
	public ItemWindow(Item p_item, boolean p_new) {
		super(p_item);
		setContentPane(new ItemPanel(p_item, p_new, this));
		setSize(GUIConstants.s_itemWindowSize);
		renameWindow(p_item);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		setVisible(false);

		Item x_item = getComponent();
		Thread x_thread = (Thread) x_item.getParentComponent();

		if(x_item.getDueDate() == null && LookupHelper.getActiveUpdates(x_thread).size() == 2 && JOptionPane.showConfirmDialog(null, "Set previous updates inactive ?", "Supersede previous updates ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			for(int i = 0; i < x_thread.getThreadItemCount(); i++) {
				ThreadItem x_groupItem = x_thread.getThreadItem(i);

				if(x_groupItem instanceof Item)  {
					Item x_otherItem = (Item) x_groupItem;

					if(x_otherItem != x_item && x_otherItem.getDueDate() == null && x_otherItem.isActive()) {
						x_otherItem.setActive(false);
					}
				}
			}
		}
	}
}
