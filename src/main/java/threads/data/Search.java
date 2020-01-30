package threads.data;

import java.util.UUID;

public class Search {
	private UUID o_id;
	private ComponentType o_componentType;
	private String o_text;
	private boolean o_includeNotes = false;
	private boolean o_caseSensitive = false;
	private boolean o_exactText = false;

	boolean check(Component p_component) {
		if(o_id != null && checkId(p_component)) {
			return true;
		}

		boolean x_typeOk = o_componentType == null || checkType(p_component);

		if(o_text != null && checkText(p_component) && x_typeOk) {
			return true;
		}

        if(o_includeNotes && p_component instanceof Item && checkNotes((Item) p_component) && x_typeOk) {
			return true;
		}

		return false;
	}

	private boolean checkId(Component p_component) {
        return p_component.getId().equals(o_id);
    }

	private boolean checkType(Component p_component) {
		return p_component.getType() == o_componentType;
	}

    private boolean checkText(Component p_component) {
		String x_text = p_component.getText();
		boolean x_response = o_caseSensitive ? (o_exactText ? x_text.equals(o_text) : x_text.contains(o_text)) : (o_exactText ? x_text.equalsIgnoreCase(o_text) : x_text.toLowerCase().contains(o_text.toLowerCase()));
		return x_response;
    }

    private boolean checkNotes(Item p_component) {
        return p_component.getNotes() != null && (o_caseSensitive ? p_component.getNotes().contains(o_text) : p_component.getNotes().toLowerCase().contains(o_text.toLowerCase()));

    }

    public static class Builder {
		private Search o_search = new Search();

		public Builder withId(UUID p_uuid) {
			o_search.o_id = p_uuid;
			return this;
		}

		public Builder ofType(ComponentType p_componentType) {
			o_search.o_componentType = p_componentType;
			return this;
		}

		public Builder withText(String p_text) {
			o_search.o_text = p_text;
			return this;
		}

		public Builder includeNotes(boolean p_includeNotes) {
			o_search.o_includeNotes = p_includeNotes;
			return this;
		}

		public Builder caseSensitive(boolean p_caseSensitive) {
		    o_search.o_caseSensitive = p_caseSensitive;
		    return this;
        }

		public Builder exactText(boolean p_exactText) {
		    o_search.o_exactText = p_exactText;
		    return this;
        }

		public Search build() {
			Search x_search = o_search;
			o_search = new Search();
			return x_search;
		}
	}
}
