package gui;

import data.*;
import util.*;

import javax.swing.table.DefaultTableModel;
import java.util.List;

abstract class ComponentTableModel <COMPONENT extends Component, DATA_TYPE> extends DefaultTableModel implements TimeUpdateListener, GoogleSyncListener, ComponentChangeListener {
    private final COMPONENT o_component;
    private final String[] o_columnNames;

	ComponentTableModel(COMPONENT p_component, String[] p_columnNames) {
        o_component = p_component;
        o_columnNames = p_columnNames;
		o_component.addComponentChangeListener(this);
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

    @Override
    public int getRowCount() {
		if(getComponent() == null) {
			return 0;
		}

		return getDataItems().size();
	}

	@Override
    public void componentChanged(ComponentChangeEvent p_cce) {
		fireTableDataChanged();
    }

	@Override
    public void timeUpdate() {
        fireTableDataChanged();
    }

	@Override
	public void googleSyncStarted() {
		// do nothing by default
	}

	public void googleSynced() {
		fireTableDataChanged();
	}

	abstract List<DATA_TYPE> getDataItems();

	DATA_TYPE getDataItem(int p_row, int p_col) {
		return getDataItems().get(p_row);
	}
}
