package threads.data;

import java.util.UUID;

public class Search {
	private UUID o_id;
	private String o_text;
	private boolean o_includeNotes = false;
	private boolean o_caseSensitive = false;

	boolean check(Component p_component) {
		if(o_id != null && checkId(p_component)) {
			return true;
		}

		if(o_text != null && checkText(p_component)) {
			return true;
		}

        if(o_includeNotes && p_component instanceof Item && checkNotes((Item) p_component)) {
			return true;
		}

		return false;
	}

    private boolean checkId(Component p_component) {
        return p_component.getId().equals(o_id);
    }

    private boolean checkText(Component p_component) {
        return o_caseSensitive ? p_component.getText().contains(o_text) : p_component.getText().toLowerCase().contains(o_text.toLowerCase());
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

		public Search build() {
			Search x_search = o_search;
			o_search = new Search();
			return x_search;
		}
	}
}
