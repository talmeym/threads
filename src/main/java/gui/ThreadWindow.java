package gui;

import data.Thread;

public class ThreadWindow extends ComponentWindow<Thread> {

	public ThreadWindow(Thread p_thread, int p_tabIndex) {
		super(p_thread);
		setContentPane(new ThreadPanel(p_thread, p_tabIndex));
		setSize(GUIConstants.s_threadWindowSize);
		renameWindow(p_thread);
	}
}
