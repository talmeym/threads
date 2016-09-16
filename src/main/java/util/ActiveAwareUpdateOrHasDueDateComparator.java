package util;

import data.*;

import java.util.*;

public class ActiveAwareUpdateOrHasDueDateComparator implements Comparator<Component>
{
    public int compare(Component obj1, Component obj2)
    {
		if(obj1.isActive() != obj2.isActive()) {
			return obj1.isActive() ? -1 : 1;
		}

		return getDate(obj1).compareTo(getDate(obj2));
    }

    public Date getDate(Component x_component) {
    	if(x_component instanceof HasDueDate && ((HasDueDate)x_component).getDueDate() != null) {
			return ((HasDueDate) x_component).getDueDate();
		}

		return x_component.getModifiedDate();
	}
}
