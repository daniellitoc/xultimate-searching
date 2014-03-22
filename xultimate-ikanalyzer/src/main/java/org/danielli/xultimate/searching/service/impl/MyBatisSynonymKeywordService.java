package org.danielli.xultimate.searching.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.danielli.xultimate.searching.biz.SynonymKeywordBiz;
import org.danielli.xultimate.searching.po.SynonymKeyword;
import org.danielli.xultimate.searching.service.SynonymKeywordService;
import org.springframework.stereotype.Service;

@Service("myBatisSynonymKeywordService")
public class MyBatisSynonymKeywordService implements SynonymKeywordService {

	@Resource(name = "myBatisSynonymKeywordBiz")
	private SynonymKeywordBiz synonymKeywordBiz;
	
	@Override
	public List<SynonymKeyword> find(Integer pageNo, Integer pageSize) {
		return synonymKeywordBiz.find(pageNo, pageSize);
	}

}
