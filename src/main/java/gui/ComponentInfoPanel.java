package gui;

import data.*;
import data.Component;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
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

		x_parentLabel.setEnabled(o_component.getParentComponent() != null);
		x_activeLabel.setEnabled(o_component.getParentComponent() != null && o_component.isActive());
		x_removeLabel.setEnabled(o_component.getParentComponent() != null);
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
			@Override public void insertUpdate(DocumentEvent documentEvent) { p_listener.componentInfoChanged(false); }
			@Override public void removeUpdate(DocumentEvent documentEvent) { p_listener.componentInfoChanged(false); }
			@Override public void changedUpdate(DocumentEvent documentEvent) { p_listener.componentInfoChanged(false); }
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

//		x_parentButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 5));
        
        JPanel x_activeCheckBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        x_activeCheckBoxPanel.add(x_activeLabel);
        x_activeCheckBoxPanel.add(x_removeLabel);
        x_activeCheckBoxPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        add(x_parentButtonPanel, BorderLayout.WEST);
        add(x_textField, BorderLayout.CENTER);
        add(x_activeCheckBoxPanel, BorderLayout.EAST);
    }
}
