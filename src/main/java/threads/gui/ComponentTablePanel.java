package threads.gui;

import threads.data.Component;
import threads.data.*;
import threads.util.*;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static threads.gui.GUIConstants.s_tableRowHeight;

abstract class ComponentTablePanel <COMPONENT extends Component, TYPE> extends JPanel implements TimedUpdateListener, GoogleSyncListener, TableSelectionListener<TYPE> {
	private final ComponentTableModel<COMPONENT, TYPE> o_tableModel;
	final JTable o_table;
	private final List<TableSelectionListener<TYPE>> o_listeners = new ArrayList<>();

	ComponentTablePanel(ComponentTableModel<COMPONENT, TYPE> p_tableModel, BaseCellRenderer<COMPONENT, TYPE> p_cellRenderer) {
        super(new BorderLayout());
		p_cellRenderer.setTableModel(p_tableModel);

		o_tableModel = p_tableModel;
		o_listeners.add(this);
		o_table = new JTable(p_tableModel);
        o_table.setRowHeight(s_tableRowHeight);
        o_table.setDefaultRenderer(String.class, p_cellRenderer);
        o_table.setDefaultRenderer(Object[].class, p_cellRenderer);
        o_table.setDefaultRenderer(Date.class, p_cellRenderer);
        o_table.setDefaultRenderer(ComponentType.class, p_cellRenderer);
        o_table.setDefaultRenderer(GoogleAccount.class, p_cellRenderer);
		o_table.setSelectionMode(SINGLE_SELECTION);

        o_table.addMouseListener(new MouseAdapter(){
			@Override
            public void mouseClicked(MouseEvent p_me) {
				if(p_me.getButton() == MouseEvent.BUTTON1 && p_me.getClickCount() == 1) {
					o_listeners.forEach(l -> l.tableRowClicked(o_table.getSelectedRow(), o_table.getSelectedColumn(), getSelectedObject()));
				}

                if(p_me.getButton() == MouseEvent.BUTTON1 && p_me.getClickCount() == 2) {
					o_listeners.forEach(l -> tableRowDoubleClicked(o_table.getSelectedRow(), o_table.getSelectedColumn(), getSelectedObject()));
                }

                if(p_me.getButton() == MouseEvent.BUTTON3) {
					o_table.getSelectionModel().setSelectionInterval(o_table.rowAtPoint(p_me.getPoint()), o_table.rowAtPoint(p_me.getPoint()));
					showContextMenu(o_table, o_table.rowAtPoint(p_me.getPoint()), o_table.columnAtPoint(p_me.getPoint()), p_me.getPoint(), getSelectedObject());
				}
            }
        });

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(new JScrollPane(o_table), BorderLayout.CENTER);
    }

    void showContextMenu(java.awt.Component p_origin, int p_row, int p_col, Point p_point, TYPE p_selectedObject) {
    	// do nothing by default
	}

	void addTableSelectionListener(TableSelectionListener<TYPE> p_listener) {
		o_listeners.add(p_listener);
	}

    void fixColumnWidth(int p_column, int p_width) {
        TableColumnModel x_model = o_table.getColumnModel();        
        x_model.getColumn(p_column).setPreferredWidth(p_width);
        x_model.getColumn(p_column).setMinWidth(p_width);
        x_model.getColumn(p_column).setMaxWidth(p_width);
    }
    
    TYPE getSelectedObject() {
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
	public void googleSyncStarted() {
		// do nothing by default
	}

	@Override
	public void googleSynced() {
		tableRowClicked(-1, -1, null);
	}

	@Override
	public void googleSynced(List<HasDueDate> p_hasDueDates, boolean p_import) {
		tableRowClicked(-1, -1, null);
	}
}
