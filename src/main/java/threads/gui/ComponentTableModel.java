package threads.gui;

import threads.data.Component;
import threads.data.HasDueDate;
import threads.util.GoogleSyncListener;
import threads.util.GoogleSyncer;
import threads.util.TimedUpdater;

import javax.swing.table.DefaultTableModel;
import java.util.List;

abstract class ComponentTableModel <COMPONENT extends Component, DATA_TYPE> extends DefaultTableModel implements GoogleSyncListener {
    private final COMPONENT o_component;
    private final String[] o_columnNames;

	private List<DATA_TYPE> o_dataItems;

	ComponentTableModel(COMPONENT p_component, String[] p_columnNames) {
        o_component = p_component;
        o_columnNames = p_columnNames;
		o_component.addComponentChangeListener(e -> reloadData());
		o_dataItems = getDataItems();
		TimedUpdater.getInstance().addActivityListener(this::reloadData);
		GoogleSyncer.getInstance().addActivityListener(this);
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

		return o_dataItems.size();
	}

	@Override
	public void googleSyncStarted() {
		// do nothing by default
	}

	@Override
	public void googleSynced() {
		fireTableDataChanged();
	}

	public void googleSynced(List<HasDueDate> p_hasDueDates) {
		fireTableDataChanged();
	}

	abstract List<DATA_TYPE> getDataItems();

	DATA_TYPE getDataItem(int p_row, int p_col) {
		return o_dataItems.get(p_row);
	}

	void reloadData() {
		o_dataItems = getDataItems();
		fireTableDataChanged();
	}

	int indexOf(DATA_TYPE p_dataItem) {
		return o_dataItems.indexOf(p_dataItem);
	}
}
