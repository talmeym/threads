package gui;

import data.*;
import data.Thread;

import java.util.List;

class ThreadThreadCellRenderer extends DataItemsCellRenderer<Thread, Thread> {
	ThreadThreadCellRenderer(Thread p_thread) {
		super(p_thread);
	}

	@Override
	List<Thread> getDataItems(Thread p_thread) {
		return LookupHelper.getAllActiveThreads(p_thread);
	}
}
