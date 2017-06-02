package threads.gui;

import threads.data.Component;
import threads.data.Search;
import threads.data.Thread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.Color.black;
import static java.awt.Color.gray;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static threads.util.ImageUtil.getThreadsIcon;
import static threads.util.Settings.*;

class SearchPanel extends JPanel {
	private static final String s_defaultTextString = " Search ...";

	private Thread o_topLevelThread;

	private JCheckBox o_caseSensitiveCheckBox = new JCheckBox("Case Sensitive");
	private JCheckBox o_includeNotesCheckBox = new JCheckBox("Include Notes");
	private JTextField o_searchField = new JTextField();

	SearchPanel(Thread p_topLevelThread) {
		super(new BorderLayout());
		o_topLevelThread = p_topLevelThread;
		setText(s_defaultTextString);

		o_caseSensitiveCheckBox.setSelected(registerForSetting(s_SEARCHCASE, (k, v) -> o_caseSensitiveCheckBox.setSelected((Boolean)v), false));
		o_caseSensitiveCheckBox.addActionListener(l -> updateSetting(s_SEARCHCASE, o_caseSensitiveCheckBox.isSelected()));

		o_includeNotesCheckBox.setSelected(registerForSetting(s_SEARCHNOTES, (k, v) -> o_includeNotesCheckBox.setSelected((Boolean)v), false));
		o_includeNotesCheckBox.addActionListener(l -> updateSetting(s_SEARCHNOTES, o_includeNotesCheckBox.isSelected()));

		o_searchField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent focusEvent) {
				if(o_searchField.getText().equals(s_defaultTextString)) {
					setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent focusEvent) {
				if(o_searchField.getText().length() == 0) {
					setText(s_defaultTextString);
				}
			}
		});

		JPanel x_checkBoxPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        x_checkBoxPanel.add(o_caseSensitiveCheckBox);
        x_checkBoxPanel.add(o_includeNotesCheckBox);

		o_searchField.addActionListener(e -> search());
		add(x_checkBoxPanel, NORTH);
		add(o_searchField, CENTER);
		setBorder(createEmptyBorder(0, 0, 5, 0));
	}

	private void search() {
		String x_text = o_searchField.getText();
        Search.Builder x_builder = new Search.Builder().withText(x_text).includeNotes(o_includeNotesCheckBox.isSelected()).caseSensitive(o_caseSensitiveCheckBox.isSelected());
        List<Component> x_results = o_topLevelThread.search(x_builder.build());

		if(x_results.size() > 0) {
			new SearchResults(o_topLevelThread, x_builder.build(), x_text, x_results);
		} else {
			showMessageDialog(this, "'" + x_text + "' not found.", "No Can Do", INFORMATION_MESSAGE, getThreadsIcon());
		}

		setText("");
	}

	private void setText(String p_text) {
		o_searchField.setText(p_text);
		o_searchField.setForeground(s_defaultTextString.equals(p_text) ? gray : black);
	}
}
