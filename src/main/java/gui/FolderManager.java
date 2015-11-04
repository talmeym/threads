package gui;

import data.ThreadItem;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;

public class FolderManager
{
    private static final String s_explorerCommand = "c:\\WINDOWS\\explorer.exe";
    
    public static void openDocFolder(ThreadItem p_item)
    {
        if(p_item.getDocFolder() != null)
        {
            Runtime x_runTime = Runtime.getRuntime();
            
            String p_folderName = "\"" + p_item.getDocFolder().getAbsolutePath() + "\"";
            
            try
            {
                x_runTime.exec(s_explorerCommand + " " + p_folderName);
            }
            catch(IOException ioe)
            {
                System.err.println("Error running explorer: " + ioe);
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
