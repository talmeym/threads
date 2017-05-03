package gui;

import data.Component;
import data.Thread;
import data.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;

public class NavigationPanel extends JPanel implements TreeSelectionListener {
	private final JTree o_navigationTree;

	public NavigationPanel(Thread p_topLevelThread) {
		super(new BorderLayout());

		o_navigationTree = new JTree(new ThreadTreeModel(p_topLevelThread));
		o_navigationTree.setCellRenderer(new ThreadTreeCellRenderer());
		o_navigationTree.addTreeSelectionListener(this);

		JPanel x_navigationPanel = new JPanel(new BorderLayout());
		x_navigationPanel.add(new JScrollPane(o_navigationTree), BorderLayout.CENTER);
		x_navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		add(x_navigationPanel, BorderLayout.CENTER);
		add(new ThreadsStatusPanel(), BorderLayout.SOUTH);
	}

	public void selectComponent(Component p_component) {
		if(p_component instanceof Reminder) {
			p_component = p_component.getParentComponent();
		}

		o_navigationTree.removeTreeSelectionListener(this);
		List<Component> x_hierarchy = p_component.getHierarchy();
		o_navigationTree.setSelectionPath(new TreePath(x_hierarchy.toArray(new Component[x_hierarchy.size()])));
		o_navigationTree.addTreeSelectionListener(this);
	}

	@Override
	public void valueChanged(TreeSelectionEvent p_treeSelectionEvent) {
		if(p_treeSelectionEvent.getNewLeadSelectionPath() != null && !p_treeSelectionEvent.getNewLeadSelectionPath().equals(p_treeSelectionEvent.getOldLeadSelectionPath())) {
			TreePath x_path = p_treeSelectionEvent.getPath();
			ThreadItem x_threadItem = (ThreadItem) x_path.getLastPathComponent();
			WindowManager.getInstance().openComponent(x_threadItem);
		}
	}

}
