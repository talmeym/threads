package util;

import data.Thread;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class GoogleSyncer extends java.lang.Thread {
    private static GoogleSyncer s_INSTANCE = null;
    private static final int s_frequency = 120000;

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

	private final Object o_lockObj = new Object();
	private final List<GoogleSyncListener> o_googleListeners = new ArrayList<>();
    private boolean o_continueRunning = true;
    private long o_nextSync = System.currentTimeMillis();

    private GoogleSyncer(Thread p_topThread, boolean p_enabled) {
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

	public void addGoogleSyncListener(GoogleSyncListener p_listener) {
        synchronized (o_googleListeners) {
            o_googleListeners.add(p_listener);
        }

		p_listener.googleSynced();
	}

	public long nextSync() {
        synchronized(o_lockObj) {
            return o_nextSync;
        }
    }

	public void run() {
		while(continueRunning()) {
			try {
				sleep(1000);

                if (o_nextSync < System.currentTimeMillis()) {
                    synchronized (o_googleListeners) {
                        googleSyncing();
                        GoogleUtil.syncWithGoogle();
                        googleSynced();
                    }

                    synchronized(o_lockObj) {
                        while(o_nextSync < System.currentTimeMillis()) {
                            o_nextSync += s_frequency;
                        }
                    }
                }
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }

	private void googleSyncing() {
		for(GoogleSyncListener x_listener: o_googleListeners) {
			x_listener.googleSyncStarted();
		}
	}

	private void googleSynced() {
        for (GoogleSyncListener x_listener : o_googleListeners) {
            x_listener.googleSynced();
        }
	}

	void updateGoogleListeners() {
        synchronized (o_googleListeners) {
            googleSynced();
        }
    }

    public synchronized void stopRunning() {
        o_continueRunning = false;
    }
    
    private synchronized boolean continueRunning() {
        return o_continueRunning;
    }
}
