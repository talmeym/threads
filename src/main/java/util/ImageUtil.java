package util;

import com.apple.eawt.Application;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ImageUtil {

	public static void addIcon(Window p_window) {
		Image image = getThreadsImage();
		p_window.setIconImage(image);
		Application.getApplication().setDockIconImage(image);
	}

	public static Image getThreadsImage() {
		return loadImage("/threads.gif");
	}

	private static Image loadImage(String filename) {
		try {
			InputStream inputStream = SystemTrayUtil.class.getResourceAsStream(filename);

			if(inputStream != null && inputStream.available() > 0) {
				return ImageIO.read(inputStream);
			}
		} catch (IOException e) {
			// do nothing
		}

		throw new RuntimeException("Cannot load image");
	}

	public static Icon getThreadsIcon() {
		return new ImageIcon(loadImage("/threads_icon.gif"));
	}

	public static Icon getPlusIcon() {
		return new ImageIcon(loadImage("/plus.png"));
	}

	public static Icon getMinusIcon() {
		return new ImageIcon(loadImage("/minus.png"));
	}

	public static Icon getTickIcon() {
		return new ImageIcon(loadImage("/tick.png"));
	}

	public static Icon getUpIcon() {
		return new ImageIcon(loadImage("/up.png"));
	}

	public static Icon getCalendarIcon() {
		return new ImageIcon(loadImage("/calendar.png"));
	}

	public static Icon getCalendarSmallIcon() {
		return new ImageIcon(loadImage("/calendar_tab.png"));
	}

	public static Icon getLeftIcon() {
		return new ImageIcon(loadImage("/left.png"));
	}

	public static Icon getRightIcon() {
		return new ImageIcon(loadImage("/right.png"));
	}

	public static Icon getCrossIcon() {
		return new ImageIcon(loadImage("/revert.png"));
	}

	public static Icon getMoveIcon() {
		return new ImageIcon(loadImage("/move.png"));
	}

	public static Icon getDuplicateIcon() {
		return new ImageIcon(loadImage("/duplicate.png"));
	}

	public static Icon getFolderIcon() {
		return new ImageIcon(loadImage("/folder.png"));
	}

	public static Icon getLinkIcon() {
		return new ImageIcon(loadImage("/link.png"));
	}

	public static Icon getGoogleIcon() {
		return new ImageIcon(loadImage("/google.png"));
	}

	public static Icon getGoogleSmallIcon() {
		return new ImageIcon(loadImage("/google_icon.png"));
	}

	public static Icon getActionIcon() {
		return new ImageIcon(loadImage("/action_tab.png"));
	}

	public static Icon getUpdateIcon() {
		return new ImageIcon(loadImage("/update.png"));
	}

	public static Icon getThreadIcon() {
		return new ImageIcon(loadImage("/thread.png"));
	}

	public static Icon getReminderIcon() {
		return new ImageIcon(loadImage("/reminder_tab.png"));
	}

	public static Icon getReturnIcon() {
		return new ImageIcon(loadImage("/set.png"));
	}

	public static Icon getFolderSmallIcon() {
		return new ImageIcon(loadImage("/contents_tab.png"));
	}

	public static Icon getTrashIcon() {
		return new ImageIcon(loadImage("/trash.png"));
	}
}
