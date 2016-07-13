package util;

import java.util.*;

public class TimeUpdater extends java.lang.Thread {
    private static TimeUpdater s_INSTANCE;
    private static final int s_frequency = 60000;

	public static void initialise() {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise time updater twice");
		}

		s_INSTANCE = new TimeUpdater();
	}

	public static TimeUpdater getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Time updater not initialised");
		}

		return s_INSTANCE;
	}

	private boolean continueRunning = true;
	private List<TimeUpdateListener> o_updateListeners = new ArrayList<TimeUpdateListener>();
    
    private TimeUpdater() {
		setDaemon(true);
		start();
    }
    
    public synchronized void addTimeUpdateListener(TimeUpdateListener p_listener) {
        o_updateListeners.add(p_listener);
    }
    
    public void run() {
        while(continueRunning()) {
            try {
                java.lang.Thread.sleep(s_frequency);

                synchronized(this) {
					for (TimeUpdateListener o_updateListener : o_updateListeners) {
						o_updateListener.timeUpdate();
					}
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

	public synchronized void stopRunning() {
		continueRunning = false;
	}

	private synchronized boolean continueRunning() {
		return continueRunning;
	}
}
