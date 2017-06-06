package threads.data;

import java.util.Date;
import java.util.UUID;

public interface HasDueDate {
	UUID getId();
	ComponentType getType();
	boolean isActive();
	String getText();
	void setText(String x_text);
	Date getDueDate();
	void setDueDate(Date p_dueDate);
	String getNotes();
	void setNotes(String p_notes);
	boolean isDue();
	void addComponentChangeListener(ComponentChangeListener p_listener);

}
