package gui;

import data.*;
import data.Thread;
import util.TimedSaver;

import java.awt.event.*;
import java.io.File;

public class WindowManager {
    private static WindowManager s_INSTANCE;

	public static void initialise(Thread p_topLevelThread, String filePath) {
		if(s_INSTANCE != null) {
			throw new IllegalStateException("Cannot initialise window manager twice");
		}

		s_INSTANCE = new WindowManager(p_topLevelThread, filePath);
	}

    public static WindowManager getInstance() {
		if(s_INSTANCE == null) {
			throw new IllegalStateException("Window manager not initialised");
		}

        return s_INSTANCE;
    }

	private final ThreadWindow threadWindow;
	private ItemWindow itemWindow;
	private ReminderWindow reminderWindow;

    private WindowManager(final Thread p_topLevelThread, final String filePath) {
		threadWindow = new ThreadWindow(p_topLevelThread);

		threadWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				TimedSaver.getInstance().stopRunning();
				Saver.saveDocument(p_topLevelThread, filePath);
				System.exit(0);
			}
		});
    }

    public void openComponent(Component p_component, boolean p_new, int p_tabIndex) {
		if(reminderWindow != null && reminderWindow.isVisible()) {
			reminderWindow.close();
		}

		if(itemWindow != null && itemWindow.isVisible()) {
			itemWindow.close();
		}

		if(p_component instanceof Thread) {
			threadWindow.showThread((Thread) p_component, p_new, p_tabIndex);
		}

		if(p_component instanceof Item) {
			itemWindow = new ItemWindow((Item)p_component, p_new, threadWindow);
		}

		if(p_component instanceof Reminder) {
			Reminder reminder = (Reminder) p_component;
			itemWindow = new ItemWindow((Item)reminder.getParentComponent(), false, threadWindow);
			reminderWindow = new ReminderWindow(reminder, p_new, threadWindow);
		}

		threadWindow.selectComponent(p_component);
    }

    public void closeComponent(Component p_component) {
		if(p_component instanceof Item) {
			itemWindow.close();
		}

		if(p_component instanceof Reminder) {
			reminderWindow.close();
		}
    }
}