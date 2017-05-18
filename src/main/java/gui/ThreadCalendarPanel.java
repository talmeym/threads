package gui;

import data.Component;
import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.List;

import static gui.Actions.*;
import static gui.ThreadCalendarCellRenderer.MyListCellRenderer.*;
import static java.lang.Integer.parseInt;
import static util.GuiUtil.setUpButtonLabel;
import static util.Settings.*;

class ThreadCalendarPanel extends ComponentTablePanel<Thread, Date> implements SettingChangeListener {
	private static final String[] s_monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

	private final Thread o_thread;
	private final JPanel o_parentPanel;
	private final JLabel o_currentMonthLabel = new JLabel(getMonthLabel(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH)));
	private final JCheckBox o_includeActionsCheckBox = new JCheckBox("Actions");
	private final JCheckBox o_includeUpdatesCheckBox = new JCheckBox("Updates");
	private final JCheckBox o_includeRemindersCheckBox = new JCheckBox("Reminders");
	private final JCheckBox o_allCheckBox = new JCheckBox("All");


	ThreadCalendarPanel(Thread p_thread, JPanel p_parentPanel) {
		super(new ThreadCalendarTableModel(p_thread), new ThreadCalendarCellRenderer());
		o_thread = p_thread;
		o_parentPanel = p_parentPanel;

		Calendar x_calendar = Calendar.getInstance();
		setTime(registerForSetting(Settings.s_DATE, this, x_calendar.get(Calendar.MONTH) + "_" + x_calendar.get(Calendar.YEAR)));
		addTableSelectionListener(this);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				o_table.setRowHeight(getHeight() / 5 - 19);
			}
		});

		o_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		o_table.setShowGrid(true);
		o_table.setGridColor(Color.lightGray);

		JLabel x_previousLabel = new JLabel(ImageUtil.getLeftIcon());
		x_previousLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				changeMonth(false);
			}
		});

		JLabel x_todayLabel = new JLabel(ImageUtil.getCalendarIcon());
		x_todayLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Calendar x_calendar = Calendar.getInstance();
				int x_year = x_calendar.get(Calendar.YEAR);
				int x_month = x_calendar.get(Calendar.MONTH);
				setTime(x_year, x_month);
				updateSetting(Settings.s_DATE, x_month + "_" + x_year);
			}
		});

		JLabel x_nextLabel = new JLabel(ImageUtil.getRightIcon());
		x_nextLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				changeMonth(true);
			}
		});

		o_currentMonthLabel.setHorizontalAlignment(JLabel.CENTER);
		o_currentMonthLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		ThreadCalendarTableModel x_tableModel = (ThreadCalendarTableModel) o_table.getModel();
		x_tableModel.setIncludeActions(registerForSetting(s_CALENDARACT, this, true));
		x_tableModel.setIncludeUpdates(registerForSetting(s_CALENDARUP, this, false));
		x_tableModel.setIncludeReminders(registerForSetting(s_CALENDARREM, this, false));

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
			updateSetting(s_CALENDARACT, o_includeActionsCheckBox.isSelected());
		});

		o_includeUpdatesCheckBox.addActionListener(e -> {
			setAllCheckbox();
			x_tableModel.setIncludeUpdates(o_includeUpdatesCheckBox.isSelected());
			updateSetting(s_CALENDARUP, o_includeUpdatesCheckBox.isSelected());
		});

		o_includeRemindersCheckBox.addActionListener(e -> {
			setAllCheckbox();
			x_tableModel.setIncludeReminders(o_includeRemindersCheckBox.isSelected());
			updateSetting(s_CALENDARREM, o_includeRemindersCheckBox.isSelected());
		});

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(x_previousLabel));
		x_buttonPanel.add(setUpButtonLabel(x_todayLabel));
		x_buttonPanel.add(setUpButtonLabel(x_nextLabel));
		x_buttonPanel.add(o_allCheckBox);
		x_buttonPanel.add(o_includeActionsCheckBox);
		x_buttonPanel.add(o_includeUpdatesCheckBox);
		x_buttonPanel.add(o_includeRemindersCheckBox);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		add(o_currentMonthLabel, BorderLayout.NORTH);
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
		updateSetting(Settings.s_DATE, x_month + "_" + x_year);
	}

	private void setTime(String x_date) {
		setTime(parseInt(x_date.substring(x_date.indexOf("_") + 1)), parseInt(x_date.substring(0, x_date.indexOf("_"))));
	}

	private void setTime(int p_year, int p_month) {
		((ThreadCalendarTableModel) o_table.getModel()).setTime(p_year, p_month);
		((ThreadCalendarCellRenderer)o_table.getCellRenderer(0, 0)).setTime(p_year, p_month);
		o_currentMonthLabel.setText(getMonthLabel(p_year, p_month));
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
			Icon x_icon = GoogleUtil.isLinked(x_component) ? ImageUtil.getGoogleVerySmallIcon() : ImageUtil.getGoogleVerySmallBlankIcon();
			JMenuItem x_menuItem = new JMenuItem(buildTextForItem(x_component), x_icon);
			x_menuItem.setForeground(x_component.isActive() ? Color.black : Color.gray);
			x_menuItem.setBackground(x_component.isActive() && x_component instanceof HasDueDate ? ((HasDueDate)x_component).isDue() ? ColourConstants.s_goneByColour : Color.white : Color.white);
			x_menuItem.setFont(x_component.isActive() ? x_menuItem.getFont() : makeStrikeThrough(x_menuItem.getFont()));
			x_menuItem.setToolTipText(buildToolTipTextForItem(x_component));
			x_menu.add(x_menuItem);
			x_menuItem.addActionListener(e -> WindowManager.getInstance().openComponent(x_component));
		}

		if(x_components.size() > 0) {
			x_menu.add(new JSeparator(JSeparator.HORIZONTAL));
		}

		JMenuItem x_newMenuItem = new JMenuItem("Add Action", ImageUtil.getPlusVerySmallIcon());
		x_newMenuItem.setForeground(Color.gray);
		x_newMenuItem.addActionListener(actionEvent -> {
			Item x_item = addAction(null, o_thread, p_date, o_parentPanel, false);

			if(x_item != null && p_date.before(new Date())) {
				if (JOptionPane.showConfirmDialog(o_parentPanel, "Your action is in the past. Set it Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
					x_item.setActive(false);
				}
			}
		});

		x_menu.add(x_newMenuItem);

		if(x_components.size() > 0) {
			JMenuItem x_linkMenuItem = new JMenuItem("Link to Google", ImageUtil.getLinkVerySmallIcon());
			x_linkMenuItem.setForeground(Color.gray);
			x_menu.add(x_linkMenuItem);
			x_linkMenuItem.addActionListener(actionEvent -> linkToGoogle(LookupHelper.getHasDueDates(x_components), o_parentPanel));

			JMenuItem x_makeInactiveItem = new JMenuItem("Set Inactive", ImageUtil.getTickVerySmallIcon());
			x_makeInactiveItem.setForeground(Color.gray);
			x_menu.add(x_makeInactiveItem);

			x_makeInactiveItem.addActionListener(actionEvent -> {
				if (JOptionPane.showConfirmDialog(o_parentPanel, "Set " + x_components.size() + " Item" + (x_components.size() > 1 ? "s" : "") + " Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
					x_components.forEach(c -> c.setActive(false));
				}
			});
		}

		return x_menu;
	}

	@SuppressWarnings("unchecked")
	private Font makeStrikeThrough(Font x_font) {
		Map attributes = x_font.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		return new Font(attributes);
	}

	@Override
	public void settingChanged(String p_name, Object p_value) {
		if(p_name.equals(Settings.s_DATE)) {
			setTime((String) p_value);
			return;
		}

		boolean x_value = (boolean) p_value;

		if(p_name.equals(s_CALENDARACT)) {
			((ThreadCalendarTableModel)o_table.getModel()).setIncludeActions(x_value);
			o_includeActionsCheckBox.setSelected(x_value);
		}

		if(p_name.equals(s_CALENDARUP)) {
			((ThreadCalendarTableModel)o_table.getModel()).setIncludeUpdates(x_value);
			o_includeUpdatesCheckBox.setSelected(x_value);
		}

		if(p_name.equals(s_CALENDARREM)) {
			((ThreadCalendarTableModel)o_table.getModel()).setIncludeReminders(x_value);
			o_includeRemindersCheckBox.setSelected(x_value);
		}

		setAllCheckbox();
	}
}
