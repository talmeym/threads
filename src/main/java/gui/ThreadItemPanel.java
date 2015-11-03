package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.Item;
import data.Thread;

public class ThreadItemPanel extends TablePanel
{
    private final Thread o_thread;
    
    private final JButton o_addItemButton = new JButton("Add Item");
    
    private final JButton o_removeItemButton = new JButton("Remove Item");
    
    private final JButton o_openDocFolderButton = new JButton("Open Doc Folder");
    
    private final JButton o_setDocFolderButton = new JButton("Set Doc Folder");
    
    protected ThreadItemPanel(final Thread p_thread)
    {
        super(new ThreadItemTableModel(p_thread), 
              new CellRenderer(p_thread));
        o_thread = p_thread;
        fixColumnWidth(0, GUIConstants.s_creationDateWidth);
        fixColumnWidth(2, GUIConstants.s_creationDateWidth);
        fixColumnWidth(3, GUIConstants.s_dateStatusWidth);
        
        o_addItemButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                addItem();               
            }            
        });

        o_removeItemButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                removeItem();               
            }            
        });
        
        o_openDocFolderButton.setEnabled(o_thread.getDocFolder() != null);
        
        o_openDocFolderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                FolderManager.openDocFolder(o_thread);               
            }            
        });
        
        o_setDocFolderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                FolderManager.setDocFolder(o_thread);   
                o_openDocFolderButton.setEnabled(o_thread.getDocFolder() != null);
            }            
        });
        
        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(o_addItemButton);
        x_buttonPanel.add(o_removeItemButton);
        x_buttonPanel.add(o_openDocFolderButton);
        x_buttonPanel.add(o_setDocFolderButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(x_buttonPanel, BorderLayout.SOUTH);
    }
    
    protected void addItem()
    {
        Item x_item = new Item();
        o_thread.addItem(x_item);
        WindowManager.getInstance().openComponentWindow(x_item, true);        
    }

    private void showItem(int p_index)
    {
        if(p_index != -1)
        {
            WindowManager.getInstance().openComponentWindow(o_thread.getItem(p_index), false);
        }
    }

    private void removeItem()
    {
        int x_index = getSelectedRow();
        
        if(x_index != -1)
        {
            Item x_item = o_thread.getItem(x_index);
            
            if(JOptionPane.showConfirmDialog(null, "Remove Item '" + x_item.getText() + "' ?") == JOptionPane.YES_OPTION)
            {
                WindowManager.getInstance().closeComponentWindow(x_item);
                o_thread.removeItem(x_item);
            }
        }
    }    

    void tableRowDoubleClicked(int col, int row)
    {
        showItem(row);
    }
}
