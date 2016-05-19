package gui;

import data.*;
import data.Thread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ItemPanel extends TablePanel implements ChangeListener, ActionListener
{
    private final Item o_item;
    private final boolean o_new;
    
    private final ComponentInfoPanel o_compInfoPanel;    

    private final JButton o_addReminderButton = new JButton("Add Reminder");
    private final JButton o_removeReminderButton = new JButton("Remove Reminder");
    private final JButton o_closeButton = new JButton("Close");

    ItemPanel(Item p_item, boolean p_new)
    {        
        super(new ItemReminderTableModel(p_item), 
                new CellRenderer(p_item));
        
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusWidth);
        
        o_item = p_item;
        o_new = p_new;
        
        o_compInfoPanel = new ComponentInfoPanel(p_item, p_new);

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

        o_closeButton.addActionListener(this);

		JPanel x_dueDatePanel = new JPanel(new BorderLayout());
        x_dueDatePanel.add(new DateSuggestionPanel(o_item, this), BorderLayout.CENTER);

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(o_compInfoPanel, BorderLayout.NORTH);
        x_panel.add(x_dueDatePanel, BorderLayout.CENTER);
        x_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(o_addReminderButton);
        x_buttonPanel.add(o_removeReminderButton);
        x_buttonPanel.add(o_closeButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_panel, BorderLayout.NORTH);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

	@Override
	public void changed(boolean saved) {
		o_closeButton.setText(saved ? "Close" : "Cancel");
	}


    public void actionPerformed(ActionEvent e)
    {
		Thread x_thread = o_item.getThread();

		if(ThreadHelper.getActiveUpdates(x_thread).size() > 1 && JOptionPane.showConfirmDialog(null, "Set previous updates inactive ?") == JOptionPane.YES_OPTION)
		{

			for(int i = 0; i < x_thread.getThreadItemCount(); i++)
			{
				ThreadItem x_groupItem = x_thread.getThreadItem(i);

				if(x_groupItem instanceof Item)  {
					Item x_item = (Item) x_groupItem;

					if(x_item != o_item && x_item.getDueDate() == null && x_item.isActive())
					{
						x_item.setActive(false);
					}
				}
			}
		}

		WindowManager.getInstance().closeComponentWindow(o_item);
    }
    
    private void addReminder()
    {
        if(o_item.getDueDate() != null)
        {
            Reminder x_reminder = new Reminder(o_item);
            o_item.addReminder(x_reminder);
            WindowManager.getInstance().openComponentWindow(x_reminder, true, 0);
        }
    }
    
    private void removeReminder()
    {
        if(o_item.getDueDate() != null)
        {
            int x_index = getSelectedRow();
            
            if(x_index != -1)
            {
                Reminder x_reminder = o_item.getReminder(x_index);
                
                if(JOptionPane.showConfirmDialog(null, "Remove Reminder '" + x_reminder.getText() + "' ?") == JOptionPane.YES_OPTION)
                {
                    WindowManager.getInstance().closeComponentWindow(x_reminder);
                    o_item.removeReminder(x_reminder);
                }
            }
        }
    }  
    
    private void showReminder(int p_index)
    {
        if(p_index != -1)
        {
            WindowManager.getInstance().openComponentWindow(o_item.getReminder(p_index), false, 0);
        }
    }

	@Override
	void tableRowClicked(int col, int row) {
		//do nothing
	}

	void tableRowDoubleClicked(int col, int row)
    {
		switch(col) {
			default: showReminder(row);
		}
    }
}