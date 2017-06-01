package threads.gui;

import threads.data.Thread;
import threads.data.*;

import javax.swing.*;

import static java.awt.Color.*;

class ThreadContentsCellRenderer extends BaseCellRenderer<Thread, ThreadItem> {
	@Override
	public java.awt.Component getTableCellRendererComponent(JTable p_table, Object p_value, boolean p_isSelected, boolean p_hasFocus, int p_row, int p_col) {
		java.awt.Component x_awtComponent = super.getTableCellRendererComponent(p_table, p_value, p_isSelected, p_hasFocus, p_row, p_col);
		ThreadItem x_threadItem = o_tableModel.getDataItem(p_row, p_col);
		x_awtComponent.setForeground(x_threadItem.isActive() ? black : gray);
		return x_awtComponent;
	}
}
