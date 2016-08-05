package gui;

import data.Component;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.event.*;
import java.util.function.Supplier;

public class ContextualPopup <TYPE> extends JPopupMenu {
	private final JMenuItem o_addItem = new JMenuItem("", ImageUtil.getPlusIcon());
	private final JMenuItem o_dismissItem = new JMenuItem("", ImageUtil.getTickIcon());

	private Component o_component;

	public ContextualPopup(Supplier<TYPE> p_supplier) {
		add(o_addItem);
		add(new JMenuItem("Remove", ImageUtil.getMinusIcon()));
		add(o_dismissItem);
		add(new JMenuItem("Move", ImageUtil.getMoveIcon()));
		add(new JMenuItem("Link to Google Calendar", ImageUtil.getLinkIcon()));

		addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				TYPE type = p_supplier.get();

				if(type instanceof Component) {
					o_component = (Component) type;
					o_addItem.setText("Add " + o_component.getType() + " to '" + o_component.getParentComponent().getText() + "'");
					o_dismissItem.setText(o_component.isActive() ? "Make Inactive" : "Make Active");
				}
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// do nothing
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// do nothing
			}
		});
	}
}
