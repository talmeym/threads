package threads.gui;

import threads.data.ActionTemplate;
import threads.data.Configuration;

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
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static threads.gui.GUIConstants.s_tableRowHeight;
import static threads.gui.GUIConstants.s_threadColumnWidth;


class ShowActionTemplatesDialog extends JDialog {
    private final List<ActionTemplate> o_actionTemplates;
    private int o_maxReminders;

    ShowActionTemplatesDialog(Configuration p_configuration, JFrame p_frame) {
        super(p_frame, "Action Templates", true);
        this.o_actionTemplates = p_configuration.getActionTemplates();

        for(ActionTemplate x_actionTemplate: o_actionTemplates) {
            o_maxReminders = Math.max(o_maxReminders, x_actionTemplate.getReminderTemplates().size());
        }

        JTable x_table = new JTable(new TableModel());
        fixColumnWidth(x_table, 0, s_threadColumnWidth);

        TableCellRenderer x_cellRenderer = new BaseCellRenderer();
        x_table.setDefaultRenderer(Date.class, x_cellRenderer);
        x_table.setDefaultRenderer(String.class, x_cellRenderer);
        x_table.setDefaultRenderer(Icon.class, x_cellRenderer);

        x_table.setRowHeight(s_tableRowHeight);
        x_table.setSelectionMode(SINGLE_SELECTION);

        JButton x_doneButton = new JButton("Done");
        x_doneButton.addActionListener(e -> setVisible(false));

        JButton x_removeButton = new JButton("Remove");
        x_removeButton.setEnabled(false);
        x_removeButton.addActionListener(e -> {
            o_actionTemplates.remove(x_table.getSelectedRow());
            x_table.repaint();
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
        x_buttonPanel.add(x_doneButton);
        x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        JPanel x_contentPane = new JPanel(new BorderLayout());
        x_contentPane.add(x_tablePanel, CENTER);
        x_contentPane.add(x_buttonPanel, SOUTH);

        setContentPane(x_contentPane);
        pack();
        setSize(new Dimension((int) getSize().getWidth() + (350 * o_maxReminders), 300));
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
        private List<String> o_columnNames = Arrays.asList("Name", "Token Prompt", "Token Default", "Text Template");

        @Override
        public int getRowCount() {
            return o_actionTemplates.size();
        }

        @Override
        public int getColumnCount() {
            return o_columnNames == null ? 0 : o_columnNames.size() + o_maxReminders;
        }

        @Override
        public Class<?> getColumnClass(int p_col) {
            return String.class;
        }

        @Override
        public String getColumnName(int p_col) {
            return p_col < 4 ? o_columnNames.get(p_col) : "Reminder " + (p_col - 3);
        }

        @Override
        public Object getValueAt(int p_row, int p_col) {
            ActionTemplate x_actionTemplate = o_actionTemplates.get(p_row);

            switch(p_col) {
                case 0: return x_actionTemplate.getName();
                case 1: return x_actionTemplate.getTokenPrompt();
                case 2: return x_actionTemplate.getTokenDefault();
                case 3: return x_actionTemplate.getTextTemplate();
                default: return p_col < x_actionTemplate.getReminderTemplates().size() + 4 ? x_actionTemplate.getReminderTemplates().get(p_col - 4).getTextTemplate() : "";
            }
        }

        @Override
        public boolean isCellEditable(int p_row, int p_column) {
            return false;
        }
    }
}
