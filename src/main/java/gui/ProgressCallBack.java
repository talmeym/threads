package gui;

public interface ProgressCallBack {
	public void started(int max);
	public void progress(String update);
	public void success();
	public void error(String errorDesc);
}
