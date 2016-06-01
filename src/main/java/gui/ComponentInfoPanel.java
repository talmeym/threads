package gui;

import data.Component;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ComponentInfoPanel extends JPanel implements DocumentListener {
    private final Component o_component;

	private List<ComponentInfoChangeListener> o_listeners;

    public ComponentInfoPanel(Component p_component, boolean p_new, ComponentInfoChangeListener p_compInfoListeners) {
		this(p_component, p_new, Arrays.asList(p_compInfoListeners));
	}

    public ComponentInfoPanel(Component p_component, boolean p_new, List<ComponentInfoChangeListener> p_componentInfoListeners) {
        super(new BorderLayout());
        o_component = p_component;
		o_listeners = p_componentInfoListeners;

		JButton o_parentButton = new JButton("Parent");
		o_parentButton.setEnabled(o_component.getParentComponent() != null);
        o_parentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				WindowManager.getInstance().openComponent(o_component.getParentComponent(), false, -1);
			}
		});

		final JTextField x_textField = new JTextField();
        x_textField.setPreferredSize(new Dimension(200, 25));
		x_textField.setText(p_component.getText());
		x_textField.getDocument().addDocumentListener(this);
		x_textField.setHorizontalAlignment(JTextField.CENTER);
		x_textField.setEnabled(p_component.getParentComponent() != null);
		x_textField.setForeground(p_component.isActive() ? Color.BLACK : Color.gray);

        x_textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (x_textField.getText().length() > 0) {
					if (!x_textField.getText().equals(o_component.getText())) {
						o_component.setText(x_textField.getText());
					}
				} else {
					x_textField.setText(o_component.getText());
				}

				updateListeners(true);
			}
		});

		if(p_new) {
			x_textField.requestFocus();
			x_textField.setSelectionStart(0);
			x_textField.setSelectionEnd(x_textField.getText().length());
		}

		final JCheckBox o_activeCheckBox = new JCheckBox("Active");
		o_activeCheckBox.setEnabled(o_component.getParentComponent() != null);
        o_activeCheckBox.setSelected(p_component.isActive());

		o_activeCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				o_component.setActive(o_activeCheckBox.isSelected());
				x_textField.setForeground(o_component.isActive() ? Color.BLACK : Color.gray);
			}
		});
        
        JPanel x_parentButtonPanel = new JPanel(new BorderLayout());
        x_parentButtonPanel.add(o_parentButton, BorderLayout.CENTER);
        x_parentButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        JPanel x_activeCheckBoxPanel = new JPanel(new BorderLayout());
        x_activeCheckBoxPanel.add(o_activeCheckBox, BorderLayout.CENTER);
        x_activeCheckBoxPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        add(x_parentButtonPanel, BorderLayout.WEST);
        add(x_textField, BorderLayout.CENTER);
        add(x_activeCheckBoxPanel, BorderLayout.EAST);
    }
    
	@Override
	public void insertUpdate(DocumentEvent documentEvent) {
		updateListeners(false);
	}

	@Override
	public void removeUpdate(DocumentEvent documentEvent) {
		updateListeners(false);
	}

	@Override
	public void changedUpdate(DocumentEvent documentEvent) {
		updateListeners(false);
	}

	private void updateListeners(boolean p_saved) {
		for(ComponentInfoChangeListener listener: o_listeners) {
			listener.componentInfoChanged(p_saved);
		}
	}
}
