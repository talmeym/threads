package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import data.Item;
import data.Thread;
import data.ThreadGroup;

public class ThreadGroupTreeModel implements TreeModel, Observer
{
    private final ThreadGroup o_threadGroup;
    
    private List o_listeners = new ArrayList();
    
    public ThreadGroupTreeModel(ThreadGroup p_threadGroup)
    {
        o_threadGroup = p_threadGroup;
        o_threadGroup.addObserver(this);
    }
    
    public Object getRoot()
    {
        return o_threadGroup;
    }

    public int getChildCount(Object parent)
    {
        if(parent instanceof ThreadGroup)
        {
            return ((ThreadGroup)parent).getThreadGroupItemCount();
        }
        
        if(parent instanceof Thread)
        {
            return ((Thread)parent).getItemCount();
        }
        
        if(parent instanceof Item)
        {
            return 0;
        }
        
        throw new IllegalStateException("Asking about invalid parent: " + parent); 
    }

    public boolean isLeaf(Object node)
    {
        return node instanceof Item;
    }

    public void addTreeModelListener(TreeModelListener l)
    {
        o_listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
        o_listeners.remove(l);
    }

    public Object getChild(Object parent, int index)
    {
        if(parent instanceof ThreadGroup)
        {
            return ((ThreadGroup)parent).getThreadGroupItem(index);
        }
        
        if(parent instanceof Thread)
        {
            return ((Thread)parent).getItem(index);
        }

        throw new IllegalStateException("Asking about invalid parent: " + parent); 
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        if(parent instanceof ThreadGroup)
        {
            ThreadGroup x_threadGroup = (ThreadGroup) parent;
            
            for(int i = 0; i < x_threadGroup.getThreadGroupItemCount(); i++)
            {
                if(x_threadGroup.getThreadGroupItem(i) == child)
                {
                    return i;
                }
            }
        }
        
        if(parent instanceof Thread)
        {
            Thread x_thread = (Thread) parent;
            
            for(int i = 0; i < x_thread.getItemCount(); i++)
            {
                if(x_thread.getItem(i) == child)
                {
                    return i;
                }
            }
        }
        
        throw new IllegalStateException("Asking about invalid child: " + child);
    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {
        // do nothing
    }

    public void update(Observable o, Object arg)
    {
//        ObservableChangeEvent x_event = (ObservableChangeEvent) arg;
//        
//        Component x_component = (Component) x_event.getObservableObserver();
//        
//        List x_list = new ArrayList();
//        Component x_comp = x_component;
//        
//        do
//        {
//            x_list.add(x_comp);
//            x_comp = x_comp.getParentComponent();
//        }
//        while(x_comp != o_threadGroup);
//        
//        TreeModelEvent x_treeEvent = new TreeModelEvent(x_component, x_list.toArray(new Object[0]));
//        
//        for(int i = 0; i < o_listeners.size(); i++)
//        {
//            switch(x_event.getType())
//            {
//            case ObservableChangeEvent.s_CHANGE: ((TreeModelListener)o_listeners.get(i)).treeNodesChanged(x_treeEvent); break;
//            case ObservableChangeEvent.s_ADDED: ((TreeModelListener)o_listeners.get(i)).treeNodesInserted(x_treeEvent); break;
//            case ObservableChangeEvent.s_REMOVED: ((TreeModelListener)o_listeners.get(i)).treeNodesRemoved(x_treeEvent); break;
//            }
//        }
    }
}
