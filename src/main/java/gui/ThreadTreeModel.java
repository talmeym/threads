package gui;

import data.*;
import data.Thread;

import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;

public class ThreadTreeModel implements TreeModel, Observer {
    private final Thread o_thread;
    private final List<TreeModelListener> o_listeners = new ArrayList<TreeModelListener>();
    
    public ThreadTreeModel(Thread p_thread) {
        o_thread = p_thread;
        o_thread.addObserver(this);
    }
    
    public Object getRoot() {
        return o_thread;
    }

    public int getChildCount(Object parent) {
        if(parent instanceof Thread) {
            return ((Thread)parent).getThreadItemCount();
        }
        
        if(parent instanceof Item) {
            return 0;
        }
        
        throw new IllegalStateException("Asking about invalid parent: " + parent); 
    }

    public boolean isLeaf(Object node) {
        return node instanceof Item;
    }

    public void addTreeModelListener(TreeModelListener l) {
        o_listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        o_listeners.remove(l);
    }

    public Object getChild(Object parent, int index) {
        if(parent instanceof Thread) {
            return ((Thread)parent).getThreadItem(index);
        }
        
        return -1;
    }

    public int getIndexOfChild(Object parent, Object child) {
        if(parent instanceof Thread) {
            Thread x_thread = (Thread) parent;
            
            for(int i = 0; i < x_thread.getThreadItemCount(); i++) {
                if(x_thread.getThreadItem(i) == child) {
                    return i;
                }
            }
        }
        
        return -1;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        // do nothing
    }

    public void update(Observable observable, Object o) {
		ComponentChangeEvent x_cce = (ComponentChangeEvent) o;

		if(x_cce.getSource() instanceof Reminder || x_cce.getType() == ComponentChangeEvent.s_MOVED) {
			return;
		}

		Component x_component = x_cce.getSource();
		TreePath treePath = new TreePath(getPathObjs(x_component));

		for(TreeModelListener x_listener: o_listeners) {
			switch(x_cce.getType()) {
				case ComponentChangeEvent.s_ADDED: x_listener.treeNodesInserted(new TreeModelEvent(this, treePath, new int[]{x_cce.getIndex()}, null)); break;
				case ComponentChangeEvent.s_REMOVED: x_listener.treeNodesRemoved(new TreeModelEvent(this, treePath, new int[]{x_cce.getIndex()}, null)); break;
				default: x_listener.treeNodesChanged(new TreeModelEvent(this, treePath));
			}
		}
	}

	private Object[] getPathObjs(Component p_component) {
		List<Component> parentComponents = new ArrayList<Component>();

		while(p_component != o_thread) {
			parentComponents.add(0, p_component);
			p_component = p_component.getParentComponent();
		}

		parentComponents.add(0, o_thread);
		return parentComponents.toArray(new Object[parentComponents.size()]);
	}
}
