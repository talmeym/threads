package util;

public interface TimedSaveListener extends ActivityListener {
	void saveStarted();
	void saved();
}
