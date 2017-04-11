package util;

import data.Thread;

import java.io.File;
import java.text.*;
import java.util.*;

import static data.Saver.saveDocument;
import static java.lang.Thread.sleep;

public class TimedSaver extends java.lang.Thread {
    private static TimedSaver s_INSTANCE = null;
    private static final int s_frequency = 300000;
	private static final DateFormat s_dateFormat = new SimpleDateFormat("yyMMddHH");

	public static void initialise(Thread p_topLevelThread, File p_dataFile) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise timed saver twice");
		}

		s_INSTANCE = new TimedSaver(p_topLevelThread, p_dataFile);
	}

	public static TimedSaver getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Timed saver not initialised");
		}

		return s_INSTANCE;
    }

	private final Thread o_topThread;
    private final File o_originalFile;
	private final List<TimedSaveListener> o_saveListeners = new ArrayList<TimedSaveListener>();
    private boolean o_continueRunning = true;
	private long o_nextSync = System.currentTimeMillis() + s_frequency;

    private TimedSaver(Thread p_topThread, File p_orignalFile) {
		o_topThread = p_topThread;
		o_originalFile = p_orignalFile;
		setDaemon(true);
        start();
    }

	public synchronized void addTimedSaveListener(TimedSaveListener p_listener) {
    	synchronized(o_saveListeners) {
			o_saveListeners.add(p_listener);
			p_listener.saved();
		}
	}

    public long nextSync() {
        synchronized(o_topThread) {
            return o_nextSync;
        }
    }

    public void run() {
        while(continueRunning()) {
            try {
                sleep(1000);

                if(o_nextSync < System.currentTimeMillis()) {
                	synchronized(o_saveListeners) {
						saving();
						saveDocument(o_topThread, o_originalFile);
						saveDocument(o_topThread, getBackupFile());
						sleep(1000);
						saved();
                        System.out.println("Timed Save: " + new Date());
					}

					synchronized(o_topThread) {
						while(o_nextSync < System.currentTimeMillis()) {
							o_nextSync += s_frequency;
						}
                    }
				}
            } catch (InterruptedException e) {
                // do nothing
            }            
        }
    }

	private File getBackupFile() {
		String x_fileName = o_originalFile.getName();
		File x_originalFolder = o_originalFile.getParentFile();
		File x_backupFolder = new File(x_originalFolder, "backups");
		x_backupFolder.mkdirs();
		return new File(x_backupFolder, x_fileName.substring(0, x_fileName.indexOf(".xml")) + ".backup." + s_dateFormat.format(new Date()) + ".xml");
	}

	private void saving() {
		for(TimedSaveListener x_listener: o_saveListeners) {
			x_listener.saveStarted();
		}
	}

	private void saved() {
		for(TimedSaveListener x_listener: o_saveListeners) {
			x_listener.saved();
		}
	}

    public synchronized void stopRunning() {
        o_continueRunning = false;
    }
    
    private synchronized boolean continueRunning() {
        return o_continueRunning;
    }
}
