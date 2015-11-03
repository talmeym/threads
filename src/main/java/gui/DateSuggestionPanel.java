package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

class DateSuggestionPanel extends JPanel
{   
    private static final DateFormat s_dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
    
    private static DateItem[] s_timeItems = new DateItem[]{new DateItem("9 AM", 9), 
                                                           new DateItem("Midday", 12), 
                                                           new DateItem("C.O.B.", 18)};  

    private static DateItem[] s_weekItems = new DateItem[]{new DateItem("This", 0),
                                                           new DateItem("Next", 7),
                                                           new DateItem("2 Weeks", 21), 
                                                           new DateItem("3 Weeks", 28), 
                                                           new DateItem("4 Weeks", 35)};
    
    private static DateItem[] s_dayItems = new DateItem[]{new DateItem("Mon", 2),
                                                          new DateItem("Tues", 3),
                                                          new DateItem("Wed", 4),
                                                          new DateItem("Thur", 5),
                                                          new DateItem("Fri", 6),
                                                          new DateItem("Sat", 7),
                                                          new DateItem("Sun", 1)};
    
    private JComboBox o_timeBox = new JComboBox(s_timeItems);

    private JComboBox o_weekBox = new JComboBox(s_weekItems);

    private JComboBox o_dayBox = new JComboBox(s_dayItems);

    private JButton o_suggestButton = new JButton("Suggest");

    private final JTextField o_textField;
    
    DateSuggestionPanel(JTextField p_textField)
    {
        super(new GridLayout(1, 0, 5, 5));
        o_textField = p_textField;

        o_suggestButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                suggest();
            }
        });
        
        add(o_timeBox);        
        add(o_weekBox);
        add(o_dayBox);
        add(o_suggestButton);
    }
    
    private void suggest()
    {
        Calendar x_calendar = Calendar.getInstance();
        
        x_calendar.set(Calendar.HOUR_OF_DAY, ((DateItem)o_timeBox.getSelectedItem()).o_value);
        x_calendar.set(Calendar.MINUTE, 0);
        x_calendar.set(Calendar.SECOND, 0);
        x_calendar.set(Calendar.MILLISECOND, 0);

        x_calendar.set(Calendar.DAY_OF_WEEK, ((DateItem)o_dayBox.getSelectedItem()).o_value);
        
        int x_daysToAdd = ((DateItem)o_weekBox.getSelectedItem()).o_value;               

        int x_daysLeftInYear = 365 - x_calendar.get(Calendar.DAY_OF_YEAR);
        
        if(x_daysToAdd > x_daysLeftInYear)
        {
            x_calendar.roll(Calendar.YEAR, true);
        }
        
        int x_daysLeftInMonth = x_calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - x_calendar.get(Calendar.DAY_OF_MONTH);
        
        if(x_daysToAdd > x_daysLeftInMonth)
        {
            x_calendar.roll(Calendar.MONTH, true);
            x_daysToAdd++;
        }

        x_calendar.roll(Calendar.DATE, x_daysToAdd);
        
        o_textField.setText(s_dateFormat.format(x_calendar.getTime()));
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
