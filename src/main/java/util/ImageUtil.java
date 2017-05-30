package util;

import com.apple.eawt.Application;
import data.ComponentType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;

import static data.ComponentType.*;
import static data.ComponentType.Thread;

public class ImageUtil {

	public static void addIcon(Window p_window) {
		Image image = getThreadsImage();
		p_window.setIconImage(image);
		Application.getApplication().setDockIconImage(image);
	}

	static Image getThreadsImage() {
		return loadImage("/threads418x364.gif");
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
		return new ImageIcon(loadImage("/threads50x44.gif"));
	}

	public static Icon getPlusIcon() {
		return new ImageIcon(loadImage("/plus20x20.png"));
	}

	public static Icon getPlusSmallIcon() {
		return new ImageIcon(loadImage("/plus12x12.png"));
	}

	public static Icon getMinusIcon() {
		return new ImageIcon(loadImage("/minus20x20.png"));
	}

	public static Icon getTickIcon() {
		return new ImageIcon(loadImage("/tick20x20.png"));
	}

	public static Icon getTickSmallIcon() {
		return new ImageIcon(loadImage("/tick12x12.png"));
	}

	public static Icon getUpIcon() {
		return new ImageIcon(loadImage("/up20x20.png"));
	}

	public static Icon getCalendarIcon() {
		return new ImageIcon(loadImage("/calendar20x20.png"));
	}

	public static Icon getCalendarSmallIcon() {
		return new ImageIcon(loadImage("/calendar16x16.png"));
	}

	public static Icon getLeftIcon() {
		return new ImageIcon(loadImage("/left20x20.png"));
	}

	public static Icon getRightIcon() {
		return new ImageIcon(loadImage("/right20x20.png"));
	}

	public static Icon getCrossIcon() {
		return new ImageIcon(loadImage("/revert20x20.png"));
	}

	public static Icon getMoveIcon() {
		return new ImageIcon(loadImage("/move20x20.png"));
	}

	public static Icon getDuplicateIcon() {
		return new ImageIcon(loadImage("/duplicate17x17.png"));
	}

	public static Icon getFolderIcon() {
		return new ImageIcon(loadImage("/folder20x20.png"));
	}

	public static Icon getLinkIcon() {
		return new ImageIcon(loadImage("/link20x20.png"));
	}

	public static Icon getLinkSmallIcon() {
		return new ImageIcon(loadImage("/link12x12.png"));
	}

	public static Icon getGoogleIcon() {
		return new ImageIcon(loadImage("/google50x50.png"));
	}

	public static Icon getGoogleSmallIcon() {
		return new ImageIcon(loadImage("/google18x18.png"));
	}

	public static Icon getGoogleVerySmallIcon() {
		return new ImageIcon(loadImage("/google12x12.png"));
	}

	public static Icon getActionIcon() {
		return new ImageIcon(loadImage("/action16x16.png"));
	}

	private static Icon getActionSmallIcon() {
		return new ImageIcon(loadImage("/action12x12.png"));
	}

	public static Icon getUpdateIcon() {
		return new ImageIcon(loadImage("/update14x14.png"));
	}

	private static Icon getUpdateSmallIcon() {
		return new ImageIcon(loadImage("/update12x12.png"));
	}

	public static Icon getThreadIcon() {
		return new ImageIcon(loadImage("/thread15x15.png"));
	}

	private static Icon getThreadSmallIcon() {
		return new ImageIcon(loadImage("/thread12x12.png"));
	}

	public static Icon getReminderIcon() {
		return new ImageIcon(loadImage("/reminder20x20.png"));
	}

	private static Icon getReminderSmallIcon() {
		return new ImageIcon(loadImage("/reminder12x12.png"));
	}

	public static Icon getReturnIcon() {
		return new ImageIcon(loadImage("/set22x18.png"));
	}

	public static Icon getFolderSmallIcon() {
		return new ImageIcon(loadImage("/contents12x12.png"));
	}

	public static Icon getTrashIcon() {
		return new ImageIcon(loadImage("/trash19x22.png"));
	}

	public static Icon getHomeIcon() {
		return new ImageIcon(loadImage("/home22x20.png"));
	}

	public static Icon getSaveIcon() {
		return new ImageIcon(loadImage("/save12x12.png"));
	}

	public static Icon getTimeUpdateIcon() {
		return new ImageIcon(loadImage("/timeUpdate12x12.png"));
	}

	public static Icon getTemplateIcon() {
		return new ImageIcon(loadImage("/template17x16.png"));
	}

	public static Icon getTemplateSmallIcon() {
		return new ImageIcon(loadImage("/template12x12.png"));
	}

	public static Icon getIconForType(ComponentType p_type) {
		if(p_type == Thread) {
			return getThreadIcon();
		}
		if(p_type == Update) {
			return getUpdateIcon();
		}
		if(p_type == Action) {
			return getActionIcon();
		}
		if(p_type == Reminder) {
			return getReminderIcon();
		}

		throw new IllegalArgumentException("Wrong type: " + p_type);
	}
	public static Icon getSmallIconForType(ComponentType p_type) {
		if(p_type == Thread) {
			return getThreadSmallIcon();
		}
		if(p_type == Update) {
			return getUpdateSmallIcon();
		}
		if(p_type == Action) {
			return getActionSmallIcon();
		}
		if(p_type == Reminder) {
			return getReminderSmallIcon();
		}

		throw new IllegalArgumentException("Wrong type: " + p_type);
	}
}
