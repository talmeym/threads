package gui;

import data.Thread;

import javax.swing.*;
import java.awt.*;

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

