package gui;

import data.*;
import data.Component;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ThreadWindow extends JFrame implements TreeSelectionListener, Observer {

	private final JTree navigationTree;

	private final CardLayout cardLayout = new CardLayout();

	private final List<String> ids = new ArrayList<String>();

	private final JPanel threadPanel;

	private Thread o_currentThread;

	public ThreadWindow(Thread p_topLevelThread) {
		super();

		p_topLevelThread.addObserver(this);

		JPanel navigationPanel = new JPanel(new BorderLayout());
		navigationTree = new JTree(new ThreadTreeModel(p_topLevelThread));
		navigationTree.setCellRenderer(new ThreadTreeCellRenderer());
		navigationPanel.add(new JScrollPane(navigationTree), BorderLayout.CENTER);
		navigationTree.addTreeSelectionListener(this);
		ImageUtil.addIconToWindow(this);

		threadPanel = new JPanel(cardLayout);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(300);
		splitPane.setLeftComponent(navigationPanel);
		splitPane.setRightComponent(threadPanel);

		setContentPane(splitPane);
		setSize(GUIConstants.s_threadWindowSize);
		GUIUtil.centreWindow(this);

		showThread(p_topLevelThread, true, 0);

		ImageUtil.addIconToWindow(this);
		setVisible(true);
	}

	public void showThread(Thread p_thread, boolean p_new, int p_tabIndex) {
		o_currentThread = p_thread;
		String idString = p_thread.getId().toString();

		if(!ids.contains(idString)) {
			threadPanel.add(new ThreadPanel(p_thread, p_new, 0), idString);
			ids.add(idString);
		}

		cardLayout.show(threadPanel, idString);
		renameWindow(p_thread);
	}

	void renameWindow(Component p_component) {
		StringBuilder x_title = new StringBuilder("Threads: ");
		List<String> x_parentNames = new ArrayList<String>();

		while(p_component.getParentComponent() != null) {
			x_parentNames.add(p_component.getParentComponent().getText());
			p_component = p_component.getParentComponent();
		}

		for(int i = x_parentNames.size() - 1; i > -1; i--) {
			x_title.append(x_parentNames.get(i)).append(" > ");
		}

		x_title.append(p_component.getText());
		setTitle(x_title.toString());
	}

	@Override
	public void valueChanged(TreeSelectionEvent p_treeSelectionEvent) {
		TreePath x_path = p_treeSelectionEvent.getPath();
		data.Component x_component = (data.Component) x_path.getLastPathComponent();
		WindowManager.getInstance().openComponent(x_component, false, -1);
	}

	@Override
	public void update(Observable observable, Object o) {
		renameWindow(o_currentThread);
	}

	public void selectComponent(Component p_component) {
		if(p_component instanceof Reminder) {
			p_component = p_component.getParentComponent();
		}

		Object[] pathObjs = getPathObjs(p_component);
		navigationTree.setSelectionPath(new TreePath(pathObjs));
	}

	private Object[] getPathObjs(Component p_component) {
		List<Component> parentComponents = new ArrayList<Component>();

		parentComponents.add(p_component);

		while(p_component.getParentComponent() != null) {
			parentComponents.add(0, p_component.getParentComponent());
			p_component = p_component.getParentComponent();
		}

		return parentComponents.toArray(new Object[parentComponents.size()]);
	}
}
