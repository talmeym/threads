package gui;

import data.*;
import data.Thread;

import javax.swing.*;

public class NavigationWindow extends JFrame {
	private final NavigationPanel o_navigationPanel;

	public NavigationWindow(Thread p_topLevelThread) {
		o_navigationPanel = new NavigationPanel(p_topLevelThread);
		setContentPane(o_navigationPanel);
		setSize(GUIConstants.s_navWindowSize);
		setTitle("Navigation");
	}

	public void selectComponent(Component p_component) {
		o_navigationPanel.selectComponent(p_component);
	}
}
