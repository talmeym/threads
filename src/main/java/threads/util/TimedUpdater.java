package threads.util;

public class TimedUpdater extends TimedActivity<TimedUpdateListener> {
    private static TimedUpdater s_INSTANCE;

	public static void initialise() {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise time updater twice");
		}

		s_INSTANCE = new TimedUpdater();
	}

	public static TimedUpdater getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Time updater not initialised");
		}

		return s_INSTANCE;
	}

    private TimedUpdater() {
    	super(60000, false);
		setDaemon(true);
		start();
    }
    
	void action() {
		// do nothing
	}

	@Override
	void informOfStart(TimedUpdateListener p_listener) {
		// do nothing
	}

	@Override
	void informOfFinish(TimedUpdateListener p_listener) {
		p_listener.timeUpdate();
	}
}
