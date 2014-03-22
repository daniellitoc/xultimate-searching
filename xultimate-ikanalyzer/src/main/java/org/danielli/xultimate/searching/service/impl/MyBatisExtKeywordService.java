package org.danielli.xultimate.searching.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.danielli.xultimate.searching.biz.ExtKeywordBiz;
import org.danielli.xultimate.searching.service.ExtKeywordService;
import org.springframework.stereotype.Service;

@Service("myBatisExtKeywordService")
public class MyBatisExtKeywordService implements ExtKeywordService {

	@Resource(name = "myBatisExtKeywordBiz")
	private ExtKeywordBiz extKeywordBiz;
	
	@Override
	public List<String> find(Integer pageNo, Integer pageSize) {
		return extKeywordBiz.find(pageNo, pageSize);
	}

}
