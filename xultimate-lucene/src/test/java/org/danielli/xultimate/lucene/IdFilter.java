package org.danielli.xultimate.lucene;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.OpenBitSet;

public class IdFilter extends Filter {

	private Long[] existIds;
	
	public IdFilter(Long[] existIds) {
		this.existIds = existIds;
	}
	
	@Override
	public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
		OpenBitSet bits = new OpenBitSet(context.reader().maxDoc());
		for (Long id : existIds) {
			DocsEnum docsEnum = context.reader().termDocsEnum(new Term("id", String.valueOf(id)));
			if(docsEnum != null && docsEnum.nextDoc() != -1) {   
	            bits.set(docsEnum.docID());
	        }  
		}
		return bits;
	}

}
