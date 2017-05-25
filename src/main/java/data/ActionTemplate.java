package data;

import java.util.*;

public class ActionTemplate {
	private static final String s_TOKEN = "[TOKEN]";

	private String o_name;
	private String o_tokenPrompt;
	private String o_tokenDefault;
	private String o_textTemplate;
	private List<ReminderTemplate> o_reminderTemplates;

	public ActionTemplate(String o_name, String o_tokenPrompt, String p_tokenDefault, String o_textTemplate, List<ReminderTemplate> o_reminderTemplates) {
		this.o_name = o_name;
		this.o_tokenPrompt = o_tokenPrompt;
		o_tokenDefault = p_tokenDefault;
		this.o_textTemplate = o_textTemplate;
		this.o_reminderTemplates = o_reminderTemplates;
	}

	public String getName() {
		return o_name;
	}

	public String getTokenPrompt() {
		return o_tokenPrompt;
	}

	public String getTokenDefault() {
		return o_tokenDefault;
	}

	public static class ReminderTemplate {
		private String o_textTemplate;
		private long o_offset;

		public ReminderTemplate(String o_textTemplate, long o_offset) {
			this.o_textTemplate = o_textTemplate;
			this.o_offset = o_offset;
		}
	}

	public Item buildAndAddToThread(Date p_startingDate, String p_replacementText, Thread p_thread) {
		Item x_item = new Item(getReplacementText(this.o_textTemplate, p_replacementText), p_startingDate);
		p_thread.addThreadItem(x_item);
		o_reminderTemplates.forEach(r -> x_item.addReminder(new Reminder(getReplacementText(r.o_textTemplate, p_replacementText), new Date(p_startingDate.getTime() + r.o_offset))));
		return x_item;
	}

	private String getReplacementText(String p_textTemplate, String p_replacementText) {
		return p_textTemplate.replace(s_TOKEN, p_replacementText);
	}

	public String toString() {
		return o_name;
	}
}
