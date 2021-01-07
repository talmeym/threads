package threads.gui;

import com.google.api.services.calendar.model.Event;
import threads.data.Component;
import threads.data.ComponentChangeEvent;
import threads.data.Configuration;
import threads.data.HasDueDate;
import threads.util.GoogleSyncListener;
import threads.util.GoogleSyncer;
import threads.util.Settings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static threads.data.ComponentChangeEvent.Field.CONTENT;
import static threads.data.ComponentChangeEvent.Field.DUE_DATE;
import static threads.gui.GUIConstants.*;
import static threads.util.DateUtil.isAllDay;
import static threads.util.FileUtil.logToFile;
import static threads.util.GoogleUtil.getDate;
import static threads.util.ImageUtil.getGoogleSmallIcon;
import static threads.util.ImageUtil.getIconForType;
import static threads.util.Settings.Setting.*;

class ActionLogWindow extends JFrame {
	private static final DateFormat s_dateTimeFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
	private static final DateFormat s_dateFormat = new SimpleDateFormat("dd/MM/yy");

	private final Settings o_settings;
	private List<Action> o_log = new ArrayList<>();

	ActionLogWindow(Configuration p_configuration) {
		super("Action Log - " + p_configuration.getXmlFile().getName());
		o_settings = p_configuration.getSettings();
		TableModel x_tableModel = new TableModel();

		p_configuration.getTopLevelThread().addComponentChangeListener(e -> {
			if(e.getField() != CONTENT) {
                Action x_action = buildAction(e);
                logActionToDisk(p_configuration, x_action);
                o_log.add(x_action);
				x_tableModel.fireTableDataChanged();
			}
		});

		GoogleSyncer.getInstance().addActivityListener(new GoogleSyncListener() {
			@Override
			public void googleSyncStarted() {
                Action x_action = new Action(new Date(), getGoogleSmallIcon(), "", "", "Google Sync Started", "", "", "");
                o_log.add(x_action);
                logActionToDisk(p_configuration, x_action);
				x_tableModel.fireTableDataChanged();
			}

			@Override
			public void googleSyncFinished() {
                Action x_action = new Action(new Date(), getGoogleSmallIcon(), "", "", "Google Sync Completed", "", "", "");
                o_log.add(x_action);
                logActionToDisk(p_configuration, x_action);
				x_tableModel.fireTableDataChanged();
			}

			@Override
			public void itemsLinked(List<HasDueDate> p_hasDueDates) {
				for(HasDueDate x_hasDueDate: p_hasDueDates) {
					Action x_action = new Action(new Date(), getIconForType(x_hasDueDate.getType()), x_hasDueDate.getText(), getString(x_hasDueDate.getDueDate()), "Linked to Google Calendar", "", "", "");
					o_log.add(x_action);
					logActionToDisk(p_configuration, x_action);
				}

				x_tableModel.fireTableDataChanged();
			}

			@Override
			public void googleSynced(List<HasDueDate> p_hasDueDates, List<Event> p_events) {
				for(Event x_event: p_events) {
					Action x_action = new Action(new Date(), getGoogleSmallIcon(), x_event.getSummary(), getString(getDate(x_event.getStart())), "Deleted from Google Calendar", "", "", "");
					o_log.add(x_action);
					logActionToDisk(p_configuration, x_action);
				}

				x_tableModel.fireTableDataChanged();
			}
		});

		JTable x_table = new JTable(x_tableModel);

		fixColumnWidth(x_table, 0, s_creationDateColumnWidth);
		fixColumnWidth(x_table, 1, s_typeColumnWidth);
		fixColumnWidth(x_table, 6, s_typeColumnWidth);

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
		Date x_dueDate = null;

		if(x_source instanceof HasDueDate) {
			x_dueDate = ((HasDueDate)x_source).getDueDate();

			if(e.isValueChange() && e.getField() == DUE_DATE) {
				x_dueDate = (Date) e.getOldValue();
			}
		}

		return new Action(new Date(), getIconForType(x_source.getType()), x_source.getText(), getString(x_dueDate), getActionString(e), getString(e.getOldValue()), e.isValueChange() ? "=>" : "", getString(e.getNewValue()));
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

	private void logActionToDisk(Configuration p_configuation, Action p_action) {
	    String x_date = new SimpleDateFormat("dd MMM yy HH:mm").format((Date)p_action.o_data[0]);
        String x_name = (String) p_action.o_data[2];
        String x_info = (String) p_action.o_data[3];
        String x_action = (String) p_action.o_data[4];
        String x_old = (String) p_action.o_data[5];
        String x_arrow = (String) p_action.o_data[6];
        String x_new = (String) p_action.o_data[7];
        File x_xmlFile = p_configuation.getXmlFile();
        File x_logFile = new File(x_xmlFile.getParentFile(), x_xmlFile.getName() + ".log");
        logToFile(x_logFile, x_date + ", " + (x_name.length() > 0 ? "'" + x_name + "' " : "") + (x_info.length() > 0 ? "'" + x_info + "' " : "") + x_action + " " + (x_old.equals("-") ? "" : x_old) + (x_arrow.length() > 0 ? " " + x_arrow + " " : "") + (x_new.equals("-") ? "" : x_new));
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

	void showLog() {
		int x_height = o_settings.getIntSetting(WINH);
		int x_x = o_settings.getIntSetting(WINY);
		int x_y = o_settings.getIntSetting(WINY);
		int x_splitDivider = o_settings.getIntSetting(NAVDIVLOC);

		setSize(new Dimension(1000, 400));
		setLocation(new Point(x_x + x_splitDivider + 5, x_y + x_height - 470));
		setVisible(true);
	}

	private class TableModel extends DefaultTableModel {
		private List<String> s_columnNames = Arrays.asList("Date", "Type", "Name", "Info", "Action", "From", "", "To");

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

	private class Action {
		private Object[] o_data;

		public Action(Date p_date, Icon p_type, String p_name, String p_info, String p_action, String p_oldValue, String p_arrow, String p_newValue) {
			o_data = new Object[]{p_date, p_type, p_name, p_info, p_action, p_oldValue, p_arrow, p_newValue};
		}
	}
}
