package org.danielli.xultimate.searching.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import org.danielli.xultimate.searching.biz.SynonymKeywordBiz;
import org.danielli.xultimate.searching.dao.SynonymKeywordDAO;
import org.danielli.xultimate.searching.po.SynonymKeyword;
import org.danielli.xultimate.util.math.NumberUtils;
import org.springframework.stereotype.Service;

@Service("myBatisSynonymKeywordBiz")
public class MyBatisSynonymKeywordBiz implements SynonymKeywordBiz {

	@Resource(name = "synonymKeywordDAO")
	private SynonymKeywordDAO synonymKeywordDAO;
	
	@Override
	public List<SynonymKeyword> find(Integer pageNo, Integer pageSize) {
		if (!NumberUtils.isPositiveNumber(pageNo)) {
			pageNo = 1;
		}
		Integer offset = (pageNo - 1) * pageSize;
		return synonymKeywordDAO.find(offset, pageSize);
	}

}
