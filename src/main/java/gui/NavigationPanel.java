package gui;

import data.Component;
import data.Thread;
import data.*;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;

public class NavigationPanel extends JPanel implements TreeSelectionListener, TimeUpdateListener {
	public static final DateFormat s_dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm");

	private final JTree o_navigationTree;
	private final JLabel o_dateTimeLabel;

	public NavigationPanel(Thread p_topLevelThread) {
		super(new BorderLayout());

		o_navigationTree = new JTree(new ThreadTreeModel(p_topLevelThread));
		o_navigationTree.setCellRenderer(new ThreadTreeCellRenderer());
		o_navigationTree.addTreeSelectionListener(this);

		JPanel x_navigationPanel = new JPanel(new BorderLayout());
		x_navigationPanel.add(new JScrollPane(o_navigationTree), BorderLayout.CENTER);
		x_navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		o_dateTimeLabel = new JLabel(s_dateFormat.format(new Date()));
		o_dateTimeLabel.setHorizontalAlignment(JLabel.CENTER);
		o_dateTimeLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		add(x_navigationPanel, BorderLayout.CENTER);
		add(o_dateTimeLabel, BorderLayout.SOUTH);

		TimeUpdater.getInstance().addTimeUpdateListener(this);
	}

	public void selectComponent(Component p_component) {
		if(p_component instanceof Reminder) {
			p_component = p_component.getParentComponent();
		}

		o_navigationTree.removeTreeSelectionListener(this);
		o_navigationTree.setSelectionPath(getTreePath(p_component));
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

	private TreePath getTreePath(Component p_component) {
		List<Component> x_parentComponents = new ArrayList<Component>();
		x_parentComponents.add(p_component);

		while(p_component.getParentComponent() != null) {
			x_parentComponents.add(0, p_component.getParentComponent());
			p_component = p_component.getParentComponent();
		}

		return new TreePath(x_parentComponents.toArray(new Object[x_parentComponents.size()]));
	}

	@Override
	public void timeUpdate() {
		o_dateTimeLabel.setText(s_dateFormat.format(new Date()));
	}
}
