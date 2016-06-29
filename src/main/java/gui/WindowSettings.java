package gui;

import java.awt.*;
import java.util.*;

public class WindowSettings {
	public static final String s_TAB_INDEX = "tabindex";
	public static final String s_MONTH = "month";
	public static final String s_UUID = "uuid";
	public static final String s_DIVLOC = "divloc";
	public static final String s_NAVDIVLOC = "navdivloc";

	private Dimension o_windowSize = GUIConstants.s_windowSize;
	private Point o_windowLocation = GUIConstants.s_windowLocation;

	public Dimension getWindowSize() {
		return o_windowSize;
	}

	public void setWindowSize(Dimension p_size) {
		o_windowSize = p_size;
	}

	public Point getWindowLocation() {
		return o_windowLocation;
	}

	public void setWindowLocation(Point o_windowLocation) {
		this.o_windowLocation = o_windowLocation;
	}

	public Properties getProperties() {
		Properties x_properties = new Properties();

		if(o_windowSize != null) {
			x_properties.setProperty("winw", String.valueOf((int)o_windowSize.getWidth()));
			x_properties.setProperty("winh", String.valueOf((int)o_windowSize.getHeight()));
		}

		if(o_windowLocation != null) {
			x_properties.setProperty("winx", String.valueOf((int)o_windowLocation.getX()));
			x_properties.setProperty("winy", String.valueOf((int)o_windowLocation.getY()));
		}

		return x_properties;
	}

	public void applyProperties(Properties p_properties) {
		if(p_properties.containsKey("winw")) {
			o_windowSize = new Dimension(Integer.parseInt(p_properties.getProperty("winw")), Integer.parseInt(p_properties.getProperty("winh")));
		}

		if(p_properties.containsKey("winx")) {
			o_windowLocation = new Point(Integer.parseInt(p_properties.getProperty("winx")), Integer.parseInt(p_properties.getProperty("winy")));
		}
	}
}
