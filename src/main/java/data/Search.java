package data;

import java.util.UUID;

public class Search {
	private UUID o_id;
	private String o_text;

	boolean check(Component p_component) {
		if(o_id != null && !p_component.getId().equals(o_id)) {
			return false;
		}

		if(o_text != null && !p_component.getText().toLowerCase().contains(o_text.toLowerCase())) {
			return false;
		}

		return true;
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

		public Search build() {
			Search x_search = o_search;
			o_search = new Search();
			return x_search;
		}
	}
}
