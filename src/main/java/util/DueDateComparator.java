package util;

import data.HasDueDate;

import java.util.*;

public class DueDateComparator implements Comparator<HasDueDate> {
	@Override
	public int compare(HasDueDate obj1, HasDueDate obj2) {
		Date x_dueDate1 = obj1.getDueDate();
		Date x_dueDate2 = obj2.getDueDate();

		if(x_dueDate1 != null && x_dueDate2 != null) {
			return x_dueDate1.compareTo(obj2.getDueDate());
		}

		throw new IllegalArgumentException("Invalid comparison object: " + obj1);
	}
}
