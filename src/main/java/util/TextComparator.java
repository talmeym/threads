package util;

import data.*;

import java.util.Comparator;

public class TextComparator <TYPE extends Component> implements Comparator<TYPE> {
    public int compare(TYPE obj1, TYPE obj2) {
		return obj1.getText().compareTo(obj2.getText());
	}
}
