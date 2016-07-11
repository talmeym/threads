package util;

import data.*;

import java.util.*;

public class DueDateComparator <TYPE extends HasDueDate> implements Comparator<TYPE> {
	@Override
	public int compare(HasDueDate obj1, HasDueDate obj2) {
		Date x_dueDate1 = obj1.getDueDate();
		Date x_dueDate2 = obj2.getDueDate();

		if(x_dueDate1 != null && x_dueDate2 != null) {
			return x_dueDate1.compareTo(x_dueDate2);
		}

		throw new IllegalArgumentException("Invalid comparison object: " + obj1);
	}
}
