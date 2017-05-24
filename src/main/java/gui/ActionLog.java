package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.List;

import static data.ComponentChangeEvent.Field.CONTENT;
import static gui.GUIConstants.*;
import static java.awt.BorderLayout.CENTER;
import static util.Settings.*;

class ActionLog extends JFrame implements SettingChangeListener {
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");

	private List<Date> o_dates = new ArrayList<>();
	private List<Object> o_log = new ArrayList<>();

	ActionLog(Thread p_topLevelThread) {
		super("Action Log");

		TableModel x_tableModel = new TableModel();

		p_topLevelThread.addComponentChangeListener(e -> {
			if(e.getField() != CONTENT) {
				o_dates.add(new Date());
				o_log.add(e);
				x_tableModel.fireTableDataChanged();
			}
		});

		GoogleSyncer.getInstance().addActivityListener(new GoogleSyncListener() {
			@Override
			public void googleSyncStarted() {
				o_dates.add(new Date());
				o_log.add("Google sync started ...");
				x_tableModel.fireTableDataChanged();
			}

			@Override
			public void googleSynced() {
				o_dates.add(new Date());
				o_log.add("Google sync completed");
				x_tableModel.fireTableDataChanged();
			}
		});

		JTable x_table = new JTable(x_tableModel);
		x_table.setRowHeight(s_tableRowHeight);
		x_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		fixColumnWidth(x_table, 0, GUIConstants.s_creationDateColumnWidth);
		fixColumnWidth(x_table, 1, GUIConstants.s_typeColumnWidth);
		fixColumnWidth(x_table, 5, GUIConstants.s_typeColumnWidth);
		CellRenderer x_cellRenderer = new CellRenderer();
		x_table.setDefaultRenderer(Date.class, x_cellRenderer);
		x_table.setDefaultRenderer(String.class, x_cellRenderer);
		x_table.setDefaultRenderer(ComponentType.class, x_cellRenderer);

		JPanel x_contentPane = new JPanel(new BorderLayout());
		x_contentPane.add(new JScrollPane(x_table), CENTER);
		x_contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		setContentPane(x_contentPane);
	}

	private void fixColumnWidth(JTable p_table, int p_column, int p_width) {
		TableColumnModel x_model = p_table.getColumnModel();
		x_model.getColumn(p_column).setPreferredWidth(p_width);
		x_model.getColumn(p_column).setMinWidth(p_width);
		x_model.getColumn(p_column).setMaxWidth(p_width);
	}

	@Override
	public void settingChanged(String p_name, Object p_value) {
		// do nothing
	}

	void showLog() {
		int x_height = registerForSetting(s_WINH, this, s_windowHeight);
		int x_x = registerForSetting(s_WINY, this, s_windowX);
		int x_y = registerForSetting(s_WINY, this, s_windowY);
		int x_splitDivider = registerForSetting(Settings.s_NAVDIVLOC, this, 250);

		setSize(new Dimension(1000, 400));
		setLocation(new Point(x_x + x_splitDivider + 5, x_y + x_height - 470));
		setVisible(true);
	}

	private class TableModel extends DefaultTableModel {
		private List<String> s_columnNames = Arrays.asList("Date", "Type", "Name", "Action", "From", "", "To");

		@Override
		public int getRowCount() {
			return o_log.size();
		}

		@Override
		public int getColumnCount() {
			return s_columnNames.size();
		}

		@Override
		public Class<?> getColumnClass(int p_col) {
			switch(p_col) {
				case 0: return Date.class;
				case 2: return ComponentType.class;
				default: return String.class;
			}
		}

		@Override
		public String getColumnName(int p_col) {
			return s_columnNames.get(p_col);
		}

		@Override
		public Object getValueAt(int p_row, int p_col) {
			Date x_date = o_dates.get(p_row);
			Object x_object = o_log.get(p_row);

			if(x_object instanceof String) {
				return p_col == 0 ? x_date : p_col == 3 ? x_object : "";
			}

			ComponentChangeEvent x_event = (ComponentChangeEvent) x_object;

			switch(p_col) {
				case 0: return x_date;
				case 1: return x_event.getSource().getType();
				case 2: return x_event.getSource().getText();
				case 3: return getActionString(x_event);
				case 4: return x_event.getOldValue() == null ? "-" : "'" + x_event.getOldValue() + "'";
				case 5: return x_event.isValueChange() ? "=>" : "";
				default: return x_event.getNewValue() == null ? "-" : "'" + x_event.getNewValue() + "'";
			}
		}

		private String getActionString(ComponentChangeEvent p_event) {
			if(p_event.isValueChange()) {
				return "Changed '" + p_event.getField() + "'";
			}

			if(p_event.isComponentRemoved()) {
				return "Removed From";
			}

			if(p_event.isComponentAdded()) {
				return "Added To";
			}

			return null; // never get here
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	private class CellRenderer extends BaseCellRenderer {
	}
}
