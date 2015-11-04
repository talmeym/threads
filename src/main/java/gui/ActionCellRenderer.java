package gui;

import data.*;
import data.ThreadGroup;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.Component;
import java.text.*;
import java.util.*;
import java.util.List;

public class ActionCellRenderer extends DefaultTableCellRenderer
{
	public static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");
    
    private final ThreadGroup o_threadGroup;
    
    ActionCellRenderer(ThreadGroup p_threadGroup)    
    {
        o_threadGroup = p_threadGroup;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
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

		List x_actionItems = ThreadGroupHelper.getActionItems(o_threadGroup);
        
        Item x_item = (Item) x_actionItems.get(row);
        
        Date x_dueDate = x_item.getDueDate();
        Date x_now = new Date();
        Date x_eightPmToday = getEightPmToday();
        Date x_eightPmTomorrow = getEightPmTomorrow();        
        
        if(x_now.after(x_dueDate))
        {
            x_component.setBackground(Color.RED);
        }
        else if(x_dueDate.before(x_eightPmToday))
        {
            x_component.setBackground(Color.ORANGE);
        }
        else if(x_dueDate.before(x_eightPmTomorrow))
        {
            x_component.setBackground(Color.YELLOW);
        }
        else if((x_dueDate.getTime() - x_now.getTime()) < (1000 * 60 * 60 * 24 * 7))
        {
            x_component.setBackground(Color.GREEN);
        }
        else
        {
            x_component.setBackground(Color.WHITE);
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
