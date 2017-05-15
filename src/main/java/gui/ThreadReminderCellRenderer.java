package gui;

import data.*;
import data.Thread;

import java.awt.Component;
import java.util.List;

import static data.LookupHelper.getAllActiveReminders;

class ThreadReminderCellRenderer extends DataItemsCellRenderer<Thread, Reminder> {
	private boolean o_onlyDueReminders;

	ThreadReminderCellRenderer(Thread p_thread) {
		super(p_thread);
    }

	@Override
	List<Reminder> getDataItems(Thread p_component) {
		return getAllActiveReminders(p_component, o_onlyDueReminders);
	}

	@Override
	void customSetup(Reminder p_reminder, Component p_awtComponent, boolean p_isSelected) {
		if(!p_isSelected) {
			p_awtComponent.setBackground(getColourForTime(p_reminder.getDueDate()));
		}
	}

	void setOnlyDueReminders(boolean p_onlyDueReminders) {
		o_onlyDueReminders = p_onlyDueReminders;
	}
}