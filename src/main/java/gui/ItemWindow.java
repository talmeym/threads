package gui;

import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.awt.event.*;

public class ItemWindow extends ComponentWindow<Item> implements ActionListener {
	public ItemWindow(Item p_item) {
		super(p_item);
		setContentPane(new ItemPanel(p_item, this));
		setSize(GUIConstants.s_itemWindowSize);
		renameWindow(p_item);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		setVisible(false);
		Thread x_thread = (Thread) o_component.getParentComponent();

		if(o_component.getDueDate() == null && LookupHelper.getActiveUpdates(x_thread).size() == 2 && JOptionPane.showConfirmDialog(this, MessagingConstants.s_supersedeUpdatesDesc, MessagingConstants.s_supersedeUpdatesTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
			for(int i = 0; i < x_thread.getThreadItemCount(); i++) {
				ThreadItem x_groupItem = x_thread.getThreadItem(i);

				if(x_groupItem instanceof Item)  {
					Item x_otherItem = (Item) x_groupItem;

					if(x_otherItem != o_component && x_otherItem.getDueDate() == null && x_otherItem.isActive()) {
						x_otherItem.setActive(false);
					}
				}
			}
		}
	}
}
