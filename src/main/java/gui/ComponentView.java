package gui;

import data.Component;

public abstract class ComponentView
{
    private final Component o_component;
    
    public ComponentView(Component p_component)
    {
        o_component = p_component;
    }
    
    public abstract String[] getColumnNames();
    
    public abstract int[] getColumnWidths();
    
    public abstract Class[] getColumnClasses();
    
    public abstract Object getValue(int p_col, int p_row);
    
    public String getColumnName(int p_col)
    {
        return getColumnNames()[p_col];
    }
    
    public int getColumnWidth(int p_col)
    {
        return getColumnWidths()[p_col];
    }
    
    public Class getColumnClass(int p_col)
    {
        return getColumnClasses()[p_col];
    }
}
