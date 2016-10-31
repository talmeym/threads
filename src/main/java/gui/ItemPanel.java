package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class ItemPanel extends ComponentTablePanel<Item, Reminder> implements ComponentChangeListener {
    private final Item o_item;
	private JPanel o_parentPanel;
	private final JLabel o_linkItemLabel = new JLabel(ImageUtil.getLinkIcon());

	ItemPanel(Item p_item, final JPanel p_parentPanel) {
        super(new ItemReminderTableModel(p_item),  new ComponentCellRenderer(p_item));
        o_item = p_item;
		o_parentPanel = p_parentPanel;
		o_item.addComponentChangeListener(this);

        fixColumnWidth(1, GUIConstants.s_creationDateColumnWidth);
        fixColumnWidth(2, GUIConstants.s_dateStatusColumnWidth);
        fixColumnWidth(3, 30);

		o_linkItemLabel.setEnabled(o_item.getDueDate() != null);
		o_linkItemLabel.setToolTipText("Link to Google Calendar");
		o_linkItemLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				linkToGoogle();
			}
		});

        JPanel x_panel = new JPanel(new BorderLayout());
        x_panel.add(new ComponentInfoPanel(p_item, o_parentPanel, true, o_linkItemLabel), BorderLayout.NORTH);
        x_panel.add(new DateSuggestionPanel(o_item, o_parentPanel), BorderLayout.SOUTH);
		x_panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		add(x_panel, BorderLayout.NORTH);

		GoogleSyncer.getInstance().addGoogleSyncListener(this);
    }

	private void linkToGoogle() {
		if (o_linkItemLabel.isEnabled()) {
			if (JOptionPane.showConfirmDialog(o_parentPanel, "Link '" + o_item.getText() + "' to Google Calendar ?", "Link to Google Calendar ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
				GoogleLinkTask x_task = new GoogleLinkTask(Arrays.asList(o_item), new GoogleProgressWindow(o_parentPanel), new ProgressAdapter() {
					@Override
					public void finished() {
						JOptionPane.showMessageDialog(o_parentPanel, "'" + o_item.getText() + "' was linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
					}
				});
				x_task.execute();
			}
		}
	}

	@Override
	public void componentChanged(ComponentChangeEvent p_event) {
		if(p_event.getSource() == o_item) {
			o_linkItemLabel.setEnabled(o_item.getDueDate() != null);
		}
	}

	@Override
	public void googleSynced() {
		if(GoogleUtil.isLinked(o_item)) {
			o_linkItemLabel.setIcon(ImageUtil.getGoogleSmallIcon());
		} else {
			o_linkItemLabel.setIcon(ImageUtil.getLinkIcon());
		}
	}
}