package gui;

import data.*;
import data.Thread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ThreadCalendarPanel extends TablePanel  {
	private static final String[] s_monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

	private JButton o_previousButton = new JButton("Previous");
	private JButton o_currentMonthButton = new JButton(getMonthLabel(Calendar.getInstance().get(Calendar.MONTH)));
	private JButton o_nextButton = new JButton("Next");
	private Thread o_thread;

	public ThreadCalendarPanel(Thread p_thread) {
		super(new ThreadCalendarTableModel(p_thread, Calendar.getInstance().get(Calendar.MONTH)), new ThreadCalendarCellRenderer(Calendar.getInstance().get(Calendar.MONTH)));
		o_thread = p_thread;

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				o_table.setRowHeight(getHeight() / 5 - 12);
			}
		});

		o_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		o_table.setShowGrid(true);
		o_table.setGridColor(Color.black);

		o_previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				changeMonth(false);
			}
		});

		o_nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				changeMonth(true);
			}
		});

		o_currentMonthButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				setMonth(Calendar.getInstance().get(Calendar.MONTH));
			}
		});

		o_currentMonthButton.setHorizontalAlignment(JLabel.CENTER);

		JPanel x_centrePanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(x_centrePanel, BoxLayout.X_AXIS);
		x_centrePanel.setLayout(boxLayout);
		x_centrePanel.add(Box.createHorizontalGlue());
		x_centrePanel.add(o_currentMonthButton);
		x_centrePanel.add(Box.createHorizontalGlue());

		JPanel x_buttonPanel = new JPanel(new BorderLayout());
		x_buttonPanel.add(o_previousButton, BorderLayout.WEST);
		x_buttonPanel.add(x_centrePanel, BorderLayout.CENTER);
		x_buttonPanel.add(o_nextButton, BorderLayout.EAST);

		add(x_buttonPanel, BorderLayout.NORTH);
	}

	private void changeMonth(boolean up) {
		ThreadCalendarTableModel x_model = (ThreadCalendarTableModel) o_table.getModel();
		int x_currentMonth = x_model.getMonth();
		int x_month = up ? x_currentMonth == 11 ? 0 : x_currentMonth + 1 : x_currentMonth == 0 ? 11 : x_currentMonth - 1;
		setMonth(x_month);
	}

	private void setMonth(int x_month) {
		ThreadCalendarTableModel x_model = (ThreadCalendarTableModel) o_table.getModel();
		((ThreadCalendarCellRenderer)o_table.getCellRenderer(0, 0)).setMonth(x_month);
		x_model.setMonth(x_month);
		o_currentMonthButton.setText(getMonthLabel(x_month));
	}

	private String getMonthLabel(int x_month) {
		return s_monthNames[x_month] + " " + Calendar.getInstance().get(Calendar.YEAR);
	}

	@Override
	void tableRowClicked(int row, int col) {
		if(getSelectedRow() != -1) {
			JPopupMenu x_menu = new JPopupMenu();
			ThreadCalendarTableModel x_model = (ThreadCalendarTableModel) o_table.getModel();
			Date x_date = x_model.getDate(row, col);
			List<Item> x_actions = LookupHelper.getAllActions(o_thread, x_date);

			for(final Item x_action: x_actions) {
				JMenuItem x_menuItem = new JMenuItem(ThreadCalendarCellRenderer.MyListCellRenderer.buildTextForItem(x_action));

				x_menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						WindowManager.getInstance().openComponent(x_action, false, -1);
					}
				});

				x_menu.add(x_menuItem);
			}

			x_menu.show(o_table, (o_table.getWidth() / 7) * col - 15, (o_table.getHeight() / 5) * row + 23);
		}
	}

	@Override
	void tableRowDoubleClicked(int row, int col) {
	}
}
