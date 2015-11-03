package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import data.Component;

public class ComponentInfoPanel extends JPanel implements ActionListener
{
    private final Component o_component;
    
    private final JButton o_parentButton = new JButton("Parent");
    
    private final JTextField o_textField = new JTextField();
    
    private final JCheckBox o_activeCheckBox = new JCheckBox("Active");
    
    public ComponentInfoPanel(Component p_component)
    {
        super(new BorderLayout());
        o_component = p_component;
        
        o_parentButton.setEnabled(o_component.getParentComponent() != null);
        o_parentButton.addActionListener(this);
        
        o_textField.setPreferredSize(new Dimension(200, 25));
        o_textField.setText(p_component.getText());
        o_textField.addActionListener(this);
        
		o_activeCheckBox.setEnabled(o_component.getParentComponent() != null);
        o_activeCheckBox.setSelected(p_component.isActive());
        o_activeCheckBox.addActionListener(this);
        
        JPanel x_textLabelPanel = new JPanel(new BorderLayout());
        x_textLabelPanel.add(o_parentButton, BorderLayout.CENTER);
        x_textLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        JPanel x_activeCheckBoxPanel = new JPanel(new BorderLayout());
        x_activeCheckBoxPanel.add(o_activeCheckBox, BorderLayout.CENTER);
        x_activeCheckBoxPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        add(x_textLabelPanel, BorderLayout.WEST);
        add(o_textField, BorderLayout.CENTER);
        add(x_activeCheckBoxPanel, BorderLayout.EAST);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    JTextField getTextField()
    {
        return o_textField;
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
                    WindowManager.getInstance().renameWindow(o_component);
                }
            }
        }
        
        if(e.getSource() == o_activeCheckBox)
        {
            o_component.setActive(o_activeCheckBox.isSelected());
        }
        
        if(e.getSource() == o_parentButton)
        {
            WindowManager.getInstance().openComponentWindow(o_component.getParentComponent(), false);
        }
    }
}
