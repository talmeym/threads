package threads;

import threads.data.Loader.Configuration;
import threads.data.Saver;
import threads.data.Thread;
import threads.gui.WindowManager;
import threads.util.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import static threads.data.Loader.loadConfiguration;
import static threads.gui.Actions.getActionTemplates;
import static threads.gui.Actions.setActionTemplates;
import static threads.util.Settings.*;

public class Threads {
    public static void main(String[] args) {
		File x_dataFile = new File(args.length > 0 ? args[0] : "threads.xml");
		Configuration x_config = x_dataFile.exists() ? loadConfiguration(x_dataFile) : null;
		new Threads(x_dataFile, x_config);
	}

	public Threads(File p_dataFile, Configuration p_configuration) {
		File x_settingsFile = new File(p_dataFile.getParentFile(), p_dataFile.getName() + ".properties");
		Thread x_topThread = p_configuration != null ? p_configuration.getTopLevelThread() : new Thread("threads.Threads");

		if(p_configuration != null) {
			setActionTemplates(p_configuration.getActionTemplates());
		}

		TimedUpdater.initialise();
		TimedSaver.initialise(x_topThread, p_dataFile);
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
				Saver.saveDocument(x_topThread, getActionTemplates(), p_dataFile);
				save(x_settingsFile);
				System.exit(0);
			}
		});
	}
}
