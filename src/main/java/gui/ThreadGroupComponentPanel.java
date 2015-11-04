package gui;

import data.*;
import data.ThreadGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class ThreadGroupComponentPanel extends TablePanel
{
    private final ThreadGroup o_threadGroup;
    
    private final JButton o_addItemButton = new JButton("Add Item");
    
    private final JButton o_addGroupButton = new JButton("Add Thread");
    
    private final JButton o_removeButton = new JButton("Remove Selected");
    
    private final JButton o_openDocFolderButton = new JButton("Open Doc Folder");
    
    private final JButton o_setDocFolderButton = new JButton("Set Doc Folder");
    
    public ThreadGroupComponentPanel(final ThreadGroup p_threadGroup)
    {
        super(new ComponentListTableModel(p_threadGroup), 
              new CellRenderer(p_threadGroup));
        o_threadGroup = p_threadGroup;
        
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(1, GUIConstants.s_typeWidth);
        fixColumnWidth(3, GUIConstants.s_statsWidth);
        fixColumnWidth(4, GUIConstants.s_statsWidth);
        fixColumnWidth(5, GUIConstants.s_statsWidth);

        o_addItemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addItem();
			}
		}
		);
        
        o_addGroupButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                addNewThreadGroup();
            }            
        }
        );
        
        o_removeButton.addActionListener(new ActionListener(){            
            public void actionPerformed(ActionEvent e)
            {
                removeComponent();
            }            
        }
        );
        
        o_openDocFolderButton.setEnabled(o_threadGroup.getDocFolder() != null);
        
        o_openDocFolderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                FolderManager.openDocFolder(o_threadGroup);               
            }            
        });
        
        o_setDocFolderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                FolderManager.setDocFolder(o_threadGroup);     
                o_openDocFolderButton.setEnabled(o_threadGroup.getDocFolder() != null);
            }            
        });
        
        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(o_addItemButton);
        x_buttonPanel.add(o_addGroupButton);
        x_buttonPanel.add(o_removeButton);
        x_buttonPanel.add(o_openDocFolderButton);
        x_buttonPanel.add(o_setDocFolderButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

	protected void addItem()
	{
		Item x_item = new Item();
		o_threadGroup.addThreadGroupItem(x_item);
		WindowManager.getInstance().openComponentWindow(x_item, true, 0);
	}
    
    private void addNewThreadGroup()
    {
        String x_name = JOptionPane.showInputDialog(this, "Enter Thread Group Name");
        
        if(x_name != null)
        {
            ThreadGroup x_threadGroup = new ThreadGroup(new Date(), true, x_name, null, null);
            o_threadGroup.addThreadGroupItem(x_threadGroup);
            WindowManager.getInstance().openComponentWindow(x_threadGroup, true, 0);
        }
    }
    
    private void removeComponent()
    {
        int x_index = getSelectedRow();
        
        if(x_index != -1)
        {
            ThreadGroupItem x_groupItem = o_threadGroup.getThreadGroupItem(x_index);
            String x_message = "Remove " + x_groupItem.getType() + " '" + x_groupItem.getText() + "' ?";
            
            if(JOptionPane.showConfirmDialog(null, x_message) == JOptionPane.YES_OPTION)
            {
                WindowManager.getInstance().closeComponentWindow(x_groupItem);
                o_threadGroup.removeThreadGroupItem(x_groupItem);
            }
        }
    }
    
    private void showComponent(int p_col, int p_row)
    {
        if(p_row != -1)
        {
			WindowManager.getInstance().openComponentWindow(o_threadGroup.getThreadGroupItem(p_row), false, p_col > 2 ? p_col - 2 : 0);
        }
    }
    
    void tableRowDoubleClicked(int col, int row)
    {
        showComponent(col, row);
    }
}
