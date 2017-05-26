package util;

import data.Thread;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static data.Saver.saveDocument;
import static gui.Actions.getActionTemplates;

public class TimedSaver extends TimedActivity<TimedSaveListener> {
    private static TimedSaver s_INSTANCE = null;

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

    private TimedSaver(Thread p_topThread, File p_orignalFile) {
    	super(300000, false);
		o_topThread = p_topThread;
		o_originalFile = p_orignalFile;
		setDaemon(true);
        start();
    }

	void action() {
		saveDocument(o_topThread, getActionTemplates(), o_originalFile);
		saveDocument(o_topThread, getActionTemplates(), getBackupFile());

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

	private File getBackupFile() {
		String x_fileName = o_originalFile.getName();
		File x_originalFolder = o_originalFile.getParentFile();
		File x_backupFolder = new File(x_originalFolder, "backups");
		x_backupFolder.mkdirs();
		return new File(x_backupFolder, x_fileName.substring(0, x_fileName.indexOf(".xml")) + ".backup." + new SimpleDateFormat("yyMMddHH").format(new Date()) + ".xml");
	}
}
