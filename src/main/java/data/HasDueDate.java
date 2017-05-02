package data;

import java.util.Date;

public interface HasDueDate {
	Date getDueDate();
	void setDueDate(Date dueDate);
	boolean isDue();
}
