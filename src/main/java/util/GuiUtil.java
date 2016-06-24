package util;

import javax.swing.*;
import java.awt.event.*;

public class GuiUtil {
	public static JLabel setUpButtonLabel(final JLabel p_label) {
		p_label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		p_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent mouseEvent) {
				p_label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent) {
				p_label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				p_label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				p_label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
			}
		});

		return p_label;
	}
}
