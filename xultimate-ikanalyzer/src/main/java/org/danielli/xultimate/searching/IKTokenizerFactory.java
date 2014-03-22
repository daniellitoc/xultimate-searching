package org.danielli.xultimate.searching;

import java.io.Reader;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource.AttributeFactory;
import org.wltea.analyzer.lucene.IKTokenizer;


public class IKTokenizerFactory extends TokenizerFactory {

	public static final boolean DEFAULT_USE_SMART = false;
	
	private boolean useSmart;
	
	public boolean useSmart() {
		return useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}
	
	public IKTokenizerFactory(Map<String, String> args) {
		super(args);
	    assureMatchVersion();
	    useSmart = getBoolean(args, "useSmart", DEFAULT_USE_SMART);
	}

	@Override
	public Tokenizer create(AttributeFactory factory, Reader input) {
		Tokenizer _IKTokenizer = new IKTokenizer(input , this.useSmart());
		return _IKTokenizer;
	}

}
