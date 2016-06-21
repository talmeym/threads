package gui;

import data.*;
import data.Component;
import data.Thread;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ComponentInfoPanel extends JPanel {
    private final Component o_component;

	public ComponentInfoPanel(Component p_component, final JPanel p_parentPanel, final ComponentInfoChangeListener p_listener) {
        super(new BorderLayout());
        o_component = p_component;

		final JLabel x_parentLabel = new JLabel(ImageUtil.getUpIcon());
		final JTextField x_textField = new JTextField(p_component.getText());
		final JLabel x_activeLabel = new JLabel(ImageUtil.getTickIcon());
		final JLabel x_removeLabel = new JLabel(ImageUtil.getCrossIcon());
		final JLabel x_duplicateLabel = new JLabel(ImageUtil.getDuplicateIcon());
		final JLabel x_folderLabel = new JLabel(ImageUtil.getFolderIcon());

		x_parentLabel.setEnabled(o_component.getParentComponent() != null);
		x_activeLabel.setEnabled(o_component.getParentComponent() != null && o_component.isActive());
		x_removeLabel.setEnabled(o_component.getParentComponent() != null);
		x_duplicateLabel.setEnabled(o_component.getParentComponent() != null);
		x_textField.setForeground(p_component.isActive() ? Color.BLACK : Color.gray);

		x_parentLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		x_parentLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (x_parentLabel.isEnabled()) {
					WindowManager.getInstance().openComponent(o_component.getParentComponent());
				}
			}
		});

		x_textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent documentEvent) {
				p_listener.componentInfoChanged(false);
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent) {
				p_listener.componentInfoChanged(false);
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent) {
				p_listener.componentInfoChanged(false);
			}
		});

        x_textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (x_textField.getText().length() > 0 && !x_textField.getText().equals(o_component.getText())) {
					o_component.setText(x_textField.getText());
				}

				x_textField.setText(o_component.getText());
				p_listener.componentInfoChanged(true);
			}
		});

		x_activeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (x_parentLabel.isEnabled()) {
					o_component.setActive(!o_component.isActive());
					x_activeLabel.setEnabled(o_component.isActive());
					x_textField.setForeground(o_component.isActive() ? Color.BLACK : Color.gray);
				}
			}
		});

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

						WindowManager.getInstance().closeComponent(o_component);
						WindowManager.getInstance().openComponent(x_parentComponent);
					}
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
				}
			}
		});

		x_folderLabel.setToolTipText("Document Folder");
		x_folderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				JPopupMenu x_popupMenu = new JPopupMenu();

				JMenuItem x_setItem = new JMenuItem("Set");
				x_setItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						FolderManager.setDocFolder(o_component);
					}
				});

				JMenuItem x_openItem = new JMenuItem("Open");
				x_openItem.setEnabled(o_component.getDocFolder() != null);
				x_openItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						FolderManager.openDocFolder(o_component);
					}
				});

				JMenuItem x_clearItem = new JMenuItem("Clear");
				x_clearItem.setEnabled(o_component.getDocFolder() != null);
				x_clearItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						if(o_component.getDocFolder() != null) {
							if (JOptionPane.showConfirmDialog(p_parentPanel, "Unset document folder '" + o_component.getDocFolder().getAbsolutePath() + "' ?", "Clear document folder for '" + o_component.getText() + "' ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageUtil.getThreadsIcon()) == JOptionPane.OK_OPTION) {
								o_component.setDocFolder(null);
							}
						}
					}
				});

				x_popupMenu.add(x_setItem);
				x_popupMenu.add(x_openItem);
				x_popupMenu.add(x_clearItem);

				x_popupMenu.show(x_folderLabel, mouseEvent.getX(), mouseEvent.getY());
			}
		});

		o_component.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object o) {
				if(observable == ((ObservableChangeEvent)o).getObservableObserver()) {
					x_activeLabel.setEnabled(o_component.isActive() && o_component.getParentComponent() != null);
					x_textField.setForeground(o_component.isActive() ? Color.BLACK : Color.gray);
				}
			}
		});

        JPanel x_parentButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        x_parentButtonPanel.add(x_parentLabel, BorderLayout.CENTER);

		Component x_parent = o_component.getParentComponent();
		List<JLabel> x_parentLabels = new ArrayList<JLabel>();

		while(x_parent != null) {
			JLabel x_label = new JLabel(x_parent.getText());
			final Component x_comp = x_parent;

			x_label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent mouseEvent) {
					WindowManager.getInstance().openComponent(x_comp);
				}
			});

			x_label.setToolTipText("Go to '" + x_label.getText() + "'");
			x_parentLabels.addAll(0, Arrays.asList(x_label, new JLabel(" > ")));
			x_parent = x_parent.getParentComponent();
		}

		for(JLabel x_label: x_parentLabels) {
			x_parentButtonPanel.add(x_label);
		}

        JPanel x_activeCheckBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        x_activeCheckBoxPanel.add(x_activeLabel);
        x_activeCheckBoxPanel.add(x_removeLabel);
        x_activeCheckBoxPanel.add(x_duplicateLabel);
        x_activeCheckBoxPanel.add(x_folderLabel);
        x_activeCheckBoxPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));

        add(x_parentButtonPanel, BorderLayout.WEST);
        add(x_textField, BorderLayout.CENTER);
        add(x_activeCheckBoxPanel, BorderLayout.EAST);
    }
}
