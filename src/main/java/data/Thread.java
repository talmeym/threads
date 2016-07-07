package data;

import util.*;

import java.io.File;
import java.util.*;

public class Thread extends ThreadItem<ThreadItem> {
    public Thread(String text) {
        this(UUID.randomUUID(), new Date(), new Date(), true, text, null, null);
    }
    
    public Thread(UUID id, Date p_creationDate, Date p_modifiedDate, boolean p_active, String p_text, List<ThreadItem> p_items, File p_docFolder) {
        super(id, p_creationDate, p_modifiedDate, p_active, p_text, p_items, new TypeAwareTextComparator<ThreadItem>(), p_docFolder);
    }

	public Thread(Thread p_thread, boolean p_addCopyText) {
		this(UUID.randomUUID(), new Date(), new Date(), p_thread.isActive(), (p_addCopyText ? "Copy of " : "") + p_thread.getText(), new ArrayList<ThreadItem>(), p_thread.getDocFolder());

		for(int i = 0; i < p_thread.getThreadItemCount(); i++) {
			ThreadItem x_threadItem = p_thread.getThreadItem(i);
			addThreadItem(x_threadItem instanceof Item ? new Item((Item) x_threadItem, false) : new Thread((Thread) x_threadItem, false));
		}
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