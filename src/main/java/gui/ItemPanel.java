package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import data.Deadline;
import data.Item;
import data.Reminder;
import data.Thread;

class ItemPanel extends TablePanel implements DocumentListener, ActionListener
{
    private static final DateFormat o_dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");     
    private static final Dimension o_dueFieldSize = new Dimension(130, 25);
    
    private final Item o_item;
    private final boolean o_new;
    
    private final ComponentInfoPanel o_compInfoPanel;    
    private final JTextField o_dueDateField = new JTextField();  
    private final JButton o_saveButton = new JButton("Save & Close");
    private final JButton o_cancelButton = new JButton("Cancel");    

    private final JButton o_addReminderButton = new JButton("Add Reminder");
    private final JButton o_removeReminderButton = new JButton("Remove Reminder");
    
    ItemPanel(Item p_item, boolean p_new)
    {        
        super(new ItemReminderTableModel(p_item), 
                new CellRenderer(p_item));
        
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusWidth);
        
        o_item = p_item;
        o_new = p_new;
        
        o_compInfoPanel = new ComponentInfoPanel(p_item);
        
        o_dueDateField.setPreferredSize(o_dueFieldSize);
        o_dueDateField.getDocument().addDocumentListener(this);

        o_saveButton.setEnabled(false);
        
        if(o_item.getDeadline() != null)
        {
            o_dueDateField.setText(o_dateFormat.format(o_item.getDeadline().getDueDate()));            
        }
        
        if(p_new)
        {
            JTextField o_textField = o_compInfoPanel.getTextField(); 
            o_textField.setSelectionStart(0);
            o_textField.setSelectionEnd(o_textField.getText().length());
        }
        
        checkFields();
        
        o_addReminderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                addReminder();
            }            
        });

        o_removeReminderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                removeReminder();
            }            
        });

        JPanel x_dueDateLabelPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        x_dueDateLabelPanel.add(new JLabel("Due Date"));
        x_dueDateLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        JPanel x_dueDatePanel = new JPanel(new BorderLayout());
        x_dueDatePanel.add(x_dueDateLabelPanel, BorderLayout.WEST);
        x_dueDatePanel.add(o_dueDateField, BorderLayout.CENTER);
        DateSuggestionPanel x_dateSugPanel = new DateSuggestionPanel(o_dueDateField);
        x_dateSugPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        x_dueDatePanel.add(x_dateSugPanel, BorderLayout.EAST);
        x_dueDatePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, BorderLayout.NORTH);
        x_panel.add(x_dueDatePanel, BorderLayout.CENTER);
        x_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        o_saveButton.addActionListener(this);
        o_cancelButton.addActionListener(this);
        o_dueDateField.addActionListener(this);
        
        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(o_saveButton);
        x_buttonPanel.add(o_addReminderButton);
        x_buttonPanel.add(o_removeReminderButton);
        x_buttonPanel.add(o_cancelButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
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
    
    private boolean checkFields()
    {
        try
        {
            if(o_dueDateField.getText() != null && o_dueDateField.getText().length() > 0)
            {
                Date x_date = o_dateFormat.parse(o_dueDateField.getText());
            }
            
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
                    JTextField x_textFields = o_compInfoPanel.getTextField();
                    
                    String x_text = x_textFields.getText();
                    String x_due = o_dueDateField.getText(); 
                    
                    if(!x_text.equals(o_item.getText()))
                    {
                        o_item.setText(x_textFields.getText());
                    }
                    
                    if(x_due != null && x_due.length() > 0)
                    {
                        if(o_item.getDeadline() == null)
                        {
                            o_item.setDeadline(new Deadline(new Date(), true, "Deadline", o_dateFormat.parse(x_due), null));
                        }
                        else
                        {
                            Date x_dueDate = o_dateFormat.parse(x_due);
                            
                            if(!o_item.getDeadline().getDueDate().equals(x_dueDate))
                            {
                                o_item.getDeadline().setActive(true);
                                o_item.getDeadline().setDueDate(x_dueDate);
                            }
                        }
                    }
                    else
                    {
                        if(o_item.getDeadline() != null)
                        {
                            o_item.setDeadline(null);
                        }
                    }
                    
                    WindowManager.getInstance().closeComponentWindow(o_item);
                    
                    if(o_new && o_dueDateField.getText().length() == 0)
                    {
                        if(JOptionPane.showConfirmDialog(null, "Set previous updates inactive ?") == JOptionPane.YES_OPTION)
                        {
                            Thread x_thread = o_item.getThread();
                            
                            for(int i = 0; i < x_thread.getItemCount(); i++)
                            {
                                Item x_item = x_thread.getItem(i);
                                
                                if(x_item != o_item && x_item.getDeadline() == null && x_item.isActive())
                                {
                                    x_item.setActive(false);
                                }
                            }
                        }
                    }
                }
            }
            else if(e.getSource() == o_dueDateField)
            {
                if(checkFields())
                {
                    if(o_dueDateField.getText().length() != 0)
                    {
                        Date x_dueDate = o_dateFormat.parse(o_dueDateField.getText()); 
                    
                        if(o_item.getDeadline() != null)
                        {
                            if(!x_dueDate.equals(o_item.getDeadline().getDueDate()))
                            {
                                o_item.getDeadline().setDueDate(x_dueDate);
                            }
                        }
                        else
                        {
                            o_item.setDeadline(new Deadline(new Date(), true, "Deadline", x_dueDate, null));
                        }
                    }
                    else
                    {
                        o_item.setDeadline(null);
                    }
                }
            }
            else
            {
                if(o_new)
                {
                    o_item.getThread().removeItem(o_item);
                }
                
                WindowManager.getInstance().closeComponentWindow(o_item);
            }
        }
        catch(ParseException pe)
        {
            JOptionPane.showMessageDialog(null, "Error creating item: " + pe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }            
    }
    
    private void addReminder()
    {
        if(o_item.getDeadline() != null)
        {
            Reminder x_reminder = new Reminder(o_item);
            o_item.getDeadline().addReminder(x_reminder);
            WindowManager.getInstance().openComponentWindow(x_reminder, true);
        }
    }
    
    private void removeReminder()
    {
        if(o_item.getDeadline() != null)
        {
            int x_index = getSelectedRow();
            
            if(x_index != -1)
            {
                Reminder x_reminder = o_item.getDeadline().getReminder(x_index);
                
                if(JOptionPane.showConfirmDialog(null, "Remove Reminder '" + x_reminder.getText() + "' ?") == JOptionPane.YES_OPTION)
                {
                    WindowManager.getInstance().closeComponentWindow(x_reminder);
                    o_item.getDeadline().removeReminder(x_reminder);
                }
            }
        }
    }  
    
    private void showReminder(int p_index)
    {
        if(p_index != -1)
        {
            WindowManager.getInstance().openComponentWindow(o_item.getDeadline().getReminder(p_index), false);
        }
    }
    
    void tableRowDoubleClicked(int col, int row)
    {
        showReminder(row);
    }
    
    
}