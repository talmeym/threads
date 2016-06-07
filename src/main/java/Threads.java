import data.*;
import data.Thread;
import gui.*;
import util.*;

import java.io.*;
import java.util.Properties;

public class Threads {
    public static void main(String[] args) {
        TimeUpdater.getInstance().start();
		String x_filePath = args.length > 0 ? args[0] : "threads.xml";
		File x_file = new File(x_filePath);
		Thread x_topThread = x_file.exists() ? Loader.loadDocumentThread(x_filePath) : new Thread("Threads");
		Properties x_settings = x_file.exists() ? Loader.loadDocumentSettings(x_filePath) : new Properties();

		// TODO remove this soon
		x_topThread.setText("Threads");

        TimedSaver.getInstance().setThread(x_topThread, x_filePath);
		SystemTrayUtil.initialise(x_topThread);
		WindowManager.initialise(x_topThread, x_filePath, x_settings);
	}
}
