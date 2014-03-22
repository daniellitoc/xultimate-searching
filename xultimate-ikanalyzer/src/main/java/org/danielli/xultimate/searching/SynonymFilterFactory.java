package org.danielli.xultimate.searching;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SolrSynonymParser;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.Version;

public class SynonymFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {

	private final boolean ignoreCase;
	private final String tokenizerFactory;
	private final String format;
	private final boolean expand;
	private final Map<String, String> tokArgs = new HashMap<String, String>();

	private SynonymMap map;

	public SynonymFilterFactory(Map<String, String> args) {
		super(args);
		ignoreCase = getBoolean(args, "ignoreCase", false);
		format = get(args, "format");
		expand = getBoolean(args, "expand", true);

		tokenizerFactory = get(args, "tokenizerFactory");
		if (tokenizerFactory != null) {
			assureMatchVersion();
			tokArgs.put("luceneMatchVersion", getLuceneMatchVersion().toString());
			for (Iterator<String> itr = args.keySet().iterator(); itr.hasNext();) {
				String key = itr.next();
				tokArgs.put(key.replaceAll("^tokenizerFactory\\.", ""), args.get(key));
				itr.remove();
			}
		}
		if (!args.isEmpty()) {
			throw new IllegalArgumentException("Unknown parameters: " + args);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public TokenStream create(TokenStream input) {
	    // if the fst is null, it means there's actually no synonyms... just return the original stream
	    // as there is nothing to do here.
	    return map.fst == null ? input : new SynonymFilter(input, map, ignoreCase);
	}

	@SuppressWarnings("resource")
	@Override
	public void inform(ResourceLoader loader) throws IOException {
		final TokenizerFactory factory = tokenizerFactory == null ? null : loadTokenizerFactory(loader, tokenizerFactory);

		Analyzer analyzer = new Analyzer() {
			@Override
			protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
				Tokenizer tokenizer = factory == null ? new WhitespaceTokenizer(Version.LUCENE_45, reader) : factory.create(reader);
				TokenStream stream = ignoreCase ? new LowerCaseFilter(Version.LUCENE_45, tokenizer) : tokenizer;
				return new TokenStreamComponents(tokenizer, stream);
			}
		};

		try {
			if (format == null || format.equals("solr")) {
				// TODO: expose dedup as a parameter?
				map = loadSolrSynonyms(loader, true, analyzer);
			} else {
				// TODO: somehow make this more pluggable
				throw new IllegalArgumentException("Unrecognized synonyms format: " + format);
			}
		} catch (ParseException e) {
			throw new IOException("Error parsing synonyms file:", e);
		}
	}

	/**
	 * Load synonyms from the solr format, "format=solr".
	 */
	private SynonymMap loadSolrSynonyms(ResourceLoader loader, boolean dedup, Analyzer analyzer) throws IOException, ParseException {
		SolrSynonymParser parser = new SolrSynonymParser(dedup, expand, analyzer);
		
		SolrSynonymDtabaseLoader solrSynonymDtabaseLoader = new SolrSynonymDtabaseLoader();
		solrSynonymDtabaseLoader.handle(parser);
		
		return parser.build();
	}

	// (there are no tests for this functionality)
	private TokenizerFactory loadTokenizerFactory(ResourceLoader loader, String cname) throws IOException {
		Class<? extends TokenizerFactory> clazz = loader.findClass(cname, TokenizerFactory.class);
		try {
			TokenizerFactory tokFactory = clazz.getConstructor(Map.class).newInstance(tokArgs);
			if (tokFactory instanceof ResourceLoaderAware) {
				((ResourceLoaderAware) tokFactory).inform(loader);
			}
			return tokFactory;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
