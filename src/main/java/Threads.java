import data.*;
import data.Thread;
import gui.WindowManager;
import util.*;

import java.awt.event.*;
import java.io.File;

import static util.Settings.registerForSetting;

public class Threads {
    public static void main(String[] args) {
		FontUtil.standardiseFontSizes();

		File x_dataFile = new File(args.length > 0 ? args[0] : "threads.xml");
		File x_settingsFile = new File(x_dataFile.getParentFile(), x_dataFile.getName() + ".properties");
		Thread x_topThread = x_dataFile.exists() ? Loader.loadDocumentThread(x_dataFile) : new Thread("Threads");

		x_topThread.addComponentChangeListener(e -> System.out.println("Change: '" + e.getSource().getText() + "': " + e.getField() + ", " + e.getOldValue() + " -> " + e.getNewValue()));

		TimedUpdater.initialise();
		TimedSaver.initialise(x_topThread, x_dataFile);
		NotificationUpdater.initialise(x_topThread);
		SystemTrayUtil.initialise(x_topThread);

		Settings.load(x_settingsFile);

		boolean x_googleEnabled = registerForSetting(Settings.s_GOOGLE, (p_name, p_value) -> { }, false);
		GoogleSyncer.initialise(x_topThread, x_googleEnabled);

		WindowManager.initialise(x_topThread, new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				TimedSaver.getInstance().stopRunning();
				GoogleSyncer.getInstance().stopRunning();
				TimedUpdater.getInstance().stopRunning();
				Saver.saveDocument(x_topThread, x_dataFile);
				Settings.save(x_settingsFile);
				System.exit(0);
			}
		});
	}

}
