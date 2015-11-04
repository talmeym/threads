import data.*;
import data.Thread;
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
		final Thread x_topThread = args.length > 0 ? Loader.loadDocument(filePath) : new Thread(new Date(), true, "Welcome to Threads", null, null);
        Window x_window = WindowManager.getInstance().openComponentWindow(x_topThread, false, 0);
        
        x_window.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we)
            {
                TimedSaver.getInstance().stopRunning();
                Saver.saveDocument(x_topThread, filePath);
                System.exit(0);
            }
        });
        
        TimedSaver.getInstance().setThread(x_topThread, filePath);
    }
}
