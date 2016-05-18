package gui;

import data.Reminder;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.Date;

public class ReminderPanel extends JPanel implements ActionListener, DocumentListener
{
    private static final DateFormat o_dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
    private static final Dimension o_dueFieldSize = new Dimension(130, 25);
    
    private final Reminder o_reminder;
    private final boolean o_new;
    
    private final ComponentInfoPanel o_compInfoPanel;
    
    private final JTextField o_remindDateField = new JTextField();
    
    private final JButton o_saveButton = new JButton("Save & Close");    
    private final JButton o_cancelButton = new JButton("Cancel"); 
    
    public ReminderPanel(Reminder p_reminder, boolean p_new)
    {
        super(new BorderLayout());
        o_reminder = p_reminder;
        o_new = p_new;
        
        o_compInfoPanel = new ComponentInfoPanel(p_reminder);
             
        o_remindDateField.setPreferredSize(o_dueFieldSize);
        o_remindDateField.getDocument().addDocumentListener(this);
        
        o_saveButton.setEnabled(true);
        
        o_remindDateField.setText(o_dateFormat.format(o_reminder.getDueDate()));
        
        if(p_new)
        {
            JTextField o_textField = o_compInfoPanel.getTextField(); 
            o_textField.setSelectionStart(0);
            o_textField.setSelectionEnd(o_textField.getText().length());
        }
        
        checkFields();
                
        JPanel x_dueDateLabelPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        x_dueDateLabelPanel.add(new JLabel("Due Date"));
        x_dueDateLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        JPanel x_remindDatePanel = new JPanel(new BorderLayout());
        x_remindDatePanel.add(x_dueDateLabelPanel, BorderLayout.WEST);
        x_remindDatePanel.add(o_remindDateField, BorderLayout.CENTER);
        
        RemindDateSuggestionPanel x_dateSugPanel = new RemindDateSuggestionPanel(o_reminder, o_remindDateField);
        x_dateSugPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		x_remindDatePanel.add(x_dateSugPanel, BorderLayout.EAST);
		x_remindDatePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, BorderLayout.NORTH);
        x_panel.add(x_remindDatePanel, BorderLayout.CENTER);
        x_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        o_saveButton.addActionListener(this);
        o_cancelButton.addActionListener(this);
        
        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(o_saveButton);
        x_buttonPanel.add(o_cancelButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

    private boolean checkFields()
    {
        try
        {
            o_dateFormat.parse(o_remindDateField.getText());
            JTextField x_textFields = o_compInfoPanel.getTextField();
            o_saveButton.setEnabled(x_textFields.getText() != null && x_textFields.getText().length() > 0);
        }
        catch(ParseException pe)
        {
            o_saveButton.setEnabled(false);
        }
        
        return o_saveButton.isEnabled();
    }
    
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            if(e.getSource() == o_saveButton)
            {
                if(checkFields())
                {
                    JTextField x_textField = o_compInfoPanel.getTextField();
                    
                    String x_text = x_textField.getText();
                    
                    if(!x_text.equals(o_reminder.getText()))
                    {
                        o_reminder.setText(x_textField.getText());
                    }
                    
                    Date x_thisDate = o_dateFormat.parse(o_remindDateField.getText());
                    
                    if(!x_thisDate.equals(o_reminder.getDueDate()))
                    {
                        o_reminder.setDueDate(x_thisDate);
                    }
                                    
                    WindowManager.getInstance().closeComponentWindow(o_reminder);                
                }
            }
            else
            {
                if(o_new)
                {
                    o_reminder.getItem().removeReminder(o_reminder);
                }
                
                WindowManager.getInstance().closeComponentWindow(o_reminder);
            }
        }
        catch(ParseException pe)
        {
            JOptionPane.showMessageDialog(null, "Error creating item: " + pe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }            
    }

    public void changedUpdate(DocumentEvent e)
    {
        checkFields();
    }

    public void insertUpdate(DocumentEvent e)
    {
        checkFields();
    }

    public void removeUpdate(DocumentEvent e)
    {
        checkFields();
    }
}
