package util;

import data.*;
import data.ThreadGroup;

import java.lang.Thread;

public class TimedSaver extends Thread
{
    private static TimedSaver s_INSTANCE = null;

    private static final int s_updateFrequency = 10000; 
    
    public static TimedSaver getInstance()
    {
        if(s_INSTANCE == null)
        {
            s_INSTANCE = new TimedSaver();
        }
        
        return s_INSTANCE;
    }
    
    private ThreadGroup o_topThreadGroup;
    
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
                Thread.sleep(s_updateFrequency);
                
                if(o_topThreadGroup != null)
                {
                    Saver.saveDocument(o_topThreadGroup, o_saveLocation);
                }
                
            } catch (InterruptedException e)
            {
                // do nothing
            }            
        }
    }
    
    public synchronized void setThreadGroup(ThreadGroup p_topThreadGroup, String p_saveLocation)
    {
        o_topThreadGroup = p_topThreadGroup;
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
