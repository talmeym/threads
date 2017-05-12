package gui;

import data.*;
import data.Thread;

import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;

import static data.ComponentChangeEvent.*;

class ThreadTreeModel implements TreeModel, ComponentChangeListener {
    private final Thread o_thread;
    private final List<TreeModelListener> o_listeners = new ArrayList<>();
    
    ThreadTreeModel(Thread p_thread) {
        o_thread = p_thread;
        o_thread.addComponentChangeListener(this);
    }
    
    public Object getRoot() {
        return o_thread;
    }

    public int getChildCount(Object p_parent) {
        if(p_parent instanceof Thread) {
            return ((Thread)p_parent).getThreadItems().size();
        }
        
        if(p_parent instanceof Item) {
            return 0;
        }
        
        throw new IllegalStateException("Asking about invalid parent: " + p_parent);
    }

    public boolean isLeaf(Object p_node) {
        return p_node instanceof Item;
    }

    public void addTreeModelListener(TreeModelListener p_listener) {
        o_listeners.add(p_listener);
    }

    public void removeTreeModelListener(TreeModelListener p_listener) {
        o_listeners.remove(p_listener);
    }

    public Object getChild(Object p_parent, int p_index) {
        if(p_parent instanceof Thread) {
            return ((Thread)p_parent).getThreadItem(p_index);
        }
        
        return null;
    }

    public int getIndexOfChild(Object p_parent, Object p_child) {
        if(p_parent instanceof Thread && p_child instanceof ThreadItem) {
			List<ThreadItem> x_threadItems = ((Thread)p_parent).getThreadItems();

			if(x_threadItems.contains(p_child)) {
				return x_threadItems.indexOf(p_child);
			}
        }

        return -1;
    }

    public void valueForPathChanged(TreePath p_path, Object p_newValue) {
        // do nothing
    }

	@Override
    public void componentChanged(ComponentChangeEvent p_cce) {
		if(p_cce.getSource() instanceof Reminder) {
			return;
		}

		Component x_component = p_cce.getSource();
		List<Component> x_hierarchy = x_component.getHierarchy();
		TreePath treePath = new TreePath(x_hierarchy.toArray(new Component[x_hierarchy.size()]));

		o_listeners.forEach(x_listener -> {
			switch(p_cce.getType()) {
				case s_CONTENT_ADDED: x_listener.treeNodesInserted(new TreeModelEvent(this, treePath, new int[]{p_cce.getIndex()}, null)); break;
				case s_CONTENT_REMOVED: x_listener.treeNodesRemoved(new TreeModelEvent(this, treePath, new int[]{p_cce.getIndex()}, null)); break;
				case s_DELETED: x_listener.treeNodesRemoved(new TreeModelEvent(this, treePath));
				default: x_listener.treeNodesChanged(new TreeModelEvent(this, treePath)); // s_CHANGED
			}
		});
	}
}
