package com.sutpc.bigdata.schema.psql

import java.sql.Timestamp

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/7/25  15:42
  */

case class CityTimeDim(
                        tname: String,
                        source: String,
                        date: String,
                        area: String,
                        time_dim: Int

                      )


//id bigint NOT NULL DEFAULT nextval('index_metadata_id_seq'::regclass),
//table_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
//column_name_en character varying(255) COLLATE pg_catalog."default",
//column_name character varying(255) COLLATE pg_catalog."default",
//description character varying(1000) COLLATE pg_catalog."default",
//data_type character varying(40) COLLATE pg_catalog."default",
//column_size character varying(40) COLLATE pg_catalog."default",
//is_nullable boolean,
//created_by character varying(255) COLLATE pg_catalog."default",
//modified_by character varying(255) COLLATE pg_catalog."default",
//accessed_date timestamp without time zone,
//gmt_create timestamp without time zone,
//gmt_modified timestamp without time zone,
case class Index_MetaData(
                           table_name: String,
                           column_name_en: String,
                           column_name: String,
                           description: String,
                           gmt_create: Timestamp,
                           gmt_modified: Timestamp,
                           data_type: String,
                           column_size: String,
                           is_nullable: Boolean,
                           created_by: String,
                           modified_by: String,
                           accessed_date: Timestamp
                         )

case class HDFS_STAT_CFG(
                            id: Long,
                            category: String,
                            name: String,
                            path: String,
                            city: String,
                            city_pinyin: String,
                            create_time: Timestamp,
                            creator: String
                          )


case class HDFS_STAT_DAILY(
                            id: String,
                            category: String,
                            name: String,
                            path: String,
                            city: String,
                            city_pinyin: String,
                            date:String,
                            cnt:Long,
                            create_time: Timestamp,
                            creator: String
                          )