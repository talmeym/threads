package util;

import data.*;
import data.Thread;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleSyncer extends TimedActivity<GoogleSyncListener> {
    private static GoogleSyncer s_INSTANCE = null;
	private final boolean o_enabled;

	public static void initialise(Thread p_topLevelThread, boolean p_enabled) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise google syncer twice");
		}

		s_INSTANCE = new GoogleSyncer(p_topLevelThread, p_enabled);
	}

	public static GoogleSyncer getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Google syncer not initialised");
		}

		return s_INSTANCE;
	}

    private GoogleSyncer(Thread p_topThread, boolean p_enabled) {
    	super(120000, true);
		o_enabled = p_enabled;

		if(p_enabled) {
			try {
				GoogleUtil.initialise(p_topThread);
			} catch (GeneralSecurityException | IOException e) {
				throw new RuntimeException("Error initialising google util", e);
			}

			setDaemon(true);
			start();
		}
    }

	void action() {
			GoogleUtil.syncWithGoogle();
		}

	@Override
	void informOfStart(GoogleSyncListener p_listener) {
		p_listener.googleSyncStarted();
	}

	@Override
	void informOfFinish(GoogleSyncListener p_listener) {
		p_listener.googleSynced();
	}

	void componentsSynced(List<HasDueDate> p_hasDueDates) {
		getListeners().forEach(l -> l.googleSynced(p_hasDueDates));
	}

	public boolean isGoogleEnabled() {
		return o_enabled;
	}
}
