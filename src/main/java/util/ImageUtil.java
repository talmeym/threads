package util;

//import com.apple.eawt.Application;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ImageUtil {

	public static void addIconToWindow(Window p_window) {
		Image image = getThreadsImage();
		p_window.setIconImage(image);
		//Application.getApplication().setDockIconImage(image);
	}

	public static Image getThreadsImage() {
		return loadImage("/threads.gif");
	}

	public static Image getThreadsIconImage() {
		return loadImage("/threads_icon.gif");
	}

	public static Image getTreeImage() {
		String filename = "/spool2.png";
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
}
