package gui;

import data.*;
import data.Thread;

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
    
    private final Thread o_thread;
    
    ActionCellRenderer(Thread p_thread)
    {
        o_thread = p_thread;
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

		List x_actionItems = ThreadHelper.getAllActiveActions(o_thread);
        Item x_item = (Item) x_actionItems.get(row);

		setColourForTime(x_component, x_item);

		if(isSelected) {
			x_component.setForeground(Color.BLACK);
			x_component.setBackground(new Color(155, 255, 255));
		}

        return x_component;
    }

	private void setColourForTime(Component x_component, Item x_item) {
		Date x_dueDate = x_item.getDueDate();
		Date x_now = new Date();
		Date x_lastThingToday = getLastThingToday();
		Date x_lastThingTomorrow = getLastThingTomorrow();

		if(x_now.after(x_dueDate))
		{
			x_component.setBackground(new Color(255, 155, 155));
		}
		else if(x_dueDate.before(x_lastThingToday))
		{
			x_component.setBackground(new Color(255, 203, 100));
		}
		else if(x_dueDate.before(x_lastThingTomorrow))
		{
			x_component.setBackground(new Color(255, 255, 155));
		}
		else if((x_dueDate.getTime() - x_now.getTime()) < (1000 * 60 * 60 * 24 * 7))
		{
			x_component.setBackground(new Color(155,255,155));
		}
		else
		{
			x_component.setBackground(Color.WHITE);
		}
	}

	private Date getLastThingToday()
	{
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(Calendar.HOUR_OF_DAY, 23);
		x_calendar.set(Calendar.MINUTE, 59);
		x_calendar.set(Calendar.SECOND, 59);
		x_calendar.set(Calendar.MILLISECOND, 999);
		return x_calendar.getTime();
	}

	private Date getLastThingTomorrow()
	{
		Calendar x_calendar = Calendar.getInstance();
		x_calendar.set(Calendar.HOUR_OF_DAY, 23);
		x_calendar.set(Calendar.MINUTE, 59);
		x_calendar.set(Calendar.SECOND, 59);
		x_calendar.set(Calendar.MILLISECOND, 999);
		x_calendar.roll(Calendar.DATE, true);
		return x_calendar.getTime();
	}
}
