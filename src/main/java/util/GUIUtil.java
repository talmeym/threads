package util;

import java.awt.*;

public class GUIUtil
{
    public static void centreWindow(Window p_window)
    {
        Dimension x_screenSize = p_window.getToolkit().getScreenSize();
        p_window.setLocation((x_screenSize.width - p_window.getWidth()) / 2, (x_screenSize.height - p_window.getHeight()) / 2);
    }
}
