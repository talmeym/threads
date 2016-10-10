package util;

import data.*;

import java.util.*;

public class TypeAwareTextComparator<TYPE extends Component> implements Comparator<TYPE> {
    public int compare(TYPE obj1, TYPE obj2) {
		String x_type1 = obj1.getType();
		String x_type2 = obj2.getType();

		if(!x_type1.equals(x_type2)) {
			int result = x_type2.compareTo(x_type1);
			List<String> x_types = Arrays.asList(x_type1, x_type2);

			if(x_types.contains("Thread") && x_types.contains("Update")) {
				result *= -1;
			}

			return result;
		}

		if(obj1.getText().equals(obj2.getText())) {
			if (x_type1.equals("Update")) {
				return obj1.getModifiedDate().compareTo(obj2.getModifiedDate());
			}

			if (x_type1.equals("Action")) {
				return ((Item) obj1).getDueDate().compareTo(((Item) obj2).getDueDate());
			}
		}

		// Thread
		return obj1.getText().compareTo(obj2.getText());
	}
}
