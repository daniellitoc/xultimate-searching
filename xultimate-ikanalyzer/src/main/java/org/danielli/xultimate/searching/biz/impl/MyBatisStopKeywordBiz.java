package org.danielli.xultimate.searching.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import org.danielli.xultimate.searching.biz.StopKeywordBiz;
import org.danielli.xultimate.searching.dao.StopKeywordDAO;
import org.danielli.xultimate.util.math.NumberUtils;
import org.springframework.stereotype.Service;

@Service("myBatisStopKeywordBiz")
public class MyBatisStopKeywordBiz implements StopKeywordBiz {

	@Resource(name = "stopKeywordDAO")
	private StopKeywordDAO stopKeywordDAO;
	
	@Override
	public List<String> find(Integer pageNo, Integer pageSize) {
		if (!NumberUtils.isPositiveNumber(pageNo)) {
			pageNo = 1;
		}
		Integer offset = (pageNo - 1) * pageSize;
		return stopKeywordDAO.find(offset, pageSize);
	}

}
