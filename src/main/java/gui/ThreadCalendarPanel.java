package gui;

import data.*;
import data.Component;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static gui.ThreadCalendarCellRenderer.MyListCellRenderer.buildTextForItem;
import static gui.ThreadCalendarCellRenderer.MyListCellRenderer.buildToolTipTextForItem;
import static util.GuiUtil.setUpButtonLabel;
import static util.Settings.*;

public class ThreadCalendarPanel extends ComponentTablePanel<Thread, Date> implements TableSelectionListener<Date>, SettingChangeListener {
	private static final String[] s_monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

	private Thread o_thread;
	private JPanel o_parentPanel;
	private JLabel o_currentMonthLabel = new JLabel(getMonthLabel(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH)));
	private final JCheckBox o_includeActionsCheckBox = new JCheckBox("Actions");
	private final JCheckBox o_includeUpdatesCheckBox = new JCheckBox("Updates");
	private final JCheckBox o_includeRemindersCheckBox = new JCheckBox("Reminders");
	private final JCheckBox o_allCheckBox = new JCheckBox("All");


	public ThreadCalendarPanel(Thread p_thread, JPanel p_parentPanel) {
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
		x_tableModel.setIncludeActions(registerForSetting(s_CALENDARACT, this, 1) == 1);
		x_tableModel.setIncludeUpdates(registerForSetting(s_CALENDARUP, this, 0) == 1);
		x_tableModel.setIncludeReminders(registerForSetting(s_CALENDARREM, this, 0) == 1);

		o_includeActionsCheckBox.setSelected(x_tableModel.includeActions());
		o_includeUpdatesCheckBox.setSelected(x_tableModel.includeUpdates());
		o_includeRemindersCheckBox.setSelected(x_tableModel.includeReminders());
		o_allCheckBox.setSelected(o_includeActionsCheckBox.isSelected() && o_includeUpdatesCheckBox.isSelected() && o_includeRemindersCheckBox.isSelected());

		o_allCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = o_allCheckBox.isSelected();
				o_includeActionsCheckBox.setSelected(selected);
				o_includeUpdatesCheckBox.setSelected(selected);
				o_includeRemindersCheckBox.setSelected(selected);
				x_tableModel.setIncludeActions(selected);
				x_tableModel.setIncludeUpdates(selected);
				x_tableModel.setIncludeReminders(selected);
			}
		});

		o_includeActionsCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				o_allCheckBox.setSelected(o_includeActionsCheckBox.isSelected() && o_includeUpdatesCheckBox.isSelected() && o_includeRemindersCheckBox.isSelected());
				x_tableModel.setIncludeActions(o_includeActionsCheckBox.isSelected());
				updateSetting(s_CALENDARACT, o_includeActionsCheckBox.isSelected() ? 1 : 0);
			}
		});

		o_includeUpdatesCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				o_allCheckBox.setSelected(o_includeActionsCheckBox.isSelected() && o_includeUpdatesCheckBox.isSelected() && o_includeRemindersCheckBox.isSelected());
				x_tableModel.setIncludeUpdates(o_includeUpdatesCheckBox.isSelected());
				updateSetting(s_CALENDARUP, o_includeUpdatesCheckBox.isSelected() ? 1 : 0);
			}
		});

		o_includeRemindersCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				o_allCheckBox.setSelected(o_includeActionsCheckBox.isSelected() && o_includeUpdatesCheckBox.isSelected() && o_includeRemindersCheckBox.isSelected());
				x_tableModel.setIncludeReminders(o_includeRemindersCheckBox.isSelected());
				updateSetting(s_CALENDARREM, o_includeRemindersCheckBox.isSelected() ? 1 : 0);
			}
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
		int x_year = Integer.parseInt(x_date.substring(x_date.indexOf("_") + 1));
		int x_month = Integer.parseInt(x_date.substring(0, x_date.indexOf("_")));
		setTime(x_year, x_month);
	}

	private void setTime(int p_year, int p_month) {
		ThreadCalendarTableModel x_model = (ThreadCalendarTableModel) o_table.getModel();
		x_model.setTime(p_year, p_month);
		((ThreadCalendarCellRenderer)o_table.getCellRenderer(0, 0)).setTime(p_year, p_month);
		o_currentMonthLabel.setText(getMonthLabel(p_year, p_month));
	}

	private String getMonthLabel(int p_year, int x_month) {
		return s_monthNames[x_month] + " " + p_year;
	}

	@Override
	void showContextMenu(int p_row, int p_col, Point p_point, java.awt.Component p_origin, Date p_selectedObject) {

	}

	@Override
	public void tableRowClicked(int row, int col, final Date p_date) {
		if(p_date != null) {
			ThreadCalendarTableModel x_model = (ThreadCalendarTableModel) o_table.getModel();
			JPopupMenu x_menu = new JPopupMenu();
			final List<Component> x_components = LookupHelper.getAllComponents(o_thread, p_date, x_model.includeActions(), x_model.includeUpdates(), x_model.includeReminders());

			for(final Component x_component: x_components) {
				String x_text = buildTextForItem(x_component);
				Icon x_icon = GoogleUtil.isLinked(x_component) ? ImageUtil.getGoogleVerySmallIcon() : ImageUtil.getGoogleVerySmallBlankIcon();
				JMenuItem x_menuItem = new JMenuItem(x_text, x_icon);
				x_menuItem.setForeground(x_component.isActive() ? Color.black : Color.gray);
				x_menuItem.setFont(x_component.isActive() ? x_menuItem.getFont() : makeStrikeThrough(x_menuItem.getFont()));
				x_menuItem.setToolTipText(buildToolTipTextForItem(x_component));
				x_menu.add(x_menuItem);

				x_menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						WindowManager.getInstance().openComponent(x_component);
					}
				});
			}

			if(x_components.size() > 0) {
				x_menu.add(new JSeparator(JSeparator.HORIZONTAL));
			}

			JMenuItem x_newMenuItem = new JMenuItem("Add Action", ImageUtil.getPlusVerySmallIcon());
			x_newMenuItem.setForeground(Color.gray);
			x_menu.add(x_newMenuItem);

			x_newMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
					x_threads.add(0, o_thread);
					Thread x_thread;

					if(x_threads.size() > 1) {
						x_thread = (Thread) JOptionPane.showInputDialog(o_parentPanel, "Choose a Thread to add it to:", "Add new Action ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
					} else {
						x_thread = o_thread;
					}

					if(x_thread != null) {
						String x_text = (String) JOptionPane.showInputDialog(o_parentPanel, "Enter new Action text:", "Add new Action to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, null);

						if(x_text != null) {
							Item x_item = new Item(x_text);
							x_item.setDueDate(p_date);
							x_thread.addItem(x_item);
						}
					}
				}
			});

			if(x_components.size() > 0) {
				JMenuItem x_linkMenuItem = new JMenuItem("Link to Google", ImageUtil.getLinkVerySmallIcon());
				x_linkMenuItem.setForeground(Color.gray);
				x_menu.add(x_linkMenuItem);

				x_linkMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						if (JOptionPane.showConfirmDialog(o_parentPanel, "Link " + x_components.size() + " Action" + (x_components.size() > 1 ? "s" : "") + " to Google Calendar ?", "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
							GoogleLinkTask x_task = new GoogleLinkTask(getItems(x_components), new GoogleProgressWindow(o_parentPanel), new ProgressAdapter(){
								@Override
								public void success() {
									JOptionPane.showMessageDialog(o_parentPanel, x_components.size() + " Action" + (x_components.size() > 1 ? "s were" : " was") + " linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
								}

								@Override
								public void error(String errorDesc) {
									JOptionPane.showMessageDialog(o_parentPanel, errorDesc, "Error linking to Google Calendar ...", JOptionPane.ERROR_MESSAGE);
								}
							});
							x_task.execute();
						}
					}
				});

				JMenuItem x_makeInactiveItem = new JMenuItem("Set Inactive", ImageUtil.getTickVerySmallIcon());
				x_makeInactiveItem.setForeground(Color.gray);
				x_menu.add(x_makeInactiveItem);

				x_makeInactiveItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						if (JOptionPane.showConfirmDialog(o_parentPanel, "Set " + x_components.size() + " Item" + (x_components.size() > 1 ? "s" : "") + " Inactive ?", "Set Inactive ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
							for (Component x_component : x_components) {
								x_component.setActive(false);
							}
						}
					}
				});
			}

			int x_xPosition = ((o_table.getWidth() / 7) * col) - 12;

			x_menu.show(o_table, x_xPosition, (o_table.getHeight() / 5) * row + 16);
		}
	}

	private List<Item> getItems(List<Component> p_components) {
		List<Component> collect = p_components.stream().filter(component -> component instanceof Item).collect(Collectors.toList());
		return collect.stream().map(component -> (Item) component).collect(Collectors.toList());
	}

	private Font makeStrikeThrough(Font x_font) {
		Map attributes = x_font.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		return new Font(attributes);
	}

	@Override
	public void settingChanged(String p_name, Object p_value) {
		if(p_name.equals(s_CALENDARACT)) {
			((ThreadCalendarTableModel)o_table.getModel()).setIncludeActions(p_value.equals(1));
			o_includeActionsCheckBox.setSelected(p_value.equals(1));
		}

		if(p_name.equals(s_CALENDARUP)) {
			((ThreadCalendarTableModel)o_table.getModel()).setIncludeUpdates(p_value.equals(1));
			o_includeUpdatesCheckBox.setSelected(p_value.equals(1));
		}

		if(p_name.equals(s_CALENDARREM)) {
			((ThreadCalendarTableModel)o_table.getModel()).setIncludeReminders(p_value.equals(1));
			o_includeRemindersCheckBox.setSelected(p_value.equals(1));
		}

		o_allCheckBox.setSelected(o_includeActionsCheckBox.isSelected() && o_includeUpdatesCheckBox.isSelected() && o_includeRemindersCheckBox.isSelected());

		if(p_name.equals(Settings.s_DATE)) {
			setTime((String) p_value);
		}
	}
}
