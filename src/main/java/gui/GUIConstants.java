package gui;

import data.Component;
import data.*;

import java.awt.*;

class GUIConstants
{
    public static final int s_creationDateColumnWidth = 130;
    public static final int s_typeColumnWidth = 120;
    public static final int s_threadColumnWidth = 140;
    public static final int s_dateStatusColumnWidth = 90;
	public static final int s_statsColumnWidth = 90;

	public static final int s_tableRowHeight = 25;

	public static final Dimension s_navWindowSize = new Dimension(300, 600);
    public static final Dimension s_threadWindowSize = new Dimension(800, 600);
    public static final Dimension s_itemWindowSize = new Dimension(700, 270);
    public static final Dimension s_reminderWindowSize = new Dimension(760, 145);

	public static Dimension dimensionFor(Component p_component) {
		if(p_component instanceof data.Thread) {
			return GUIConstants.s_threadWindowSize;
		} else if(p_component instanceof Item) {
			return GUIConstants.s_itemWindowSize;
		} else if(p_component instanceof Reminder) {
			return GUIConstants.s_reminderWindowSize;
		}

		throw new IllegalArgumentException("Invalid component: " + p_component.getClass());
	}
}
