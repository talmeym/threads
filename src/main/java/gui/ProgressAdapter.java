package gui;

public class ProgressAdapter implements ProgressCallBack {
	@Override
	public void started(int max) {
		// do nothing
	}

	@Override
	public void progress(String update) {
		// do nothing
	}

	@Override
	public void success() {
		// do nothing
	}

	@Override
	public void error(String errorDesc) {
		// do nothing
	}
}
