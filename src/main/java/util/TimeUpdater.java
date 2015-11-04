package util;

import java.util.*;

public class TimeUpdater extends java.lang.Thread
{
    private static final int s_updateFrequency = 60000;
    
    private static final TimeUpdater s_INSTANCE = new TimeUpdater();
    
    public static TimeUpdater getInstance()
    {
        return s_INSTANCE;
    }
    
    private List o_updateListeners;
    
    TimeUpdater()
    {
        super();
        
        o_updateListeners = new ArrayList();
    }
    
    public synchronized void addTimeUpdateListener(TimeUpdateListener p_listener)
    {
        o_updateListeners.add(p_listener);
    }
    
    public synchronized void removeTimeUpdateListener(TimeUpdateListener p_listener)
    {
        o_updateListeners.remove(p_listener);
    }
    
    public void run()
    {
        while(true)
        {
            try
            {
                java.lang.Thread.sleep(s_updateFrequency);
            
                synchronized(this)
                {
                
                    for(int i = 0; i < o_updateListeners.size(); i++)
                    {
                        TimeUpdateListener x_listener = (TimeUpdateListener) o_updateListeners.get(i);                        
                        x_listener.timeUpdate();
                    }
                }
            } 
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
