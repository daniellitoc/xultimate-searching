package org.danielli.xultimate.searching.po;

import java.util.Date;

public class SynonymKeyword {
	
	private Long id;
	
	private String keyword;
	
	private String synonymKeyword;
	
	private Date createTime;
	
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getSynonymKeyword() {
		return synonymKeyword;
	}

	public void setSynonymKeyword(String synonymKeyword) {
		this.synonymKeyword = synonymKeyword;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
