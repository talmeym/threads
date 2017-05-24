package util;

import java.util.*;

public abstract class TimedActivity<LISTENER_TYPE extends ActivityListener> extends java.lang.Thread {
	private int o_frequency;

	private final Object o_lockObj = new Object();
	private final List<LISTENER_TYPE> o_listeners = new ArrayList<>();
	private boolean o_continueRunning = true;
	private long o_nextSync = System.currentTimeMillis();

	TimedActivity(int o_frequency) {
		this.o_frequency = o_frequency;
	}

	public void addActivityListener(LISTENER_TYPE p_listener) {
		synchronized (o_listeners) {
			o_listeners.add(p_listener);
		}
	}

	public long nextSync() {
		synchronized(o_lockObj) {
			return o_nextSync;
		}
	}

	List<LISTENER_TYPE> getListeners() {
		return o_listeners;
	}

	public void run() {
		while(continueRunning()) {
			try {
				sleep(1000);

				if (o_nextSync < System.currentTimeMillis()) {
					doAction();

					synchronized(o_lockObj) {
						while(o_nextSync < System.currentTimeMillis()) {
							o_nextSync += o_frequency;
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void doAction() {
		synchronized (o_listeners) {
			started();
			action();
			finished();
		}
	}

	abstract void action();

	abstract void informOfStart(LISTENER_TYPE p_listener);

	abstract void informOfFinish(LISTENER_TYPE p_listener);

	private void started() {
		for(LISTENER_TYPE x_listener: o_listeners) {
			informOfStart(x_listener);
		}
	}

	void finished() {
		for (LISTENER_TYPE x_listener : o_listeners) {
			informOfFinish(x_listener);
		}
	}

	public synchronized void stopRunning() {
		o_continueRunning = false;
	}

	private synchronized boolean continueRunning() {
		return o_continueRunning;
	}
}
