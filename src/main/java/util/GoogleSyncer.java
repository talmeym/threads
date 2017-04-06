package util;

import data.Thread;

import java.util.*;

public class GoogleSyncer extends java.lang.Thread {
    private static GoogleSyncer s_INSTANCE = null;
    private static final int s_frequency = 120000;

	public static void initialise(Thread p_topLevelThread) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise google syncer twice");
		}

		s_INSTANCE = new GoogleSyncer(p_topLevelThread);
	}

	public static GoogleSyncer getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Google syncer not initialised");
		}

		return s_INSTANCE;
	}

    private final Thread o_topThread;
	private final List<GoogleSyncListener> o_googleListeners = new ArrayList<GoogleSyncListener>();
    private boolean o_continueRunning = true;
    private long o_nextSync = System.currentTimeMillis();

    private GoogleSyncer(Thread p_topThread) {
		o_topThread = p_topThread;
		setDaemon(true);
		start();
    }

	public void addGoogleSyncListener(GoogleSyncListener p_listener) {
        synchronized (o_googleListeners) {
            o_googleListeners.add(p_listener);
        }

		p_listener.googleSynced();
	}

	public long nextSync() {
        synchronized(o_topThread) {
            return o_nextSync;
        }
    }

	public void run() {
		while(continueRunning()) {
			try {
				sleep(500);

                if (o_nextSync < System.currentTimeMillis()) {
                    synchronized (o_googleListeners) {
                        googleSyncing();
                        GoogleUtil.syncWithGoogle(o_topThread);
                        googleSynced();
                    }

                    synchronized(o_topThread) {
                        o_nextSync += s_frequency;
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
