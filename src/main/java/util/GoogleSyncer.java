package util;

import data.Thread;

import java.util.*;

public class GoogleSyncer extends java.lang.Thread {
    private static GoogleSyncer s_INSTANCE = null;
    private static final int s_updateFrequency = 29000;

    public static GoogleSyncer getInstance() {
        if(s_INSTANCE == null) {
            s_INSTANCE = new GoogleSyncer();
        }

        return s_INSTANCE;
    }

    private Thread o_topThread;
    private boolean continueRunning;
	private List<GoogleSyncListener> o_googleListeners;

    private GoogleSyncer() {
		o_googleListeners = new ArrayList<GoogleSyncListener>();
        continueRunning = true;
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

					GoogleUtil.syncWithGoogle(o_topThread);

					googleSynced();
				}

                java.lang.Thread.sleep(s_updateFrequency);
            } catch (InterruptedException e) {
                // do nothing
            }            
        }
    }

	public void googleSynced() {
		for(GoogleSyncListener x_listener: o_googleListeners) {
			x_listener.googleSynced();
		}
	}

	public synchronized void setThread(Thread p_topThread) {
        o_topThread = p_topThread;
    }
    
    public synchronized void stopRunning() {
        continueRunning = false;
    }
    
    private synchronized boolean continueRunning() {
        return continueRunning;
    }
}
