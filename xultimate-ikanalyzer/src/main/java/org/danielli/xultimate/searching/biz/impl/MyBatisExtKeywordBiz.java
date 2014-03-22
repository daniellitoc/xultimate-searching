package org.danielli.xultimate.searching.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import org.danielli.xultimate.searching.biz.ExtKeywordBiz;
import org.danielli.xultimate.searching.dao.ExtKeywordDAO;
import org.danielli.xultimate.util.math.NumberUtils;
import org.springframework.stereotype.Service;

@Service("myBatisExtKeywordBiz")
public class MyBatisExtKeywordBiz implements ExtKeywordBiz {
	
	@Resource(name = "extKeywordDAO")
	private ExtKeywordDAO extKeywordDAO;
	
	@Override
	public List<String> find(Integer pageNo, Integer pageSize) {
		if (!NumberUtils.isPositiveNumber(pageNo)) {
			pageNo = 1;
		}
		Integer offset = (pageNo - 1) * pageSize;
		return extKeywordDAO.find(offset, pageSize);
	}
}
