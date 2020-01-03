package threads.gui;

import threads.data.ComponentType;
import threads.data.HasDueDate;
import threads.data.Item;
import threads.data.Thread;
import threads.util.ImageUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static threads.gui.GUIConstants.s_tableRowHeight;
import static threads.gui.GUIConstants.s_typeColumnWidth;
import static threads.util.DateUtil.getFormattedDate;
import static threads.util.ImageUtil.getThreadsIcon;


class ConfirmAutoSortDialog extends JDialog {
    private final List<HasDueDate> o_hasDueDates;
    private final List<Thread> o_destinations;

    ConfirmAutoSortDialog(List<HasDueDate> p_hasDueDates, List<Thread> p_destinations, JFrame p_frame) {
        super(p_frame, "Confirm Auto-Sort", true);
        this.o_hasDueDates = p_hasDueDates;
        this.o_destinations = p_destinations;

        JTable x_table = new JTable(new TableModel());
        fixColumnWidth(x_table, 0, s_typeColumnWidth);

        TableCellRenderer x_cellRenderer = new BaseCellRenderer();
        x_table.setDefaultRenderer(Date.class, x_cellRenderer);
        x_table.setDefaultRenderer(String.class, x_cellRenderer);
        x_table.setDefaultRenderer(Icon.class, x_cellRenderer);

        x_table.setRowHeight(s_tableRowHeight);
        x_table.setSelectionMode(SINGLE_SELECTION);

        JButton x_moveButton = new JButton("Sort All");
        x_moveButton.addActionListener(e -> {
            for(int i = 0; i < o_hasDueDates.size(); i++) {
                Item x_item = (Item) o_hasDueDates.get(i);
                x_item.getParentThread().removeThreadItem(x_item);
                o_destinations.get(i).addThreadItem(x_item);
            }

            setVisible(false);
            showMessageDialog(p_frame.getContentPane(), o_hasDueDates.size() + " item" + (o_hasDueDates.size() > 1 ? "s" : "") + " sorted to new location" + (o_hasDueDates.size() > 1 ? "s" : "") + ".", "Google Auto-Sort", INFORMATION_MESSAGE, getThreadsIcon());
        });

        JButton x_cancelButton = new JButton("Cancel");
        x_cancelButton.addActionListener(e -> setVisible(false));

        JButton x_removeButton = new JButton("Remove");
        x_removeButton.setEnabled(false);
        x_removeButton.addActionListener(e -> {
            o_hasDueDates.remove(x_table.getSelectedRow());
            o_destinations.remove(x_table.getSelectedRow());
            x_table.repaint();
            x_moveButton.setEnabled(o_hasDueDates.size() > 0);
            x_removeButton.setEnabled(false);
        });

        x_table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent p_me) {
                x_removeButton.setEnabled(x_table.getSelectedRow() != -1);
            }
        });

        JPanel x_tablePanel = new JPanel(new BorderLayout());
        x_tablePanel.add(new JScrollPane(x_table), CENTER);
        x_tablePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_buttonPanel.add(x_removeButton);
        x_buttonPanel.add(x_cancelButton);
        x_buttonPanel.add(x_moveButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        JPanel x_contentPane = new JPanel(new BorderLayout());
        x_contentPane.add(x_tablePanel, CENTER);
        x_contentPane.add(x_buttonPanel, SOUTH);

        setContentPane(x_contentPane);
        pack();
        setLocation(((int) p_frame.getSize().getWidth() - (int) getSize().getWidth()) / 2, ((int) p_frame.getSize().getHeight() - (int) getSize().getHeight()) / 2);
        setVisible(true);
    }

    private void fixColumnWidth(JTable p_table, int p_column, int p_width) {
        TableColumnModel x_model = p_table.getColumnModel();
        x_model.getColumn(p_column).setPreferredWidth(p_width);
        x_model.getColumn(p_column).setMinWidth(p_width);
        x_model.getColumn(p_column).setMaxWidth(p_width);
    }

    private class TableModel extends DefaultTableModel {
        private List<String> o_columnNames = Arrays.asList("Type", "Name", "Due Date", "Sort to Thread");

        @Override
        public int getRowCount() {
            return o_hasDueDates.size();
        }

        @Override
        public int getColumnCount() {
            return o_columnNames == null ? 0 : o_columnNames.size();
        }

        @Override
        public Class<?> getColumnClass(int p_col) {
            switch(p_col) {
                case 0: return Icon.class;
                case 2: return Date.class;
                default: return String.class;
            }
        }

        @Override
        public String getColumnName(int p_col) {
            return o_columnNames.get(p_col);
        }

        @Override
        public Object getValueAt(int p_row, int p_col) {
            HasDueDate x_hasDueDate = o_hasDueDates.get(p_row);
            Thread x_destination = o_destinations.get(p_row);

            switch(p_col) {
                case 0: return x_hasDueDate.getType() == ComponentType.Action ? ImageUtil.getActionIcon() : ImageUtil.getUpdateIcon();
                case 1: return x_hasDueDate.getText();
                case 2: return getFormattedDate(x_hasDueDate.getDueDate());
                default: return x_destination.getText();
            }
        }

        @Override
        public boolean isCellEditable(int p_row, int p_column) {
            return false;
        }
    }
}
