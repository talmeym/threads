package gui;

import data.ComponentType;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ContextualPopupMenu {
	private final JMenuItem o_activeLabel = new JMenuItem("Set Inactive", ImageUtil.getTickIcon());
	private final JMenuItem o_removeLabel = new JMenuItem("Remove", ImageUtil.getMinusIcon());
	private final JMenuItem o_moveLabel = new JMenuItem("Move", ImageUtil.getMoveIcon());
	private final JMenuItem o_linkLabel = new JMenuItem("Link",ImageUtil.getLinkIcon());

	private boolean o_moveEnabled;
	private boolean o_linkEnabled;
	private ActionListener o_activateListener;
	private ActionListener o_deactivateListener;
	private ActionListener o_removeListener;
	private ActionListener o_moveListener;
	private ActionListener o_linkListener;

	public ContextualPopupMenu(boolean p_moveEnabled, boolean p_linkEnabled, ComponentType p_componentType) {
		o_moveEnabled = p_moveEnabled;
		o_linkEnabled = p_linkEnabled;

		o_activeLabel.setEnabled(false);
		o_removeLabel.setEnabled(false);
		o_moveLabel.setEnabled(false);
		o_linkLabel.setEnabled(false);

		String x_componentString = p_componentType != null ? p_componentType.name() + " " : " ";
		o_activeLabel.setToolTipText("Set " + x_componentString + "Active / Inactive");
		o_removeLabel.setToolTipText("Remove " + x_componentString);
		o_moveLabel.setToolTipText("Move " + x_componentString);
		o_linkLabel.setToolTipText("Link " + x_componentString + "to Google Calendar");
	}

	public void setActivateActionListener(ActionListener p_listener) {
		o_activateListener = p_listener;
	}

	public void setDeactivateActionListener(ActionListener p_listener) {
		o_deactivateListener = p_listener;
	}

	public void setRemoveActionListener(ActionListener p_listener) {
		o_removeListener = p_listener;
	}

	public void setMoveActionListener(ActionListener p_listener) {
		o_moveListener = p_listener;
	}

	public void setLinkActionListener(ActionListener p_listener) {
		o_linkListener = p_listener;
	}

	public void setStatus(boolean p_activeEnabled, boolean p_removeEnabled, boolean p_moveEnabled, boolean p_linkEnabled, data.Component p_component) {
		o_activeLabel.setEnabled(p_component != null && p_activeEnabled);
		o_removeLabel.setEnabled(p_component != null && p_removeEnabled);
		o_moveLabel.setEnabled(p_component != null && p_moveEnabled);
		o_linkLabel.setEnabled(p_component != null && p_linkEnabled);

		if(p_removeEnabled) {
			o_removeLabel.addActionListener(o_removeListener);
		} else {
			o_removeLabel.removeActionListener(o_removeListener);
		}

		if(p_moveEnabled) {
			o_moveLabel.addActionListener(o_moveListener);
		} else {
			o_moveLabel.removeActionListener(o_moveListener);
		}

		if(p_linkEnabled) {
			o_linkLabel.addActionListener(o_linkListener);
		} else {
			o_linkLabel.removeActionListener(o_linkListener);
		}

		if(p_component != null) {
			boolean x_active = p_component.isActive();
			o_activeLabel.setText(x_active ? "Set Inactive" : "Set Active");
			o_activeLabel.removeActionListener(o_activateListener);
			o_activeLabel.removeActionListener(o_deactivateListener);
			o_activeLabel.addActionListener(x_active ? o_deactivateListener : o_activateListener);
		}
	}

	public void show(Point p_point, Component p_origin) {
		JPopupMenu x_menu = new JPopupMenu();
		x_menu.add(o_removeLabel);
		x_menu.add(o_activeLabel);

		if(o_moveEnabled) {
			x_menu.add(o_moveLabel);
		}

		if(o_linkEnabled) {
			x_menu.add(o_linkLabel);
		}

		x_menu.show(p_origin, p_point.x, p_point.y);
	}

}
