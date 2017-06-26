package threads.gui;

import threads.data.Component;
import threads.data.Reminder;
import threads.data.ThreadItem;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.awt.Color.*;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.*;
import static javax.swing.JOptionPane.*;
import static threads.data.ComponentChangeEvent.Field.TEXT;
import static threads.gui.Actions.moveThreadItem;
import static threads.gui.Actions.removeComponent;
import static threads.gui.ColourConstants.s_editedColour;
import static threads.gui.WidgetFactory.createLabel;
import static threads.gui.WidgetFactory.setUpButtonLabel;
import static threads.util.ImageUtil.*;

class ComponentInfoPanel extends JPanel {
    private final Component o_component;

	private final JTextField o_textField = new JTextField();
	private final JLabel o_applyLabel = createLabel(getReturnIcon(), "Apply Change", false, e-> setText());
	private final JLabel o_revertLabel = createLabel(getCrossIcon(), "Revert Change", false);
	private final JPanel o_breadcrumbsPanel = new JPanel(new FlowLayout(LEFT));

	ComponentInfoPanel(Component p_component, final JPanel p_parentPanel, boolean p_showParents, JLabel... p_extraLabels) {
        super(new BorderLayout());
        o_component = p_component;

		final JLabel x_homeLabel = createLabel(getHomeIcon(), "Go to Top", o_component.getParentComponent() != null);
		final JLabel x_parentLabel = createLabel(getUpIcon(), "Go to Parent", o_component.getParentComponent() != null);
		final JLabel o_activeLabel = createLabel(getTickIcon(), "Set Active / Inactive", o_component.isActive());
		final JLabel x_moveLabel = createLabel(getMoveIcon(), "Move", o_component.getParentComponent() != null);
		final JLabel x_removeLabel = createLabel(getTrashIcon(), "Remove", o_component.getParentComponent() != null);
		final JLabel x_duplicateLabel = createLabel(getDuplicateIcon(), "Duplicate", o_component.getParentComponent() != null);
		final JLabel x_folderLabel = createLabel(getFolderIcon(), "Document folder", true);

		final DocumentListener x_listener = new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent p_de) { edited(); }
			@Override public void removeUpdate(DocumentEvent p_de) { edited(); }
			@Override public void changedUpdate(DocumentEvent p_de) { edited(); }

			private void edited() {
				o_textField.setBackground(s_editedColour);
				o_applyLabel.setEnabled(true);
				o_revertLabel.setEnabled(true);
			}
		};

		o_component.addComponentChangeListener(e -> {
			if(e.getSource() == o_component) {
				if(e.isValueChange()) {
					o_textField.getDocument().removeDocumentListener(x_listener);
					o_textField.setText(o_component.getText());
					o_textField.getDocument().addDocumentListener(x_listener);
					o_textField.setForeground(o_component.isActive() ? black : gray);
					o_activeLabel.setEnabled(o_component.isActive());
				}

				if (e.isComponentAdded()) {
					o_breadcrumbsPanel.removeAll();

					Component x_parent = o_component.getParentComponent();
					List<JLabel> x_parentLabels = new ArrayList<>();

					while(x_parent != null) {
						final JLabel x_label = getParentLabel(x_parent);
						x_parentLabels.addAll(0, Arrays.asList(x_label, new JLabel(">")));
						final Component x_parentFinal = x_parent;

						x_parentFinal.addComponentChangeListener(f -> {
							if (f.getSource() == x_parentFinal && f.getField() == TEXT) {
								x_label.setText(x_parentFinal.getText());
							}
						});

						x_parent = x_parent.getParentComponent();
					}

					x_parentLabels.forEach(o_breadcrumbsPanel::add);
					o_breadcrumbsPanel.repaint();
					repaint();
				}
			}
		});

		o_textField.setText(p_component.getText());
		o_textField.setForeground(p_component.isActive() ? black : gray);
		o_textField.setToolTipText("Press enter to Apply Change");
		o_textField.getDocument().addDocumentListener(x_listener);
		o_textField.setBorder(createCompoundBorder(createLineBorder(lightGray), createEmptyBorder(0, 5, 0, 5)));

		o_revertLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				o_textField.setText(o_component.getText());
				o_textField.setBackground(white);
				o_applyLabel.setEnabled(false);
				o_revertLabel.setEnabled(false);
			}
		});

        o_textField.addActionListener(e -> setText());

		x_homeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				if(o_component.getParentComponent() != null) {
					WindowManager.getInstance().openComponent(p_component.getHierarchy().get(0));
				} else {
					showMessageDialog(p_parentPanel, "You are already at the top Thread.", "No can do", WARNING_MESSAGE, getThreadsIcon());
				}
			}
		});

		x_parentLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(o_component.getParentComponent() != null) {
					WindowManager.getInstance().openComponent(o_component.getParentComponent());
				} else {
					showMessageDialog(p_parentPanel, "You are already at the top Thread.", "No can do", WARNING_MESSAGE, getThreadsIcon());
				}
			}
		});

		o_activeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				if (o_component.getParentComponent() != null) {
					boolean x_active = !o_component.isActive();

					if(showConfirmDialog(p_parentPanel, "Set '" + o_component.getText() + "' " + (x_active ? "active" : "inactive") + " ?", "Set " + (x_active ? "active" : "inactive") + " ?", OK_CANCEL_OPTION, WARNING_MESSAGE, getThreadsIcon()) == OK_OPTION) {
						o_component.setActive(x_active);
						o_activeLabel.setEnabled(o_component.isActive());
						o_textField.setForeground(o_component.isActive() ? black : gray);
					}
				} else {
					showMessageDialog(p_parentPanel, "The top Thread cannot be made inactive.", "No can do", WARNING_MESSAGE, getThreadsIcon());
				}
			}
		});

		x_moveLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(o_component.getParentComponent() != null) {
					moveThreadItem((ThreadItem) o_component, p_parentPanel);
				} else {
					showMessageDialog(p_parentPanel, "The top Thread cannot be moved.", "No can do", WARNING_MESSAGE, getThreadsIcon());
				}
			}
		});

		x_removeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(o_component.getParentComponent() != null) {
					removeComponent(o_component, p_parentPanel, true);
				} else {
					showMessageDialog(p_parentPanel, "The top Thread cannot be removed.", "No can do", WARNING_MESSAGE, getThreadsIcon());
				}
			}
		});

		x_duplicateLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent p_me) {
				if (o_component.getParentComponent() != null) {
					if (showConfirmDialog(p_parentPanel, "Create duplicate of '" + o_component.getText() + "' ?", "Duplicate ?", OK_CANCEL_OPTION, INFORMATION_MESSAGE, getThreadsIcon()) == OK_OPTION) {
						WindowManager.getInstance().openComponent(o_component.duplicate(true));
					}
				} else {
					showMessageDialog(p_parentPanel, "The top Thread cannot be duplicated.", "No can do", WARNING_MESSAGE, getThreadsIcon());
				}
			}
		});

		x_folderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				JPopupMenu x_popupMenu = new JPopupMenu();

				JMenuItem x_setItem = new JMenuItem("Set");
				x_setItem.addActionListener(e -> FolderManager.setDocFolder(o_component, p_parentPanel));

				JMenuItem x_openItem = new JMenuItem("Open");
				x_openItem.setEnabled(o_component.getDocFolder() != null);
				x_openItem.addActionListener(e -> FolderManager.openDocFolder(o_component));

				JMenuItem x_clearItem = new JMenuItem("Clear");
				x_clearItem.setEnabled(o_component.getDocFolder() != null);
				x_clearItem.addActionListener(e -> {
					if(o_component.getDocFolder() != null) {
						if (showConfirmDialog(p_parentPanel, "Unset document folder '" + o_component.getDocFolder().getAbsolutePath() + "' ?", "Clear document folder for '" + o_component.getText() + "' ?", OK_CANCEL_OPTION, INFORMATION_MESSAGE, getThreadsIcon()) == OK_OPTION) {
							o_component.setDocFolder(null);
						}
					}
				});

				x_popupMenu.add(x_setItem);
				x_popupMenu.add(x_openItem);
				x_popupMenu.add(x_clearItem);

				x_popupMenu.show(x_folderLabel, p_me.getX(), p_me.getY());
			}
		});

        JPanel x_parentButtonsPanel = new JPanel(new FlowLayout(LEFT));

		if(p_showParents) {
			x_parentButtonsPanel.add(x_homeLabel, BorderLayout.CENTER);
			x_parentButtonsPanel.add(x_parentLabel, BorderLayout.CENTER);
			x_parentButtonsPanel.setBorder(createEmptyBorder(0, 5, 0, 0));

			Component x_parent = o_component.getParentComponent();
			List<JLabel> x_parentLabels = new ArrayList<>();

			while(x_parent != null) {
				final JLabel x_label = getParentLabel(x_parent);
				x_parentLabels.addAll(0, Arrays.asList(x_label, new JLabel(">")));
				final Component x_parentFinal = x_parent;

				x_parent.addComponentChangeListener(e -> {
					if(e.getSource() == x_parentFinal && e.getField() == TEXT) {
						x_label.setText(x_parentFinal.getText());
					}
				});

				x_parent = x_parent.getParentComponent();
			}

			if(x_parentLabels.size() > 0) {
				x_parentLabels.forEach(o_breadcrumbsPanel::add);
				x_parentButtonsPanel.add(o_breadcrumbsPanel);
			}
		}

        JPanel x_buttonPanel = new JPanel(new FlowLayout(LEFT));
        x_buttonPanel.add(o_applyLabel);
        x_buttonPanel.add(o_revertLabel);
		x_buttonPanel.add(o_activeLabel);

		if(!(o_component instanceof Reminder)) {
			x_buttonPanel.add(x_moveLabel);
		}

        x_buttonPanel.add(x_removeLabel);
        x_buttonPanel.add(x_duplicateLabel);
        x_buttonPanel.add(x_folderLabel);
		x_buttonPanel.setBorder(createEmptyBorder(0, 0, 0, 5));

		for(JLabel x_label: p_extraLabels) {
			x_buttonPanel.add(x_label);
		}

		JPanel x_fieldPanel = new JPanel();
		x_fieldPanel.setLayout(new BoxLayout(x_fieldPanel, BoxLayout.Y_AXIS));
		x_fieldPanel.add(Box.createVerticalStrut(6));
		x_fieldPanel.add(o_textField);
		x_fieldPanel.add(Box.createVerticalStrut(6));

		add(x_parentButtonsPanel, BorderLayout.WEST);
        add(x_fieldPanel, BorderLayout.CENTER);
        add(x_buttonPanel, BorderLayout.EAST);
    }

	private JLabel getParentLabel(final Component x_parent) {
		final JLabel x_label = setUpButtonLabel(new JLabel(x_parent.getText()));

		x_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent p_me) {
				WindowManager.getInstance().openComponent(x_parent);
			}
		});

		x_label.setToolTipText("Go to '" + x_parent.getText() + "'");
		return x_label;
	}

	private void setText() {
		if (o_textField.getText().length() > 0 && !o_textField.getText().equals(o_component.getText())) {
			o_component.setText(o_textField.getText());
		}

		o_textField.setText(o_component.getText());
		o_applyLabel.setEnabled(false);
		o_revertLabel.setEnabled(false);
		o_textField.setBackground(white);
	}
}
