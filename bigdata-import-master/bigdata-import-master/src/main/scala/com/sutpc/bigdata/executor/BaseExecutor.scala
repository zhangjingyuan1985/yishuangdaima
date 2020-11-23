package com.sutpc.bigdata.executor

import com.sutpc.bigdata.utils.CharsetUtils
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.joda.time.DateTime

/**
  * <p>Title: TagBaseExecutor</p>
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/7/17  17:48
  */
trait BaseExecutor[T] extends Serializable {

  def getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode: String = "#") = {
    println("读取【" + inputPath + "】数据")

    val sc = spark.sparkContext
    var charset = encode

    if (encode.equals("#")) {
      println("检测编码格式")
      val samples = spark.read.textFile(inputPath).filter(!_.isEmpty).take(1)
      charset = CharsetUtils.getEncode(samples(0))
    }

    println(charset)

    sc.hadoopFile(inputPath, classOf[TextInputFormat],
      classOf[LongWritable], classOf[Text])
      .map(pair => new String(pair._2.getBytes, 0, pair._2.getLength, charset))
  }

  def filter(x: String): Boolean

  def map(x: String): T

  def execute()

  def smartLoop(inputPath: String, spark: SparkSession) = {
    val path = new Path(inputPath)
    val hadoopConf = spark.sparkContext.hadoopConfiguration
    val hdfs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)

    var flag = false

    if (hdfs.exists(path)) {

      val stats = hdfs.listStatus(path)
      //      val arr = hdfs.listFiles(path, false)

      //      import collection.JavaConversions._
      //      import collection.convert.wrapAsScala._ //只是完成 Java  到 Scala 集合的隐式转换

      for (i <- 0 to stats.length - 1) {
        if (stats(i).isFile) {
          flag = false
        } else if (stats(i).isDirectory) {
          flag = true
        } else if (stats(i).isSymlink) {
          flag = false
        }
      }


      //全是目录
      if (flag) {
        for (i <- 0 to stats.length - 1) {
          val e = stats(i)
          println("循环读取子目录：" + e.getPath.toString)
          process(e.getPath.toString)
        }
      }
    }

    if (!flag) {
      println("读取单个目录")
      process(inputPath)
    }

  }

  def process(dir: String)

  def cache(res: Dataset[T]): Unit = {
    res.persist(StorageLevel.MEMORY_AND_DISK_SER)
    println("输入数据量【" + res.count() + " 】")
    res.distinct.show(false)
  }

  def outputHBase(zkUrl: String, outputTable: String, res: Dataset[T]) = {

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


  def outputHiveStd(spark: SparkSession, outputTable: String, partitions: Array[String], res: Dataset[T], paraNum: Int = 16, isPersist: Boolean = true, db: String = "transpaas_440300_std", isCopy2Tag: Boolean = false, tag_table: String = "#", dupliCols: Seq[String]) = {


    //开启动态分区
    spark.conf.set("spark.sql.sources.partitionOverwriteMode", "dynamic")
    spark.sql("set hive.exec.dynamic.partition.mode=nonstrict")
    spark.sql("set hive.exec.max.dynamic.partitions=90000")

    spark.sql(s"use $db")

    //hive强制分桶
//    spark.sql("set hive.enforce.bucketing=true")
//    spark.sqlContext.setConf("spark.sql.hive.convertMetastoreParquet", "false")

    println("输出到hive的表" + outputTable)

    //.dropDuplicates(dupliCols) distinct
    res.dropDuplicates(dupliCols)
      //      .coalesce(paraNum)
      .write
      .mode(SaveMode.Append)
      .format("Hive")
      .partitionBy(partitions: _*)
      .saveAsTable(outputTable)

    spark.sql(s"select * from $outputTable limit 20")

    if (isCopy2Tag) {

      spark.sql(s"use transpass_tag")

      var tag_tbl = "t_" + outputTable.substring(2)

      if (!tag_table.equals("#")) {
        tag_tbl = tag_table
      }


      println("输出到hive的表" + tag_tbl)

      //dropDuplicates(dupliCols). distinct
      res.dropDuplicates(dupliCols)
        //        .coalesce(paraNum)
        .write
        .mode(SaveMode.Append)
        .format("Hive")
        .partitionBy(partitions: _*)
        .saveAsTable(tag_tbl)

      spark.sql(s"select * from $tag_tbl limit 20")
    }

    if (isPersist) res.unpersist()

    println("当前时间：" + DateTime.now().toString)
  }


  def outputHiveTag(spark: SparkSession, outputTable: String, partitions: Array[String], res: Dataset[T], paraNum: Int = 16, isPersist: Boolean = true, dupliCols: Seq[String]) = {

    outputHiveStd(spark: SparkSession, outputTable: String, partitions: Array[String], res: Dataset[T], paraNum, isPersist, db = "transpass_tag", dupliCols = dupliCols)

  }


  def outputParquet(spark: SparkSession, outputTable: String, partitions: Array[String], res: Dataset[T], paraNum: Int = 16) = {

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
