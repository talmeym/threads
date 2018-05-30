package threads.data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface HasDueDate {
	UUID getId();
	ComponentType getType();
	boolean isActive();
	void setActive(boolean active);
	String getText();
	void setText(String x_text);
	Date getDueDate();
	void setDueDate(Date p_dueDate);
	String getNotes();
	void setNotes(String p_notes);
	boolean isDue();
	void addComponentChangeListener(ComponentChangeListener p_listener);
	Date getModifiedDate();
	List<Component> getHierarchy();
	Component duplicate(boolean p_addCopyText);
	Component component();
}
