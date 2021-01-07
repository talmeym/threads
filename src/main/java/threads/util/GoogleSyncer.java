package threads.util;

import com.google.api.services.calendar.model.Event;
import threads.data.HasDueDate;

import java.util.List;

import static threads.util.GoogleUtil.syncWithGoogle;

public class GoogleSyncer extends TimedActivity<GoogleSyncListener> {
    private static GoogleSyncer s_INSTANCE = null;

	public static void initialise() {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise google syncer twice");
		}

		s_INSTANCE = new GoogleSyncer();
	}

	public static GoogleSyncer getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Google syncer not initialised");
		}

		return s_INSTANCE;
	}

    private GoogleSyncer() {
    	super(120000, true);

		try {
			GoogleUtil.initialise();
		} catch (Exception e) {
			throw new IllegalStateException("Error initialising google util", e);
		}

		setDaemon(true);
		start();
    }

	void action() {
			syncWithGoogle();
		}

	@Override
	void informOfStart(GoogleSyncListener p_listener) {
		p_listener.googleSyncStarted();
	}

	@Override
	void informOfFinish(GoogleSyncListener p_listener) {
		p_listener.googleSyncFinished();
	}

	void componentsLinked(List<HasDueDate> p_hasDueDates) {
		getListeners().forEach(l -> l.itemsLinked(p_hasDueDates));
	}

	void componentsSynced(List<HasDueDate> p_hasDueDates, List<Event> p_events) {
		getListeners().forEach(l -> l.googleSynced(p_hasDueDates, p_events));
	}
}
