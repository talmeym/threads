package gui;

import data.Component;
import data.*;
import data.Thread;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.*;

public class ThreadTreePanel extends JPanel implements TreeSelectionListener, ActionListener
{
    private final JTree o_threadFromTree;
    private final JTree o_threadToTree;

    private JButton o_moveButton = new JButton("Move Selected");
    
    public ThreadTreePanel(Thread p_thread)
    {
        super(new BorderLayout());
        
        TreeModel x_model = new ThreadTreeModel(p_thread);
        
        o_threadFromTree = new JTree(x_model);
        o_threadFromTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        o_threadFromTree.addTreeSelectionListener(this);
        
        o_threadToTree = new JTree(x_model);
        o_threadToTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        o_threadToTree.addTreeSelectionListener(this);
        
        o_moveButton.setEnabled(false);
        o_moveButton.addActionListener(this);
        
        JPanel x_panel = new JPanel(new GridLayout(1, 0, 5, 5));        
        x_panel.add(new JScrollPane(o_threadFromTree));
        x_panel.add(new JScrollPane(o_threadToTree));
        
        JPanel x_buttonBox = new JPanel(new BorderLayout());
        x_buttonBox.add(o_moveButton, BorderLayout.CENTER);
        x_buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_panel, BorderLayout.CENTER);
        add(x_buttonBox, BorderLayout.SOUTH);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void valueChanged(TreeSelectionEvent e)
    {
        if(o_threadFromTree.getSelectionPath() != null &&
           o_threadToTree.getSelectionPath() != null)
        {
            Object x_fromObj = o_threadFromTree.getSelectionPath().getLastPathComponent();
            Object x_toObj = o_threadToTree.getSelectionPath().getLastPathComponent();
            o_moveButton.setEnabled(moveValid(x_fromObj, x_toObj));
        }
    }

    private boolean moveValid(Object p_obj1, Object p_obj2)
    {
        
        if(p_obj1 == p_obj2)
        {
            return false;
        }
        
        return ((p_obj1 instanceof Thread && p_obj2 instanceof Thread));
    }

    public void actionPerformed(ActionEvent e)
    {
        Component x_fromObj = (Component) o_threadFromTree.getSelectionPath().getLastPathComponent();
        Component x_toObj = (Component) o_threadToTree.getSelectionPath().getLastPathComponent();
        
        if(x_fromObj instanceof Item)
        {
            Item x_toMove = (Item) x_fromObj;
            x_toMove.getThread().removeItem(x_toMove);
            Thread x_thread = (Thread) x_toObj;
            x_thread.addItem(x_toMove);
        }
        
        if(x_fromObj instanceof Thread)
        {
            Thread x_toMove = (Thread) x_fromObj;
            x_toMove.getThread().removeThreadItem(x_toMove);
            Thread x_thread = (Thread) x_toObj;
            x_thread.addThreadItem(x_toMove);
        }
    }
}
