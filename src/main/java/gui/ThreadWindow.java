package gui;

import data.Thread;

public class ThreadWindow extends ComponentWindow<Thread> {
	private final ThreadPanel o_threadPanel;

	public ThreadWindow(Thread p_thread, int p_tabIndex) {
		super(p_thread);
		o_threadPanel = new ThreadPanel(p_thread, p_tabIndex);
		setContentPane(o_threadPanel);
		setSize(GUIConstants.s_threadWindowSize);
		renameWindow(p_thread);
	}

	@Override
	public void setTabIndex(int p_tabIndex) {
		o_threadPanel.setTabIndex(p_tabIndex);
	}
}
