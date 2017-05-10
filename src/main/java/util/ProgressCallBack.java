package util;

public interface ProgressCallBack {
	void started(int max);
	void progress(String update);
	void success();
	void error(String errorDesc);
}
