package gui;

import data.Component;
import data.Thread;
import data.*;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.List;

public class NavigationPanel extends JPanel implements TreeSelectionListener, TimeUpdateListener, GoogleSyncListener, TimedSaveListener {
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
		o_dateTimeLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 10, 10));

		add(x_navigationPanel, BorderLayout.CENTER);
		add(o_dateTimeLabel, BorderLayout.SOUTH);

		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
		TimedSaver.getInstance().addTimedSaveListener(this);
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

	@Override
	public void timeUpdate() {
		o_dateTimeLabel.setText(s_dateFormat.format(new Date()));
	}

	@Override
	public void googleSyncStarted() {
		o_dateTimeLabel.setText("Syncing with Google ...");
		o_dateTimeLabel.setIcon(ImageUtil.getGoogleVerySmallIcon());
		repaint();
	}

	@Override
	public void googleSynced() {
		o_dateTimeLabel.setText(s_dateFormat.format(new Date()));
		o_dateTimeLabel.setIcon(null);
		repaint();
	}

	@Override
	public void saveStarted() {
		o_dateTimeLabel.setText("Saving data to Disc ...");
		o_dateTimeLabel.setIcon(ImageUtil.getSaveIcon());
		repaint();
	}

	@Override
	public void saved() {
		o_dateTimeLabel.setText(s_dateFormat.format(new Date()));
		o_dateTimeLabel.setIcon(null);
		repaint();
	}
}
