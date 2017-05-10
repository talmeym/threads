package gui;

import data.*;
import util.GoogleUtil;

import javax.swing.*;
import java.util.*;

public class GoogleLinkTask extends SwingWorker<Void, Void> {
	private final List<HasDueDate> o_hasDueDates;
	private final ProgressCallBack[] o_callbacks;

	public GoogleLinkTask(List<HasDueDate> p_hasDueDates, ProgressCallBack... p_callbacks) {
		this.o_hasDueDates = p_hasDueDates;
		this.o_callbacks = p_callbacks;
	}

	@Override
	protected Void doInBackground() throws Exception {
		GoogleUtil.linkHasDueDatesToGoogle(o_hasDueDates, o_callbacks);
		return null;
	}
}
