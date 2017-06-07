package threads;

import threads.data.Loader.Configuration;
import threads.data.Saver;
import threads.data.Thread;
import threads.gui.WindowManager;
import threads.util.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import static threads.data.Loader.loadConfiguration;
import static threads.gui.Actions.getActionTemplates;
import static threads.gui.Actions.setActionTemplates;
import static threads.util.Settings.*;

public class Threads {
    public static void main(String[] args) {
		File x_xmlFile = new File(args.length > 0 ? args[0] : "threads.xml");
        new Threads(x_xmlFile.exists() ? loadConfiguration(x_xmlFile) : new Configuration(x_xmlFile, new Thread("Threads"), new ArrayList<>()));
	}

	   public Threads(Configuration p_configuration) {
    	File x_xmlFile = p_configuration.getXmlFile();
		File x_settingsFile = new File(x_xmlFile.getParentFile(), x_xmlFile.getName() + ".properties");
		Thread x_topThread = p_configuration.getTopLevelThread();
        setActionTemplates(p_configuration.getActionTemplates());

		TimedUpdater.initialise();
		TimedSaver.initialise(x_topThread, x_xmlFile);
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
				Saver.saveDocument(x_topThread, getActionTemplates(), x_xmlFile);
				save(x_settingsFile);
				System.exit(0);
			}
		});
	}
}
