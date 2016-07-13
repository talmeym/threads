import data.*;
import data.Thread;
import gui.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Threads {
    public static void main(String[] args) {
		doFontStuff();

		String x_dataFilePath = args.length > 0 ? args[0] : "threads.xml";
		File x_dataFile = new File(x_dataFilePath);
		Thread x_topThread = x_dataFile.exists() ? Loader.loadDocumentThread(x_dataFile) : new Thread("Threads");

		TimeUpdater.initialise();
		TimedSaver.initialise(x_topThread, x_dataFile);
		SystemTrayUtil.initialise(x_topThread);
		GoogleSyncer.initialise(x_topThread);

		WindowManager.initialise(x_topThread, x_dataFile, new File("threads.properties"));

	}

	public static void doFontStuff() {
		String x_fontName = ((Font) UIManager.get("Button.font")).getFontName();
		Font x_font = new Font(x_fontName, Font.PLAIN, 12);

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
