package util;

import data.*;

import java.util.*;

public class AllDayAwareDueDateComparator implements Comparator<HasDueDate>
{
    public int compare(HasDueDate obj1, HasDueDate obj2) {
		Date x_dueDate1 = obj1.getDueDate();
		Date x_dueDate2 = obj2.getDueDate();

		if(x_dueDate1 != null && x_dueDate2 != null) {
			if(DateUtil.isSameDay(x_dueDate1, x_dueDate2)) {
				if(DateUtil.isAllDay(x_dueDate1) || DateUtil.isAllDay(x_dueDate2)) {
					return x_dueDate1.compareTo(obj2.getDueDate()) * -1;
				}
			}

			return x_dueDate1.compareTo(obj2.getDueDate());
		}

        throw new IllegalArgumentException("Invalid comparison object: " + obj1);
    }
}
