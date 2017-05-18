package gui;

import java.util.*;

class TableDataCache <TYPE> {
	private Map<Integer, Map<Integer, TYPE>> o_dataCache = new HashMap<>();

	public TYPE get(int p_row, int p_col) {
		Map<Integer, TYPE> x_rowDataMap = o_dataCache.get(p_row);
		return x_rowDataMap != null ? x_rowDataMap.get(p_col) : null;
	}

	TYPE fillOrGet(int p_row, int p_col, Producer<TYPE> p_producer) {
		if(!hasDataFor(p_row, p_col)) {
			put(p_row, p_col, p_producer.produce());
		}

		return get(p_row, p_col);
	}

	void put(int p_row, int p_col, TYPE p_obj) {
		Map<Integer, TYPE> x_rowDataMap = o_dataCache.get(p_row);

		if(x_rowDataMap == null) {
			x_rowDataMap = new HashMap<>();
			o_dataCache.put(p_row, x_rowDataMap);
		}

		x_rowDataMap.put(p_col, p_obj);
	}

	boolean hasDataFor(int p_row, int p_col) {
		Map<Integer, TYPE> x_rowDataMap = o_dataCache.get(p_row);
		return x_rowDataMap != null && x_rowDataMap.containsKey(p_col);
	}

	void invalidate() {
		o_dataCache = new HashMap<>();
	}

	interface Producer<CONTENTS> {
		CONTENTS produce();
	}
}
