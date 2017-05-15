package gui;

import data.Component;
import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;

import static data.LookupHelper.*;
import static gui.GUIConstants.*;
import static java.awt.BorderLayout.CENTER;
import static util.Settings.*;

class SearchResults extends JFrame implements SettingChangeListener {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");

	private List<String> s_columnNames = Arrays.asList("Creation Date", "Type", "Name", "Info", "");

	private List<Component> o_searchResults;
	private JTable o_table;

	SearchResults(String p_searchTerm, List<Component> p_searchResults) {
		super("Search Results - '" + p_searchTerm + "'");
		o_searchResults = p_searchResults;

		o_table = new JTable(new TableModel());
		TableCellRenderer x_cellRenderer = new BaseCellRenderer();
		o_table.setDefaultRenderer(Date.class, x_cellRenderer);
		o_table.setDefaultRenderer(ComponentType.class, x_cellRenderer);
		o_table.setDefaultRenderer(String.class, x_cellRenderer);
		o_table.setDefaultRenderer(Boolean.class, x_cellRenderer);
		o_table.setRowHeight(s_tableRowHeight);
		o_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		o_table.addMouseListener(new MouseAdapter() {
			 @Override
			 public void mouseClicked(MouseEvent e) {
				 int x_selectedRow = o_table.getSelectedRow();

				 if(x_selectedRow != -1 && e.getButton() == MouseEvent.BUTTON1) {
					 if(e.getClickCount() > 1) {
						 setVisible(false);
					 }

					 WindowManager.getInstance().openComponent(o_searchResults.get(x_selectedRow));
				 }
			 }
		 });

		fixColumnWidth(0, s_creationDateColumnWidth);
		fixColumnWidth(1, s_typeColumnWidth);
		fixColumnWidth(3, s_threadItemInfoColumnWidth);
		fixColumnWidth(4, s_googleStatusColumnWidth);

		JPanel x_contentPane = new JPanel(new BorderLayout());
		x_contentPane.add(new JScrollPane(o_table), CENTER);
		x_contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		setContentPane(x_contentPane);

		int x_height = registerForSetting(s_WINH, this, s_windowHeight);
		int x_x = registerForSetting(s_WINY, this, s_windowX);
		int x_y = registerForSetting(s_WINY, this, s_windowY);
		int x_splitDivider = registerForSetting(Settings.s_NAVDIVLOC, this, 250);

		setSize(new Dimension(800, 400));
		setLocation(new Point(x_x + x_splitDivider + 5, x_y + x_height - 420));
		setVisible(true);
	}

	private void fixColumnWidth(int p_column, int p_width) {
		TableColumnModel x_model = o_table.getColumnModel();
		x_model.getColumn(p_column).setPreferredWidth(p_width);
		x_model.getColumn(p_column).setMinWidth(p_width);
		x_model.getColumn(p_column).setMaxWidth(p_width);
	}

	@Override
	public void settingChanged(String p_name, Object p_value) {
		// do nothing
	}

	private class TableModel extends DefaultTableModel {
		@Override
		public int getRowCount() {
			return o_searchResults.size();
		}

		@Override
		public int getColumnCount() {
			return s_columnNames.size();
		}

		@Override
		public Class<?> getColumnClass(int col) {
			switch(col) {
				case 0: return Date.class;
				case 1: return ComponentType.class;
				case 4: return Boolean.class;
				default: return String.class;
			}
		}

		@Override
		public String getColumnName(int p_column) {
			return s_columnNames.get(p_column);
		}

		@Override
		public Object getValueAt(int p_row, int p_column) {
			data.Component x_component = o_searchResults.get(p_row);

			switch(p_column) {
				case 0: return x_component.getCreationDate();
				case 1: return x_component.getType();
				case 2: return x_component.getText();
				case 3:
				if(x_component instanceof Thread) {
					int x_th = getAllActiveThreads((Thread) x_component).size();
					int x_up = getAllActiveUpdates((Thread) x_component).size();
					int x_ac = getAllActiveActions((Thread) x_component, false).size();
					return "Ths: " + x_th + " Ups: " + x_up + " Acs:" + x_ac;
				}

				if(x_component instanceof Item) {
					Item x_item = (Item) x_component;

					if(x_item.getDueDate() != null) {
						return "Due " + s_dateFormat.format(x_item.getDueDate());
					}

					return "Updated " + DateUtil.getDateStatus(x_item.getCreationDate());
				}

				if(x_component instanceof Reminder) {
					Reminder x_reminder = (Reminder) x_component;
					return "Due " + s_dateFormat.format(x_reminder.getDueDate());
				}

				return ""; // never get here
				default: return GoogleUtil.isLinked(x_component);
			}
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}