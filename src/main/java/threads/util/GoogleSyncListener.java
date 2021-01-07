package threads.util;

import com.google.api.services.calendar.model.Event;
import threads.data.HasDueDate;

import java.util.List;

public interface GoogleSyncListener extends ActivityListener {
	void googleSyncStarted();
	void googleSyncFinished();
	void itemsLinked(List<HasDueDate> p_hasDueDatesSynced);
	void googleSynced(List<HasDueDate> p_itemsCreated, List<Event> p_eventsDeleted);
}
