package util;

import data.Component;

import java.util.Comparator;

public class CreationDateComparator implements Comparator
{
    public int compare(Object obj1, Object obj2)
    {
        if(obj1 instanceof Component)
        {
            Component x_item1 = (Component) obj1;
            Component x_item2 = (Component) obj2; 
            
            return x_item2.getCreationDate().compareTo(x_item1.getCreationDate());
        }
        
        throw new IllegalArgumentException("Invalid compare object: " + obj1);
    }
}
