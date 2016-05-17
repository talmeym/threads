package gui;

import data.ThreadItem;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.*;

public class FolderManager
{
    public static void openDocFolder(ThreadItem p_item)
    {
        if(p_item.getDocFolder() != null)
        {
			try
            {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(new File(p_item.getDocFolder().getAbsolutePath()));
				}
            }
            catch(IOException ioe)
            {
                System.err.println("Error opening folder: " + ioe);
            }
        }
    }
    
    public static void setDocFolder(ThreadItem p_item)
    {
        JFileChooser x_chooser;
        
        if(p_item.getDocFolder() == null)
        {
            if(p_item.getThread() != null && p_item.getThread().getDocFolder() != null)
            {
                x_chooser = new JFileChooser(p_item.getThread().getDocFolder());
            }
            else
            {
                x_chooser = new JFileChooser();
            }
        }
        else
        {               
            x_chooser = new JFileChooser(p_item.getDocFolder());
        }
        
        x_chooser.setFileFilter(new FileFilter(){

            public boolean accept(File p_file)
            {                        
                return p_file.exists() && p_file.isDirectory();
            }

            public String getDescription()
            {
                return "Folders";
            }
            
        });
        x_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int returnVal = x_chooser.showDialog(null, "Select");
        
        if(returnVal == JFileChooser.APPROVE_OPTION) 
        {
            File x_folder = x_chooser.getSelectedFile(); 
            
            if(x_folder != null)
            {
                p_item.setDocFolder(x_folder);
            }
        }
    }
}
