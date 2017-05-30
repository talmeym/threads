package gui;

import data.*;
import data.ActionTemplate.ReminderTemplate;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static java.awt.BorderLayout.*;
import static java.awt.Color.*;

class ActionTemplateBuilderDialog extends JDialog {

	private Item o_item;

	private JTextField x_tokenCaptureField = getImmutableTextField("");
	private JTextField x_tokenField = new JTextField();
	private JLabel x_promptNumberLabel = new JLabel("2.");
	private JLabel x_promptInstLabel = new JLabel("Enter a question (whose answer will replace the token)");
	private JLabel x_answerNumberLabel = new JLabel("3.");
	private JLabel x_answerInstLabel = new JLabel("Enter an example answer");
	private JLabel x_promptLabel = new JLabel("Prompt:");
	private JTextField x_promptField = new JTextField("Enter something:");
	private JLabel x_answerLabel = new JLabel("Answer:");
	private JTextField x_answerField = new JTextField("Something");
	private JTextField x_itemField = new JTextField();
	private JTextField x_itemResultField = getImmutableTextField("");
	private List<JTextField> x_reminderFields = new ArrayList<>();
	private List<JTextField> x_reminderResultFields = new ArrayList<>();
	private JTextField x_nameField = new JTextField();
	private JButton x_saveButton = new JButton("Save");

	private String o_lastSelection = null;

	ActionTemplateBuilderDialog(Item p_item, JFrame p_frame) {
		super(p_frame, "Create an Action Template", true);
		o_item = new Item(p_item, false);

		x_tokenCaptureField.setText(o_item.getText());
		x_itemField.setText(o_item.getText());
		x_itemResultField.setText(o_item.getText());

		x_promptNumberLabel.setForeground(gray);
		x_promptInstLabel.setForeground(gray);
		x_promptLabel.setForeground(gray);
		x_promptField.setEnabled(false);
		x_answerNumberLabel.setForeground(gray);
		x_answerInstLabel.setForeground(gray);
		x_answerLabel.setForeground(gray);
		x_answerField.setEnabled(false);

		x_saveButton.setEnabled(false);

		JPanel x_labelPanel1 = new JPanel(new GridLayout(0, 1, 5, 5));
		x_labelPanel1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		JPanel x_fieldPanel1 = new JPanel(new GridLayout(0, 1, 5, 5));

		JPanel x_labelPanel2 = new JPanel(new GridLayout(0, 1, 5, 5));
		x_labelPanel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		JPanel x_fieldPanel2 = new JPanel(new GridLayout(0, 1, 5, 5));

		DocumentListener x_resultsListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				evaluateResults();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				evaluateResults();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				evaluateResults();
			}
		};

		x_answerField.getDocument().addDocumentListener(x_resultsListener);

		x_tokenCaptureField.addCaretListener(e -> {
			boolean x_selectionMade = e.getMark() != e.getDot();

			if(x_selectionMade) {
				x_tokenField.setText(x_tokenCaptureField.getSelectedText());
				evaluateFields();
				evaluateResults();
			}
		});

		x_tokenField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e1) {
				evaluateFields();
				evaluateResults();
			}

			@Override
			public void removeUpdate(DocumentEvent e1) {
				evaluateFields();
				evaluateResults();
			}

			@Override
			public void changedUpdate(DocumentEvent e1) {
				evaluateFields();
				evaluateResults();
			}
		});

		x_tokenField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				setPromptAndAnswerEnabled();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				setPromptAndAnswerEnabled();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				setPromptAndAnswerEnabled();
			}

			void setPromptAndAnswerEnabled() {
				x_promptNumberLabel.setForeground(x_tokenField.getText().length() > 0 ? black : gray);
				x_promptInstLabel.setForeground(x_tokenField.getText().length() > 0 ? black : gray);
				x_promptLabel.setForeground(x_tokenField.getText().length() > 0 ? black : gray);
				x_promptField.setEnabled(x_tokenField.getText().length() > 0);
				x_answerNumberLabel.setForeground(x_tokenField.getText().length() > 0 ? black : gray);
				x_answerInstLabel.setForeground(x_tokenField.getText().length() > 0 ? black : gray);
				x_answerLabel.setForeground(x_tokenField.getText().length() > 0 ? black : gray);
				x_answerField.setEnabled(x_tokenField.getText().length() > 0);
			}
		});

		x_nameField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				x_saveButton.setEnabled(x_nameField.getText().length() > 0);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				x_saveButton.setEnabled(x_nameField.getText().length() > 0);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				x_saveButton.setEnabled(x_nameField.getText().length() > 0);
			}
		});

		x_itemField.getDocument().addDocumentListener(x_resultsListener);

		x_labelPanel1.add(new JLabel("1."));
		x_fieldPanel1.add(new JLabel("To make template dynamic, select a token from below text"));
		x_labelPanel2.add(new JLabel("..."));
		x_fieldPanel2.add(new JLabel("Or type it in yourself"));
		x_labelPanel1.add(new JLabel("Action:"));
		x_fieldPanel1.add(x_tokenCaptureField);
		x_labelPanel2.add(new JLabel("Token:"));
		x_fieldPanel2.add(x_tokenField);
		x_labelPanel1.add(x_promptNumberLabel);
		x_fieldPanel1.add(x_promptInstLabel);
		x_labelPanel2.add(x_answerNumberLabel);
		x_fieldPanel2.add(x_answerInstLabel);
		x_labelPanel1.add(x_promptLabel);
		x_fieldPanel1.add(x_promptField);
		x_labelPanel2.add(x_answerLabel);
		x_fieldPanel2.add(x_answerField);
		x_labelPanel1.add(new JLabel("4."));
		x_fieldPanel1.add(new JLabel("Check (or edit) the text templates below"));
		x_labelPanel2.add(new JLabel("..."));
		x_fieldPanel2.add(new JLabel("to ensure their outputs look right"));
		x_labelPanel1.add(new JLabel("Item:"));
		x_fieldPanel1.add(x_itemField);
		x_labelPanel2.add(new JLabel("==>"));
		x_fieldPanel2.add(x_itemResultField);

		List<Reminder> x_reminders = o_item.getReminders();

		for(int i = 0; i < x_reminders.size(); i++) {
			Reminder x_reminder = x_reminders.get(i);
			x_labelPanel1.add(new JLabel("Reminder " + (i  + 1) + ":"));
			JTextField x_reminderField = new JTextField(x_reminder.getText());
			x_reminderField.getDocument().addDocumentListener(x_resultsListener);
			x_reminderFields.add(x_reminderField);
			x_fieldPanel1.add(x_reminderField);
			x_labelPanel2.add(new JLabel("==>"));
			JTextField x_reminderResultField = getImmutableTextField(x_reminder.getText());
			x_reminderResultFields.add(x_reminderResultField);
			x_fieldPanel2.add(x_reminderResultField);
		}

		x_labelPanel1.add(new JLabel("5."));
		x_fieldPanel1.add(new JLabel("Give the template a name"));
		x_labelPanel2.add(new JLabel("6."));
		x_fieldPanel2.add(new JLabel("Finally, press 'Save'"));

		x_labelPanel1.add(new JLabel("Name:"));
		x_fieldPanel1.add(x_nameField);
		x_labelPanel2.add(new JLabel("Save:"));
		x_fieldPanel2.add(x_saveButton);

		x_saveButton.addActionListener(e -> {
			List<ReminderTemplate> x_reminderTemplates = new ArrayList<>();

			for(int i = 0; i < x_reminderFields.size(); i++) {
				x_reminderTemplates.add(new ReminderTemplate(x_reminderFields.get(i).getText(), x_reminders.get(i).getDueDate().getTime() - o_item.getDueDate().getTime()));
			}

			Actions.addActionTemplate(new ActionTemplate(x_nameField.getText(), x_promptField.getText(), x_answerField.getText(), x_itemField.getText(), x_reminderTemplates));
			setVisible(false);
		});

		JPanel x_leftPanel = new JPanel(new BorderLayout());
		x_leftPanel.add(x_labelPanel1, WEST);
		x_leftPanel.add(x_fieldPanel1, CENTER);

		JPanel x_rightPanel = new JPanel(new BorderLayout());
		x_rightPanel.add(x_labelPanel2, WEST);
		x_rightPanel.add(x_fieldPanel2, CENTER);

		JPanel x_fieldsPanel = new JPanel(new GridLayout(0, 2, 15, 5));
		x_fieldsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		x_fieldsPanel.add(x_leftPanel);
		x_fieldsPanel.add(x_rightPanel);

		x_itemField.setSelectionStart(-1);
		x_itemField.setSelectionEnd(-1);

		setContentPane(x_fieldsPanel);
		pack();
		setSize(new Dimension((int) getSize().getWidth() + 50, (int) getSize().getHeight()));
		setLocation(((int) p_frame.getSize().getWidth() - (int) getSize().getWidth()) / 2, ((int) p_frame.getSize().getHeight() - (int) getSize().getHeight()) / 2);
		setVisible(true);
	}

	private void evaluateFields() {
		if(o_lastSelection != null) {
			x_itemField.setText(x_itemField.getText().replace("[TOKEN]", o_lastSelection));

			for(int i = 0; i < o_item.getReminders().size(); i++) {
				x_reminderFields.get(i).setText(x_reminderFields.get(i).getText().replace("[TOKEN]", o_lastSelection));
			}
		}

		if(x_tokenField.getText().length() > 0) {
			x_itemField.setText(x_itemField.getText().replace(x_tokenField.getText(), "[TOKEN]"));

			for (int i = 0; i < o_item.getReminders().size(); i++) {
				x_reminderFields.get(i).setText(x_reminderFields.get(i).getText().replace(x_tokenField.getText(), "[TOKEN]"));
			}

			o_lastSelection = x_tokenField.getText();
		}
	}

	private void evaluateResults() {
		x_itemResultField.setText(x_itemField.getText().replace("[TOKEN]", x_answerField.getText()));

		for(int i = 0; i < o_item.getReminders().size(); i++) {
			x_reminderResultFields.get(i).setText(x_reminderFields.get(i).getText().replace("[TOKEN]", x_answerField.getText()));
		}
	}

	private JTextField getImmutableTextField(String p_text) {
		JTextField x_textField = new JTextField(p_text);
		x_textField.setEditable(false);
		return x_textField;
	}
}
