package gui;

import data.Reminder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.Date;

public class RemindDateSuggestionPanel extends JPanel
{
    private final Reminder o_reminder;
    
    private static final DateFormat s_dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    private static final DateItem[] s_weekItems;
    
    private static final DateItem[] s_dayItems;
    
    private static final DateItem[] s_hourItems;
    
    private static final DateItem[] s_minItems;  

    static
    {
        s_weekItems = new DateItem[5];
        
        for(int i = 0; i < s_weekItems.length; i++)
        {
            s_weekItems[i] = new DateItem(i + " Ws", i * 7 * 24 * 60 * 60 * 1000);
        }

        s_dayItems = new DateItem[7];
        
        for(int i = 0; i < s_dayItems.length; i++)
        {
            s_dayItems[i] = new DateItem(i + " Ds", i * 24 * 60 * 60 * 1000);
        }
        
        s_hourItems = new DateItem[24];
        
        for(int i = 0; i < s_hourItems.length; i++)
        {
            s_hourItems[i] = new DateItem(i + " Hs", i * 60 * 60 * 1000);
        }
        
        s_minItems = new DateItem[12];
        
        for(int i = 0; i < s_minItems.length; i++)
        {
            s_minItems[i] = new DateItem(i * 5 + " Ms", i * 5 * 60 * 1000);
        }        
    }
    
    private JComboBox o_minBox = new JComboBox(s_minItems);

    private JComboBox o_hourBox = new JComboBox(s_hourItems);

    private JComboBox o_dayBox = new JComboBox(s_dayItems);

    private JComboBox o_weekBox = new JComboBox(s_weekItems);

    private JButton o_suggestButton = new JButton("Suggest");

    private final JTextField o_textField;
    
    public RemindDateSuggestionPanel(Reminder p_reminder, JTextField p_textField)
    {
        super(new GridLayout(1, 0, 5, 5));
        
        o_reminder = p_reminder;
        o_textField = p_textField;
        
        o_suggestButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                suggest();
            }
        });
        
        add(o_weekBox);        
        add(o_dayBox);        
        add(o_hourBox);
        add(o_minBox);
        add(o_suggestButton);
    }
    
    private void suggest()
    {
        long x_timeToSubtract = 0;
        
        x_timeToSubtract += ((DateItem)o_weekBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_dayBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_hourBox.getSelectedItem()).o_value;
        x_timeToSubtract += ((DateItem)o_minBox.getSelectedItem()).o_value;
        
        Date x_dueDate = o_reminder.getItem().getDueDate();
        
        o_textField.setText(s_dateFormat.format(new Date(x_dueDate.getTime() - x_timeToSubtract)));
    }
    
    private static class DateItem
    {
        public final String o_display;
        
        public final int o_value;
        
        public DateItem(String p_display, int p_value)
        {
            o_display = p_display;
            o_value = p_value;
        }
        
        public String toString()
        {
            return o_display;
        }
    }
}
