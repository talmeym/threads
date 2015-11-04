package gui;

import data.*;
import data.Thread;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.util.*;

public class ThreadTreeModel implements TreeModel, Observer
{
    private final Thread o_thread;
    
    private List o_listeners = new ArrayList();
    
    public ThreadTreeModel(Thread p_thread)
    {
        o_thread = p_thread;
        o_thread.addObserver(this);
    }
    
    public Object getRoot()
    {
        return o_thread;
    }

    public int getChildCount(Object parent)
    {
        if(parent instanceof Thread)
        {
            return ((Thread)parent).getThreadItemCount();
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
        if(parent instanceof Thread)
        {
            return ((Thread)parent).getThreadItem(index);
        }
        
        throw new IllegalStateException("Asking about invalid parent: " + parent);
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        if(parent instanceof Thread)
        {
            Thread x_thread = (Thread) parent;
            
            for(int i = 0; i < x_thread.getThreadItemCount(); i++)
            {
                if(x_thread.getThreadItem(i) == child)
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

    }
}
