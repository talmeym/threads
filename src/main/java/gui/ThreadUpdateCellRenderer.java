package gui;

import data.*;
import data.Thread;

import java.util.List;

class ThreadUpdateCellRenderer extends DataItemsCellRenderer<Thread, Item> {


	ThreadUpdateCellRenderer(Thread p_thread) {
		super(p_thread);
	}

	@Override
	List<Item> getDataItems(Thread p_thread) {
		return LookupHelper.getAllActiveUpdates(p_thread);
	}
}
