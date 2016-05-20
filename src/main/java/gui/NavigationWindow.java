package gui;

import data.Component;
import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NavigationWindow extends JFrame implements TreeSelectionListener {
	private JTree tree;

	public NavigationWindow(Thread p_thread) throws HeadlessException {
		JPanel treePanel = new JPanel(new BorderLayout());
		tree = new JTree(new ThreadTreeModel(p_thread));
		tree.setCellRenderer(new ThreadTreeCellRenderer());
		treePanel.add(new JScrollPane(tree), BorderLayout.CENTER);
		tree.addTreeSelectionListener(this);
		ImageUtil.addIconToWindow(this);
		setContentPane(treePanel);
		setSize(300, 600);
		setTitle("Navigation");
		Dimension x_screenSize = getToolkit().getScreenSize();
		setLocation(((x_screenSize.width - getWidth()) / 2) - 600, (x_screenSize.height - getHeight()) / 2);
		setVisible(true);
	}

	public void selectComponent(Component p_component) {
		if(p_component instanceof Reminder) {
			p_component = p_component.getParentComponent();
		}

		List<Component> parentComponents = new ArrayList<Component>();

		parentComponents.add(p_component);

		while(p_component.getParentComponent() != null) {
			parentComponents.add(0, p_component.getParentComponent());
			p_component = p_component.getParentComponent();
		}

		Object[] pathObjs = parentComponents.toArray(new Object[parentComponents.size()]);

		tree.setSelectionPath(new TreePath(pathObjs));
	}

	@Override
	public void valueChanged(TreeSelectionEvent p_treeSelectionEvent) {
		TreePath x_path = p_treeSelectionEvent.getPath();
		data.Component x_component = (data.Component) x_path.getLastPathComponent();
		WindowManager.getInstance().openComponentWindow(x_component, false, -1);
	}
}
