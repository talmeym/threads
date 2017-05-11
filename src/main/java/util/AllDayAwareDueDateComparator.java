package util;

import data.*;

import java.util.*;

import static util.DateUtil.isAllDay;
import static util.DateUtil.isSameDay;

public class AllDayAwareDueDateComparator implements Comparator<HasDueDate>
{
    public int compare(HasDueDate obj1, HasDueDate obj2) {
		Date x_dueDate1 = obj1.getDueDate();
		Date x_dueDate2 = obj2.getDueDate();

		if(isSameDay(x_dueDate1, x_dueDate2) && (isAllDay(x_dueDate1) != isAllDay(x_dueDate2)) && (obj1.isDue() != obj2.isDue())) {
			return x_dueDate1.compareTo(x_dueDate2) * -1;
		}

		return x_dueDate1.compareTo(obj2.getDueDate());
	}
}
