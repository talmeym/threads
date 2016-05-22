package util;

import java.util.*;

public class TimeUpdater extends java.lang.Thread {
    private static final int s_updateFrequency = 30000;
    private static final TimeUpdater s_INSTANCE = new TimeUpdater();
    
    public static TimeUpdater getInstance() {
        return s_INSTANCE;
    }
    
    private List<TimeUpdateListener> o_updateListeners;
    
    TimeUpdater() {
        o_updateListeners = new ArrayList<TimeUpdateListener>();
    }
    
    public synchronized void addTimeUpdateListener(TimeUpdateListener p_listener) {
        o_updateListeners.add(p_listener);
    }
    
    public void run() {
        while(true) {
            try {
                java.lang.Thread.sleep(s_updateFrequency);

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
}
