package gui;

import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;

public class NavigationWindow extends JFrame {
	private final NavigationPanel o_navigationPanel;

	public NavigationWindow(Thread p_topLevelThread) {
		o_navigationPanel = new NavigationPanel(p_topLevelThread);
		setContentPane(o_navigationPanel);
		setSize(GUIConstants.s_navWindowSize);
		setTitle("Threads");
		ImageUtil.addIcon(this);
	}

	public void selectComponent(Component p_component) {
		o_navigationPanel.selectComponent(p_component);
	}
}
