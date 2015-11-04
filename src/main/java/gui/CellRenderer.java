package gui;

import data.Component;
import data.*;
import data.Thread;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.*;
import java.util.*;

class CellRenderer extends DefaultTableCellRenderer
{
    public static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");
    
    private final Component o_component;
    
    CellRenderer(Component p_component)
    {
        o_component = p_component;
    }
    
    public java.awt.Component getTableCellRendererComponent(JTable table, 
                                                   Object value, 
                                                   boolean isSelected, 
                                                   boolean hasFocus, 
                                                   int row, 
                                                   int column)
    {
        Object x_value = value;
        
        if(x_value instanceof Date)
        {
            x_value = s_dateFormat.format((Date)x_value);
        }
        
        java.awt.Component x_component = 
            super.getTableCellRendererComponent(table, 
                                                x_value, 
                                                isSelected, 
                                                hasFocus, 
                                                row, 
                                                column);

		x_component.setForeground(Color.BLACK);
		x_component.setBackground(Color.WHITE);

        if(o_component instanceof Thread)
        {
            Thread x_thread = (Thread) o_component;
            
            ThreadItem x_item = x_thread.getThreadItem(row);
            
            if(!x_item.isActive())
            {
                x_component.setBackground(Color.LIGHT_GRAY);
            }
            else
            {
                x_component.setBackground(Color.WHITE);
            }
        }
        
        if(o_component instanceof Item)
        {
            Item x_item = (Item) o_component;
            
            Reminder x_reminder = x_item.getReminder(row);
            
            if(!x_reminder.isActive())
            {
                x_component.setBackground(Color.LIGHT_GRAY);
            }
            else
            {
                x_component.setBackground(Color.WHITE);
            }
        }

		if(isSelected) {
			x_component.setForeground(Color.BLACK);
			x_component.setBackground(Color.CYAN);
		}

        return x_component;
    }
    
    private Date getEightPmToday()
    {
        Calendar x_calendar = Calendar.getInstance();
        x_calendar.set(Calendar.HOUR_OF_DAY, 20);
        x_calendar.set(Calendar.MINUTE, 0);
        x_calendar.set(Calendar.SECOND, 0);
        x_calendar.set(Calendar.MILLISECOND, 0);
        return x_calendar.getTime();
    }
    
    private Date getEightPmTomorrow()
    {
        Calendar x_calendar = Calendar.getInstance();
        x_calendar.set(Calendar.HOUR_OF_DAY, 20);
        x_calendar.set(Calendar.MINUTE, 0);
        x_calendar.set(Calendar.SECOND, 0);
        x_calendar.set(Calendar.MILLISECOND, 0);
        x_calendar.roll(Calendar.DATE, true);
        return x_calendar.getTime();
    }
}
