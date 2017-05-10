package gui;

import util.ProgressCallBack;

import javax.swing.*;
import java.awt.*;

public class GoogleProgressWindow extends JDialog implements ProgressCallBack {
	private final JProgressBar o_progressBar = new JProgressBar();
	private int finished = 0;

	public GoogleProgressWindow(JPanel p_parentPanel) {
		setTitle("Linking to Google Calendar ...");
		o_progressBar.setStringPainted(true);
		o_progressBar.setString("Connecting ...");

		JPanel x_panel = new JPanel(new BorderLayout());
		x_panel.add(o_progressBar, BorderLayout.CENTER);
		x_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		setContentPane(x_panel);
		setSize(new Dimension(300, 70));
		setLocation(new Point((int)p_parentPanel.getLocationOnScreen().getX() + (p_parentPanel.getWidth() / 2) - (getWidth() / 2), (int) p_parentPanel.getLocationOnScreen().getY() + (p_parentPanel.getHeight() / 2) - (getHeight() / 2)));
	}

	@Override
	public void started(int p_max) {
		o_progressBar.setMaximum(p_max);
		setVisible(true);
	}

	@Override
	public void progress(String item) {
		o_progressBar.setValue(++finished);
		o_progressBar.setString(item);
	}

	@Override
	public void success() {
		setVisible(false);
	}

	@Override
	public void error(String errorDesc) {
		setVisible(false);
	}
}
