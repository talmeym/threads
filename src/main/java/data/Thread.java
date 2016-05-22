package data;

import util.TextComparator;

import java.io.File;
import java.util.*;

public class Thread extends ThreadItem<ThreadItem> {
    public Thread() {
        this(new Date(), true, "New Thread", null, null);
    }
    
    public Thread(Date p_creationDate, boolean p_active, String p_text, List<ThreadItem> p_items, File p_docFolder) {
        super(p_creationDate, p_active, p_text, p_items, new TextComparator<ThreadItem>(), p_docFolder);
    }
    
    public int getThreadItemCount() {
        return getComponentCount();
    }
    
    public ThreadItem getThreadItem(int p_index) {
        return getComponent(p_index);
    }
    
    public void addThreadItem(ThreadItem p_threadItem) {
        addComponent(p_threadItem);
    }
    
    public void removeThreadItem(ThreadItem p_threadItem) {
        removeComponent(p_threadItem);
    }

	public void addItem(Item p_item) {
		addComponent(p_item);
	}

	public void removeItem(Item p_item) {
		removeComponent(p_item);
	}

	public String getType() {
		return "Thread";
	}
}