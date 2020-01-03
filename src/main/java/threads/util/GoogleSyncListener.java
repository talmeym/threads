package threads.util;

import threads.data.HasDueDate;

import java.util.List;

public interface GoogleSyncListener extends ActivityListener {
	void googleSyncStarted();
	void googleSynced();
	void googleSynced(List<HasDueDate> p_hasDueDates, boolean p_import);
}
