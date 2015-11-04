package gui;

import data.Component;
import data.*;
import data.ThreadGroup;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.*;

public class ThreadGroupTreePanel extends JPanel implements TreeSelectionListener, ActionListener
{
    private final JTree o_threadGroupFromTree;
    private final JTree o_threadGroupToTree;

    private JButton o_moveButton = new JButton("Move Selected");
    
    public ThreadGroupTreePanel(ThreadGroup p_threadGroup)
    {
        super(new BorderLayout());
        
        TreeModel x_model = new ThreadGroupTreeModel(p_threadGroup);
        
        o_threadGroupFromTree = new JTree(x_model);
        o_threadGroupFromTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        o_threadGroupFromTree.addTreeSelectionListener(this);
        
        o_threadGroupToTree = new JTree(x_model);
        o_threadGroupToTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        o_threadGroupToTree.addTreeSelectionListener(this);
        
        o_moveButton.setEnabled(false);
        o_moveButton.addActionListener(this);
        
        JPanel x_panel = new JPanel(new GridLayout(1, 0, 5, 5));        
        x_panel.add(new JScrollPane(o_threadGroupFromTree));
        x_panel.add(new JScrollPane(o_threadGroupToTree));
        
        JPanel x_buttonBox = new JPanel(new BorderLayout());
        x_buttonBox.add(o_moveButton, BorderLayout.CENTER);
        x_buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        add(x_panel, BorderLayout.CENTER);
        add(x_buttonBox, BorderLayout.SOUTH);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void valueChanged(TreeSelectionEvent e)
    {
        if(o_threadGroupFromTree.getSelectionPath() != null &&
           o_threadGroupToTree.getSelectionPath() != null)
        {
            Object x_fromObj = o_threadGroupFromTree.getSelectionPath().getLastPathComponent();
            Object x_toObj = o_threadGroupToTree.getSelectionPath().getLastPathComponent();            
            o_moveButton.setEnabled(moveValid(x_fromObj, x_toObj));
        }
    }

    private boolean moveValid(Object p_obj1, Object p_obj2)
    {
        
        if(p_obj1 == p_obj2)
        {
            return false;
        }
        
        return ((p_obj1 instanceof ThreadGroup && p_obj2 instanceof ThreadGroup));
    }

    public void actionPerformed(ActionEvent e)
    {
        Component x_fromObj = (Component) o_threadGroupFromTree.getSelectionPath().getLastPathComponent();
        Component x_toObj = (Component) o_threadGroupToTree.getSelectionPath().getLastPathComponent();
        
        if(x_fromObj instanceof Item)
        {
            Item x_toMove = (Item) x_fromObj;
            x_toMove.getThreadGroup().removeItem(x_toMove);
            ThreadGroup x_threadGroup = (ThreadGroup) x_toObj;
            x_threadGroup.addItem(x_toMove);
        }
        
        if(x_fromObj instanceof ThreadGroup)
        {
            ThreadGroup x_toMove = (ThreadGroup) x_fromObj;
            x_toMove.getThreadGroup().removeThreadGroupItem(x_toMove);
            ThreadGroup x_threadGroup = (ThreadGroup) x_toObj;
            x_threadGroup.addThreadGroupItem(x_toMove);
        }
    }
    
    /** 
     *         o_threadGroupFromTree.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent me)
            {
                if(me.getButton() == MouseEvent.BUTTON2 || me.getButton() == MouseEvent.BUTTON3)                   
                {
                    WindowManager.getInstance().openComponentWindow((Component)o_threadGroupFromTree.getSelectionPath().getLastPathComponent(), false);
                }
            }
        });
        
        o_threadGroupFromTree.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent ke)
            {
                if(ke.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    if(o_threadGroupFromTree.getSelectionPath() != null)
                    {
                        WindowManager.getInstance().openComponentWindow((Component)o_threadGroupFromTree.getSelectionPath().getLastPathComponent(), false);
                    }
                }
            }
        });
     * */
}
