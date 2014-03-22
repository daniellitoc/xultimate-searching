package org.danielli.xultimate.searching;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.danielli.xultimate.searching.service.ExtKeywordService;
import org.danielli.xultimate.util.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;

@Service("extKeywordInitializer")
@Lazy(false)
public class ExtKeywordInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtKeywordInitializer.class);
	
	@Resource(name = "myBatisExtKeywordService")
	private ExtKeywordService extKeywordService;
	
	@PostConstruct
	public void init() {
		LOGGER.info("开始加载扩展词词库从数据库");
		Dictionary.initial(DefaultConfig.getInstance());
		for (int pageNo = 1; ; pageNo++) {
			List<String> extKeywordList = extKeywordService.find(pageNo, 10000);
			if (CollectionUtils.isEmpty(extKeywordList)) {
				break;
			}
			Dictionary.getSingleton().addWords(extKeywordList);
		}
	}
}
