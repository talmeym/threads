package util;

import data.*;

import java.util.Comparator;

public class DueDateComparator implements Comparator
{
    public int compare(Object obj1, Object obj2)
    {
        if(obj1 instanceof Item)
        {
            Item x_item1 = (Item) obj1;
            Item x_item2 = (Item) obj2;
        
            if(x_item1.getDueDate() != null)
            {
                return x_item1.getDueDate().compareTo(x_item2.getDueDate());
            }

            throw new IllegalArgumentException("Can only due date compare action items");
        }
        
        if(obj1 instanceof Reminder)
        {
            Reminder x_reminder1 = (Reminder) obj1;
            Reminder x_reminder2 = (Reminder) obj2;
        
            return x_reminder1.getDueDate().compareTo(x_reminder2.getDueDate());
        }
        
        throw new IllegalArgumentException("Invalid comparison object: " + obj1);
    }
}
