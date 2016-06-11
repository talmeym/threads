package gui;

import data.Thread;

public class ThreadWindow extends ComponentWindow<Thread> {
	private final ThreadPanel o_threadPanel;

	public ThreadWindow(Thread p_thread) {
		super(p_thread);
		o_threadPanel = new ThreadPanel(p_thread);
		setContentPane(o_threadPanel);
		setSize(GUIConstants.s_threadWindowSize);
		renameWindow(p_thread);
	}
}
