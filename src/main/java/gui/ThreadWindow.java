package gui;

import data.Component;
import data.Thread;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.util.List;

public class ThreadWindow extends JFrame implements Observer {

	private Thread o_thread;

	public ThreadWindow(Thread p_thread, boolean p_new, int p_tabIndex, ChangeListener p_listener) {
		super();
		o_thread = p_thread;
		p_thread.addObserver(this);


		setContentPane(new ThreadPanel(p_thread, p_new, p_tabIndex, p_listener));
		setSize(GUIConstants.s_threadWindowSize);
		renameWindow(p_thread);
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
	public void update(Observable observable, Object o) {
		renameWindow(o_thread);
	}
}
