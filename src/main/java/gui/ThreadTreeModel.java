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

    public void update(Observable o, Object arg) {
		if(o instanceof Reminder) {
			return;
		}

		ObservableChangeEvent x_oce = (ObservableChangeEvent) arg;
		Component x_component = (Component) x_oce.getObservableObserver();
		TreePath treePath = new TreePath(getPathObjs(x_component));

		for(TreeModelListener x_listener: o_listeners) {
			switch(x_oce.getType()) {
				case ObservableChangeEvent.s_ADDED: x_listener.treeNodesInserted(new TreeModelEvent(this, treePath, new int[]{x_oce.getIndex()}, null)); break;
				case ObservableChangeEvent.s_REMOVED: x_listener.treeNodesRemoved(new TreeModelEvent(this, treePath, new int[]{x_oce.getIndex()}, null)); break;
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
