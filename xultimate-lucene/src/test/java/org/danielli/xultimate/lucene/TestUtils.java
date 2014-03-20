package org.danielli.xultimate.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

public class TestUtils {

	private static String[] values = { "网站访问量搜索", "近实时搜索" };
	
	public static Document[] getDocuments() {
		Document[] documents = new Document[100];
		for (int i = 0; i < documents.length; i++) {
			documents[i] = createDocument((long) i);
		}
		return documents;
	}
	
	private static Document createDocument(Long id) {
		Document document = new Document();
		document.add(new LongField("time", System.currentTimeMillis(), Store.YES));
		document.add(new StringField("id", String.valueOf(id), Store.YES));
		document.add(new StringField("noAnalyzer", values[(int) (id % 2)], Store.YES));
		document.add(new TextField("analyzer", values[(int) (id % 2)], Store.YES));
		return document;
	}
	
	private static void release(SearcherManager searcherManager, IndexSearcher indexSearcher) {
		if (indexSearcher != null) {
			try {
				searcherManager.release(indexSearcher);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}
	
	public static List<Document> getDocuments(SearcherManager searcherManager, Query query, Sort sort) {
		IndexSearcher indexSearcher = null;
		try {
			indexSearcher = searcherManager.acquire();
			TopDocs topDocs = indexSearcher.search(query, 200, sort);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(indexSearcher.doc(scoreDoc.doc));
			}
			return documents;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			release(searcherManager, indexSearcher);
		}
	}
	
	public static List<Long> getIdList(SearcherManager searcherManager, Query query, Filter filter) {
		IndexSearcher indexSearcher = null;
		try {
			indexSearcher = searcherManager.acquire();
			IdCollector collector = new IdCollector(); 
			indexSearcher.search(query, filter, collector);
			return collector.getResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			release(searcherManager, indexSearcher);
		}
	}
}
