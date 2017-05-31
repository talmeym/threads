package threads.gui;

interface TableSelectionListener<TYPE> {
	void tableRowClicked(int p_row, int p_col, TYPE o_obj);
	void tableRowDoubleClicked(int p_row, int p_col, TYPE o_obj);
}
