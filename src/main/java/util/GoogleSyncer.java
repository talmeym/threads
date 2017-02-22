package util;

import data.Thread;

import java.util.*;

public class GoogleSyncer extends java.lang.Thread {
    private static GoogleSyncer s_INSTANCE = null;
    private static final int s_frequency = 59000;

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
    private boolean continueRunning = true;
	private List<GoogleSyncListener> o_googleListeners = new ArrayList<GoogleSyncListener>();

    private GoogleSyncer(Thread p_topThread) {
		o_topThread = p_topThread;
		setDaemon(true);
		start();
    }

	public synchronized void addGoogleSyncListener(GoogleSyncListener p_listener) {
		o_googleListeners.add(p_listener);
		p_listener.googleSynced();
	}

	public void run() {
        while(continueRunning()) {
            try {
                if(o_topThread != null) {
					java.lang.Thread.sleep(1000);

					googleSyncing();
					GoogleUtil.syncWithGoogle(o_topThread);
					googleSynced();
				}

                java.lang.Thread.sleep(s_frequency);
            } catch (InterruptedException e) {
                // do nothing
            }            
        }
    }

	public void googleSyncing() {
		for(GoogleSyncListener x_listener: o_googleListeners) {
			x_listener.googleSyncStarted();
		}
	}

	public void googleSynced() {
		for(GoogleSyncListener x_listener: o_googleListeners) {
			x_listener.googleSynced();
		}
	}
    
    public synchronized void stopRunning() {
        continueRunning = false;
    }
    
    private synchronized boolean continueRunning() {
        return continueRunning;
    }
}
