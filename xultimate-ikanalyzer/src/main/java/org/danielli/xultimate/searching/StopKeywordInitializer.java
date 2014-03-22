package org.danielli.xultimate.searching;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.danielli.xultimate.searching.service.StopKeywordService;
import org.danielli.xultimate.util.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;

@Service("stopKeywordInitializer")
@Lazy(false)
public class StopKeywordInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(StopKeywordInitializer.class);
	
	@Resource(name = "myBatisStopKeywordService")
	private StopKeywordService stopKeywordService;
	
	@PostConstruct
	public void init() {
		LOGGER.info("开始加载停用词词库从数据库");
		Dictionary.initial(DefaultConfig.getInstance());
		for (int pageNo = 1; ; pageNo++) {
			List<String> stopKeywordList = stopKeywordService.find(pageNo, 10000);
			if (CollectionUtils.isEmpty(stopKeywordList)) {
				break;
			}
			Dictionary.getSingleton().addStopWords(stopKeywordList);
		}
	}
}
