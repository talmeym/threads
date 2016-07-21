package util;

import data.HasDueDate;

import java.util.*;

public class ActiveAwareDueDateComparator implements Comparator<HasDueDate>
{
    public int compare(HasDueDate obj1, HasDueDate obj2)
    {
		if(obj1.getDueDate() != null)
		{
			if(obj1.isActive() != obj2.isActive()) {
				return obj1.isActive() ? -1 : 1;
			}

			return obj1.getDueDate().compareTo(obj2.getDueDate());
		}

        throw new IllegalArgumentException("Invalid comparison object: " + obj1);
    }
}
