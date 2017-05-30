import data.Loader.Configuration;
import data.*;
import data.Thread;
import gui.WindowManager;
import util.*;

import java.awt.event.*;
import java.io.File;

import static data.Loader.loadConfiguration;
import static gui.Actions.*;
import static util.Settings.*;

public class Threads {
    public static void main(String[] args) {
		FontUtil.standardiseFontSizes();

		File x_dataFile = new File(args.length > 0 ? args[0] : "threads.xml");
		File x_settingsFile = new File(x_dataFile.getParentFile(), x_dataFile.getName() + ".properties");

		Configuration x_config = x_dataFile.exists() ? loadConfiguration(x_dataFile) : null;
		Thread x_topThread = x_config != null ? x_config.getTopLevelThread() : new Thread("Threads");

		if(x_config != null) {
			setActionTemplates(x_config.getActionTemplates());
		}

		TimedUpdater.initialise();
		TimedSaver.initialise(x_topThread, x_dataFile);
		NotificationUpdater.initialise(x_topThread);
		SystemTrayUtil.initialise(x_topThread);

		load(x_settingsFile);

		boolean x_googleEnabled = registerForSetting(s_GOOGLE, (p_name, p_value) -> { }, false);
		GoogleSyncer.initialise(x_topThread, x_googleEnabled);

		WindowManager.initialise(x_topThread, new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				TimedSaver.getInstance().stopRunning();
				GoogleSyncer.getInstance().stopRunning();
				TimedUpdater.getInstance().stopRunning();
				Saver.saveDocument(x_topThread, getActionTemplates(), x_dataFile);
				save(x_settingsFile);
				System.exit(0);
			}
		});
	}

}
