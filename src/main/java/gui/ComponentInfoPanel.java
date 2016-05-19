package gui;

import data.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ComponentInfoPanel extends JPanel implements ActionListener
{
    private final Component o_component;
    
    private final JButton o_parentButton = new JButton("Parent");
    
    private final JTextField o_textField = new JTextField();
    
    private final JCheckBox o_activeCheckBox = new JCheckBox("Active");
    
    public ComponentInfoPanel(Component p_component, boolean p_new)
    {
        super(new BorderLayout());
        o_component = p_component;

		o_parentButton.setEnabled(o_component.getParentComponent() != null);
        o_parentButton.addActionListener(this);
        
        o_textField.setPreferredSize(new Dimension(200, 25));
        o_textField.setText(p_component.getText());
        o_textField.addActionListener(this);

		if(p_new)
		{
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
        
        add(x_parentButtonPanel, BorderLayout.WEST);
        add(o_textField, BorderLayout.CENTER);
        add(x_activeCheckBoxPanel, BorderLayout.EAST);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == o_textField)
        {
            if(o_textField.getText().length() > 0)
            {
                if(!o_textField.getText().equals(o_component.getText()))
                {
                    o_component.setText(o_textField.getText());
                    WindowManager.getInstance().renameAllWindows();
                }
            }
        }
        
        if(e.getSource() == o_activeCheckBox)
        {
            o_component.setActive(o_activeCheckBox.isSelected());
        }
        
        if(e.getSource() == o_parentButton)
        {
            WindowManager.getInstance().closeComponentWindow(o_component);
            WindowManager.getInstance().openComponentWindow(o_component.getParentComponent(), false, 0);
        }
    }
}
