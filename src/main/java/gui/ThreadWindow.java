package gui;

import data.Thread;

public class ThreadWindow extends ComponentWindow<Thread> {
	public ThreadWindow(Thread p_thread) {
		super(p_thread);
		setContentPane(new ThreadPanel(p_thread));
		setSize(GUIConstants.s_threadWindowSize);
		renameWindow(p_thread);
	}
}
