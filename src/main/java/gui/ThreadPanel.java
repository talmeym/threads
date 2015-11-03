package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import data.Thread;

class ThreadPanel extends JPanel
{
    private final Thread o_thread;
    
    ThreadPanel(Thread p_thread)
    {
        super(new BorderLayout());
        o_thread = p_thread;        
        
        add(new ComponentInfoPanel(p_thread), BorderLayout.NORTH);
        add(new ThreadItemPanel(p_thread), BorderLayout.CENTER);    
    }
}

