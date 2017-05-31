package threads.gui;

import java.util.HashMap;
import java.util.Map;

class TableDataCache <TYPE> {
	private Map<Integer, Map<Integer, TYPE>> o_dataCache = new HashMap<>();

	TYPE fillOrGet(int p_row, int p_col, Producer<TYPE> p_producer) {
		if(!hasDataFor(p_row, p_col)) {
			put(p_row, p_col, p_producer.produce());
		}

		return get(p_row, p_col);
	}

	private TYPE get(int p_row, int p_col) {
		return o_dataCache.computeIfAbsent(p_row, k -> new HashMap<>()).get(p_col);
	}

	private void put(int p_row, int p_col, TYPE p_obj) {
		o_dataCache.computeIfAbsent(p_row, k -> new HashMap<>()).put(p_col, p_obj);
	}

	private boolean hasDataFor(int p_row, int p_col) {
		return o_dataCache.computeIfAbsent(p_row, k -> new HashMap<>()).containsKey(p_col);
	}

	void invalidate() {
		o_dataCache = new HashMap<>();
	}

	interface Producer<CONTENTS> {
		CONTENTS produce();
	}
}
