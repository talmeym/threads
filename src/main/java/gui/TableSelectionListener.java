package gui;

public interface TableSelectionListener<TYPE extends Object> {
	public void tableRowClicked(int p_row, int p_col, TYPE o_obj);
	public void tableRowDoubleClicked(int p_row, int p_col, TYPE o_obj);
}
