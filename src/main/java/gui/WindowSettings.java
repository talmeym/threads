package gui;

import java.awt.*;
import java.util.*;

public class WindowSettings {
	public static final String s_TAB_INDEX = "tabindex";
	public static final String s_MONTH = "month";
	public static final String s_UUID = "uuid";
	public static final String s_DIVLOC = "divloc";

	private Dimension o_navSize = GUIConstants.s_navWindowSize;
	private Point o_navLocation = GUIConstants.s_navLocation;
	private Dimension o_windowSize = GUIConstants.s_itemWindowSize;
	private Point o_windowLocation = new Point((int) o_navLocation.getX() + (int) o_navSize.getWidth() + 20, (int) o_navLocation.getY());

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

	public Dimension getNavSize() {
		return o_navSize;
	}

	public void setNavSize(Dimension o_navSize) {
		this.o_navSize = o_navSize;
	}

	public Point getNavLocation() {
		return o_navLocation;
	}

	public void setNavLocation(Point o_navLocation) {
		this.o_navLocation = o_navLocation;
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

		x_properties.setProperty("navx", String.valueOf((int)o_navLocation.getX()));
		x_properties.setProperty("navy", String.valueOf((int)o_navLocation.getY()));

		x_properties.setProperty("navw", String.valueOf((int)o_navSize.getWidth()));
		x_properties.setProperty("navh", String.valueOf((int)o_navSize.getHeight()));

		return x_properties;
	}

	public void applyProperties(Properties p_properties) {
		if(p_properties.containsKey("winw")) {
			o_windowSize = new Dimension(Integer.parseInt(p_properties.getProperty("winw")), Integer.parseInt(p_properties.getProperty("winh")));
		}

		if(p_properties.containsKey("winx")) {
			o_windowLocation = new Point(Integer.parseInt(p_properties.getProperty("winx")), Integer.parseInt(p_properties.getProperty("winy")));
		}

		if(p_properties.containsKey("navw")) {
			o_navSize = new Dimension(Integer.parseInt(p_properties.getProperty("navw")), Integer.parseInt(p_properties.getProperty("navh")));
		}

		if(p_properties.containsKey("navx")) {
			o_navLocation = new Point(Integer.parseInt(p_properties.getProperty("navx")), Integer.parseInt(p_properties.getProperty("navy")));
		}
	}
}
