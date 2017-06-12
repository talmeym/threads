package threads.util;

import threads.data.Configuration;

import java.util.ArrayList;
import java.util.List;

import static threads.data.Saver.saveDocument;

public class TimedSaver extends TimedActivity<TimedSaveListener> {
    private static TimedSaver s_INSTANCE = null;

	public static void initialise() {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise timed saver twice");
		}

		s_INSTANCE = new TimedSaver();
	}

	public static TimedSaver getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Timed saver not initialised");
		}

		return s_INSTANCE;
    }

	private final List<Configuration> o_configurations;

    private TimedSaver() {
    	super(300000, false);
		o_configurations = new ArrayList<>();
		setDaemon(true);
        start();
    }

    public void addConfiguration(Configuration p_configuration) {
    	o_configurations.add(p_configuration);
	}

	public void removeConfiguration(Configuration p_configuration) {
        saveDocument(p_configuration, false);
    	o_configurations.remove(p_configuration);
	}

	void action() {
    	for(Configuration x_configuration: o_configurations) {
			saveDocument(x_configuration, false);
			saveDocument(x_configuration, true);
		}

		try {
			sleep(1000);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	@Override
	void informOfStart(TimedSaveListener p_listener) {
		p_listener.saveStarted();
	}

	@Override
	void informOfFinish(TimedSaveListener p_listener) {
		p_listener.saved();
	}
}
