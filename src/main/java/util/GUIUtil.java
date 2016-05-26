package util;

import java.awt.*;

public class GUIUtil {
    public static void centreWindow(Window p_window) {
        Dimension x_screenSize = p_window.getToolkit().getScreenSize();
        p_window.setLocation((x_screenSize.width - p_window.getWidth()) / 2, (x_screenSize.height - p_window.getHeight()) / 2);
    }

	public static void centreToWindow(Window p_window, Window p_parentWindow)
    {
		p_window.setLocation(new Point(p_parentWindow.getX() + (p_parentWindow.getWidth() / 2) - (p_window.getWidth() / 2), p_parentWindow.getY() + (p_parentWindow.getHeight() / 2) - (p_window.getHeight() / 2)));
	}
}
