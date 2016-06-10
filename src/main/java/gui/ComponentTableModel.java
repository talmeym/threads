package gui;

import data.Component;
import util.TimeUpdateListener;

import javax.swing.table.DefaultTableModel;
import java.util.*;

class ComponentTableModel extends DefaultTableModel implements TimeUpdateListener, Observer {
    private final Component o_component;
    private final String[] o_columnNames;
    
    ComponentTableModel(Component p_component, String[] p_columnNames) {
        o_component = p_component;
        o_columnNames = p_columnNames;
        
        if(o_component != null) {
            o_component.addObserver(this);
        }
    }
    
    protected final Component getComponent() {
        return o_component;
    }
    
    public final int getColumnCount() {
        return o_columnNames != null ? o_columnNames.length : 0;
    }
    
    public final String getColumnName(int col) {
        return o_columnNames[col];
    }
    
    public final boolean isCellEditable(int row, int col) {
        return false;
    }
    
    public void update(Observable o, Object arg) {
        fireTableDataChanged();
    }

    public void timeUpdate() {
        // do nothing by default
    }
}
