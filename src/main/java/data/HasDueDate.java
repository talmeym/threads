package data;

import java.util.*;

public interface HasDueDate {
	UUID getId();
	ComponentType getType();
	boolean isActive();
	String getText();
	Date getDueDate();
	void setDueDate(Date dueDate);
	boolean isDue();
}
