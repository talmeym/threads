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

	public static Image getTickIconImage() {
		return loadImage("/tick.png");
	}

	public static Image getMinusIconImage() {
		return loadImage("/minus.png");
	}

	public static Image getPlusIconImage() {
		return loadImage("/plus.png");
	}

	public static Image getUpIconImage() {
		return loadImage("/up.png");
	}

	public static Image getCalendarIconImage() {
		return loadImage("/calendar.png");
	}

	public static Image getCalendarSmallIconImage() {
		return loadImage("/calendar_small.png");
	}

	public static Image getLeftIconImage() {
		return loadImage("/left.png");
	}

	public static Image getRightIconImage() {
		return loadImage("/right.png");
	}

	public static Image getCrossIconImage() {
		return loadImage("/cross.png");
	}

	public static Image getMoveIconImage() {
		return loadImage("/move.png");
	}

	public static Image getDuplicateIconImage() {
		return loadImage("/duplicate.png");
	}

	public static Image getFolderIconImage() {
		return loadImage("/folder.png");
	}

	public static Image getLinkIconImage() {
		return loadImage("/link.png");
	}

	public static Image getGoogleIconImage() {
		return loadImage("/google.png");
	}

	public static Image getGoogleSmallIconImage() {
		return loadImage("/google_icon.png");
	}

	public static Image getActionIconImage() {
		return loadImage("/action.png");
	}

	public static Image getUpdateIconImage() {
		return loadImage("/update.png");
	}

	public static Image getThreadsIconImage() {
		return loadImage("/threads_icon.gif");
	}

	public static Image getThreadImage() {
		String filename = "/spool2.png";
		return loadImage(filename);
	}

	public static Image getReminderIconImage() {
		String filename = "/reminder.png";
		return loadImage(filename);
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
		return new ImageIcon(ImageUtil.getThreadsIconImage());
	}

	public static Icon getPlusIcon() {
		return new ImageIcon(ImageUtil.getPlusIconImage());
	}

	public static Icon getMinusIcon() {
		return new ImageIcon(ImageUtil.getMinusIconImage());
	}

	public static Icon getTickIcon() {
		return new ImageIcon(ImageUtil.getTickIconImage());
	}

	public static Icon getUpIcon() {
		return new ImageIcon(ImageUtil.getUpIconImage());
	}

	public static Icon getCalendarIcon() {
		return new ImageIcon(ImageUtil.getCalendarIconImage());
	}

	public static Icon getCalendarSmallIcon() {
		return new ImageIcon(ImageUtil.getCalendarSmallIconImage());
	}

	public static Icon getLeftIcon() {
		return new ImageIcon(ImageUtil.getLeftIconImage());
	}

	public static Icon getRightIcon() {
		return new ImageIcon(ImageUtil.getRightIconImage());
	}

	public static Icon getCrossIcon() {
		return new ImageIcon(ImageUtil.getCrossIconImage());
	}

	public static Icon getMoveIcon() {
		return new ImageIcon(ImageUtil.getMoveIconImage());
	}

	public static Icon getDuplicateIcon() {
		return new ImageIcon(ImageUtil.getDuplicateIconImage());
	}

	public static Icon getFolderIcon() {
		return new ImageIcon(ImageUtil.getFolderIconImage());
	}

	public static Icon getLinkIcon() {
		return new ImageIcon(ImageUtil.getLinkIconImage());
	}

	public static Icon getGoogleIcon() {
		return new ImageIcon(ImageUtil.getGoogleIconImage());
	}

	public static Icon getGoogleSmallIcon() {
		return new ImageIcon(ImageUtil.getGoogleSmallIconImage());
	}

	public static Icon getActionIcon() {
		return new ImageIcon(getActionIconImage());
	}

	public static Icon getUpdateIcon() {
		return new ImageIcon(getUpdateIconImage());
	}

	public static Icon getThreadIcon() {
		return new ImageIcon(getThreadImage());
	}

	public static Icon getReminderIcon() {
		return new ImageIcon(getReminderIconImage());
	}
}
