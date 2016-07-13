package gui;

import util.*;

import javax.swing.*;
import java.awt.*;
import java.text.*;
import java.util.Date;

public class StatusPanel extends JPanel implements TimeUpdateListener, GoogleSyncListener, TimedSaveListener {
	public static final DateFormat s_dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm");

	private final JLabel o_dateTimeLabel;

	public StatusPanel() {
		super(new BorderLayout());

		o_dateTimeLabel = new JLabel(s_dateFormat.format(new Date()));
		o_dateTimeLabel.setHorizontalAlignment(JLabel.CENTER);
		o_dateTimeLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 10, 10));
		add(o_dateTimeLabel, BorderLayout.CENTER);

		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
		TimedSaver.getInstance().addTimedSaveListener(this);
	}

	@Override
	public void timeUpdate() {
		o_dateTimeLabel.setText(s_dateFormat.format(new Date()));
	}

	@Override
	public void googleSyncStarted() {
		o_dateTimeLabel.setText("Syncing with Google Calendar ...");
		o_dateTimeLabel.setIcon(ImageUtil.getGoogleVerySmallIcon());
	}

	@Override
	public void googleSynced() {
		o_dateTimeLabel.setText(s_dateFormat.format(new Date()));
		o_dateTimeLabel.setIcon(null);
	}

	@Override
	public void saveStarted() {
		o_dateTimeLabel.setText("Saving data to Local Disc ...");
		o_dateTimeLabel.setIcon(ImageUtil.getSaveIcon());
	}

	@Override
	public void saved() {
		o_dateTimeLabel.setText(s_dateFormat.format(new Date()));
		o_dateTimeLabel.setIcon(null);
	}

}
