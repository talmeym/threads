package gui;

import data.Component;

import javax.swing.*;
import java.awt.event.*;
import java.util.function.*;

import static javax.swing.BorderFactory.*;

class WidgetFactory {
	static <TYPE extends Component> JLabel createLabel(Icon p_icon, String p_tooltipText, TYPE p_component, Function<TYPE, Boolean> p_enabledFunction, Consumer<MouseEvent> p_mouseListener) {
		JLabel x_label = createLabel(p_icon, p_tooltipText, p_enabledFunction.apply(p_component), p_mouseListener);

		p_component.addComponentChangeListener(e -> {
			if(e.getSource() == p_component && e.isValueChange()) {
				x_label.setEnabled(p_enabledFunction.apply(p_component));
			}
		});

		return x_label;
	}

	static JLabel createLabel(Icon p_icon, String p_tooltipText, boolean p_enabled, Consumer<MouseEvent> p_mouseListener) {
		JLabel x_label = createLabel(p_icon, p_tooltipText, p_enabled);

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

	static JLabel createLabel(Icon p_icon, String p_tooltipText, boolean p_enabled) {
		JLabel x_label = new JLabel(p_icon);
		x_label.setEnabled(p_enabled);
		x_label.setToolTipText(p_tooltipText);
		return setUpButtonLabel(x_label);
	}

	static JLabel setUpButtonLabel(final JLabel p_label) {
		p_label.setBorder(createEmptyBorder(4, 4, 4, 4));

		p_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent p_me) {
				p_label.setBorder(createCompoundBorder(createRaisedBevelBorder(), createEmptyBorder(2, 2, 2, 2)));
			}

			@Override
			public void mouseExited(MouseEvent p_me) {
				p_label.setBorder(createEmptyBorder(4, 4, 4, 4));
			}

			@Override
			public void mousePressed(MouseEvent p_me) {
				p_label.setBorder(createCompoundBorder(createLoweredBevelBorder(), createEmptyBorder(2, 2, 2, 2)));
			}

			@Override
			public void mouseReleased(MouseEvent p_me) {
				p_label.setBorder(createCompoundBorder(createRaisedBevelBorder(), createEmptyBorder(2, 2, 2, 2)));
			}
		});

		return p_label;
	}
}
