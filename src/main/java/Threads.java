import data.*;
import data.Thread;
import gui.*;
import util.*;

import java.io.*;

public class Threads {
    public static void main(String[] args) {
        TimeUpdater.getInstance().start();
		String x_filePath = args.length > 0 ? args[0] : "threads.xml";
		Thread x_topThread = new File(x_filePath).exists() ? Loader.loadDocument(x_filePath) : new Thread("Welcome to Threads");

        TimedSaver.getInstance().setThread(x_topThread, x_filePath);
		SystemTrayUtil.initialise(x_topThread);

		WindowManager.initialise(x_topThread, x_filePath);
	}
}
