package gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

abstract class TablePanel extends JPanel
{
    private final JTable o_table;
    
    private int o_lastSelection;
    
    protected TablePanel(TableModel p_tableModel, 
                         TableCellRenderer p_cellRenderer)
    {
        super(new BorderLayout());
        o_table = new JTable(p_tableModel);
        o_table.setRowHeight(GUIConstants.s_tableRowHeight);
        o_table.setDefaultRenderer(String.class, p_cellRenderer);
        o_table.setDefaultRenderer(Date.class, p_cellRenderer);

        o_table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                if(e.getClickCount() == 2)
                {
                    tableRowDoubleClicked(o_table.getSelectedColumn(), o_table.getSelectedRow());
                }
                
            }
        });

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));        
        add(new JScrollPane(o_table), BorderLayout.CENTER);        
    }
    
    protected void fixColumnWidth(int p_column, int p_width)
    {
        TableColumnModel x_model = o_table.getColumnModel();        
        x_model.getColumn(p_column).setPreferredWidth(p_width);
        x_model.getColumn(p_column).setMinWidth(p_width);
        x_model.getColumn(p_column).setMaxWidth(p_width);
    }
    
    protected int getSelectedRow()
    {
        return o_table.getSelectedRow();
    }
    
    abstract void tableRowDoubleClicked(int col, int row);
}
