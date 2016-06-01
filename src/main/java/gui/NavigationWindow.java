package gui;

import data.*;
import data.Component;
import data.Thread;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class NavigationWindow extends JFrame implements TreeSelectionListener {
	private final JTree o_navigationTree;

	JButton o_setDocFolderButton = new JButton("Set Doc Folder");
	JButton o_openDocFolderButton = new JButton("Open Doc Folder");

	public NavigationWindow(Thread p_topLevelThread) {
		o_navigationTree = new JTree(new ThreadTreeModel(p_topLevelThread));
		o_navigationTree.setCellRenderer(new ThreadTreeCellRenderer());
		o_navigationTree.addTreeSelectionListener(this);

		JPanel x_navigationPanel = new JPanel(new BorderLayout());
		x_navigationPanel.add(new JScrollPane(o_navigationTree), BorderLayout.CENTER);
		x_navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		o_openDocFolderButton.setEnabled(false);
		o_setDocFolderButton.setEnabled(false);

		o_openDocFolderButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ThreadItem x_component = (ThreadItem) o_navigationTree.getSelectionPath().getLastPathComponent();
				FolderManager.openDocFolder(x_component);
			}
		});

		o_setDocFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ThreadItem x_component = (ThreadItem) o_navigationTree.getSelectionPath().getLastPathComponent();
				FolderManager.setDocFolder(x_component);
				o_openDocFolderButton.setEnabled(x_component.getDocFolder() != null);
			}
		});

		JPanel x_buttonPanel = new JPanel(new GridLayout(0, 1, 0, 0));
		x_buttonPanel.add(o_openDocFolderButton);
		x_buttonPanel.add(o_setDocFolderButton);

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(x_navigationPanel, BorderLayout.CENTER);
		contentPanel.add(x_buttonPanel, BorderLayout.SOUTH);

		setContentPane(contentPanel);
		setTitle("Navigation");
	}

	public void selectComponent(Component p_component) {
		o_navigationTree.removeTreeSelectionListener(this);
		o_navigationTree.setSelectionPath(getTreePath(p_component));
		o_navigationTree.addTreeSelectionListener(this);
	}

	@Override
	public void valueChanged(TreeSelectionEvent p_treeSelectionEvent) {
		if(p_treeSelectionEvent.getNewLeadSelectionPath() != null && !p_treeSelectionEvent.getNewLeadSelectionPath().equals(p_treeSelectionEvent.getOldLeadSelectionPath())) {
			TreePath x_path = p_treeSelectionEvent.getPath();
			ThreadItem x_threadItem = (ThreadItem) x_path.getLastPathComponent();
			WindowManager.getInstance().openComponent(x_threadItem, false, -1);
			o_openDocFolderButton.setEnabled(x_threadItem.getDocFolder() != null);
			o_setDocFolderButton.setEnabled(true);
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
}
