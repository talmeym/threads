package util;

import data.Thread;

import java.io.File;
import java.util.*;

import static data.Saver.saveDocument;

public class TimedSaver extends java.lang.Thread {
    private static TimedSaver s_INSTANCE = null;
    private static final int s_frequency = 29000;

	public static void initialise(Thread p_topLevelThread, File p_dataFile) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise timed saver twice");
		}

		s_INSTANCE = new TimedSaver(p_topLevelThread, p_dataFile);
	}

	public static TimedSaver getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Timed saver not initialised");
		}

		return s_INSTANCE;
    }

	private final Thread o_topThread;
    private final File o_saveLocation;
    private boolean continueRunning = true;
	private List<TimedSaveListener> o_saveListeners = new ArrayList<TimedSaveListener>();

    private TimedSaver(Thread p_topThread, File p_saveLocation) {
		o_topThread = p_topThread;
		o_saveLocation = p_saveLocation;
		setDaemon(true);
        start();
    }

	public synchronized void addTimedSaveListener(TimedSaveListener p_listener) {
		o_saveListeners.add(p_listener);
		p_listener.saved();
	}

	public void run() {
        while(continueRunning()) {
            try {
                java.lang.Thread.sleep(s_frequency);

                if(o_topThread != null) {
					saving();
                    saveDocument(o_topThread, o_saveLocation);
                	java.lang.Thread.sleep(1000);
					saved();
                }
            } catch (InterruptedException e) {
                // do nothing
            }            
        }
    }

	public void saving() {
		for(TimedSaveListener x_listener: o_saveListeners) {
			x_listener.saveStarted();
		}
	}

	public void saved() {
		for(TimedSaveListener x_listener: o_saveListeners) {
			x_listener.saved();
		}
	}

    public synchronized void stopRunning() {
        continueRunning = false;
    }
    
    private synchronized boolean continueRunning() {
        return continueRunning;
    }
}
