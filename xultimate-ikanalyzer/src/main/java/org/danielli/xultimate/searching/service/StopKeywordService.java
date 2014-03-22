package org.danielli.xultimate.searching.service;

import java.util.List;

public interface StopKeywordService {

	List<String> find(Integer pageNo, Integer pageSize);
}
