package threads.gui;

import threads.data.Component;
import threads.data.Thread;
import threads.data.*;
import threads.util.DateUtil;
import threads.util.SettingChangeListener;
import threads.util.Settings;
import threads.util.Settings.Setting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.awt.Color.*;
import static java.awt.FlowLayout.LEFT;
import static java.lang.Integer.parseInt;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.stream.Collectors.toList;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.JOptionPane.*;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static threads.data.LookupHelper.getHasDueDates;
import static threads.gui.Actions.*;
import static threads.gui.ColourConstants.s_goneByColour;
import static threads.gui.ThreadCalendarCellRenderer.MyListCellRenderer.buildTextForItem;
import static threads.gui.ThreadCalendarCellRenderer.MyListCellRenderer.buildToolTipTextForItem;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.DateUtil.getFirstThing;
import static threads.util.DateUtil.isAllDay;
import static threads.util.FontUtil.makeStrikeThrough;
import static threads.util.GoogleUtil.isLinked;
import static threads.util.ImageUtil.*;
import static threads.util.Settings.Setting.*;

class ThreadCalendarPanel extends ComponentTablePanel<Thread, Date> implements SettingChangeListener {
	private static final String[] s_monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

	private final Thread o_thread;
	private final Configuration o_configuration;
	private final JPanel o_parentPanel;

	private final JCheckBox o_includeActionsCheckBox = new JCheckBox("Actions");
	private final JCheckBox o_includeUpdatesCheckBox = new JCheckBox("Updates");
	private final JCheckBox o_includeRemindersCheckBox = new JCheckBox("Reminders");
	private final JCheckBox o_allCheckBox = new JCheckBox("All");
	private final JLabel o_topLabel = new JLabel(getMonthLabel(Calendar.getInstance().get(YEAR), Calendar.getInstance().get(MONTH)));

	ThreadCalendarPanel(Configuration p_configuration, Thread p_thread, JPanel p_parentPanel) {
		super(new ThreadCalendarTableModel(p_thread), new ThreadCalendarCellRenderer());
		o_thread = p_thread;
		o_configuration = p_configuration;
		o_parentPanel = p_parentPanel;

		Settings x_settings = p_configuration.getSettings();
		Calendar x_calendar = Calendar.getInstance();
		setTime(x_settings.registerForStringSetting(DATE, this));
		addTableSelectionListener(this);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				o_table.setRowHeight(getHeight() / 5 - 19);
			}
		});

		o_table.setSelectionMode(SINGLE_SELECTION);
		o_table.setShowGrid(true);
		o_table.setGridColor(lightGray);

		JLabel x_previousLabel = createLabel(getLeftIcon(), "Previous Month", true, e -> changeMonth(false));
		JLabel x_nextLabel = createLabel(getRightIcon(), "Next Month", true, e -> changeMonth(true));
		JLabel x_todayLabel = createLabel(getCalendarIcon(), "Go to Today", true, e -> {
			int x_year = x_calendar.get(YEAR);
			int x_month = x_calendar.get(MONTH);
			setTime(x_year, x_month);
			x_settings.updateSetting(DATE, x_month + "_" + x_year);
		});

		o_topLabel.setHorizontalAlignment(JLabel.CENTER);
		o_topLabel.setBorder(createEmptyBorder(0, 5, 5, 5));

		ThreadCalendarTableModel x_tableModel = (ThreadCalendarTableModel) o_table.getModel();
		x_tableModel.setIncludeActions(x_settings.registerForBooleanSetting(CALENDARACT, this));
		x_tableModel.setIncludeUpdates(x_settings.registerForBooleanSetting(CALENDARUP, this));
		x_tableModel.setIncludeReminders(x_settings.registerForBooleanSetting(CALENDARREM, this));

		o_includeActionsCheckBox.setSelected(x_tableModel.includeActions());
		o_includeUpdatesCheckBox.setSelected(x_tableModel.includeUpdates());
		o_includeRemindersCheckBox.setSelected(x_tableModel.includeReminders());
		setAllCheckbox();

		o_allCheckBox.addActionListener(e -> {
			boolean selected = o_allCheckBox.isSelected();
			o_includeActionsCheckBox.setSelected(selected);
			o_includeUpdatesCheckBox.setSelected(selected);
			o_includeRemindersCheckBox.setSelected(selected);
			x_tableModel.setIncludeActions(selected);
			x_tableModel.setIncludeUpdates(selected);
			x_tableModel.setIncludeReminders(selected);
		});

		o_includeActionsCheckBox.addActionListener(e -> {
			setAllCheckbox();
			x_tableModel.setIncludeActions(o_includeActionsCheckBox.isSelected());
			x_settings.updateSetting(CALENDARACT, o_includeActionsCheckBox.isSelected());
		});

		o_includeUpdatesCheckBox.addActionListener(e -> {
			setAllCheckbox();
			x_tableModel.setIncludeUpdates(o_includeUpdatesCheckBox.isSelected());
			x_settings.updateSetting(CALENDARUP, o_includeUpdatesCheckBox.isSelected());
		});

		o_includeRemindersCheckBox.addActionListener(e -> {
			setAllCheckbox();
			x_tableModel.setIncludeReminders(o_includeRemindersCheckBox.isSelected());
			x_settings.updateSetting(CALENDARREM, o_includeRemindersCheckBox.isSelected());
		});

		JPanel x_buttonPanel = new JPanel(new FlowLayout(LEFT));
		x_buttonPanel.add(x_previousLabel);
		x_buttonPanel.add(x_todayLabel);
		x_buttonPanel.add(x_nextLabel);
		x_buttonPanel.add(new JSeparator(JSeparator.VERTICAL));
		x_buttonPanel.add(new JLabel("View:"));
		x_buttonPanel.add(o_includeActionsCheckBox);
		x_buttonPanel.add(o_includeUpdatesCheckBox);
		x_buttonPanel.add(o_includeRemindersCheckBox);
		x_buttonPanel.add(o_allCheckBox);
		x_buttonPanel.setBorder(createEmptyBorder(5, 0, 0, 0));

		add(o_topLabel, BorderLayout.NORTH);
		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	private void setAllCheckbox() {
		o_allCheckBox.setSelected(o_includeActionsCheckBox.isSelected() && o_includeUpdatesCheckBox.isSelected() && o_includeRemindersCheckBox.isSelected());
	}

	private void changeMonth(boolean up) {
		ThreadCalendarTableModel x_model = (ThreadCalendarTableModel) o_table.getModel();
		int x_currentYear = x_model.getYear();
		int x_currentMonth = x_model.getMonth();
		int x_year = up ? x_currentMonth == 11 ? x_currentYear + 1 : x_currentYear : x_currentMonth == 0 ? x_currentYear - 1 : x_currentYear;
		int x_month = up ? x_currentMonth == 11 ? 0 : x_currentMonth + 1 : x_currentMonth == 0 ? 11 : x_currentMonth - 1;
		setTime(x_year, x_month);
		o_configuration.getSettings().updateSetting(DATE, x_month + "_" + x_year);
	}

	private void setTime(String x_date) {
		setTime(parseInt(x_date.substring(x_date.indexOf("_") + 1)), parseInt(x_date.substring(0, x_date.indexOf("_"))));
	}

	private void setTime(int p_year, int p_month) {
		((ThreadCalendarTableModel) o_table.getModel()).setTime(p_year, p_month);
		((ThreadCalendarCellRenderer)o_table.getCellRenderer(0, 0)).setTime(p_year, p_month);
		o_topLabel.setText(getMonthLabel(p_year, p_month));
	}

	private String getMonthLabel(int p_year, int x_month) {
		return s_monthNames[x_month] + " " + p_year;
	}

	@Override
	public void tableRowClicked(int row, int col, final Date p_date) {
		if(p_date != null) {
			Object[] x_data = (Object[])o_table.getModel().getValueAt(row, col);
			List<Component> x_components = new ArrayList<>();

			for(int i = 1; i < x_data.length; i++) {
				x_components.add((Component)x_data[i]);
			}

			buildPopupMenu(x_components, p_date).show(o_table, ((o_table.getWidth() / 7) * col) - 12, (o_table.getHeight() / 5) * row + 16);
		}
	}

	private JPopupMenu buildPopupMenu(List<Component> x_components, Date p_date) {
		JPopupMenu x_menu = new JPopupMenu();

		for(final Component x_component: x_components) {
			Icon x_icon = isLinked(x_component) ? getGoogleVerySmallIcon() : getSmallIconForType(x_component.getType());
			JMenuItem x_menuItem = new JMenuItem(buildTextForItem(x_component), x_icon);
			x_menuItem.setForeground(x_component.isActive() ? black : gray);
			x_menuItem.setBackground(x_component.isActive() && x_component instanceof HasDueDate ? ((HasDueDate)x_component).isDue() ? s_goneByColour : white : white);
			x_menuItem.setFont(x_component.isActive() ? x_menuItem.getFont() : makeStrikeThrough(x_menuItem.getFont()));
			x_menuItem.setToolTipText(buildToolTipTextForItem(x_component));
			x_menu.add(x_menuItem);
			x_menuItem.addActionListener(e -> WindowManager.getInstance().openComponent(x_component));
		}

		if(x_components.size() > 0) {
			x_menu.add(new JSeparator(JSeparator.HORIZONTAL));
		}

		JMenuItem x_addMenuItem = new JMenuItem("Add Action", getPlusSmallIcon());
		x_addMenuItem.setForeground(gray);
		x_addMenuItem.addActionListener(e -> {
			Item x_item = addAction(null, o_thread, p_date, o_parentPanel, false);

			if(x_item != null) {
				Date x_dueDate = x_item.getDueDate();

				if ((isAllDay(x_dueDate) && x_dueDate.before(getFirstThing(0))) || (!isAllDay(x_dueDate) && x_dueDate.before(new Date()))) {
					if (showConfirmDialog(o_parentPanel, "Your action is in the past. Set it inactive ?", "Set inactive ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getGoogleIcon()) == OK_OPTION) {
						x_item.setActive(false);
					}
				}
			}
		});

		x_menu.add(x_addMenuItem);

		JMenuItem x_addFromTemplateMenuItem = new JMenuItem("Add from Template", getTemplateSmallIcon());
		x_addFromTemplateMenuItem.setForeground(gray);
		x_addFromTemplateMenuItem.addActionListener(e -> addActionFromTemplate(o_configuration, null, o_thread, p_date, o_parentPanel, false));
		x_menu.add(x_addFromTemplateMenuItem);

		if(x_components.size() > 0) {
			JMenuItem x_linkMenuItem = new JMenuItem("Link to Google", getLinkSmallIcon());
			x_linkMenuItem.setForeground(gray);
			x_menu.add(x_linkMenuItem);
			x_linkMenuItem.addActionListener(e -> linkToGoogle(getHasDueDates(x_components), o_configuration, o_parentPanel));

			JMenuItem x_setDayInactiveItem = new JMenuItem("Set day Inactive", getTickSmallIcon());
			x_setDayInactiveItem.setForeground(gray);
			x_menu.add(x_setDayInactiveItem);

			List<Component> x_activeDayComponents = x_components.stream().filter(Component::isActive).filter(x_c -> x_c.getType() == ComponentType.Action && DateUtil.isAllDay(((HasDueDate)x_c).getDueDate())).collect(toList());
			x_setDayInactiveItem.setEnabled(x_activeDayComponents.size() > 0);

			x_setDayInactiveItem.addActionListener(e -> {
				if(x_activeDayComponents.size() > 0) {
					String x_deltaString = x_activeDayComponents.size() != x_components.size() ? " (of " + x_components.size() + ")" : "";

					if (showConfirmDialog(o_parentPanel, "Set " + x_activeDayComponents.size() + " item" + (x_activeDayComponents.size() > 1 ? "s" : "") + x_deltaString + " inactive ?", "Set all-day actions inactive ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getGoogleIcon()) == OK_OPTION) {
						x_activeDayComponents.forEach(c -> c.setActive(false));
					}
				}
			});

			JMenuItem x_setAllInactiveItem = new JMenuItem("Set all Inactive", getTickSmallIcon());
			x_setAllInactiveItem.setForeground(gray);
			x_menu.add(x_setAllInactiveItem);

            List<Component> x_activeComponents = x_components.stream().filter(Component::isActive).collect(toList());
            x_setAllInactiveItem.setEnabled(x_activeComponents.size() > 0);

			x_setAllInactiveItem.addActionListener(e -> {
                if(x_activeComponents.size() > 0) {
                    String x_deltaString = x_activeComponents.size() != x_components.size() ? " (of " + x_components.size() + ")" : "";

                    if (showConfirmDialog(o_parentPanel, "Set " + x_activeComponents.size() + " item" + (x_activeComponents.size() > 1 ? "s" : "") + x_deltaString + " inactive ?", "Set all items inactive ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getGoogleIcon()) == OK_OPTION) {
                        x_activeComponents.forEach(c -> c.setActive(false));
                    }
                }
			});
		}

		return x_menu;
	}

	@Override
	public void settingChanged(Setting p_setting, Object p_value) {
		if(p_setting == DATE) {
			setTime((String) p_value);
			return;
		}

		boolean x_value = (boolean) p_value;

		if(p_setting == CALENDARACT) {
			((ThreadCalendarTableModel)o_table.getModel()).setIncludeActions(x_value);
			o_includeActionsCheckBox.setSelected(x_value);
		}

		if(p_setting == CALENDARUP) {
			((ThreadCalendarTableModel)o_table.getModel()).setIncludeUpdates(x_value);
			o_includeUpdatesCheckBox.setSelected(x_value);
		}

		if(p_setting == CALENDARREM) {
			((ThreadCalendarTableModel)o_table.getModel()).setIncludeReminders(x_value);
			o_includeRemindersCheckBox.setSelected(x_value);
		}

		setAllCheckbox();
	}
}
