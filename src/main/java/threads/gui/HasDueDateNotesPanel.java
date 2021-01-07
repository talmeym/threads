package threads.gui;

import threads.data.HasDueDate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.BorderLayout.*;
import static java.awt.Color.white;
import static javax.swing.BorderFactory.createEmptyBorder;
import static threads.gui.ColourConstants.s_editedColour;
import static threads.gui.WidgetFactory.createLabel;
import static threads.util.ImageUtil.getCrossIcon;
import static threads.util.ImageUtil.getReturnIcon;

class HasDueDateNotesPanel extends JPanel {
	HasDueDateNotesPanel(HasDueDate p_hasDueDate) {
		super(new BorderLayout());

		JLabel x_notesLabel = new JLabel("Notes");
		x_notesLabel.setBorder(createEmptyBorder(0, 0, 0, 10));

		JTextArea x_notesArea = new JTextArea(p_hasDueDate.getNotes());

		JLabel x_applyLabel = createLabel(getReturnIcon(), "Apply Change", false);
		JLabel x_revertLabel = createLabel(getCrossIcon(), "Revert Change", false);

		x_applyLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				p_hasDueDate.setNotes(x_notesArea.getText());
				x_notesArea.setBackground(white);
				x_applyLabel.setEnabled(false);
				x_revertLabel.setEnabled(false);
			}
		});

		x_revertLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				x_notesArea.setText(p_hasDueDate.getNotes());
				x_notesArea.setBackground(white);
				x_applyLabel.setEnabled(false);
				x_revertLabel.setEnabled(false);
			}
		});

		DocumentListener x_listener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent p_de) {
				edited();
			}

			@Override
			public void removeUpdate(DocumentEvent p_de) {
				edited();
			}

			@Override
			public void changedUpdate(DocumentEvent p_de) {
				edited();
			}

			private void edited() {
				x_notesArea.setBackground(s_editedColour);
				x_applyLabel.setEnabled(true);
				x_revertLabel.setEnabled(true);
			}
		};
		x_notesArea.getDocument().addDocumentListener(x_listener);

		p_hasDueDate.addComponentChangeListener(e -> {
			if(e.getSource() == p_hasDueDate) {
				x_notesArea.getDocument().removeDocumentListener(x_listener);
				x_notesArea.setText(p_hasDueDate.getNotes());
				x_notesArea.getDocument().addDocumentListener(x_listener);
				x_notesArea.setBackground(white);
			}
		});

		JPanel x_buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
		x_buttonPanel.add(x_applyLabel);
		x_buttonPanel.add(x_revertLabel);
		x_buttonPanel.setBorder(createEmptyBorder(0, 5, 0, 0));

		add(x_notesLabel, WEST);
		add(new JScrollPane(x_notesArea), CENTER);
		add(x_buttonPanel, EAST);
		setPreferredSize(new Dimension(100, 90));
		setMinimumSize(new Dimension(100, 90));
		setBorder(createEmptyBorder(5, 5, 5, 8));
	}
}
