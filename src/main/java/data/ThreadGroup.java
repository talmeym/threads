package data;

import java.io.File;
import java.util.Date;

import util.TextComparator;

public class ThreadGroup extends ThreadGroupItem
{
    public ThreadGroup()
    {
        this(new Date(), true, "New ThreadGroup", null, null);
    }
    
    public ThreadGroup(Date p_creationDate, boolean p_active, String p_text, ThreadGroupItem[] p_items, File p_docFolder)
    {
        super(p_creationDate, p_active, p_text, p_items, new TextComparator(), p_docFolder);
    }
    
    public int getThreadGroupItemCount()
    {
        return getComponentCount();
    }
    
    public ThreadGroupItem getThreadGroupItem(int p_index)
    {
        return (ThreadGroupItem) getComponent(p_index);
    }
    
    public ThreadGroupItem[] getThreadGroupItems()
    {
        return (ThreadGroupItem[]) getComponents().toArray(new ThreadGroupItem[0]);
    }
    
    public void addThreadGroupItem(ThreadGroupItem p_threadGroupItem)
    {
        addComponent(p_threadGroupItem);
    }
    
    public void removeThreadGroupItem(ThreadGroupItem p_threadGroupItem)
    {
        removeComponent(p_threadGroupItem);
    }
}