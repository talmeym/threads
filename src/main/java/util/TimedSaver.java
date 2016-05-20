package util;

import data.*;
import data.Thread;

public class TimedSaver extends java.lang.Thread
{
    private static TimedSaver s_INSTANCE = null;

    private static final int s_updateFrequency = 30000;
    
    public static TimedSaver getInstance()
    {
        if(s_INSTANCE == null)
        {
            s_INSTANCE = new TimedSaver();
        }
        
        return s_INSTANCE;
    }
    
    private Thread o_topThread;
    
    private String o_saveLocation;
    
    private boolean continueRunning;
    
    private TimedSaver()
    {
        continueRunning = true;
        start();
    }
    
    public void run()
    {
        while(continueRunning())
        {
            try
            {
                java.lang.Thread.sleep(s_updateFrequency);
                
                if(o_topThread != null)
                {
                    Saver.saveDocument(o_topThread, o_saveLocation);
                }
                
            } catch (InterruptedException e)
            {
                // do nothing
            }            
        }
    }
    
    public synchronized void setThread(Thread p_topThread, String p_saveLocation)
    {
        o_topThread = p_topThread;
        o_saveLocation = p_saveLocation;
    }
    
    public synchronized void stopRunning()
    {
        continueRunning = false;
    }
    
    private synchronized boolean continueRunning()
    {
        return continueRunning;
    }
}
