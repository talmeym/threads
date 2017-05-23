package gui;

import data.Component;
import data.*;
import data.Thread;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;

class NavigationPanel extends JPanel implements TreeSelectionListener {
	private final JTree o_navigationTree;

	NavigationPanel(Thread p_topLevelThread) {
		super(new BorderLayout());

		o_navigationTree = new JTree(new ThreadTreeModel(p_topLevelThread));
		o_navigationTree.setCellRenderer(new ThreadTreeCellRenderer());
		o_navigationTree.addTreeSelectionListener(this);

		JPanel x_treePanel = new JPanel(new BorderLayout());
		x_treePanel.add(new JScrollPane(o_navigationTree), BorderLayout.CENTER);
		x_treePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel x_panel = new JPanel(new BorderLayout());
		x_panel.add(new SearchPanel(p_topLevelThread), BorderLayout.CENTER);
		x_panel.add(new StatusPanel(p_topLevelThread), BorderLayout.SOUTH);

		add(x_treePanel, BorderLayout.CENTER);
		add(x_panel, BorderLayout.SOUTH);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
	}

	void selectComponent(Component p_component) {
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
			ThreadItem x_threadItem = (ThreadItem) p_treeSelectionEvent.getPath().getLastPathComponent();
			WindowManager.getInstance().openComponent(x_threadItem);
		}
	}

}
