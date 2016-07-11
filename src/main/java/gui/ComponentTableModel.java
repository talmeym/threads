package gui;

import data.*;
import util.*;

import javax.swing.table.DefaultTableModel;
import java.util.*;

abstract class ComponentTableModel <COMPONENT extends Component, DATA_TYPE> extends DefaultTableModel implements TimeUpdateListener, GoogleSyncListener, Observer {
    private final COMPONENT o_component;
    private final String[] o_columnNames;

	ComponentTableModel(COMPONENT p_component, String[] p_columnNames) {
        o_component = p_component;
        o_columnNames = p_columnNames;

		if(o_component != null) {
            o_component.addObserver(this);
        }
    }
    
    protected final COMPONENT getComponent() {
        return o_component;
    }
    
    public final int getColumnCount() {
        return o_columnNames != null ? o_columnNames.length : 0;
    }
    
    public final String getColumnName(int p_col) {
        return o_columnNames[p_col];
    }
    
    public final boolean isCellEditable(int p_row, int p_col) {
        return false;
    }
    
    public void update(Observable observable, Object o) {
		fireTableDataChanged();
    }

	@Override
    public void timeUpdate() {
        fireTableDataChanged();
    }

	public void googleSynced() {
		fireTableDataChanged();
	}

	abstract DATA_TYPE getDataItem(int p_row, int p_col);
}
