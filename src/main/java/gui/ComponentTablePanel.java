package gui;

import util.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

abstract class ComponentTablePanel extends MemoryPanel implements TimeUpdateListener, GoogleSyncListener, TableSelectionListener {
    protected final JTable o_table;
	private final List<TableSelectionListener> o_listeners = new ArrayList<TableSelectionListener>();

	protected ComponentTablePanel(TableModel p_tableModel, TableCellRenderer p_cellRenderer) {
        super(new BorderLayout());
		o_listeners.add(this);
		o_table = new JTable(p_tableModel);
        o_table.setRowHeight(GUIConstants.s_tableRowHeight);
        o_table.setDefaultRenderer(String.class, p_cellRenderer);
        o_table.setDefaultRenderer(Object[].class, p_cellRenderer);
        o_table.setDefaultRenderer(Date.class, p_cellRenderer);
		o_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        o_table.addMouseListener(new MouseAdapter(){
			@Override
            public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					for(TableSelectionListener x_listener: o_listeners){
						x_listener.tableRowClicked(o_table.getSelectedRow(), o_table.getSelectedColumn());
					}
				}

                if(e.getClickCount() == 2) {
					for(TableSelectionListener x_listener: o_listeners){
						x_listener.tableRowDoubleClicked(o_table.getSelectedRow(), o_table.getSelectedColumn());
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
    
    protected int getSelectedRow() {
        return o_table.getSelectedRow();
    }

	@Override
	public void timeUpdate() {
		// do nothing by default
	}

	@Override
	public void googleSynced() {
		// do nothing by default
	}
}
