package com.sutpc.bigdata.job.batch.real

import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/11/5  14:47
  */
object TaskOutput {


  def hive(spark: SparkSession, task: EtlTask, partitions: Array[String], res: DataFrame) = {
    //开启动态分区
    spark.conf.set("spark.sql.sources.partitionOverwriteMode", "dynamic")
    spark.sql("set hive.exec.dynamic.partition.mode=nonstrict")
    spark.sql("set hive.exec.max.dynamic.partitions=90000")

    //hive强制分桶
//    spark.sql("set hive.enforce.bucketing=true")
//    spark.sqlContext.setConf("spark.sql.hive.convertMetastoreParquet", "false")

    task.output.split(",").foreach(t => {

      println("建表")
      res.where("1=0")
        //        .coalesce(paraNum)
        .write
        .mode(SaveMode.Append)
        .format("Hive")
        .partitionBy(partitions: _*)
        .saveAsTable(t)


      println("输出到hive的表【" + t + "】")
      if (t.startsWith("transpaas_std_dev")) {
        println("删除七天前的数据")
        val lastWeek = DateTime.now().minusDays(7)
        val year = lastWeek.getYear
        val month = lastWeek.getMonthOfYear
        val day = lastWeek.getDayOfMonth
        val sql = s"alter table $t drop  if exists  partition (year=$year,month=$month,day=$day)"
        println(sql)
        spark.sql(sql)
      }


      println("删除已存在的数据")
      val dateTime = DateTime.parse(task.parsed_date, DateTimeFormat.forPattern("yyyy/MM/dd"))
      val sql = s"alter table $t drop  if exists  partition (year=${dateTime.getYear},month=${dateTime.getMonthOfYear},day=${dateTime.getDayOfMonth})"
      if (JobLock.isDropPartition) {
        println(sql)
        spark.sql(sql)
      }

      println("输出hive")
      res
        //        .coalesce(paraNum)
        .write
        .mode(SaveMode.Append)
        .format("Hive")
        .partitionBy(partitions: _*)
        .saveAsTable(t)

      println("当前时间：" + DateTime.now().toString)

    })

  }


  def hbase[T](zkUrl: String, outputTable: String, res: Dataset[T]) = {

    println("输出到HBase的表" + outputTable)

    res.distinct.write
      .format("org.apache.phoenix.spark")
      .mode("overwrite")
      .option("phoenix.schema.isNamespaceMappingEnabled", "true")
      .option("table", outputTable)
      .option("zkUrl", zkUrl)
      .save()

    println("当前时间：" + DateTime.now().toString)
  }


  def hive[T](spark: SparkSession, task: EtlTask, partitions: Array[String], res: Dataset[T], paraNum: Int = 16, isPersist: Boolean = true, db: String = "transpass_std", isCopy2Tag: Boolean = false, dupliCols: Seq[String]) = {
    //开启动态分区
    spark.conf.set("spark.sql.sources.partitionOverwriteMode", "dynamic")
    spark.sql("set hive.exec.dynamic.partition.mode=nonstrict")
    spark.sql("set hive.exec.max.dynamic.partitions=90000")

    //hive强制分桶
//    spark.sql("set hive.enforce.bucketing=true")
//    spark.sqlContext.setConf("spark.sql.hive.convertMetastoreParquet", "false")

    task.output.split(",").foreach(t => {
      println("输出到hive的表【" + t + "】")
      if (t.startsWith("transpaas_std_dev")) {
        println("删除七天前的数据")
        val lastWeek = DateTime.now().minusDays(7)
        val year = lastWeek.getYear
        val month = lastWeek.getMonthOfYear
        val day = lastWeek.getDayOfMonth
        val sql = s"alter table $t drop  if exists  partition (year=$year,month=$month,day=$day)"
        println(sql)
        spark.sql(sql)
      }

      println("删除已存在的数据")
      task.parsed_date.length match {
        case 13 => {
//          val dateTime = DateTime.parse(task.parsed_date, DateTimeFormat.forPattern("yyyy/MM/dd/HH"))
//          val sql = s"alter table $t drop  if exists  partition (year=${
//            dateTime.getYear
//          },month=${
//            dateTime.getMonthOfYear
//          },day=${
//            dateTime.getDayOfMonth
//          },hours=${
//            dateTime.getHourOfDay
//          })"
//          if (JobLock.isDropPartition) {
//            println(sql)
//            spark.sql(sql)
//          }
          val dateTime = DateTime.parse(task.parsed_date, DateTimeFormat.forPattern("yyyy/MM/dd/HH"))
          val sql = s"alter table $t drop  if exists  partition (year=${
            dateTime.getYear
          },month=${
            dateTime.getMonthOfYear
          },day=${
            dateTime.getDayOfMonth
          })"
          if (JobLock.isDropPartition) {
            println(sql)
            spark.sql(sql)
          }
        }
        case 10 => {
          val dateTime = DateTime.parse(task.parsed_date, DateTimeFormat.forPattern("yyyy/MM/dd"))
          val sql = s"alter table $t drop  if exists  partition (year=${
            dateTime.getYear
          },month=${
            dateTime.getMonthOfYear
          },day=${
            dateTime.getDayOfMonth
          })"
          if (JobLock.isDropPartition) {
            println(sql)
            spark.sql(sql)
          }
        }
        case 7 => {
          val dateTime = DateTime.parse(task.parsed_date, DateTimeFormat.forPattern("yyyy/MM"))
          val sql = s"alter table $t drop  if exists  partition (year=${
            dateTime.getYear
          },month=${
            dateTime.getMonthOfYear
          })"
          if (JobLock.isDropPartitionMonth) {
            println(sql)
            spark.sql(sql)
            JobLock.isDropPartitionMonth = false
          }
        }
        case 4 => {
          val dateTime = DateTime.parse(task.parsed_date, DateTimeFormat.forPattern("yyyy"))
          val sql = s"alter table $t drop  if exists  partition (year=${
            dateTime.getYear
          }"
          if (JobLock.isDropPartitionYear) {
            println(sql)
            spark.sql(sql)
            JobLock.isDropPartitionYear = false
          }
        }
        case _ => println("日期格式不正确");
          System.exit(1)
      }

      println("输出hive")
      res
        //        .coalesce(paraNum)
        .write
        .mode(SaveMode.Append)
        .format("Hive")
        .partitionBy(partitions: _*)
        .saveAsTable(t)
    })

    if (isPersist) res.unpersist()

    println("当前时间：" + DateTime.now().toString)
  }


  def parquet[T](spark: SparkSession, outputTable: String, partitions: Array[String], res: Dataset[T], paraNum: Int = 16) = {

    println("输出到parquet" + outputTable)

    res.distinct.coalesce(paraNum)
      .write
      .mode(SaveMode.Append)
      .partitionBy(partitions: _*)
      .parquet("/parquet/" + outputTable)

    res.unpersist()

    println("当前时间：" + DateTime.now().toString)
  }

}
