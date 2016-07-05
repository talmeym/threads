package gui;

import data.*;
import data.Thread;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.List;

import static util.GuiUtil.setUpButtonLabel;

public class ThreadCalendarPanel extends ComponentTablePanel<Thread, Date> implements TableSelectionListener<Date> {
	private static final String[] s_monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

	private Thread o_thread;
	private JLabel o_currentMonthLabel = new JLabel(getMonthLabel(Calendar.getInstance().get(Calendar.MONTH)));

	public ThreadCalendarPanel(Thread p_thread) {
		super(new ThreadCalendarTableModel(p_thread), new ThreadCalendarCellRenderer());
		o_thread = p_thread;
		setMonth(recallValue(Calendar.getInstance().get(Calendar.MONTH)));
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
				int x_month = Calendar.getInstance().get(Calendar.MONTH);
				setMonth(x_month);
				rememberValue(x_month);
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

		JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		x_buttonPanel.add(setUpButtonLabel(x_previousLabel));
		x_buttonPanel.add(setUpButtonLabel(x_todayLabel));
		x_buttonPanel.add(setUpButtonLabel(x_nextLabel));
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
	public void tableRowClicked(int row, int col, final Date p_date) {
		if(p_date != null) {
			JPopupMenu x_menu = new JPopupMenu();
			final List<Item> x_actions = LookupHelper.getAllActions(o_thread, p_date);
			boolean x_anyGoogle = false;

			for(final Item x_action: x_actions) {
				String x_text = ThreadCalendarCellRenderer.MyListCellRenderer.buildTextForItem(x_action);
				Icon icon = GoogleUtil.isLinked(x_action) ? ImageUtil.getGoogleVerySmallIcon() : null;
				JMenuItem x_menuItem = new JMenuItem(x_text, icon);
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

				x_anyGoogle = x_anyGoogle || icon != null;
			}

			if(x_actions.size() > 0) {
				x_menu.add(new JSeparator(JSeparator.HORIZONTAL));
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
						x_thread = (Thread) JOptionPane.showInputDialog(x_this, "Choose a Thread to add it to:", "Add new Action ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
					} else {
						x_thread = o_thread;
					}

					if(x_thread != null) {
						String x_text = (String) JOptionPane.showInputDialog(x_this, "Enter new Action text:", "Add new Action to '" + x_thread + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), null, null);

						if(x_text != null) {
							Item x_item = new Item(x_text);
							x_item.setDueDate(p_date);
							x_thread.addItem(x_item);
						}
					}
				}
			});

			if(x_actions.size() > 0) {
				JMenuItem x_linkMenuItem = new JMenuItem("Link to Google");
				x_linkMenuItem.setForeground(Color.gray);
				x_menu.add(x_linkMenuItem);

				x_linkMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						if (JOptionPane.showConfirmDialog(x_this, "Link " + x_actions.size() + " Action" + (x_actions.size() > 1 ? "s" : "") + " to Google Calendar ?", "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
							GoogleLinkTask x_task = new GoogleLinkTask(x_actions, new GoogleProgressWindow(x_this), new ProgressAdapter(){
								@Override
								public void finished() {
									JOptionPane.showMessageDialog(x_this, x_actions.size() + " Action" + (x_actions.size() > 1 ? "s were" : " was") + " linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
								}
							});
							x_task.execute();
						}
					}
				});
			}

			int x_xPosition = ((o_table.getWidth() / 7) * col) - 14;

			if(x_anyGoogle) {
				x_xPosition -= 18;
			}

			x_menu.show(o_table, x_xPosition, (o_table.getHeight() / 5) * row + 21);
		}
	}

	private Font makeStrikeThrough(Font x_font) {
		Map attributes = x_font.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		return new Font(attributes);
	}
}
