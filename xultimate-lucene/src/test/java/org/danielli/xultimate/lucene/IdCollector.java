package org.danielli.xultimate.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCache.Longs;
import org.apache.lucene.search.Scorer;

public class IdCollector extends Collector {
	
	@SuppressWarnings("unused")
	private Scorer scorer;
	private Longs currentValues;
	private Set<Long> result = new LinkedHashSet<Long>();
	
	@Override
	public void setScorer(Scorer scorer) throws IOException {
		this.scorer = scorer;
	}
	
	@Override
	public void setNextReader(AtomicReaderContext context) throws IOException {
		this.currentValues = FieldCache.DEFAULT.getLongs(context.reader(), "id", false);
	}
	
	@Override
	public void collect(int doc) throws IOException {
		Long userId = this.currentValues.get(doc);
		result.add(userId);
	}
	
	public Integer getMatchUserCount() {
		return result.size();
	}
	
	public List<Long> getResult() {
		return new ArrayList<Long>(result);
	}
	
	@Override
	public boolean acceptsDocsOutOfOrder() {
		return false;
	}
}
