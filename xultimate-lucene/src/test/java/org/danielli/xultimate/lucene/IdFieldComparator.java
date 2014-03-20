package org.danielli.xultimate.lucene;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;

public class IdFieldComparator extends FieldComparator<Long> {
	private final long[] values;
	protected final String field;
	private FieldCache.Longs currentReaderValues;
	private long bottom;
	
	private long[] headIds;
	
	public IdFieldComparator(int numHits, String field, long[] headIds) {
		this.field = field;
		values = new long[numHits];
		this.headIds = headIds;
	}
	
	public int customCompare(long v1, long v2) {
		if (v1 == v2) {
			return 0;
		}
		
		boolean v1Exists = false;
		if (ArrayUtils.contains(headIds, v1)) {
			v1Exists = true;
		}
		boolean v2Exists = false;
		if (ArrayUtils.contains(headIds, v2)) {
			v2Exists = true;
		}
		
		if (v1Exists && v2Exists) {
			return v1 > v2 ? -1 : 1;
		} else if (v1Exists) {
			return -1;
		} else if (v2Exists) {
			return 1;
		} else {
			return v1 > v2 ? -1 : 1;
		}
	}
	
	@Override
	public int compare(int slot1, int slot2) {
		final long v1 = values[slot1];
		final long v2 = values[slot2];
//	    if (v1 > v2) {
//	    	return 1;
//	    } else if (v1 < v2) {
//	        return -1;
//	    } else {
//	        return 0;
//	    }
		return customCompare(v1, v2);
	}

	@Override
	public void setBottom(int slot) {
		 this.bottom = values[slot];
	}

	@Override
	public int compareBottom(int doc) throws IOException {
		long v2 = currentReaderValues.get(doc);
//		if (bottom > v2) {
//	        return 1;
//	    } else if (bottom < v2) {
//	        return -1;
//	    } else {
//	        return 0;
//	    }
		return customCompare(bottom, v2);
	}

	@Override
	public void copy(int slot, int doc) throws IOException {
		long v2 = currentReaderValues.get(doc);
	    values[slot] = v2;
	}

	@Override
	public FieldComparator<Long> setNextReader(AtomicReaderContext context) throws IOException {
		currentReaderValues = FieldCache.DEFAULT.getLongs(context.reader(), field, false);
		return this;
	}

	@Override
	public Long value(int slot) {
		return Long.valueOf(values[slot]);
	}

	@Override
	public int compareDocToValue(int doc, Long value) throws IOException {
		final long valueLong = value.longValue();
	    long docValue = currentReaderValues.get(doc);
//	    if (docValue < valueLong) {
//	        return -1;
//	    } else if (docValue > valueLong) {
//	        return 1;
//	    } else {
//	        return 0;
//	    }
		return customCompare(docValue, valueLong);
	}

}
