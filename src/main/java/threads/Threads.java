package threads;

import threads.data.*;
import threads.data.Thread;
import threads.gui.WindowManager;
import threads.util.*;

import java.awt.event.*;
import java.io.File;
import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static threads.data.Loader.loadConfiguration;
import static threads.util.FileUtil.getCurrentFiles;

public class Threads {
    public static void main(String[] args) throws LoadException {
		TimedUpdater.initialise();
		TimedSaver.initialise();
		NotificationUpdater.initialise();
		GoogleSyncer.initialise();
		SystemTrayUtil.initialise();
		WindowManager.initialise();

		List<File> x_args = args.length == 0 ? getCurrentFiles() : stream(args).map(File::new).collect(toList());

		for(File x_file: x_args) {
			new Threads(x_file.exists() ? loadConfiguration(x_file) : new Configuration(x_file, new Thread(x_file.getName()), new ArrayList<>()));
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
