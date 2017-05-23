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

	private List<String> s_columnNames = Arrays.asList("Date", "Action");
	private List<String> o_dates = new ArrayList<>();
	private List<String> o_log = new ArrayList<>();

	ActionLog(Thread p_topLevelThread) {
		super("Action Log");

		TableModel x_tableModel = new TableModel();

		p_topLevelThread.addComponentChangeListener(e -> {
			if(e.getField() != CONTENT) {
				o_dates.add(s_dateFormat.format(new Date()));
				o_log.add(getLogEntry(e));
				x_tableModel.fireTableDataChanged();
			}
		});

		GoogleSyncer.getInstance().addActivityListener(new GoogleSyncListener() {
			@Override
			public void googleSyncStarted() {
				o_dates.add(s_dateFormat.format(new Date()));
				o_log.add("Google sync started ...");
				x_tableModel.fireTableDataChanged();
			}

			@Override
			public void googleSynced() {
				o_dates.add(s_dateFormat.format(new Date()));
				o_log.add("Google sync completed");
				x_tableModel.fireTableDataChanged();
			}
		});

		JTable x_table = new JTable(x_tableModel);
		x_table.setRowHeight(s_tableRowHeight);
		x_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		fixColumnWidth(x_table, 0, GUIConstants.s_creationDateColumnWidth);
		x_table.setDefaultRenderer(String.class, new CellRenderer());

		JPanel x_contentPane = new JPanel(new BorderLayout());
		x_contentPane.add(new JScrollPane(x_table), CENTER);
		x_contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		setContentPane(x_contentPane);
	}

	private String getLogEntry(ComponentChangeEvent e) {
		return "Change: '" + e.getSource().getText() + "': " + e.getField() + ", " + e.getOldValue() + " -> " + e.getNewValue();
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

		setSize(new Dimension(800, 400));
		setLocation(new Point(x_x + x_splitDivider + 5, x_y + x_height - 470));
		setVisible(true);
	}

	private class TableModel extends DefaultTableModel {
		@Override
		public int getRowCount() {
			return o_log.size();
		}

		@Override
		public int getColumnCount() {
			return s_columnNames.size();
		}

		@Override
		public Class<?> getColumnClass(int col) {
			return String.class;
		}

		@Override
		public String getColumnName(int p_column) {
			return s_columnNames.get(p_column);
		}

		@Override
		public Object getValueAt(int p_row, int p_col) {
			return p_col == 0 ? o_dates.get(p_row) : o_log.get(p_row);
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	private class CellRenderer extends BaseCellRenderer {
	}
}
