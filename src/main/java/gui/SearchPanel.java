package gui;

import data.Component;
import data.*;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

class SearchPanel extends JPanel {
	private static final String s_defaultTextString = " Search ...";

	private Thread o_topLevelThread;

	private JTextField o_searchField = new JTextField();

	SearchPanel(Thread p_topLevelThread) {
		super(new BorderLayout());
		o_topLevelThread = p_topLevelThread;
		setText(s_defaultTextString);

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

		o_searchField.addActionListener(e -> search());
		add(o_searchField, BorderLayout.CENTER);
	}

	private void search() {
		List<Component> x_results = o_topLevelThread.search(new Search.Builder().withText(o_searchField.getText()).build());

		if(x_results.size() > 0) {
			new SearchResults(x_results);
		} else {
			JOptionPane.showMessageDialog(this, "'" + o_searchField.getText() + "' Not Found", "No Results Found", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon());
		}

		setText("");
	}

	private void setText(String p_text) {
		o_searchField.setText(p_text);
		o_searchField.setForeground(s_defaultTextString.equals(p_text) ? Color.gray : Color.black);
	}
}
