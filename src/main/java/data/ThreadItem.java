package data;

import java.io.File;
import java.util.*;

public abstract class ThreadItem extends CollectionComponent
{
    private File o_docFolder;
    
    ThreadItem(Date p_creationDate, boolean p_active, String p_text,
			   Component[] p_components, Comparator p_comparator, File p_docFolder)
    {
        super(p_creationDate, p_active, p_text, p_components, p_comparator);
        o_docFolder = p_docFolder;
    }
    
    public Thread getThread()
    {
        return (Thread) getParentComponent();
    }
    
    public File getDocFolder()
    {
        return o_docFolder;
    }
    
    public void setDocFolder(File p_docFolder)
    {
        o_docFolder = p_docFolder;
        changed();
    }

	public abstract String getType();
}