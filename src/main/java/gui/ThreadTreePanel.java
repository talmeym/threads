package gui;

import data.Component;
import data.*;
import data.Thread;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.*;

public class ThreadTreePanel extends JPanel implements TreeSelectionListener, ActionListener {
    private final JTree o_threadFromTree;
    private final JTree o_threadToTree;

    private JButton o_moveButton = new JButton("Move");
    
    public ThreadTreePanel(Thread p_thread) {
        super(new BorderLayout());
        
        TreeModel x_model = new ThreadTreeModel(p_thread);

        o_threadFromTree = new JTree(x_model);
        o_threadFromTree.addTreeSelectionListener(this);
		o_threadFromTree.setCellRenderer(new ThreadTreeCellRenderer());

		JPanel x_fromLabelPanel = new JPanel(new BorderLayout());
		x_fromLabelPanel.add(new JLabel("From:"), BorderLayout.CENTER);
		x_fromLabelPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

		JPanel leftTreePanel = new JPanel(new BorderLayout());
		leftTreePanel.add(x_fromLabelPanel, BorderLayout.NORTH);
		leftTreePanel.add(new JScrollPane(o_threadFromTree), BorderLayout.CENTER);

        o_threadToTree = new JTree(x_model);
        o_threadToTree.addTreeSelectionListener(this);
		o_threadToTree.setCellRenderer(new ThreadTreeCellRenderer());

		JPanel x_toLabelPanel = new JPanel(new BorderLayout());
		x_toLabelPanel.add(new JLabel("To:"), BorderLayout.CENTER);
		x_toLabelPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

		JPanel rightTreePanel = new JPanel(new BorderLayout());
		rightTreePanel.add(x_toLabelPanel, BorderLayout.NORTH);
		rightTreePanel.add(new JScrollPane(o_threadToTree), BorderLayout.CENTER);

        o_moveButton.setEnabled(false);
        o_moveButton.addActionListener(this);

        JPanel x_panel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_panel.add(leftTreePanel);
        x_panel.add(rightTreePanel);
		x_panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel x_buttonPanel = new JPanel(new BorderLayout());
        x_buttonPanel.add(o_moveButton, BorderLayout.CENTER);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        add(x_panel, BorderLayout.CENTER);
        add(x_buttonPanel, BorderLayout.SOUTH);
    }

    public void valueChanged(TreeSelectionEvent e) {
        if(o_threadFromTree.getSelectionPath() != null && o_threadToTree.getSelectionPath() != null) {
            Object x_toObj = o_threadToTree.getSelectionPath().getLastPathComponent();
            o_moveButton.setEnabled(x_toObj instanceof Thread);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Component x_fromObj = (Component) o_threadFromTree.getSelectionPath().getLastPathComponent();
        Component x_toObj = (Component) o_threadToTree.getSelectionPath().getLastPathComponent();
        
        if(x_fromObj instanceof Item) {
            Item x_toMove = (Item) x_fromObj;
            x_toMove.getParentThread().removeItem(x_toMove);
            Thread x_thread = (Thread) x_toObj;
            x_thread.addItem(x_toMove);
        }
        
        if(x_fromObj instanceof Thread) {
            Thread x_toMove = (Thread) x_fromObj;
            x_toMove.getParentThread().removeThreadItem(x_toMove);
            Thread x_thread = (Thread) x_toObj;
            x_thread.addThreadItem(x_toMove);
        }
    }
}
