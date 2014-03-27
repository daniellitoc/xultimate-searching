# xultimate-searching #

使用IKAnalyzer和Solr作为搜索服务。不通过文件维护词库，而是通过数据库维护。

## xultimate-solr ##

* 基于SolrCloud，提供了SolrJ的一些ShowCase。包含HttpSolrServer、ConcurrentUpdateSolrServer、LBHttpSolrServer、CloudSolrServer的Spring配置形式。

#### SolrCloud创建数据集Collection并自动分片，手动分配副本 ####

	http://192.168.2.150:8080/solr/admin/collections?action=CREATE&name=mycollection&numShards=4&replicationFactor=1&maxShardsPerNode=4	
	// 手动创建192.168.2.150副本Replicas。假设shard1处在192.168.1.150上
	http://192.168.2.151:8080/solr/admin/cores?action=CREATE&name=mycollection_shard1_replica_2&collection=mycollection&shard=shard1
	http://192.168.2.152:8080/solr/admin/cores?action=CREATE&name=mycollection_shard1_replica_3&collection=mycollection&shard=shard1
	http://192.168.2.153:8080/solr/admin/cores?action=CREATE&name=mycollection_shard1_replica_4&collection=mycollection&shard=shard1
	// 手动创建192.168.2.153副本Replicas。假设shard2处在192.168.1.153上
	http://192.168.2.150:8080/solr/admin/cores?action=CREATE&name=mycollection_shard2_replica_2&collection=mycollection&shard=shard2
	http://192.168.2.151:8080/solr/admin/cores?action=CREATE&name=mycollection_shard2_replica_3&collection=mycollection&shard=shard2
	http://192.168.2.152:8080/solr/admin/cores?action=CREATE&name=mycollection_shard2_replica_4&collection=mycollection&shard=shard2
	// 手动创建192.168.2.152副本Replicas。假设shard3处在192.168.1.152上
	http://192.168.2.150:8080/solr/admin/cores?action=CREATE&name=mycollection_shard3_replica_2&collection=mycollection&shard=shard3
	http://192.168.2.151:8080/solr/admin/cores?action=CREATE&name=mycollection_shard3_replica_3&collection=mycollection&shard=shard3
	http://192.168.2.153:8080/solr/admin/cores?action=CREATE&name=mycollection_shard3_replica_4&collection=mycollection&shard=shard3
	// 手动创建192.168.2.151副本Replicas	。假设shard4处在192.168.1.151上
	http://192.168.2.150:8080/solr/admin/cores?action=CREATE&name=mycollection_shard4_replica_2&collection=mycollection&shard=shard4
	http://192.168.2.152:8080/solr/admin/cores?action=CREATE&name=mycollection_shard4_replica_3&collection=mycollection&shard=shard4
	http://192.168.2.153:8080/solr/admin/cores?action=CREATE&name=mycollection_shard4_replica_4&collection=mycollection&shard=shard4

#### SolrCloud删除数据集Collection ####
	
	http://192.168.2.150:8080/solr/admin/collections?action=DELETE&name=mycollection
	
#### SolrCloud创建数据集Collection并手动分片(必须创建numShards个，否则无法使用) ####

	http://192.168.2.150:8080/solr/admin/cores?action=CREATE&name=mycollection2_shard1_replica_1&collection=mycollection2&shard=shard1&numShards=4
	http://192.168.2.151:8080/solr/admin/cores?action=CREATE&name=mycollection2_shard2_replica_1&collection=mycollection2&shard=shard2
	http://192.168.2.152:8080/solr/admin/cores?action=CREATE&name=mycollection2_shard3_replica_1&collection=mycollection2&shard=shard3
	http://192.168.2.153:8080/solr/admin/cores?action=CREATE&name=mycollection2_shard4_replica_1&collection=mycollection2&shard=shard4

#### SolrCloud对指定分片在进行自动切分 ####

	http://192.168.2.150:8080/solr/admin/collections?action=CREATE&name=mycollection3&numShards=1&replicationFactor=1&maxShardsPerNode=4	
　　
	或
	
	http://192.168.2.150:8080/solr/admin/cores?action=CREATE&name=mycollection3_shard1_replica_1&collection=mycollection3&shard=shard1&numShards=1
	http://192.168.2.150:8080/solr/admin/collections?action=SPLITSHARD&collection=mycollection3&shard=shard1	// 只支持自动分片的再切分。
	http://192.168.2.153:8080/solr/admin/cores?action=UNLOAD&core=mycollection3_shard1_replica1					// mycollection3_shard1_replica1在192.168.2.153上。

## xultimate-ikanalyzer ##

* 扩展Dictionary类，提供停用词、量词的扩展接口。
* 提供ExtKeywordInitializer，用于通过数据库维护IKAnalyzer的扩展词词库。
* 提供StopKeywordInitializer，用于通过数据库维护IKAnalyzer的停用词词库。
* 提供SynonymFilterFactory，用于通过数据库维护Solr的相近词词库。
* 提供IKTokenizerFactory，解决Solr中IKAnalyzer在查询时无法使用userSmart问题。

#### 配置扩展词、停用此、相近词从文件读取 ####

* 将项目导出为jar文件，拷贝到solr/WEB-INF/lib/下。
* 复制src/test/resources下的ext.dic IKAnalyzer.cfg.xml stopword.dic到solr/WEB-INF/classes/下。
* 编辑$SOLR_HOME/collection1/conf/synonyms.txt
* 编辑$SOLR_HOME/collection1/conf/schema.xml，添加类型

 	<fieldType name="text_ikanalyzer" class="solr.TextField" >
		<analyzer type="index">
            <tokenizer class="org.danielli.xultimate.searching.IKTokenizerFactory" useSmart="false"/>
        </analyzer> 
        <analyzer type="query">
            <tokenizer class="org.danielli.xultimate.searching.IKTokenizerFactory" useSmart="true"/>
	    	<filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
        </analyzer> 
    </fieldType>
    
#### 配置扩展词、停用词、相近词从数据库读取 ####

* 将项目导出为jar文件，拷贝到solr/WEB-INF/lib/下。
* 拷贝xultimate-ikanalyzer的依赖jar到solr/WEB-INF/lib/下。
* 删除slf4j-log4j12-1.6.6.jar jcl-over-slf4j-1.6.6.jar jul-to-slf4j-1.6.6.jar log4j-1.2.16.jar slf4j-log4j12-1.6.6.jar slf4j-api-1.6.6.jar commons-lang-2.4.jar log4j.properties
* 复制src/test/resources下的databases.properties到solr/WEB-INF/classes/下。
* 编辑solr/WEB-INF/web.xml，添加

	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:applicationContext-service-config.xml, classpath:applicationContext-service-crypto.xml, classpath:applicationContext-dao-base.xml, classpath:applicationContext-dao-generic.xml, classpath:applicationContext-service-generic.xml</param-value>
	</context-param>
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
	
* 编辑$SOLR_HOME/collection1/conf/schema.xml，添加类型

　　<fieldType name="text_ikanalyzer" class="solr.TextField" >
		<analyzer type="index">
            <tokenizer class="org.danielli.xultimate.searching.IKTokenizerFactory" useSmart="false"/>
        </analyzer> 
        <analyzer type="query">
            <tokenizer class="org.danielli.xultimate.searching.IKTokenizerFactory" useSmart="true"/>
	    <filter class="org.danielli.xultimate.searching.SynonymFilterFactory" ignoreCase="true" expand="true"/>
        </analyzer> 
    </fieldType>


## xultimate-lucene ##

* 三种Lucene近实时搜索ShowCase。包括添加自定义Collector、自定义Filter、自定义Sort。
* 通过使用Spring进行Bean管理。

