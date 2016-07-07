package gui;

import data.Component;
import util.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

abstract class ComponentTablePanel <COMPONENT extends Component, TYPE> extends MemoryPanel implements TimeUpdateListener, GoogleSyncListener, TableSelectionListener<TYPE> {
	protected final ComponentTableModel<COMPONENT, TYPE> o_tableModel;
	protected final JTable o_table;
	private final List<TableSelectionListener> o_listeners = new ArrayList<TableSelectionListener>();

	protected ComponentTablePanel(ComponentTableModel<COMPONENT, TYPE> p_tableModel, TableCellRenderer p_cellRenderer) {
        super(new BorderLayout());
		o_tableModel = p_tableModel;
		o_listeners.add(this);
		o_table = new JTable(p_tableModel);
        o_table.setRowHeight(GUIConstants.s_tableRowHeight);
        o_table.setDefaultRenderer(String.class, p_cellRenderer);
        o_table.setDefaultRenderer(Object[].class, p_cellRenderer);
        o_table.setDefaultRenderer(Date.class, p_cellRenderer);
        o_table.setDefaultRenderer(Icon.class, p_cellRenderer);
		o_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        o_table.addMouseListener(new MouseAdapter(){
			@Override
            public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					for(TableSelectionListener x_listener: o_listeners){
						x_listener.tableRowClicked(o_table.getSelectedRow(), o_table.getSelectedColumn(), getSelectedObject());
					}
				}

                if(e.getClickCount() == 2) {
					for(TableSelectionListener x_listener: o_listeners){
						x_listener.tableRowDoubleClicked(o_table.getSelectedRow(), o_table.getSelectedColumn(), getSelectedObject());
					}
                }
            }
        });

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(new JScrollPane(o_table), BorderLayout.CENTER);
    }

	public void addTableSelectionListener(TableSelectionListener p_listener) {
		o_listeners.add(p_listener);
	}

    protected void fixColumnWidth(int p_column, int p_width) {
        TableColumnModel x_model = o_table.getColumnModel();        
        x_model.getColumn(p_column).setPreferredWidth(p_width);
        x_model.getColumn(p_column).setMinWidth(p_width);
        x_model.getColumn(p_column).setMaxWidth(p_width);
    }
    
    protected TYPE getSelectedObject() {
		int x_row = o_table.getSelectedRow();
		return x_row != -1 ? o_tableModel.getDataItem(x_row, o_table.getSelectedColumn()) : null;
    }

	@Override
	public void tableRowClicked(int p_row, int p_col, TYPE o_obj) {
		// do nothing by default
	}

	@Override
	public void tableRowDoubleClicked(int p_row, int p_col, TYPE o_obj) {
		// do nothing by default
	}

	@Override
	public void timeUpdate() {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void googleSynced() {
		tableRowClicked(-1, -1, null);
	}
}
