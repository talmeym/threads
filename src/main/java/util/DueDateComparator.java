package util;

import data.*;

import java.util.Comparator;

public class DueDateComparator implements Comparator<HasDueDate>
{
    public int compare(HasDueDate obj1, HasDueDate obj2)
    {
		if(obj1.getDueDate() != null)
		{
			return obj1.getDueDate().compareTo(obj2.getDueDate());
		}

        throw new IllegalArgumentException("Invalid comparison object: " + obj1);
    }
}
