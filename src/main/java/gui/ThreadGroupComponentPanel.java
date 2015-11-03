package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.Thread;
import data.ThreadGroup;
import data.ThreadGroupItem;

public class ThreadGroupComponentPanel extends TablePanel
{
    private final ThreadGroup o_threadGroup;
    
    private final JButton o_addThreadButton = new JButton("Add Thread");
    
    private final JButton o_addGroupButton = new JButton("Add Thread Group");
    
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
        
        o_addThreadButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                addNewThread();
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
        x_buttonPanel.add(o_addThreadButton);
        x_buttonPanel.add(o_addGroupButton);
        x_buttonPanel.add(o_removeButton);
        x_buttonPanel.add(o_openDocFolderButton);
        x_buttonPanel.add(o_setDocFolderButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addNewThread()
    {
        String x_name = JOptionPane.showInputDialog(this, "Enter Thread Name");
        
        if(x_name != null)
        {
            Thread x_thread = new Thread(new Date(), true, x_name, null, null);
            o_threadGroup.addThreadGroupItem(x_thread);
            WindowManager.getInstance().openComponentWindow(x_thread, true);
        }
    }
    
    private void addNewThreadGroup()
    {
        String x_name = JOptionPane.showInputDialog(this, "Enter Thread Group Name");
        
        if(x_name != null)
        {
            ThreadGroup x_threadGroup = new ThreadGroup(new Date(), true, x_name, null, null);
            o_threadGroup.addThreadGroupItem(x_threadGroup);
            WindowManager.getInstance().openComponentWindow(x_threadGroup, true);
        }
    }
    
    private void removeComponent()
    {
        int x_index = getSelectedRow();
        
        if(x_index != -1)
        {
            ThreadGroupItem x_item = o_threadGroup.getThreadGroupItem(x_index);
            String x_message = "Remove Thread" + ((x_item instanceof Thread ? " " : "Group ") + "'" + x_item.getText() + "' ?");
            
            if(JOptionPane.showConfirmDialog(null, x_message) == JOptionPane.YES_OPTION)
            {
                WindowManager.getInstance().closeComponentWindow(x_item);
                o_threadGroup.removeThreadGroupItem(x_item);
            }
        }
    }
    
    private void showComponent(int p_index)
    {
        if(p_index != -1)
        {
            WindowManager.getInstance().openComponentWindow(o_threadGroup.getThreadGroupItem(p_index), false);
        }
    }
    
    void tableRowDoubleClicked(int col, int row)
    {
        showComponent(row);
    }
}
