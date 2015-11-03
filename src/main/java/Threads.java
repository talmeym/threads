import gui.WindowManager;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import util.TimeUpdater;
import util.TimedSaver;
import data.Loader;
import data.Saver;
import data.ThreadGroup;

public class Threads
{
    public static void main(final String[] args)
    {
        TimeUpdater.getInstance().start();
        final ThreadGroup x_topThreadGroup = Loader.loadDocument(args[0]); 
        
        Window x_window = WindowManager.getInstance().openComponentWindow(x_topThreadGroup, false);
        
        x_window.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we)
            {
                TimedSaver.getInstance().stopRunning();
                Saver.saveDocument(x_topThreadGroup, args[0]);
                System.exit(0);
            }
        });
        
        TimedSaver.getInstance().setThreadGroup(x_topThreadGroup, args[0]);
    }
}
