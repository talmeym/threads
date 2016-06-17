package gui;

import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.awt.event.*;

public class ItemWindow extends ComponentWindow<Item> {
	public ItemWindow(Item p_item) {
		super(p_item);
		setContentPane(new ItemPanel(p_item));
		setSize(GUIConstants.s_itemWindowSize);
		renameWindow(p_item);
	}
}
