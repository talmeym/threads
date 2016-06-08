package data;

import java.util.Date;

public interface HasDueDate {
	public boolean isActive();
	public Date getDueDate();
	public void setDueDate(Date dueDate);
}
