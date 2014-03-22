create table IF NOT EXISTS `solr_ext_keywords` (
  `id` bigint NOT NULL,
  `keyword` varchar(100) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = MyISAM;

insert IGNORE into `solr_ext_keywords` values (1, '李天棚', '1990-04-17 00:00:01', '1990-04-17 00:00:01');

create table IF NOT EXISTS `solr_stop_keywords` (
  `id` bigint NOT NULL,
  `keyword` varchar(100) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = MyISAM;

insert IGNORE into `solr_stop_keywords` values (1, '的', '1990-04-17 00:00:01', '1990-04-17 00:00:01');

create table IF NOT EXISTS `solr_synonym_keywords` (
  `id` bigint(20) NOT NULL,
  `keyword` varchar(100) NOT NULL,
  `synonym_keyword` varchar(100) NOT NULL,
  `create_time`  timestamp NOT NULL,
  `update_time`  timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = MyISAM;

insert IGNORE into `solr_synonym_keywords` values (1, 'Daniel Li', '李天棚', '1990-04-17 00:00:01', '1990-04-17 00:00:01');
