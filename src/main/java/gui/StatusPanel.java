package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;

import static gui.WidgetFactory.setUpButtonLabel;
import static java.awt.BorderLayout.*;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static util.ImageUtil.*;

class StatusPanel extends JPanel implements Runnable, TimedUpdateListener, GoogleSyncListener, TimedSaveListener {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm");

	private final boolean o_showGoogle;
	private final ActionLog o_actionLog;

	private final JProgressBar o_updateProgress = new JProgressBar(JProgressBar.HORIZONTAL);
	private final JProgressBar o_googleProgress = new JProgressBar(JProgressBar.HORIZONTAL);
	private final JProgressBar o_saveProgress = new JProgressBar(JProgressBar.HORIZONTAL);

	private final JLabel o_statusLabel;

	private long o_lastUpdate = currentTimeMillis();
	private long o_lastGoogle = currentTimeMillis();
	private long o_lastSave = currentTimeMillis();


	StatusPanel(Thread p_topLevelThread) {
		super(new GridLayout(0, 1, 5, 5));
		o_actionLog = new ActionLog(p_topLevelThread);

		o_showGoogle = GoogleSyncer.getInstance().isGoogleEnabled();
		o_updateProgress.setMinimum(0);

        JLabel x_updateLabel = new JLabel(getTimeUpdateIcon());
		x_updateLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				TimedUpdater.getInstance().doAction();
			}
		});

        x_updateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		JPanel x_updatePanel = new JPanel(new BorderLayout());
        x_updatePanel.add(setUpButtonLabel(x_updateLabel), WEST);
		x_updatePanel.add(o_updateProgress, CENTER);
		x_updatePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		add(x_updatePanel);


		if(o_showGoogle) {
			o_googleProgress.setMinimum(0);

			JLabel x_googleLabel = new JLabel(getGoogleVerySmallIcon());
			x_googleLabel.addMouseListener(new MouseAdapter() {
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

			x_googleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			JPanel x_googlePanel = new JPanel(new BorderLayout());
			x_googlePanel.add(setUpButtonLabel(x_googleLabel), WEST);
			x_googlePanel.add(o_googleProgress, CENTER);
			x_googlePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			add(x_googlePanel);
		}

		o_saveProgress.setMinimum(0);

		JLabel x_saveLabel = new JLabel(getSaveIcon());
		x_saveLabel.addMouseListener(new MouseAdapter() {
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

        x_saveLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		JPanel x_savePanel = new JPanel(new BorderLayout());
        x_savePanel.add(setUpButtonLabel(x_saveLabel), WEST);
		x_savePanel.add(o_saveProgress, CENTER);
		x_savePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		add(x_savePanel);

		o_statusLabel = new JLabel(s_dateFormat.format(new Date()));
		o_statusLabel.setHorizontalAlignment(JLabel.CENTER);
		o_statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		o_statusLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				o_actionLog.showLog();
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
	}

	@Override
	public void googleSyncStarted() {
		o_statusLabel.setText("Syncing with Google Calendar ...");
		o_statusLabel.setIcon(getGoogleVerySmallIcon());
	}

	@Override
	public void googleSynced() {
		o_statusLabel.setText(s_dateFormat.format(new Date()));
		o_statusLabel.setIcon(null);
		o_lastGoogle = currentTimeMillis();
	}

	@Override
	public void googleSynced(List<HasDueDate> p_hasDueDates) {
		// do nothing
	}

	@Override
	public void saveStarted() {
		o_statusLabel.setText("Saving data to Local Disc ...");
		o_statusLabel.setIcon(getSaveIcon());
	}

	@Override
	public void saved() {
		o_statusLabel.setText(s_dateFormat.format(new Date()));
		o_statusLabel.setIcon(null);
		o_lastSave = currentTimeMillis();
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

				if(o_showGoogle) {
					int x_googleMax = (int) (GoogleSyncer.getInstance().nextSync() - o_lastGoogle);
					int x_googleNow = (int) (x_currentTimeMillis - o_lastGoogle);
					o_googleProgress.setMaximum(x_googleMax);
					o_googleProgress.setValue(x_googleMax - x_googleNow);
				}

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
