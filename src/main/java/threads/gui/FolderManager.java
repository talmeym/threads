package threads.gui;

import threads.data.Component;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class FolderManager {
    static void openDocFolder(Component p_component) {
        if(p_component.getDocFolder() != null) {
			try {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(new File(p_component.getDocFolder().getAbsolutePath()));
				}
            } catch(IOException ioe) {
                System.err.println("Error opening folder: " + ioe);
            }
        }
    }
    
    static void setDocFolder(Component p_component, JPanel p_panretPanel) {
        JFileChooser x_chooser = p_component.getDocFolder() != null ? new JFileChooser(p_component.getDocFolder()) : new JFileChooser();
		x_chooser.setDialogTitle("Set Document Folder");

        x_chooser.setFileFilter(new FileFilter() {
            public boolean accept(File p_file) {
                return p_file.exists() && p_file.isDirectory();
            }

            public String getDescription() {
                return "Folders";
            }
        });

        x_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = x_chooser.showDialog(p_panretPanel, "Set");

		if(returnVal == JFileChooser.APPROVE_OPTION)  {
            File x_folder = x_chooser.getSelectedFile(); 
            
            if(x_folder != null) {
                p_component.setDocFolder(x_folder);
            }
        }
    }
}
