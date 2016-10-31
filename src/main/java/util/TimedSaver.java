package util;

import data.Thread;

import java.io.File;
import java.text.*;
import java.util.*;

import static data.Saver.saveDocument;

public class TimedSaver extends java.lang.Thread {
    private static TimedSaver s_INSTANCE = null;
    private static final int s_frequency = 299000;
	public static final DateFormat s_dateFormat = new SimpleDateFormat("yyMMddHH");

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
    private boolean continueRunning = true;
	private List<TimedSaveListener> o_saveListeners = new ArrayList<TimedSaveListener>();

    private TimedSaver(Thread p_topThread, File p_orignalFile) {
		o_topThread = p_topThread;
		o_originalFile = p_orignalFile;
		setDaemon(true);
        start();
    }

	public synchronized void addTimedSaveListener(TimedSaveListener p_listener) {
		o_saveListeners.add(p_listener);
		p_listener.saved();
	}

	public void run() {
        while(continueRunning()) {
            try {
                java.lang.Thread.sleep(s_frequency);

                if(o_topThread != null) {
					saving();

					saveDocument(o_topThread, o_originalFile);
					saveDocument(o_topThread, getBackupFile());
                	java.lang.Thread.sleep(1000);
					saved();
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

	public void saving() {
		for(TimedSaveListener x_listener: o_saveListeners) {
			x_listener.saveStarted();
		}
	}

	public void saved() {
		for(TimedSaveListener x_listener: o_saveListeners) {
			x_listener.saved();
		}
	}

    public synchronized void stopRunning() {
        continueRunning = false;
    }
    
    private synchronized boolean continueRunning() {
        return continueRunning;
    }
}
