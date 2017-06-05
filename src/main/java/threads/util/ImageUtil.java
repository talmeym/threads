package threads.util;

import threads.data.ComponentType;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static javax.imageio.ImageIO.read;
import static threads.data.ComponentType.*;
import static threads.data.ComponentType.Thread;

public class ImageUtil {
	private static Map<String, Image> p_imageCache = new HashMap<>();

	private static Image getImage(String p_filename) {
		if(p_imageCache.containsKey(p_filename)) {
			return p_imageCache.get(p_filename);
		}

		try {
			InputStream inputStream = ImageUtil.class.getResourceAsStream(p_filename);

			if(inputStream != null && inputStream.available() > 0) {
				BufferedImage x_image = read(inputStream);
				p_imageCache.put(p_filename, x_image);
				return x_image;
			}
		} catch (IOException e) {
			// do nothing
		}

		throw new RuntimeException("Cannot load image: " + p_filename);
	}

	public static Image getThreadsImage() {
		return getImage("/threads418x364.gif");
	}

	public static Icon getThreadsIcon() {
		return new ImageIcon(getImage("/threads50x44.gif"));
	}

	public static Icon getPlusIcon() {
		return new ImageIcon(getImage("/plus20x20.png"));
	}

	public static Icon getPlusSmallIcon() {
		return new ImageIcon(getImage("/plus12x12.png"));
	}

	public static Icon getTickIcon() {
		return new ImageIcon(getImage("/tick20x20.png"));
	}

	public static Icon getTickSmallIcon() {
		return new ImageIcon(getImage("/tick12x12.png"));
	}

	public static Icon getUpIcon() {
		return new ImageIcon(getImage("/up20x20.png"));
	}

	public static Icon getCalendarIcon() {
		return new ImageIcon(getImage("/calendar20x20.png"));
	}

	public static Icon getCalendarSmallIcon() {
		return new ImageIcon(getImage("/calendar16x16.png"));
	}

	public static Icon getLeftIcon() {
		return new ImageIcon(getImage("/left20x20.png"));
	}

	public static Icon getRightIcon() {
		return new ImageIcon(getImage("/right20x20.png"));
	}

	public static Icon getCrossIcon() {
		return new ImageIcon(getImage("/revert20x20.png"));
	}

	public static Icon getMoveIcon() {
		return new ImageIcon(getImage("/move20x20.png"));
	}

	public static Icon getDuplicateIcon() {
		return new ImageIcon(getImage("/duplicate17x17.png"));
	}

	public static Icon getFolderIcon() {
		return new ImageIcon(getImage("/folder20x20.png"));
	}

	public static Icon getLinkIcon() {
		return new ImageIcon(getImage("/link20x20.png"));
	}

	public static Icon getLinkSmallIcon() {
		return new ImageIcon(getImage("/link12x12.png"));
	}

	public static Icon getGoogleIcon() {
		return new ImageIcon(getImage("/google50x50.png"));
	}

	public static Icon getGoogleSmallIcon() {
		return new ImageIcon(getImage("/google18x18.png"));
	}

	public static Icon getGoogleVerySmallIcon() {
		return new ImageIcon(getImage("/google12x12.png"));
	}

	public static Icon getActionIcon() {
		return new ImageIcon(getImage("/action16x16.png"));
	}

	private static Icon getActionSmallIcon() {
		return new ImageIcon(getImage("/action12x12.png"));
	}

	public static Icon getUpdateIcon() {
		return new ImageIcon(getImage("/update14x14.png"));
	}

	private static Icon getUpdateSmallIcon() {
		return new ImageIcon(getImage("/update12x12.png"));
	}

	public static Icon getThreadIcon() {
		return new ImageIcon(getImage("/thread15x15.png"));
	}

	private static Icon getThreadSmallIcon() {
		return new ImageIcon(getImage("/thread12x12.png"));
	}

	public static Icon getReminderIcon() {
		return new ImageIcon(getImage("/reminder20x20.png"));
	}

	private static Icon getReminderSmallIcon() {
		return new ImageIcon(getImage("/reminder12x12.png"));
	}

	public static Icon getReturnIcon() {
		return new ImageIcon(getImage("/set22x18.png"));
	}

	public static Icon getFolderSmallIcon() {
		return new ImageIcon(getImage("/contents12x12.png"));
	}

	public static Icon getTrashIcon() {
		return new ImageIcon(getImage("/trash19x22.png"));
	}

	public static Icon getHomeIcon() {
		return new ImageIcon(getImage("/home22x20.png"));
	}

	public static Icon getSaveIcon() {
		return new ImageIcon(getImage("/save12x12.png"));
	}

	public static Icon getTimeUpdateIcon() {
		return new ImageIcon(getImage("/timeUpdate12x12.png"));
	}

	public static Icon getTemplateIcon() {
		return new ImageIcon(getImage("/template17x16.png"));
	}

	public static Icon getTemplateSmallIcon() {
		return new ImageIcon(getImage("/template12x12.png"));
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
