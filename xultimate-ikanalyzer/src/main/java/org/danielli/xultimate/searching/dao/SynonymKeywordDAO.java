package org.danielli.xultimate.searching.dao;

import java.util.List;

import org.danielli.xultimate.orm.mybatis.MyBatisRepository;
import org.danielli.xultimate.searching.po.SynonymKeyword;

@MyBatisRepository
public interface SynonymKeywordDAO {

	List<SynonymKeyword> find(Integer offset, Integer rows);
}
