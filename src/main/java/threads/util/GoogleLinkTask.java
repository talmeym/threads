package threads.util;

import threads.data.HasDueDate;

import javax.swing.*;
import java.util.List;

import static threads.util.GoogleUtil.linkHasDueDatesToGoogle;

public class GoogleLinkTask extends SwingWorker<Void, Void> {
	private final GoogleAccount o_googleAccount;
	private final List<HasDueDate> o_hasDueDates;
	private final ProgressCallBack[] o_callbacks;

	public GoogleLinkTask(GoogleAccount p_googleAccount, List<HasDueDate> p_hasDueDates, ProgressCallBack... p_callbacks) {
		o_googleAccount = p_googleAccount;
		this.o_hasDueDates = p_hasDueDates;
		this.o_callbacks = p_callbacks;
	}

	@Override
	protected Void doInBackground() throws Exception {
		linkHasDueDatesToGoogle(o_googleAccount, o_hasDueDates, o_callbacks);
		return null;
	}
}
