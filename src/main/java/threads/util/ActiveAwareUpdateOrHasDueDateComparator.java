package threads.util;

import threads.data.Component;
import threads.data.HasDueDate;

import java.util.Comparator;
import java.util.Date;

public class ActiveAwareUpdateOrHasDueDateComparator implements Comparator<Component>
{
    public int compare(Component obj1, Component obj2) {
		if(obj1.isActive() != obj2.isActive()) {
			return obj1.isActive() ? -1 : 1;
		}

		return getDate(obj1).compareTo(getDate(obj2));
    }

    private Date getDate(Component x_component) {
    	if(x_component instanceof HasDueDate && ((HasDueDate)x_component).getDueDate() != null) {
			return ((HasDueDate) x_component).getDueDate();
		}

		return x_component.getModifiedDate();
	}
}
