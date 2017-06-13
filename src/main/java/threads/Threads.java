package threads;

import threads.data.Configuration;
import threads.data.LoadException;
import threads.data.Thread;
import threads.gui.WindowManager;
import threads.util.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import static threads.data.Loader.loadConfiguration;

public class Threads {
    public static void main(String[] args) throws LoadException {
		TimedUpdater.initialise();
		TimedSaver.initialise();
		NotificationUpdater.initialise();
		GoogleSyncer.initialise();
		SystemTrayUtil.initialise();
		WindowManager.initialise();

		for(String x_arg: args) {
			File x_xmlFile = new File(x_arg);
			new Threads(x_xmlFile.exists() ? loadConfiguration(x_xmlFile) : new Configuration(x_xmlFile, new Thread(x_xmlFile.getName()), new ArrayList<>()));
		}
	}

	public Threads(Configuration p_configuration) {
		TimedSaver.getInstance().addConfiguration(p_configuration);
		NotificationUpdater.getInstance().addConfiguration(p_configuration);
		SystemTrayUtil.addConfiguration(p_configuration);
		GoogleUtil.addConfiguration(p_configuration);

		WindowManager.getInstance().openConfiguration(p_configuration, new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                TimedSaver.getInstance().removeConfiguration(p_configuration);
                NotificationUpdater.getInstance().removeConfiguration(p_configuration);
                SystemTrayUtil.removeConfiguration(p_configuration);
                GoogleUtil.removeConfiguration(p_configuration);
            }
        });
	}
}
