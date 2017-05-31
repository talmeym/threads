package threads.data;

import java.util.Date;
import java.util.UUID;

public interface HasDueDate {
	UUID getId();
	ComponentType getType();
	boolean isActive();
	String getText();
	Date getDueDate();
	void setDueDate(Date dueDate);
	boolean isDue();
}
