package gui;

import data.Component;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class ComponentInfoPanel extends JPanel implements ActionListener, DocumentListener {
    private final Component o_component;

	private ComponentInfoChangeListener o_listener;

	private final JButton o_parentButton = new JButton("Parent");
    
    private final JTextField o_textField = new JTextField();
    
    private final JCheckBox o_activeCheckBox = new JCheckBox("Active");
    
    public ComponentInfoPanel(Component p_component, boolean p_new, ComponentInfoChangeListener p_listener) {
        super(new BorderLayout());
        o_component = p_component;
		o_listener = p_listener;

		o_parentButton.setEnabled(o_component.getParentComponent() != null);
        o_parentButton.addActionListener(this);
        
        o_textField.setPreferredSize(new Dimension(200, 25));
		o_textField.setText(p_component.getText());
		o_textField.getDocument().addDocumentListener(this);
        o_textField.addActionListener(this);
		o_textField.setHorizontalAlignment(JTextField.CENTER);

		if(p_new) {
			o_textField.requestFocus();
			o_textField.setSelectionStart(0);
			o_textField.setSelectionEnd(o_textField.getText().length());
		}

		o_activeCheckBox.setEnabled(o_component.getParentComponent() != null);
        o_activeCheckBox.setSelected(p_component.isActive());
        o_activeCheckBox.addActionListener(this);
        
        JPanel x_parentButtonPanel = new JPanel(new BorderLayout());
        x_parentButtonPanel.add(o_parentButton, BorderLayout.CENTER);
        x_parentButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        JPanel x_activeCheckBoxPanel = new JPanel(new BorderLayout());
        x_activeCheckBoxPanel.add(o_activeCheckBox, BorderLayout.CENTER);
        x_activeCheckBoxPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        add(x_parentButtonPanel, BorderLayout.EAST);
        add(o_textField, BorderLayout.CENTER);
        add(x_activeCheckBoxPanel, BorderLayout.WEST);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == o_textField) {
            if(o_textField.getText().length() > 0) {
                if(!o_textField.getText().equals(o_component.getText())) {
                    o_component.setText(o_textField.getText());
                }
            } else {
				o_textField.setText(o_component.getText());
			}

			o_listener.componentInfoChanged(true);
        }
        
        if(e.getSource() == o_activeCheckBox) {
            o_component.setActive(o_activeCheckBox.isSelected());
        }
        
        if(e.getSource() == o_parentButton) {
            WindowManager.getInstance().closeComponent(o_component);
            WindowManager.getInstance().openComponent(o_component.getParentComponent(), false, 0);
        }
    }

	@Override
	public void insertUpdate(DocumentEvent documentEvent) {
		o_listener.componentInfoChanged(false);
	}

	@Override
	public void removeUpdate(DocumentEvent documentEvent) {
		o_listener.componentInfoChanged(false);
	}

	@Override
	public void changedUpdate(DocumentEvent documentEvent) {
		o_listener.componentInfoChanged(false);
	}
}
