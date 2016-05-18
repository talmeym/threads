package gui;

import data.*;
import data.Thread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ThreadReminderPanel extends TablePanel implements Observer
{
    private final Thread o_thread;
    
    private JButton o_dismissButton = new JButton("Dismiss");
    
    ThreadReminderPanel(Thread p_thread)
    {
        super(new ReminderTableModel(p_thread),
              new CellRenderer(null));
        o_thread = p_thread;
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_threadWidth);
        fixColumnWidth(3, GUIConstants.s_creationDateWidth);
        fixColumnWidth(4, GUIConstants.s_dateStatusWidth);

		o_dismissButton.setEnabled(false);
		o_dismissButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                int x_index = getSelectedRow();
                
                if(x_index != -1)
                {
                    Reminder x_reminder = (Reminder) ThreadHelper.getAllDueReminders(o_thread).get(x_index);
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
            Reminder x_reminder = (Reminder) ThreadHelper.getAllDueReminders(o_thread).get(p_index);
            WindowManager.getInstance().openComponentWindow(x_reminder, false, 0);
        }
    }

    private void showItem(int p_index)
    {
        if(p_index != -1)
        {
            Reminder x_reminder = (Reminder) ThreadHelper.getAllDueReminders(o_thread).get(p_index);
            WindowManager.getInstance().openComponentWindow(x_reminder.getItem(), false, 0);
        }
    }
    
    private void showThread(int p_index)
    {
        if(p_index != -1)
        {
            Reminder x_reminder = (Reminder) ThreadHelper.getAllDueReminders(o_thread).get(p_index);
            WindowManager.getInstance().openComponentWindow(x_reminder.getItem().getThread(), false, 0);
        }
    }

	@Override
	void tableRowClicked(int col, int row) {
		o_dismissButton.setEnabled(row != -1);
	}

	void tableRowDoubleClicked(int col, int row)
    {
        switch(col)
        {
			case 1: showItem(row); break;
			default: showItem(row); showReminder(row); break;
        }
    }

	@Override
	public void update(Observable observable, Object o) {
		tableRowClicked(-1, -1);
	}
}
