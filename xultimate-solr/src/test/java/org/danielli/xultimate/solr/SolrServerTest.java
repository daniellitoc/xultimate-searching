package org.danielli.xultimate.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.danielli.xultimate.util.math.RandomNumberUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext-service-solr-client.xml"})
public class SolrServerTest {

//	@Resource(name = "httpSolrServer")
//	@Resource(name = "lbHttpSolrServer")
	@Resource(name = "cloudSolrServer")
	private SolrServer querySolrServer;
	
//	@Resource(name = "concurrentUpdateSolrServer")
//	@Resource(name = "lbHttpSolrServer")
	@Resource(name = "cloudSolrServer")
	private SolrServer updateSolrServer;
	
	@Test
	public void testAddDocument() throws SolrServerException, IOException {
		addDocument();
		queryReturnDocument();
		deleteAll();
		queryReturnDocument();
	}
	
	private void addDocument() throws SolrServerException, IOException {
		List<SolrInputDocument> solrInputDocuments = new ArrayList<>();
		for (int i = 0 ; i < 100; i++) {
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", "id" + RandomNumberUtils.nextInt(1000), 1.0f);
			document.addField("name", "doc" + i, 1.0f);
			document.addField("price", RandomNumberUtils.nextInt(1000));
		    solrInputDocuments.add(document);
		}

	    // Do a commit, wait flush, wait searcher.
	    updateSolrServer.add(solrInputDocuments);	// Add the documents to Solr.
	    updateSolrServer.commit();	
	}
	
	public void deleteAll() throws SolrServerException, IOException {
		updateSolrServer.deleteByQuery("*:*");	// CAUTION: deletes everything!
		updateSolrServer.commit();
	}
	
	private void queryReturnDocument() throws SolrServerException {
		SolrQuery query = new SolrQuery();
	    query.setQuery("*:*");
	    query.addSort("price", SolrQuery.ORDER.asc);
	    QueryResponse rsp = querySolrServer.query(query);
	    SolrDocumentList documentList = rsp.getResults();
	    for (SolrDocument resultDoc : documentList) {
		     String id = (String) resultDoc.getFieldValue("id"); //id is the uniqueKey field
		     System.out.println(id);
	    }
	    System.out.println("发现" + documentList.getNumFound() + "个文档");
	}
	
//	@Test
	public void testAddBean() throws SolrServerException, IOException {
		addBean();
		queryReturnEntity();
		deleteAll();
		queryReturnEntity();
	}
	
	private void addBean() throws IOException, SolrServerException {
		Item item1 = new Item();
	    item1.setId("id" + RandomNumberUtils.nextInt(1000));
	    item1.setFeatures(Arrays.asList("aaa", "bbb", "ccc"));
	    
		Item item2 = new Item();
	    item2.setId("id" + RandomNumberUtils.nextInt(1000));
	    item2.setFeatures(Arrays.asList("ddd", "eee", "fff"));
	    
	    updateSolrServer.addBeans(Arrays.asList(item1, item2));	// Add the beans to Solr
	    updateSolrServer.commit();								// Do a commit
	}
	
	public static class Item {
	    @Field
	    private String id;

	    @Field("cat")
	    private String[] categories;

	    @Field
	    private List<String> features;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String[] getCategories() {
			return categories;
		}

//		@Field annotation can be applied on setter methods
//		@Field("cat")		
		public void setCategories(String[] categories) {
			this.categories = categories;
		}

		public List<String> getFeatures() {
			return features;
		}

		public void setFeatures(List<String> features) {
			this.features = features;
		}
	}
	
	private void queryReturnEntity() throws SolrServerException {
		SolrQuery query = new SolrQuery();
	    query.setQuery("*:*");
	    query.addSort("price", SolrQuery.ORDER.asc);
	    QueryResponse rsp = querySolrServer.query(query);
	    List<Item> beans = rsp.getBeans(Item.class);
	    for (Item bean : beans) {
		     String id = bean.getId(); //id is the uniqueKey field
		     System.out.println(id);
	    }
	}
	
//	@Test
	public void testQueryHighlighting() throws SolrServerException, IOException {
		addBean();
		queryHighlighting();
		deleteAll();
		queryHighlighting();
	}

	private void queryHighlighting() throws SolrServerException {
		SolrQuery query = new SolrQuery();
	    query.setQuery("bbb");

	    query.setHighlight(true).setHighlightSnippets(1); // set other params as needed
	    query.setParam("hl.fl", "features");				  // 设置高亮字段
	    QueryResponse queryResponse = querySolrServer.query(query);
	    
	    SolrDocumentList documentList = queryResponse.getResults();
	    for (SolrDocument resultDoc : documentList) {
	    	 Collection<Object>  features = resultDoc.getFieldValues("features");
		     String id = (String) resultDoc.getFieldValue("id"); //id is the uniqueKey field
		     if (queryResponse.getHighlighting().get(id) != null) {
		        List<String> highlightSnippets = queryResponse.getHighlighting().get(id).get("features");
		        System.out.println(Arrays.toString(highlightSnippets.toArray()));
		     } else {
		    	 System.out.println(Arrays.toString(features.toArray()));
		     }
		     
	    }
	    
	    List<Item> beans = queryResponse.getBeans(Item.class);
	    for (Item bean : beans) {
		     String id = bean.getId();
		     List<String> features = bean.getFeatures();
		     if (queryResponse.getHighlighting().get(id) != null) {
		    	 List<String> highlightSnippets = queryResponse.getHighlighting().get(id).get("features");
			     System.out.println(Arrays.toString(highlightSnippets.toArray()));
			 } else {
				 System.out.println(Arrays.toString(features.toArray()));
			 }
	    }
	}
}
