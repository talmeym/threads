import data.*;
import data.Thread;
import gui.WindowManager;
import util.*;

import java.awt.event.*;
import java.io.File;

public class Threads {
    public static void main(String[] args) {
		FontUtil.standardiseFontSizes();

		File x_dataFile = new File(args.length > 0 ? args[0] : "threads.xml");
		File x_settingsFile = new File(x_dataFile.getParentFile(), "threads.properties");
		Thread x_topThread = x_dataFile.exists() ? Loader.loadDocumentThread(x_dataFile) : new Thread("Threads");

		TimeUpdater.initialise();
		TimedSaver.initialise(x_topThread, x_dataFile);
		NotificationUpdater.initialise(x_topThread);
		SystemTrayUtil.initialise(x_topThread);

		boolean googleEnabled = Settings.load(x_settingsFile);
		GoogleSyncer.initialise(x_topThread, googleEnabled);

		WindowManager.initialise(x_topThread, new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				TimedSaver.getInstance().stopRunning();
				GoogleSyncer.getInstance().stopRunning();
				TimeUpdater.getInstance().stopRunning();
				Saver.saveDocument(x_topThread, x_dataFile);
				Settings.save(x_settingsFile);
				System.exit(0);
			}
		});
	}

}
