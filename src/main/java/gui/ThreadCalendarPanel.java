package gui;

import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.List;

public class ThreadCalendarPanel extends ComponentTablePanel {
	private static final String[] s_monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

	private Thread o_thread;
	private JLabel o_currentMonthLabel = new JLabel(getMonthLabel(Calendar.getInstance().get(Calendar.MONTH)));

	public ThreadCalendarPanel(Thread p_thread) {
		super(new ThreadCalendarTableModel(p_thread), new ThreadCalendarCellRenderer());
		o_thread = p_thread;
		setMonth(recallValue(Calendar.getInstance().get(Calendar.MONTH)));

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				o_table.setRowHeight(getHeight() / 5 - 12);
			}
		});

		o_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		o_table.setShowGrid(true);
		o_table.setGridColor(Color.lightGray);

		JButton o_previousButton = new JButton("Previous");
		o_previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				changeMonth(false);
			}
		});

		JButton o_nextButton = new JButton("Next");
		o_nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				changeMonth(true);
			}
		});

		JButton o_todalButton = new JButton("Today");
		o_todalButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				int x_month = Calendar.getInstance().get(Calendar.MONTH);
				setMonth(x_month);
				rememberValue(x_month);
			}
		});

		o_currentMonthLabel.setHorizontalAlignment(JLabel.CENTER);
		o_currentMonthLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		JPanel x_buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		x_buttonPanel.add(o_previousButton);
		x_buttonPanel.add(o_todalButton);
		x_buttonPanel.add(o_nextButton);
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0	));

		add(o_currentMonthLabel, BorderLayout.NORTH);
		add(x_buttonPanel, BorderLayout.SOUTH);
	}

	private void changeMonth(boolean up) {
		ThreadCalendarTableModel x_model = (ThreadCalendarTableModel) o_table.getModel();
		int x_currentMonth = x_model.getMonth();
		int x_month = up ? x_currentMonth == 11 ? 0 : x_currentMonth + 1 : x_currentMonth == 0 ? 11 : x_currentMonth - 1;
		setMonth(x_month);
		rememberValue(x_month);
	}

	private void setMonth(int x_month) {
		ThreadCalendarTableModel x_model = (ThreadCalendarTableModel) o_table.getModel();
		((ThreadCalendarCellRenderer)o_table.getCellRenderer(0, 0)).setMonth(x_month);
		x_model.setMonth(x_month);
		o_currentMonthLabel.setText(getMonthLabel(x_month));
	}

	@Override
	protected void memoryChanged(int p_newMemory) {
		setMonth(p_newMemory);
	}

	private String getMonthLabel(int x_month) {
		return s_monthNames[x_month] + " " + Calendar.getInstance().get(Calendar.YEAR);
	}

	@Override
	void tableRowClicked(int row, int col) {
		if(getSelectedRow() != -1) {
			JPopupMenu x_menu = new JPopupMenu();
			ThreadCalendarTableModel x_model = (ThreadCalendarTableModel) o_table.getModel();
			final Date x_date = x_model.getDate(row, col);
			List<Item> x_actions = LookupHelper.getAllActions(o_thread, x_date);

			for(final Item x_action: x_actions) {
				String x_text = ThreadCalendarCellRenderer.MyListCellRenderer.buildTextForItem(x_action);
				JMenuItem x_menuItem = new JMenuItem(x_text);
				x_menuItem.setToolTipText(x_action.getParentThread().getText() + ": " + x_text);
				x_menuItem.setForeground(x_action.isActive() ? Color.black : Color.gray);
				x_menuItem.setFont(x_action.isActive() ? x_menuItem.getFont() : makeStrikeThrough(x_menuItem.getFont()));
				x_menu.add(x_menuItem);

				x_menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						WindowManager.getInstance().openComponent(x_action);
					}
				});
			}

			JMenuItem x_newMenuItem = new JMenuItem("Add Action");
			x_newMenuItem.setForeground(Color.gray);
			x_menu.add(x_newMenuItem);
			final JPanel x_this = this;

			x_newMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					List<Thread> x_threads = LookupHelper.getAllActiveThreads(o_thread);
					x_threads.add(0, o_thread);
					Thread x_thread;

					if(x_threads.size() > 1) {
						x_thread = (Thread) JOptionPane.showInputDialog(x_this, "Choose thread", "Add new Action ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
					} else {
						x_thread = o_thread;
					}

					if(x_thread != null) {
						String x_text = (String) JOptionPane.showInputDialog(x_this, "Enter Action Text", "Add new Action to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, null);

						if(x_text != null) {
							Item x_item = new Item(x_text);
							x_item.setDueDate(x_date);
							x_thread.addItem(x_item);
						}
					}
				}
			});

			x_menu.show(o_table, ((o_table.getWidth() / 7) * col) - 14, (o_table.getHeight() / 5) * row + 21);
		}
	}

	@Override
	void tableRowDoubleClicked(int row, int col) {
	}

	private Font makeStrikeThrough(Font x_font) {
		Map attributes = x_font.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		return new Font(attributes);
	}
}
