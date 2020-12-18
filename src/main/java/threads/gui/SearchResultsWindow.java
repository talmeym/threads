package threads.gui;

import threads.data.Component;
import threads.data.*;
import threads.data.Thread;
import threads.util.GoogleAccount;
import threads.util.Settings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.Color.black;
import static java.awt.Color.gray;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static threads.data.LookupHelper.*;
import static threads.data.View.ALL;
import static threads.gui.GUIConstants.*;
import static threads.util.DateUtil.getDateStatus;
import static threads.util.GoogleUtil.googleAccount;
import static threads.util.Settings.Setting.*;

class SearchResultsWindow extends JFrame {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");

	private List<String> s_columnNames = Arrays.asList("Creation Date", "Type", "Name", "Info", "");
	private List<Component> o_searchResults;
	private JTable o_table;
	private TableDataCache<String> o_cache = new TableDataCache<>();

	SearchResultsWindow(Thread p_topLevelThread, Settings o_settings, Search p_search, String p_searchTerm, List<Component> p_searchResults) {
		super("Search Results - '" + p_searchTerm + "' - " + p_searchResults.size() + " Items");
		o_searchResults = p_searchResults;
		TableModel x_tableModel = new TableModel();
		CellRenderer x_cellRenderer = new CellRenderer();

		p_topLevelThread.addComponentChangeListener(e -> {
			o_searchResults = p_topLevelThread.search(p_search);
			x_tableModel.fireTableDataChanged();
		});

		o_table = new JTable(x_tableModel);
		o_table.setDefaultRenderer(Date.class, x_cellRenderer);
		o_table.setDefaultRenderer(ComponentType.class, x_cellRenderer);
		o_table.setDefaultRenderer(String.class, x_cellRenderer);
		o_table.setDefaultRenderer(GoogleAccount.class, x_cellRenderer);
		o_table.setRowHeight(s_tableRowHeight);
		o_table.setSelectionMode(SINGLE_SELECTION);

		o_table.addMouseListener(new MouseAdapter() {
			 @Override
			 public void mouseClicked(MouseEvent p_me) {
				 int x_selectedRow = o_table.getSelectedRow();

				 if(x_selectedRow != -1 && p_me.getButton() == MouseEvent.BUTTON1) {
					 if(p_me.getClickCount() > 1) {
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

		int x_height = o_settings.getIntSetting(WINH);
		int x_x = o_settings.getIntSetting(WINY);
		int x_y = o_settings.getIntSetting(WINY);
		int x_splitDivider = o_settings.getIntSetting(NAVDIVLOC);

		setSize(new Dimension(800, 400));
		setLocation(new Point(x_x + x_splitDivider + 5, x_y + x_height - 470));
		setVisible(true);
	}

	private void fixColumnWidth(int p_column, int p_width) {
		TableColumnModel x_model = o_table.getColumnModel();
		x_model.getColumn(p_column).setPreferredWidth(p_width);
		x_model.getColumn(p_column).setMinWidth(p_width);
		x_model.getColumn(p_column).setMaxWidth(p_width);
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
				case 4: return GoogleAccount.class;
				default: return String.class;
			}
		}

		@Override
		public String getColumnName(int p_column) {
			return s_columnNames.get(p_column);
		}

		@Override
		public Object getValueAt(int p_row, int p_col) {
			threads.data.Component x_component = o_searchResults.get(p_row);

			switch(p_col) {
				case 0: return x_component.getCreationDate();
				case 1: return x_component.getType();
				case 2: return x_component.getText();
				case 3: return o_cache.fillOrGet(p_row, p_col, () -> getInfoString(x_component));
				default: return googleAccount(x_component);
			}
		}

		String getInfoString(Component x_component) {
			if(x_component instanceof Thread) {
				int x_th = getAllActiveThreads((Thread) x_component).size();
				int x_up = getAllActiveUpdates((Thread) x_component).size();
				int x_ac = getAllActiveActions((Thread) x_component, ALL).size();
				return x_th + " ths, " + x_up + " ups, " + x_ac + " acs";
			}

			if(x_component instanceof Item) {
				Item x_item = (Item) x_component;

				if(x_item.getDueDate() != null) {
					if(x_item.getDueDate().before(new Date())) {
						return "Due " + getDateStatus(x_item.getDueDate());
					}

					return "Due in " + getDateStatus(x_item.getDueDate());
				}

				return "Updated " + getDateStatus(x_item.getCreationDate());
			}

			if(x_component instanceof Reminder) {
				Reminder x_reminder = (Reminder) x_component;
				return "Due " + s_dateFormat.format(x_reminder.getDueDate());
			}

			return ""; // never get here
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	private class CellRenderer extends BaseCellRenderer {
		@Override
		public java.awt.Component getTableCellRendererComponent(JTable o_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
			java.awt.Component x_awtComponent = super.getTableCellRendererComponent(o_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
			x_awtComponent.setForeground(o_searchResults.get(p_row).isActive() ? black : gray);
			return x_awtComponent;
		}
	}
}
