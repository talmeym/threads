package gui;

import data.*;
import data.Thread;

import java.awt.Component;
import java.util.List;

class ThreadActionCellRenderer extends DataItemsCellRenderer<Thread, Item> {
	private boolean o_onlyNext7Days;

	ThreadActionCellRenderer(Thread p_thread) {
		super(p_thread);
    }

	@Override
	List<Item> getDataItems(Thread p_thread) {
		return LookupHelper.getAllActiveActions(p_thread, o_onlyNext7Days);
	}

	@Override
	void customSetup(Item p_item, Component p_awtComponent, boolean p_isSelected) {
		if(!p_isSelected) {
			p_awtComponent.setBackground(getColourForTime(p_item.getDueDate()));
		}
	}

	void setOnlyNext7Days(boolean p_onlyNext7Days) {
		this.o_onlyNext7Days = p_onlyNext7Days;
	}
}