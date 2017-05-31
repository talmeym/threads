package threads.data;

import threads.util.TypeAwareTextComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Thread extends ThreadItem<ThreadItem> {
    public Thread(String text) {
        this(UUID.randomUUID(), new Date(), new Date(), true, text, null, null);
    }
    
    public Thread(UUID id, Date p_creationDate, Date p_modifiedDate, boolean p_active, String p_text, List<ThreadItem> p_items, File p_docFolder) {
        super(id, p_creationDate, p_modifiedDate, p_active, p_text, p_items, new TypeAwareTextComparator<>(), p_docFolder);
    }

	public Thread(Thread p_thread, boolean p_addCopyText) {
		this(UUID.randomUUID(), new Date(), new Date(), p_thread.isActive(), (p_addCopyText ? "Copy of " : "") + p_thread.getText(), new ArrayList<>(), p_thread.getDocFolder());
		p_thread.getThreadItems().forEach(ti -> addThreadItem(ti instanceof Item ? new Item((Item) ti, false) : new Thread((Thread) ti, false)));
	}

    public int getThreadItemCount() {
        return getComponentCount();
    }

    public ThreadItem getThreadItem(int p_index) {
        return getComponent(p_index);
    }

    public List<ThreadItem> getThreadItems() {
    	return getComponents();
	}
    
    public void addThreadItem(ThreadItem... p_threadItems) {
    	for(ThreadItem x_threadItem: p_threadItems) {
        	addComponent(x_threadItem);
		}
    }
    
    public void removeThreadItem(ThreadItem p_threadItem) {
        removeComponent(p_threadItem);
    }

	@Override
	public ComponentType getType() {
		return ComponentType.Thread;
	}
}