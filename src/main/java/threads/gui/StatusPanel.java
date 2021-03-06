package threads.gui;

import com.google.api.services.calendar.model.Event;
import threads.data.Component;
import threads.data.Thread;
import threads.data.*;
import threads.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static threads.gui.WidgetFactory.setUpButtonLabel;
import static threads.util.ImageUtil.*;

class StatusPanel extends JPanel implements Runnable, TimedUpdateListener, GoogleSyncListener, TimedSaveListener {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm");
	private static final DateFormat s_timeFormat = new SimpleDateFormat("HH:mm");

	private final ActionLogWindow o_actionLogWindow;
	private Configuration o_configuration;
	private final JFrame o_frame;

	private final JProgressBar o_updateProgress = new JProgressBar(JProgressBar.HORIZONTAL);
	private final JProgressBar o_googleProgress = new JProgressBar(JProgressBar.HORIZONTAL);
	private final JProgressBar o_saveProgress = new JProgressBar(JProgressBar.HORIZONTAL);

	private final JLabel o_statusLabel;

	private long o_lastUpdate = currentTimeMillis();
	private long o_lastGoogle = currentTimeMillis();
	private long o_lastSave = currentTimeMillis();

	private JLabel o_updateLabel = new JLabel(getTimeUpdateIcon());
	private JLabel o_googleLabel = new JLabel(getGoogleVerySmallIcon());
	private JLabel o_saveLabel = new JLabel(getSaveIcon());

	StatusPanel(Configuration p_configuration, JFrame p_frame) {
		super(new GridLayout(0, 1, 5, 5));

		o_configuration = p_configuration;
		o_frame = p_frame;

		o_actionLogWindow = new ActionLogWindow(p_configuration);
		o_updateProgress.setMinimum(0);

		o_updateLabel.setToolTipText("Last Updated: N/A");
		o_updateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		o_updateLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				TimedUpdater.getInstance().doAction();
			}
		});

		JPanel x_updatePanel = new JPanel(new BorderLayout());
        x_updatePanel.add(setUpButtonLabel(o_updateLabel), WEST);
		x_updatePanel.add(o_updateProgress, CENTER);
		x_updatePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		add(x_updatePanel);


		o_googleProgress.setMinimum(0);

		o_googleLabel.setToolTipText("Last Sync: N/A");
		o_googleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		o_googleLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						GoogleSyncer.getInstance().doAction();
						return null;
					}
				}.execute();
			}
		});

		JPanel x_googlePanel = new JPanel(new BorderLayout());
		x_googlePanel.add(setUpButtonLabel(o_googleLabel), WEST);
		x_googlePanel.add(o_googleProgress, CENTER);
		x_googlePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		add(x_googlePanel);

		o_saveProgress.setMinimum(0);

		o_saveLabel.setToolTipText("Last Saved: N/A");
        o_saveLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		o_saveLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						TimedSaver.getInstance().doAction();
						return null;
					}
				}.execute();
			}
		});

		JPanel x_savePanel = new JPanel(new BorderLayout());
        x_savePanel.add(setUpButtonLabel(o_saveLabel), WEST);
		x_savePanel.add(o_saveProgress, CENTER);
		x_savePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		add(x_savePanel);

		o_statusLabel = new JLabel(s_dateFormat.format(new Date()));
		o_statusLabel.setHorizontalAlignment(JLabel.CENTER);
		o_statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		o_statusLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				o_actionLogWindow.showLog();
			}
		});

		add(o_statusLabel);

		TimedUpdater.getInstance().addActivityListener(this);
		GoogleSyncer.getInstance().addActivityListener(this);
		TimedSaver.getInstance().addActivityListener(this);

		new java.lang.Thread(this).start();
	}

	@Override
	public void timeUpdate() {
		o_lastUpdate = currentTimeMillis();
		o_updateLabel.setToolTipText("Last Updated: Today " + s_timeFormat.format(new Date(o_lastUpdate)));
	}

	@Override
	public void googleSyncStarted() {
		o_statusLabel.setText("Syncing with Google Calendar ...");
		o_statusLabel.setIcon(getGoogleVerySmallIcon());
	}

	@Override
	public void googleSyncFinished() {
		o_statusLabel.setText(s_dateFormat.format(new Date()));
		o_statusLabel.setIcon(null);
		o_lastGoogle = currentTimeMillis();
		o_googleLabel.setToolTipText("Last Sync: Today " + s_timeFormat.format(new Date(o_lastGoogle)));
	}

	@Override
	public void itemsLinked(List<HasDueDate> p_hasDueDates) {
		// do nothing
	}

	@Override
	public void googleSynced(List<HasDueDate> p_itemsCreated, List<Event> p_eventsDeleted) {
			Thread x_topLevelThread = o_configuration.getTopLevelThread();
			processAutoSortPrefixes(x_topLevelThread, p_itemsCreated);
			processAutoSortRules(x_topLevelThread, p_itemsCreated);
	}

	private void processAutoSortPrefixes(Thread x_topLevelThread, List<HasDueDate> p_hasDueDates) {
		boolean x_someMultiMatch = false;
		List<HasDueDate> x_processed = new ArrayList<>();

		for(HasDueDate x_hasDueDate: p_hasDueDates) {
			String x_text = x_hasDueDate.getText();
			int x_index = x_text.indexOf(':');

			if(x_index > 0) {
				String x_threadName = x_text.substring(0, x_index);
				List<Component> x_threads = x_topLevelThread.search(new Search.Builder().ofType(ComponentType.Thread).withText(x_threadName).exactText(true).build());

				if(x_threads.size() == 1) {
					x_hasDueDate.setText(x_text.substring(x_index + 1).trim());
                    ((ThreadItem) x_hasDueDate).moveTo((Thread) x_threads.get(0));
					x_processed.add(x_hasDueDate);
				} else {
					x_someMultiMatch = true;
				}
			}
		}

		p_hasDueDates.removeAll(x_processed);

		if(x_someMultiMatch) {
			showMessageDialog(o_frame.getContentPane(), "Some prefixed items couldn't be sorted to new locations due to multiple thread matches", "Google Auto-Sort", INFORMATION_MESSAGE, getThreadsIcon());
		}
	}

	private void processAutoSortRules(Thread x_topLevelThread, List<HasDueDate> p_hasDueDates) {
		List<HasDueDate> x_toProcess = new ArrayList<>();
		List<Thread> x_threads = new ArrayList<>();

		p_hasDueDates.forEach(hdd -> {
			o_configuration.getAutoSortRules().forEach(r -> {
				if(r.getMatcher().matches(hdd.getText(), r.getTextToken())) {
					x_toProcess.add(hdd);
					x_threads.add((Thread) x_topLevelThread.search(new Search.Builder().withId(r.getThreadId()).build()).get(0));
				}
			});
		});

		if(x_toProcess.size() > 0) {
			new ConfirmAutoSortDialog(x_toProcess, x_threads, o_frame);
		}
	}

	@Override
	public void saveStarted() {
		o_statusLabel.setText("Saving threads data to Local Disc ...");
		o_statusLabel.setIcon(getSaveIcon());
	}

	@Override
	public void saved() {
		o_statusLabel.setText(s_dateFormat.format(new Date()));
		o_statusLabel.setIcon(null);
		o_lastSave = currentTimeMillis();
		o_saveLabel.setToolTipText("Last Saved: Today " + s_timeFormat.format(new Date(o_lastSave)));
	}

	@Override
	public void run() {
		while(true) {
            try {
                sleep(1000);
				long x_currentTimeMillis = currentTimeMillis();

                int x_updateMax = (int) (TimedUpdater.getInstance().nextSync() - o_lastUpdate);
				int x_updateNow = (int) (x_currentTimeMillis - o_lastUpdate);
                o_updateProgress.setMaximum(x_updateMax);
                o_updateProgress.setValue(x_updateMax - x_updateNow);

				int x_googleMax = (int) (GoogleSyncer.getInstance().nextSync() - o_lastGoogle);
				int x_googleNow = (int) (x_currentTimeMillis - o_lastGoogle);
				o_googleProgress.setMaximum(x_googleMax);
				o_googleProgress.setValue(x_googleMax - x_googleNow);

                int x_saveMax = (int) (TimedSaver.getInstance().nextSync() - o_lastSave);
                int x_saveNow = (int) (x_currentTimeMillis - o_lastSave);
                o_saveProgress.setMaximum(x_saveMax);
                o_saveProgress.setValue(x_saveMax - x_saveNow);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
	}
}
