package org.danielli.xultimate.searching;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.synonym.SolrSynonymParser;
import org.danielli.xultimate.context.util.ApplicationContextUtils;
import org.danielli.xultimate.context.util.BeanFactoryContext;
import org.danielli.xultimate.searching.po.SynonymKeyword;
import org.danielli.xultimate.searching.service.SynonymKeywordService;
import org.danielli.xultimate.util.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.util.IOUtils;

public class SolrSynonymDtabaseLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrSynonymDtabaseLoader.class);
	
	public void handle(SolrSynonymParser synonymParser) throws ParseException, IOException {
		SynonymKeywordService synonymKeywordService = ApplicationContextUtils.getBean(BeanFactoryContext.currentApplicationContext(), SynonymKeywordService.class);
		LOGGER.info("开始加载相近词词库从数据库");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
		try {
			for (int pageNo = 1; ; pageNo++) {
				List<SynonymKeyword> synonymKeywordList = synonymKeywordService.find(pageNo, 10000);
				if (CollectionUtils.isEmpty(synonymKeywordList)) {
					break;
				}
				for (SynonymKeyword synonymKeyword : synonymKeywordList) {
					writer.write(synonymKeyword.getKeyword());
					writer.write("=>");
					writer.write(synonymKeyword.getSynonymKeyword());
					writer.write("\n");
				}
			}
			writer.flush();
			Scanner scanner = new Scanner(new ByteArrayInputStream(outputStream.toByteArray()));
			while (scanner.hasNextLine()) {
				System.out.println(scanner.nextLine());
			}
			scanner.close();
			synonymParser.add(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray()))));
		} finally {
			IOUtils.close(writer);
		}
		
	}
}
