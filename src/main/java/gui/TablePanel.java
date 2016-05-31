package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

abstract class TablePanel extends JPanel {
    private final JTable o_table;
    
    protected TablePanel(TableModel p_tableModel, TableCellRenderer p_cellRenderer) {
        super(new BorderLayout());
        o_table = new JTable(p_tableModel);
        o_table.setRowHeight(GUIConstants.s_tableRowHeight);
        o_table.setDefaultRenderer(String.class, p_cellRenderer);
        o_table.setDefaultRenderer(Date.class, p_cellRenderer);
		o_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        o_table.addMouseListener(new MouseAdapter(){
			@Override
            public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					tableRowClicked(o_table.getSelectedColumn(), o_table.getSelectedRow());
				}

                if(e.getClickCount() == 2) {
                    tableRowDoubleClicked(o_table.getSelectedColumn(), o_table.getSelectedRow());
                }
            }
        });

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));        
        add(new JScrollPane(o_table), BorderLayout.CENTER);        
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
    
    abstract void tableRowClicked(int col, int row);

    abstract void tableRowDoubleClicked(int col, int row);
}
