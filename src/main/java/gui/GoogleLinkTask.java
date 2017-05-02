package gui;

import data.*;
import util.GoogleUtil;

import javax.swing.*;
import java.util.*;

public class GoogleLinkTask extends SwingWorker<Void, Void> {
	private final List<Item> o_items;
	private final List<Reminder> o_reminders;
	private final ProgressCallBack[] o_callbacks;

	public GoogleLinkTask(List<Item> p_items, ProgressCallBack... p_callbacks) {
		this.o_items = p_items;
		this.o_reminders = null;
		this.o_callbacks = p_callbacks;
	}

	public GoogleLinkTask(Reminder p_reminder, ProgressCallBack... p_callbacks) {
		this.o_items = null;
		this.o_reminders = Collections.singletonList(p_reminder);
		this.o_callbacks = p_callbacks;
	}

	@Override
	protected Void doInBackground() throws Exception {
		if(o_items != null) {
			GoogleUtil.linkItemsToGoogle(o_items, o_callbacks);
		} else {
			GoogleUtil.linkRemindersToGoogle(o_reminders, o_callbacks);
		}

		return null;
	}
}
