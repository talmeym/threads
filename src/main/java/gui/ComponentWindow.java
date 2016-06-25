package gui;

import data.*;
import util.ImageUtil;

import javax.swing.*;
import java.util.*;

public class ComponentWindow extends JFrame implements Observer {
	protected final Component o_component;

	public ComponentWindow(Component p_component) {
		this.o_component = p_component;
		o_component.addObserver(this);
		ImageUtil.addIcon(this);
		renameWindow();
	}

	@Override
	public void update(Observable observable, Object o) {
		if(observable == ((ObservableChangeEvent)o).getObservableObserver()) {
			renameWindow();
		}
	}

	protected void renameWindow() {
		setTitle(o_component.getType() + " : " + o_component.getText());
	}
}
