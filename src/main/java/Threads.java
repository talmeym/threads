import data.*;
import data.ThreadGroup;
import gui.WindowManager;
import util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class Threads
{
    public static void main(String[] args)
    {
        TimeUpdater.getInstance().start();
		final String filePath = args.length > 0 ? args[0] : "threads.xml";
		final ThreadGroup x_topThreadGroup = args.length > 0 ? Loader.loadDocument(filePath) : new ThreadGroup(new Date(), true, "Welcome to Threads", null, null);
        Window x_window = WindowManager.getInstance().openComponentWindow(x_topThreadGroup, false, 0);
        
        x_window.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we)
            {
                TimedSaver.getInstance().stopRunning();
                Saver.saveDocument(x_topThreadGroup, filePath);
                System.exit(0);
            }
        });
        
        TimedSaver.getInstance().setThreadGroup(x_topThreadGroup, filePath);
    }
}
