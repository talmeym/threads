package util;

import data.Component;

import java.util.Comparator;

public class TextComparator implements Comparator
{
    public int compare(Object obj1, Object obj2)
    {
        if(obj1 instanceof Component)
        {
            Component x_item1 = (Component) obj1;
            Component x_item2 = (Component) obj2; 
            
            return x_item1.getText().compareTo(x_item2.getText());
        }
        
        throw new IllegalArgumentException("Invalid compare object: " + obj1);
    }
}
