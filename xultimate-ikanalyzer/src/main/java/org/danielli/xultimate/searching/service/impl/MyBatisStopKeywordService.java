package org.danielli.xultimate.searching.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.danielli.xultimate.searching.biz.StopKeywordBiz;
import org.danielli.xultimate.searching.service.StopKeywordService;
import org.springframework.stereotype.Service;

@Service("myBatisStopKeywordService")
public class MyBatisStopKeywordService implements StopKeywordService {

	@Resource(name = "myBatisStopKeywordBiz")
	private StopKeywordBiz stopKeywordBiz;
	
	@Override
	public List<String> find(Integer pageNo, Integer pageSize) {
		return stopKeywordBiz.find(pageNo, pageSize);
	}

}
