package org.danielli.xultimate.searching.service;

import java.util.List;

import org.danielli.xultimate.searching.po.SynonymKeyword;

public interface SynonymKeywordService {

	List<SynonymKeyword> find(Integer pageNo, Integer pageSize);
}
