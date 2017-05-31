package threads.util;

import java.awt.*;

import static javax.swing.UIManager.get;
import static javax.swing.UIManager.put;

public class FontUtil {
	public static void standardiseFontSizes() {
		String x_fontName = ((Font) get("Button.font")).getFontName();
		Font x_font = new Font(x_fontName, Font.PLAIN, 12);

		put("Button.font", x_font);
		put("CheckBox.font", x_font);
		put("RadioButton.font", x_font);
		put("ComboBox.font", x_font);
		put("Label.font", x_font);
		put("List.font", x_font);
		put("MenuBar.font", x_font);
		put("MenuItem.font", x_font);
		put("Menu.font", x_font);
		put("PopupMenu.font", x_font);
		put("OptionPane.font", x_font);
		put("Panel.font", x_font);
		put("ScrollPane.font", x_font);
		put("Viewport.font", x_font);
		put("TabbedPane.font", x_font);
		put("Table.font", x_font);
		put("TableHeader.font", x_font);
		put("TextField.font", x_font);
		put("TextArea.font", x_font);
		put("TextPane.font", x_font);
		put("TitledBorder.font", x_font);
		put("ToolTip.font", x_font);
		put("Tree.font", x_font);
	}
}