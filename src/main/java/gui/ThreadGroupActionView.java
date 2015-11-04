package gui;

import data.ThreadGroup;

import java.util.Date;

public class ThreadGroupActionView extends ComponentView
{
    public final String[] o_colNames = new String[] {
            "Creation Date", 
            "Thread", 
            "Item", 
            "Due Date", 
            "Due"
    };
    
    public final int[] o_colWidths = new int[] {
            GUIConstants.s_creationDateWidth, 
            GUIConstants.s_threadWidth, 
            -1, 
            GUIConstants.s_creationDateWidth, 
            GUIConstants.s_dateStatusWidth
    };
    
    public final Class[] o_colClasses = new Class[]{
            Date.class,
            String.class,
            String.class,
            Date.class,
            String.class
    };
    
    public ThreadGroupActionView(ThreadGroup p_threadgroup)
    {
        super(p_threadgroup);
    }

    public String[] getColumnNames()
    {
        return o_colNames;
    }

    public int[] getColumnWidths()
    {
        return o_colWidths;
    }

    public Class[] getColumnClasses()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getValue(int p_col, int p_row)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
