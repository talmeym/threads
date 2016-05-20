import data.*;
import data.Thread;
import gui.WindowManager;
import util.*;

import java.io.*;
import java.util.Date;

public class Threads
{
    public static void main(String[] args)
    {
        TimeUpdater.getInstance().start();
		final String filePath = args.length > 0 ? args[0] : "threads.xml";
		final Thread x_topThread = new File(filePath).exists() ? Loader.loadDocument(filePath) : new Thread(new Date(), true, "Welcome to Threads", null, null);

		WindowManager.getInstance().addWindowListener(new WindowManager.WindowListener() {
			@Override
			public void lastWindowClosing() {
				TimedSaver.getInstance().stopRunning();
				Saver.saveDocument(x_topThread, filePath);
				System.exit(0);
			}
		});

		WindowManager.getInstance().showNavigationTreeWindow(x_topThread);
		WindowManager.getInstance().openComponentWindow(x_topThread, true, 0);
        TimedSaver.getInstance().setThread(x_topThread, filePath);
		SystemTrayUtil.initialise(x_topThread);
	}
}
