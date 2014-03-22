package org.danielli.xultimate.searching.biz;

import java.util.List;

import org.danielli.xultimate.searching.po.SynonymKeyword;

public interface SynonymKeywordBiz {
	
	List<SynonymKeyword> find(Integer pageNo, Integer pageSize);
}
