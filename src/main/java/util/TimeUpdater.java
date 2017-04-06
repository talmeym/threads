package util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeUpdater extends Thread {
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

	private final Object lockObj = new Object();
	private final List<TimeUpdateListener> o_updateListeners = new ArrayList<TimeUpdateListener>();
	private boolean o_continueRunning = true;
	private long o_nextSync = System.currentTimeMillis();

    private TimeUpdater() {
		setDaemon(true);
		start();
    }
    
    public synchronized void addTimeUpdateListener(TimeUpdateListener p_listener) {
    	synchronized(o_updateListeners) {
			o_updateListeners.add(p_listener);
		}
    }

    public long nextSync() {
		synchronized(lockObj) {
			return o_nextSync;
		}
	}

    public void run() {
        while(continueRunning()) {
            try {
                sleep(500);

				if (o_nextSync < System.currentTimeMillis()) {
					synchronized(o_updateListeners) {
						for (TimeUpdateListener o_updateListener : o_updateListeners) {
							o_updateListener.timeUpdate();
						}

						System.out.println("Time Update: " + new Date());
					}

					synchronized(lockObj) {
						o_nextSync += s_frequency;
					}
				}
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

	public synchronized void stopRunning() {
		o_continueRunning = false;
	}

	private synchronized boolean continueRunning() {
		return o_continueRunning;
	}
}
