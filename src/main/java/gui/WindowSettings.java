package gui;

import data.Component;

import java.awt.*;
import java.util.*;

public class WindowSettings {
	public static final String s_TAB_INDEX = "tabindex";
	public static final String s_MONTH = "month";
	public static final String s_UUID = "uuid";

	private Map<Class, Dimension> o_windowDimensions = new HashMap<Class, Dimension>();
	private Point o_windowLocation;
	private Dimension o_navSize = GUIConstants.s_navWindowSize;
	private Point o_navLocation = GUIConstants.s_navLocation;

	public Dimension getWindowSize(Component p_component) {
		Class<? extends Component> x_clazz = p_component.getClass();

		if(!o_windowDimensions.containsKey(x_clazz)) {
			o_windowDimensions.put(x_clazz, GUIConstants.dimensionFor(p_component));
		}

		return o_windowDimensions.get(x_clazz);
	}

	public void setWindowSize(Component p_component, Dimension p_size) {
		o_windowDimensions.put(p_component.getClass(), p_size);
	}

	public Point getWindowLocation() {
		if(o_windowLocation == null) {
			o_windowLocation = new Point((int) o_navLocation.getX() + (int) o_navSize.getWidth() + 20, (int) o_navLocation.getY());
		}

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

		for(Class clazz: o_windowDimensions.keySet()) {
			x_properties.setProperty("winw_" + clazz.getName(), String.valueOf((int)o_windowDimensions.get(clazz).getWidth()));
			x_properties.setProperty("winh_" + clazz.getName(), String.valueOf((int)o_windowDimensions.get(clazz).getHeight()));
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
		Enumeration x_enumeration = p_properties.propertyNames();

		while(x_enumeration.hasMoreElements()) {
			String x_propertyName = (String) x_enumeration.nextElement();

			if(x_propertyName.startsWith("winw_")) {
				try {
					Class x_clazz = Class.forName(x_propertyName.substring(5));
					String x_widthStr = p_properties.getProperty(x_propertyName);
					String x_heightStr = p_properties.getProperty("winh_" + x_clazz.getName());
					int x_width = Integer.parseInt(x_widthStr);
					int x_height = Integer.parseInt(x_heightStr);
					o_windowDimensions.put(x_clazz, new Dimension(x_width, x_height));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
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
