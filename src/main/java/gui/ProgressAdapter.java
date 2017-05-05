package gui;

public class ProgressAdapter implements ProgressCallBack {
	@Override public void started(int max) { }
	@Override public void progress(String update) { }
	@Override public void success() { }
	@Override public void error(String errorDesc) { }
}
