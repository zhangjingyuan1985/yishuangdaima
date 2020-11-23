package com.sutpc.bigdata.utils

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}
import org.joda.time.DateTime

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/11/8  17:50
  */
object SparkHiveUtil {


  def output[T](spark: SparkSession, outputTables: Array[String], partitions: Array[String], res: Dataset[T], dupliCols: Seq[String], paraNum: Int = 16, isPersist: Boolean = true) = {

    spark.conf.set("spark.sql.sources.partitionOverwriteMode", "dynamic")
    spark.sql("set hive.exec.dynamic.partition.mode=nonstrict")

    spark.sql("set hive.exec.max.dynamic.partitions=9000")

//    spark.sql("set hive.enforce.bucketing=true")
//
//    spark.sqlContext.setConf("spark.sql.hive.convertMetastoreParquet", "false")

    outputTables.map(_.trim).foreach(table => {

      println("输出到hive的表【" + table + "】")
      res.write
        .mode(SaveMode.Append)
        .format("Hive")
        .partitionBy(partitions: _*)
        .saveAsTable(table)

    })

    if (isPersist) res.unpersist()

    println("当前时间：" + DateTime.now().toString)
  }


}
