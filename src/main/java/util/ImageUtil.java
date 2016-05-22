package util;

import com.apple.eawt.Application;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

public class ImageUtil {

	public static void addIconToWindow(Window p_window) {
		Image image = getThreadsImage();
		p_window.setIconImage(image);
		Application.getApplication().setDockIconImage(image);
	}

	public static Image getThreadsImage() {
		try {
			InputStream inputStream = SystemTrayUtil.class.getResourceAsStream("/threads.gif");

			if(inputStream != null && inputStream.available() > 0) {
				return ImageIO.read(inputStream);
			}
		} catch (IOException e) {
			// do nothing
		}

		throw new RuntimeException("Cannot load image");
	}
}
