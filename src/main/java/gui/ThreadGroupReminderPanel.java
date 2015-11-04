package gui;

import data.*;
import data.ThreadGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ThreadGroupReminderPanel extends TablePanel
{
    private final ThreadGroup o_threadGroup;
    
    private JButton o_dismissButton = new JButton("Dismiss");
    
    ThreadGroupReminderPanel(ThreadGroup p_threadGroup)
    {
        super(new ReminderTableModel(p_threadGroup), 
              new CellRenderer(null));
        o_threadGroup = p_threadGroup;
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_threadWidth);
        fixColumnWidth(3, GUIConstants.s_creationDateWidth);
        fixColumnWidth(4, GUIConstants.s_dateStatusWidth);
        
        o_dismissButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                int x_index = getSelectedRow();
                
                if(x_index != -1)
                {
                    Reminder x_reminder = (Reminder) ThreadGroupHelper.getReminders(o_threadGroup).get(x_index);
                    x_reminder.setActive(false);
                }
            }            
        });
        
        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_dismissButton, BorderLayout.CENTER);
        x_panel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
        
        add(x_panel, BorderLayout.SOUTH);
    }
    
    private void showReminder(int p_index)
    {
        if(p_index != -1)
        {
            Reminder x_reminder = (Reminder) ThreadGroupHelper.getReminders(o_threadGroup).get(p_index); 
            WindowManager.getInstance().openComponentWindow(x_reminder, false);
        }
    }

    private void showItem(int p_index)
    {
        if(p_index != -1)
        {
            Reminder x_reminder = (Reminder) ThreadGroupHelper.getReminders(o_threadGroup).get(p_index); 
            WindowManager.getInstance().openComponentWindow(x_reminder.getItem(), false);
        }
    }
    
    private void showThread(int p_index)
    {
        if(p_index != -1)
        {
            Reminder x_reminder = (Reminder) ThreadGroupHelper.getReminders(o_threadGroup).get(p_index); 
            WindowManager.getInstance().openComponentWindow(x_reminder.getItem().getThread(), false);
        }
    }
    
    void tableRowDoubleClicked(int col, int row)
    {
        switch(col)
        {
        case 1: showThread(row); break;
        default: showReminder(row); break;
        }
    }
}
