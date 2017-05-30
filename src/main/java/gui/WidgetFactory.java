package gui;

import data.Component;

import javax.swing.*;
import java.awt.event.*;
import java.util.function.*;

class WidgetFactory {
	static <TYPE extends Component> JLabel createLabel(Icon p_icon, String p_tooltipText, TYPE p_component, Function<TYPE, Boolean> p_enabledFunction, Consumer<MouseEvent> p_mouseListener) {
		JLabel x_label = new JLabel(p_icon);
		x_label.setEnabled(p_enabledFunction.apply(p_component));
		x_label.setToolTipText(p_tooltipText);

		p_component.addComponentChangeListener(e -> {
			if(e.getSource() == p_component && e.isValueChange()) {
				x_label.setEnabled(p_enabledFunction.apply(p_component));
			}
		});

		x_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(x_label.isEnabled()) {
					p_mouseListener.accept(e);
				}
			}
		});

		return x_label;
	}

	public static JLabel setUpButtonLabel(final JLabel p_label) {
		p_label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		p_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent p_me) {
				p_label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
			}

			@Override
			public void mouseExited(MouseEvent p_me) {
				p_label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			}

			@Override
			public void mousePressed(MouseEvent p_me) {
				p_label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
			}

			@Override
			public void mouseReleased(MouseEvent p_me) {
				p_label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
			}
		});

		return p_label;
	}
}
