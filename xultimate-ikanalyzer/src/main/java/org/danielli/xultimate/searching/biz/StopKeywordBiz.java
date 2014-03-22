package org.danielli.xultimate.searching.biz;

import java.util.List;

public interface StopKeywordBiz {
	
	List<String> find(Integer pageNo, Integer pageSize);
}
