package threads.gui;

import threads.data.Component;
import threads.data.ComponentChangeEvent;
import threads.data.HasDueDate;
import threads.data.Thread;
import threads.util.GoogleSyncListener;
import threads.util.GoogleSyncer;
import threads.util.SettingChangeListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static threads.data.ComponentChangeEvent.Field.CONTENT;
import static threads.gui.GUIConstants.*;
import static threads.util.DateUtil.isAllDay;
import static threads.util.ImageUtil.getGoogleSmallIcon;
import static threads.util.ImageUtil.getIconForType;
import static threads.util.Settings.*;

class ActionLog extends JFrame implements SettingChangeListener {
	private static final DateFormat s_dateTimeFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd/MM/yy");

	private List<Action> o_log = new ArrayList<>();

	ActionLog(Thread p_topLevelThread) {
		super("Action Log");

		TableModel x_tableModel = new TableModel();

		p_topLevelThread.addComponentChangeListener(e -> {
			if(e.getField() != CONTENT) {
				o_log.add(buildAction(e));
				x_tableModel.fireTableDataChanged();
			}
		});

		GoogleSyncer.getInstance().addActivityListener(new GoogleSyncListener() {
			@Override
			public void googleSyncStarted() {
				o_log.add(new Action(new Date(), getGoogleSmallIcon(), "", "Google Sync started ...", "", "", ""));
				x_tableModel.fireTableDataChanged();
			}

			@Override
			public void googleSynced() {
				o_log.add(new Action(new Date(), getGoogleSmallIcon(), "", "Google Sync Completed", "", "", ""));
				x_tableModel.fireTableDataChanged();
			}

			@Override
			public void googleSynced(List<HasDueDate> p_hasDueDates) {
				if(p_hasDueDates.size() == 1) {
					HasDueDate x_hasDueDate = p_hasDueDates.get(0);
					o_log.add(new Action(new Date(), getIconForType(x_hasDueDate.getType()), x_hasDueDate.getText(), "Linked to Google Calendar", "", "", ""));
				} else {
					o_log.add(new Action(new Date(), getGoogleSmallIcon(), p_hasDueDates.size() + " items", "Linked to Google Calendar", "", "", ""));
				}

				x_tableModel.fireTableDataChanged();
			}
		});

		JTable x_table = new JTable(x_tableModel);

		fixColumnWidth(x_table, 0, s_creationDateColumnWidth);
		fixColumnWidth(x_table, 1, s_typeColumnWidth);
		fixColumnWidth(x_table, 5, s_typeColumnWidth);

		TableCellRenderer x_cellRenderer = new BaseCellRenderer();
		x_table.setDefaultRenderer(Date.class, x_cellRenderer);
		x_table.setDefaultRenderer(String.class, x_cellRenderer);
		x_table.setDefaultRenderer(Icon.class, x_cellRenderer);

		x_table.setRowHeight(s_tableRowHeight);
		x_table.setSelectionMode(SINGLE_SELECTION);

		JPanel x_contentPane = new JPanel(new BorderLayout());
		x_contentPane.add(new JScrollPane(x_table), CENTER);
		x_contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		setContentPane(x_contentPane);
	}

	private Action buildAction(ComponentChangeEvent e) {
		Component x_source = e.getSource();
		return new Action(new Date(), getIconForType(x_source.getType()), x_source.getText(), getActionString(e), getString(e.getOldValue()), e.isValueChange() ? "=>" : "", getString(e.getNewValue()));
	}

	private String getString(Object p_value) {
		p_value = p_value instanceof Date ? getDueDateText(((Date)p_value)) : p_value;
		return p_value == null ? "-" : "'" + p_value + "'";
	}

	private String getDueDateText(Date x_dueDate) {
		return isAllDay(x_dueDate) ? s_dateFormat.format(x_dueDate) : s_dateTimeFormat.format(x_dueDate);
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
		int x_splitDivider = registerForSetting(s_NAVDIVLOC, this, 250);

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
				case 1: return Icon.class;
				default: return String.class;
			}
		}

		@Override
		public String getColumnName(int p_col) {
			return s_columnNames.get(p_col);
		}

		@Override
		public Object getValueAt(int p_row, int p_col) {
			Action x_action = o_log.get(p_row);
			return x_action.o_data[p_col];
		}

		public boolean isCellEditable(int row, int column) {
			return false;
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

	private class Action {
		private Object[] o_data;

		public Action(Date p_date, Icon p_type, String p_name, String p_action, String p_oldValue, String p_arrow, String p_newValue) {
			o_data = new Object[]{p_date, p_type, p_name, p_action, p_oldValue, p_arrow, p_newValue};
		}
	}
}
