package gui;

import data.Component;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class ComponentWindow <TYPE extends Component> extends JFrame implements Observer {
	private final TYPE o_component;

	public ComponentWindow(TYPE p_component) {
		this.o_component = p_component;
		o_component.addObserver(this);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent componentEvent) {
				WindowManager.getInstance().setComponentWindowDetails(getComponent().getClass(), getLocation(), getSize());
			}

			@Override
			public void componentResized(ComponentEvent componentEvent) {
				WindowManager.getInstance().setComponentWindowDetails(getComponent().getClass(), getLocation(), getSize());
			}
		});
	}

	public TYPE getComponent() {
		return o_component;
	}

	@Override
	public void update(Observable observable, Object o) {
		renameWindow(o_component);
	}

	protected void renameWindow(Component p_component) {
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
}
