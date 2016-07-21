package util;

import data.Item;

import java.util.Comparator;

public class ModifiedDateComparator implements Comparator<Item> {
    public int compare(Item item1, Item item2) {
		return item2.getModifiedDate().compareTo(item1.getModifiedDate());
    }
}
