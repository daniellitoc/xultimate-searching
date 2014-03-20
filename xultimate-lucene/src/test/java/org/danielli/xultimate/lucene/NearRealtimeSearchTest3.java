package org.danielli.xultimate.lucene;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wltea.analyzer.lucene.IKAnalyzer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/org/danielli/xultimate/lucene/applicationContext-service-lucene3.xml"})
public class NearRealtimeSearchTest3 {

	@Resource
	private TrackingIndexWriter trackingIndexWriter;
	
	@Resource
	private SearcherManager searcherManager;
	
	@Resource
	private IKAnalyzer ikAnalyzer;
	
	public void saveDocument(Document document) {
		try {
			trackingIndexWriter.addDocument(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteDocumentById(Long id) {
		try {
			trackingIndexWriter.deleteDocuments(new Term("id", String.valueOf(id)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void before() {
		for (Document document : TestUtils.getDocuments()) {
			saveDocument(document);
		}
	}
	
	@Test
	public void test() throws InterruptedException, ParseException {
		/* Search */
		// 未分词 + 排序
		BooleanQuery noAnalyzerBooleanQuery = new BooleanQuery();
		noAnalyzerBooleanQuery.add(new TermQuery(new Term("noAnalyzer", "近实时搜索")), Occur.MUST);
		System.out.println(TestUtils.getDocuments(searcherManager, noAnalyzerBooleanQuery, Sort.RELEVANCE).size());
		Thread.sleep(3 * 1000);
		// 未分词 + 排序
		System.out.println(TestUtils.getDocuments(searcherManager, noAnalyzerBooleanQuery, Sort.RELEVANCE).size());
		// 删除
		deleteDocumentById(1L);
		// 未分词 + 排序
		System.out.println(TestUtils.getDocuments(searcherManager, noAnalyzerBooleanQuery, Sort.RELEVANCE).size());
		Thread.sleep(3 * 1000);
		// 未分词 + 排序
		System.out.println(TestUtils.getDocuments(searcherManager, noAnalyzerBooleanQuery, Sort.RELEVANCE).size());
		// 分词 + 排序
		BooleanQuery analyzerBooleanQuery = new BooleanQuery();
		analyzerBooleanQuery.add(new QueryParser(Version.LUCENE_45, "analyzer", ikAnalyzer).parse("近实时搜索"), Occur.MUST);
		System.out.println(TestUtils.getDocuments(searcherManager, analyzerBooleanQuery, Sort.RELEVANCE).size());		
		/* Filter + Collector */
		// 自定义Filter + 自定义Collector
		System.out.println(TestUtils.getIdList(searcherManager, noAnalyzerBooleanQuery, new IdFilter(new Long[] { 3L, 5L })).size());
		// Filter + 自定义Collector
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(new TermQuery(new Term("id", String.valueOf(3L))), Occur.SHOULD);
		booleanQuery.add(new TermQuery(new Term("id", String.valueOf(5L))), Occur.SHOULD);
		QueryWrapperFilter filter = new QueryWrapperFilter(booleanQuery);
		System.out.println(TestUtils.getIdList(searcherManager, noAnalyzerBooleanQuery, filter).size());
		/* 排序 */
		// ID倒排
		Sort idSort = new Sort(new SortField("id", Type.LONG, true));
		System.out.println(TestUtils.getDocuments(searcherManager, noAnalyzerBooleanQuery, idSort).get(0).get("id"));
		// 自定义排序
		idSort = new Sort(new SortField("id", new FieldComparatorSource() {
			
			@Override
			public FieldComparator<?> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
				return new IdFieldComparator(numHits, fieldname, new long[] { 59L, 57L });
			}
		}));
		System.out.println(TestUtils.getDocuments(searcherManager, noAnalyzerBooleanQuery, idSort).get(0).get("id"));
		System.out.println(TestUtils.getDocuments(searcherManager, noAnalyzerBooleanQuery, idSort).get(1).get("id"));
	}
}
