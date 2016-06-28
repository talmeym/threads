package gui;

import data.*;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class ItemPanel extends ComponentTablePanel implements Observer {
    private final Item o_item;
	private final JLabel o_linkItemLabel = new JLabel(ImageUtil.getLinkIcon());

	ItemPanel(Item p_item) {
        super(new ItemReminderTableModel(p_item),  new ComponentCellRenderer(p_item));
        o_item = p_item;
		o_item.addObserver(this);

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
        x_panel.add(new ComponentInfoPanel(p_item, this, true, o_linkItemLabel), BorderLayout.NORTH);
        x_panel.add(new DateSuggestionPanel(o_item, this), BorderLayout.SOUTH);
		x_panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		add(x_panel, BorderLayout.NORTH);

		TimeUpdater.getInstance().addTimeUpdateListener(this);
		GoogleSyncer.getInstance().addGoogleSyncListener(this);
    }

	private void linkToGoogle() {
		final JPanel x_this = this;

		if (o_linkItemLabel.isEnabled()) {
			if (JOptionPane.showConfirmDialog(x_this, "Link '" + o_item.getText() + "' to Google Calendar ?", "Link to Google ?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon()) == JOptionPane.OK_OPTION) {
				GoogleLinkTask x_task = new GoogleLinkTask(Arrays.asList(o_item), new GoogleProgressWindow(x_this), new ProgressAdapter() {
					@Override
					public void finished() {
						JOptionPane.showMessageDialog(x_this, "'" + o_item.getText() + "' was linked to Google Calendar", "Link notification", JOptionPane.WARNING_MESSAGE, ImageUtil.getGoogleIcon());
					}
				});
				x_task.execute();
			}
		}
	}

	@Override
	public void tableRowClicked(int row, int col) {
		// do nothing
	}

	public void tableRowDoubleClicked(int row, int col) {
		// do nothing
	}

	@Override
	public void update(Observable observable, Object o) {
		o_linkItemLabel.setEnabled(o_item.getDueDate() != null);
		tableRowClicked(-1, -1);
	}

	@Override
	public void timeUpdate() {
		((ComponentTableModel)o_table.getModel()).fireTableDataChanged();
		tableRowClicked(-1, -1);
	}

	@Override
	public void googleSynced() {
		((ComponentTableModel)o_table.getModel()).fireTableDataChanged();
		tableRowClicked(-1, -1);

		if(GoogleUtil.isLinked(o_item)) {
			o_linkItemLabel.setIcon(ImageUtil.getGoogleSmallIcon());
		} else {
			o_linkItemLabel.setIcon(ImageUtil.getLinkIcon());
		}
	}
}