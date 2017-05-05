package gui;

import data.*;
import data.Component;
import data.Thread;
import util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import static util.GuiUtil.setUpButtonLabel;

public class ComponentInfoPanel extends JPanel {
    private final Component o_component;

	private final JLabel o_activeLabel = new JLabel(ImageUtil.getTickIcon());
	private final JTextField o_textField = new JTextField();
	private final JLabel o_setLabel = new JLabel(ImageUtil.getReturnIcon());
	private final JLabel o_revertLabel = new JLabel(ImageUtil.getCrossIcon());
	private final JPanel o_breadcrumbsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	public ComponentInfoPanel(Component p_component, final JPanel p_parentPanel, boolean p_showParents, JLabel... p_extraLabels) {
        super(new BorderLayout());
        o_component = p_component;

		final JLabel x_homeLabel = new JLabel(ImageUtil.getHomeIcon());
		final JLabel x_parentLabel = new JLabel(ImageUtil.getUpIcon());
		final JLabel x_moveLabel = new JLabel(ImageUtil.getMoveIcon());
		final JLabel x_removeLabel = new JLabel(ImageUtil.getTrashIcon());
		final JLabel x_duplicateLabel = new JLabel(ImageUtil.getDuplicateIcon());
		final JLabel x_folderLabel = new JLabel(ImageUtil.getFolderIcon());

		final DocumentListener x_listener = new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent documentEvent) { edited(); }
			@Override public void removeUpdate(DocumentEvent documentEvent) { edited(); }
			@Override public void changedUpdate(DocumentEvent documentEvent) { edited(); }

			private void edited() {
				o_textField.setBackground(ColourConstants.s_editedColour);
				o_setLabel.setEnabled(true);
				o_revertLabel.setEnabled(true);
			}
		};

		o_component.addComponentChangeListener(new ComponentChangeListener() {
			@Override
			public void componentChanged(ComponentChangeEvent p_cce) {
				if(p_cce.getSource() == o_component) {
					o_textField.getDocument().removeDocumentListener(x_listener);
					o_textField.setText(o_component.getText());
					o_textField.getDocument().addDocumentListener(x_listener);
					o_textField.setForeground(o_component.isActive() ? Color.black : Color.gray);
					o_activeLabel.setEnabled(o_component.isActive());
				}
			}
		});

		o_component.addComponentMoveListener(new ComponentMoveListener() {
			@Override
			public void componentMoved(ComponentMoveEvent p_event) {
				o_breadcrumbsPanel.removeAll();

				Component x_parent = o_component.getParentComponent();
				List<JLabel> x_parentLabels = new ArrayList<JLabel>();

				while(x_parent != null) {
					final JLabel x_label = getParentLabel(x_parent);
					x_parentLabels.addAll(0, Arrays.asList(x_label, new JLabel(">")));
					final Component x_parentFinal = x_parent;

					x_parentFinal.addComponentChangeListener(new ComponentChangeListener() {
						@Override
						public void componentChanged(ComponentChangeEvent p_cce) {
							if (p_cce.getSource() == x_parentFinal) {
								x_label.setText(x_parentFinal.getText());
							}
						}
					});

					x_parent = x_parent.getParentComponent();
				}

				for(JLabel x_label: x_parentLabels) {
					o_breadcrumbsPanel.add(x_label);
				}

				repaint();
			}
		});

		o_textField.setText(p_component.getText());
		o_textField.setForeground(p_component.isActive() ? Color.black : Color.gray);
		o_textField.setToolTipText("Press enter to set");
		o_textField.getDocument().addDocumentListener(x_listener);
		o_textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.lightGray), BorderFactory.createEmptyBorder(0, 5, 0, 5)));

		x_homeLabel.setEnabled(o_component.getParentComponent() != null);
		x_parentLabel.setEnabled(o_component.getParentComponent() != null);
		o_activeLabel.setEnabled(o_component.isActive());
		x_moveLabel.setEnabled(o_component.getParentComponent() != null);
		x_removeLabel.setEnabled(o_component.getParentComponent() != null);
		x_duplicateLabel.setEnabled(o_component.getParentComponent() != null);

		x_parentLabel.setToolTipText("View Parent");
		x_parentLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (x_parentLabel.isEnabled()) {
					WindowManager.getInstance().openComponent(o_component.getParentComponent());
				}
			}
		});

		o_setLabel.setEnabled(false);
		o_setLabel.setToolTipText("Apply Change");
		o_setLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				setText();
			}
		});

		o_revertLabel.setEnabled(false);
		o_revertLabel.setToolTipText("Revert Change");
		o_revertLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				o_textField.setText(o_component.getText());
				o_textField.setBackground(Color.white);
				o_setLabel.setEnabled(false);
				o_revertLabel.setEnabled(false);
			}
		});

        o_textField.addActionListener(actionEvent -> setText());

		o_activeLabel.setToolTipText("Make Active/Inactive");
		o_activeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (x_parentLabel.isEnabled()) {
					boolean x_active = !o_component.isActive();

					if(JOptionPane.showConfirmDialog(p_parentPanel, "Set '" + o_component.getText() + "' " + (x_active ? "Active" : "Inactive") + " ?", "Set " + (x_active ? "Active" : "Inactive") + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
						o_component.setActive(x_active);
						o_activeLabel.setEnabled(o_component.isActive());
						o_textField.setForeground(o_component.isActive() ? Color.black : Color.gray);
					}
				} else {
					JOptionPane.showMessageDialog(p_parentPanel, "The root Thread cannot be made inactive", "No can do", JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon());
				}
			}
		});

		x_moveLabel.setToolTipText("Move");
		x_moveLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ThreadItem x_component = (ThreadItem) o_component;
				Thread x_thread = null;

				Thread x_topThread = (Thread) o_component.getHierarchy().get(0);
				List<Thread> x_threads = LookupHelper.getAllActiveThreads(x_topThread);
				x_threads.add(0, x_topThread);
				x_threads.remove(x_component.getParentThread());

				if(x_threads.size() > 0) {
					x_thread = (Thread) JOptionPane.showInputDialog(p_parentPanel, "Choose a Thread to move it to:", "Move '" + o_component.getType() + "' ?", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon(), x_threads.toArray(new Object[x_threads.size()]), x_threads.get(0));
				} else {
					JOptionPane.showMessageDialog(p_parentPanel, "This is no other Thread to move this Action to. Try creating another Thread.", "Nowhere to go", JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon());
				}

				if(x_thread != null) {
					x_component.getParentThread().removeThreadItem(x_component);
					x_thread.addThreadItem(x_component);
				}
			}
		});

		x_removeLabel.setToolTipText("Remove");
		x_removeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if(x_parentLabel.isEnabled()) {
					Component x_parentComponent = o_component.getParentComponent();

					if(JOptionPane.showConfirmDialog(p_parentPanel, "Remove '" + o_component.getText() + "' from '" + x_parentComponent.getText() + "' ?", "Remove " + o_component.getType() + " ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
						if(o_component instanceof ThreadItem) {
							ThreadItem x_threadItem = (ThreadItem) o_component;
							x_threadItem.getParentThread().removeThreadItem(x_threadItem);
						} else {
							Item x_item = (Item) x_parentComponent;
							x_item.removeReminder((Reminder)o_component);
						}

						WindowManager.getInstance().openComponent(x_parentComponent);
					}
				} else {
					JOptionPane.showMessageDialog(p_parentPanel, "The root Thread cannot be deleted", "No can do", JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon());
				}
			}
		});

		x_duplicateLabel.setToolTipText("Duplicate");
		x_duplicateLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (x_parentLabel.isEnabled() && o_component.getParentComponent() != null) {
					if (JOptionPane.showConfirmDialog(p_parentPanel, "Create duplicate of '" + o_component.getText() + "' ?", "Duplicate ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
						Component x_newComponent = null;

						if (o_component instanceof Thread) {
							x_newComponent = new Thread((Thread) o_component, true);
							((Thread) o_component.getParentComponent()).addThreadItem((Thread) x_newComponent);
						}

						if (o_component instanceof Item) {
							x_newComponent = new Item((Item) o_component, true);
							((Thread) o_component.getParentComponent()).addThreadItem((Item) x_newComponent);
						}

						if (o_component instanceof Reminder) {
							x_newComponent = new Reminder((Reminder) o_component, true);
							((Item) o_component.getParentComponent()).addReminder((Reminder) x_newComponent);
						}

						WindowManager.getInstance().openComponent(x_newComponent);
					}
				} else {
					JOptionPane.showMessageDialog(p_parentPanel, "The root Thread cannot be duplicated", "No can do", JOptionPane.WARNING_MESSAGE, ImageUtil.getThreadsIcon());
				}
			}
		});

		x_folderLabel.setToolTipText("Document Folder");
		x_folderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				JPopupMenu x_popupMenu = new JPopupMenu();

				JMenuItem x_setItem = new JMenuItem("Set");
				x_setItem.addActionListener(actionEvent -> FolderManager.setDocFolder(o_component, p_parentPanel));

				JMenuItem x_openItem = new JMenuItem("Open");
				x_openItem.setEnabled(o_component.getDocFolder() != null);
				x_openItem.addActionListener(actionEvent -> FolderManager.openDocFolder(o_component));

				JMenuItem x_clearItem = new JMenuItem("Clear");
				x_clearItem.setEnabled(o_component.getDocFolder() != null);
				x_clearItem.addActionListener(actionEvent -> {
					if(o_component.getDocFolder() != null) {
						if (JOptionPane.showConfirmDialog(p_parentPanel, "Unset document folder '" + o_component.getDocFolder().getAbsolutePath() + "' ?", "Clear document folder for '" + o_component.getText() + "' ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
							o_component.setDocFolder(null);
						}
					}
				});

				x_popupMenu.add(x_setItem);
				x_popupMenu.add(x_openItem);
				x_popupMenu.add(x_clearItem);

				x_popupMenu.show(x_folderLabel, mouseEvent.getX(), mouseEvent.getY());
			}
		});

        JPanel x_parentButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		if(p_showParents) {
			x_parentButtonsPanel.add(setUpButtonLabel(x_homeLabel), BorderLayout.CENTER);
			x_parentButtonsPanel.add(setUpButtonLabel(x_parentLabel), BorderLayout.CENTER);
			x_parentButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

			Component x_parent = o_component.getParentComponent();
			List<JLabel> x_parentLabels = new ArrayList<JLabel>();

			while(x_parent != null) {
				final JLabel x_label = getParentLabel(x_parent);
				x_parentLabels.addAll(0, Arrays.asList(x_label, new JLabel(">")));
				final Component x_parentFinal = x_parent;

				x_parent.addComponentChangeListener(new ComponentChangeListener() {
					@Override
					public void componentChanged(ComponentChangeEvent p_cce) {
						if(p_cce.getSource() == x_parentFinal) {
							x_label.setText(x_parentFinal.getText());
						}
					}
				});

				if(x_parent.getParentComponent() == null) {
					final Component x_homeComponent = x_parent;

					x_homeLabel.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent mouseEvent) {
							WindowManager.getInstance().openComponent(x_homeComponent);
						}
					});
				}

				x_parent = x_parent.getParentComponent();
			}

			if(x_parentLabels.size() > 0) {
				for(JLabel x_label: x_parentLabels) {
					o_breadcrumbsPanel.add(x_label);
				}

				x_parentButtonsPanel.add(o_breadcrumbsPanel);
			}
		}

        JPanel x_buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        x_buttonPanel.add(setUpButtonLabel(o_setLabel));
        x_buttonPanel.add(setUpButtonLabel(o_revertLabel));
		x_buttonPanel.add(setUpButtonLabel(o_activeLabel));

		if(!(o_component instanceof Reminder)) {
			x_buttonPanel.add(setUpButtonLabel(x_moveLabel));
		}

        x_buttonPanel.add(setUpButtonLabel(x_removeLabel));
        x_buttonPanel.add(setUpButtonLabel(x_duplicateLabel));
        x_buttonPanel.add(setUpButtonLabel(x_folderLabel));
		x_buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

		for(JLabel x_label: p_extraLabels) {
			setUpButtonLabel(x_label);
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
			public void mouseClicked(MouseEvent mouseEvent) {
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
		o_setLabel.setEnabled(false);
		o_revertLabel.setEnabled(false);
		o_textField.setBackground(Color.white);
	}
}
