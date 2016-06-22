package util;

import data.Thread;

public class TimedGoogleSyncer extends java.lang.Thread {
    private static TimedGoogleSyncer s_INSTANCE = null;
    private static final int s_updateFrequency = 20000;

    public static TimedGoogleSyncer getInstance() {
        if(s_INSTANCE == null) {
            s_INSTANCE = new TimedGoogleSyncer();
        }

        return s_INSTANCE;
    }

    private Thread o_topThread;
    private boolean continueRunning;

    private TimedGoogleSyncer() {
        continueRunning = true;
        start();
    }
    
    public void run() {
        while(continueRunning()) {
            try {
                java.lang.Thread.sleep(s_updateFrequency);

                if(o_topThread != null) {
					GoogleUtil.syncWithGoogle(o_topThread);
                }

            } catch (InterruptedException e) {
                // do nothing
            }            
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
