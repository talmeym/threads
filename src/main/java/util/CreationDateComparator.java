package util;

import data.*;

import java.util.Comparator;

class CreationDateComparator implements Comparator<Item> {
    public int compare(Item item1, Item item2) {
		return item2.getCreationDate().compareTo(item1.getCreationDate());
    }
}
