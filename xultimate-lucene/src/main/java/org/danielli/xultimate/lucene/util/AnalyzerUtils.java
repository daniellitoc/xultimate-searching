package org.danielli.xultimate.lucene.util;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class AnalyzerUtils {

	public static List<String> tokenStream(Analyzer analyzer, Reader analyzerReader) throws IOException {
		List<String> result = new ArrayList<String>();
		TokenStream tokenStream = analyzer.tokenStream(null, analyzerReader);
		CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
		while (tokenStream.incrementToken()) {
			result.add(attribute.toString());
		}
		return result;
	}
	
}
