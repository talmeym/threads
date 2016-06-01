package gui;

import data.Thread;

import javax.swing.event.ChangeListener;

public class ThreadWindow extends ComponentWindow<Thread> {

	public ThreadWindow(Thread p_thread, boolean p_new, int p_tabIndex, ChangeListener p_listener) {
		super(p_thread);
		setContentPane(new ThreadPanel(p_thread, p_new, p_tabIndex, p_listener, this));
		setSize(GUIConstants.s_threadWindowSize);
		renameWindow(p_thread);
	}
}
