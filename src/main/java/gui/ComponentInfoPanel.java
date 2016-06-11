package gui;

import data.*;
import data.Component;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ComponentInfoPanel extends JPanel {
    private final Component o_component;

	public ComponentInfoPanel(Component p_component, final ComponentInfoChangeListener p_listener) {
        super(new BorderLayout());
        o_component = p_component;

		final JButton x_parentButton = new JButton("Parent");
		final JTextField x_textField = new JTextField(p_component.getText());
		final JCheckBox x_activeCheckBox = new JCheckBox("Active");

		x_parentButton.setEnabled(o_component.getParentComponent() != null);
		x_activeCheckBox.setEnabled(o_component.getParentComponent() != null);
		x_textField.setEnabled(p_component.getParentComponent() != null);

		x_textField.setHorizontalAlignment(JTextField.CENTER);
		x_textField.setForeground(p_component.isActive() ? Color.BLACK : Color.gray);
        x_activeCheckBox.setSelected(p_component.isActive());

		x_parentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				WindowManager.getInstance().openComponent(o_component.getParentComponent());
			}
		});

		x_textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent documentEvent) { p_listener.componentInfoChanged(false); }
			@Override public void removeUpdate(DocumentEvent documentEvent) { p_listener.componentInfoChanged(false); }
			@Override public void changedUpdate(DocumentEvent documentEvent) { p_listener.componentInfoChanged(false); }
		});

        x_textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (x_textField.getText().length() > 0 && !x_textField.getText().equals(o_component.getText())) {
					o_component.setText(x_textField.getText());
				}

				x_textField.setText(o_component.getText());
				p_listener.componentInfoChanged(true);
			}
		});

		x_activeCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				o_component.setActive(x_activeCheckBox.isSelected());
				x_textField.setForeground(o_component.isActive() ? Color.BLACK : Color.gray);
			}
		});

		o_component.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object o) {
				if(observable == ((ObservableChangeEvent)o).getObservableObserver()) {
					x_activeCheckBox.setSelected(o_component.isActive());
					x_textField.setForeground(o_component.isActive() ? Color.BLACK : Color.gray);
				}
			}
		});

        JPanel x_parentButtonPanel = new JPanel(new BorderLayout());
        x_parentButtonPanel.add(x_parentButton, BorderLayout.CENTER);
        x_parentButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        JPanel x_activeCheckBoxPanel = new JPanel(new BorderLayout());
        x_activeCheckBoxPanel.add(x_activeCheckBox, BorderLayout.CENTER);
        x_activeCheckBoxPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        add(x_parentButtonPanel, BorderLayout.WEST);
        add(x_textField, BorderLayout.CENTER);
        add(x_activeCheckBoxPanel, BorderLayout.EAST);
    }
}
