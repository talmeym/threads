import data.*;
import data.Thread;
import gui.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;

public class Threads {
    public static void main(String[] args) {
		doFontStuff();

		TimeUpdater.getInstance().start();
		String x_filePath = args.length > 0 ? args[0] : "threads.xml";
		File x_file = new File(x_filePath);
		Thread x_topThread = x_file.exists() ? Loader.loadDocumentThread(x_filePath) : new Thread("Threads");
		Properties x_settings = x_file.exists() ? Loader.loadDocumentSettings(x_filePath) : new Properties();

//		// TODO remove this soon
//		x_topThread.setText("Threads");

        TimedSaver.getInstance().setThread(x_topThread, x_filePath);
		SystemTrayUtil.initialise(x_topThread);
		WindowManager.initialise(x_topThread, x_filePath, x_settings);
	}

	public static void doFontStuff() {
		String x_fontName = System.getProperty("os.name").equals("Mac OS X") ? "Avenir" : ((Font) UIManager.get("Button.font")).getFontName();
		Font x_font = new Font(x_fontName, Font.PLAIN, 14);

		UIManager.put("Button.font", x_font);
		UIManager.put("CheckBox.font", x_font);
		UIManager.put("ComboBox.font", x_font);
		UIManager.put("Label.font", x_font);
		UIManager.put("List.font", x_font);
		UIManager.put("MenuBar.font", x_font);
		UIManager.put("MenuItem.font", x_font);
		UIManager.put("Menu.font", x_font);
		UIManager.put("PopupMenu.font", x_font);
		UIManager.put("OptionPane.font", x_font);
		UIManager.put("Panel.font", x_font);
		UIManager.put("ScrollPane.font", x_font);
		UIManager.put("Viewport.font", x_font);
		UIManager.put("TabbedPane.font", x_font);
		UIManager.put("Table.font", x_font);
		UIManager.put("TableHeader.font", x_font);
		UIManager.put("TextField.font", x_font);
		UIManager.put("TextPane.font", x_font);
		UIManager.put("TitledBorder.font", x_font);
		UIManager.put("ToolTip.font", x_font);
		UIManager.put("Tree.font", x_font);
	}
}
