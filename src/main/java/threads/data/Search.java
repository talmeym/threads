package threads.data;

import java.util.UUID;

public class Search {
	private UUID o_id;
	private String o_text;
	private String o_notes;

	boolean check(Component p_component) {
		if(o_id != null && p_component.getId().equals(o_id)) {
			return true;
		}

		if(o_text != null && p_component.getText().toLowerCase().contains(o_text.toLowerCase())) {
			return true;
		}

        if(o_notes != null && p_component instanceof Item && ((Item) p_component).getNotes() != null && ((Item) p_component).getNotes().contains(o_notes)) {
			return true;
		}

		return false;
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

		public Builder withNotes(String p_notes) {
			o_search.o_notes = p_notes;
			return this;
		}

		public Search build() {
			Search x_search = o_search;
			o_search = new Search();
			return x_search;
		}
	}
}
