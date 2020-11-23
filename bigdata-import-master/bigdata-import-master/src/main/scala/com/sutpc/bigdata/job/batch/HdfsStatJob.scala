package com.sutpc.bigdata.job.batch

import java.net.URI
import java.sql.Timestamp
import java.util.UUID

import com.sutpc.bigdata.jdbc.DBUrls._
import com.sutpc.bigdata.schema.psql.{HDFS_STAT_CFG, HDFS_STAT_DAILY}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.joda.time.DateTime

/**
  * <p>Description:TODO 原始库实时数据每日统计  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/8/13  12:38
  */
object HdfsStatJob {

  def main(args: Array[String]): Unit = {
    val startTime = DateTime.now()

    val app = s"${this.getClass.getSimpleName}".filter(!_.equals('$'))

    Logger.getLogger("org").setLevel(Level.ERROR)

    //TODO:参数验证
    require(args.length >= 1, s"Usage: $app " + "<date>")

    val yesterday = args(0)

    println("参数")
    args.foreach(println)


    //TODO:spark初始化
    //    val spark = SparkSession.builder().master("yarn").appName(app).getOrCreate()

    val spark = SparkSession.builder().master("local[*]").appName(app).getOrCreate()

    import spark.implicits._

    val df = spark.read.jdbc(transpass_std_url, "public.stat_bigdata_hdfs_stat_cfg", transpass_prop).as[HDFS_STAT_CFG].cache()

    //    df.show(false)

    val res = df.mapPartitions(rows => {
      val conf = new Configuration
      conf.set("HADOOP_USER_NAME", "hdfs")
      conf.set("HADOOP_ROOT_LOGGER", "WARN")
      conf.set("fs.defaultFS", "hadoop.user")

      //    conf.addResource("hdfs-site.xml")
      //    conf.addResource("core-site.xml")

      val HDFSUri: String = "hdfs://node2.sutpc.cc:8020"
      val hdfs = FileSystem.get(new URI(HDFSUri), conf, "hdfs")

      rows.map(x => {
        println(x.category + x.name)

//        val yesterDay = DateTime.now().minusDays(1).toString("yyyyMMdd")

        val date = yesterday.substring(0, 4) + "/" + yesterday.substring(4, 6) + "/" + yesterday.substring(6, 8)

//                val date = "2019/08/12"

        val cnt = dirCnt(hdfs, x.path + "/" + x.city_pinyin + "/" + date)

        HDFS_STAT_DAILY(
          UUID.randomUUID().toString.replaceAll("-", ""): String,
          x.category: String,
          x.name: String,
          x.path: String,
          x.city: String,
          x.city_pinyin: String,
          date: String,
          cnt: Long,
          new Timestamp(System.currentTimeMillis()): Timestamp,
          "zhangyongtian": String
        )
      })
    })

    res.show(false)

    res
      .write
      .mode(SaveMode.Append)
      .jdbc(transpass_std_url, "public.stat_bigdata_hdfs_stat_daily", transpass_prop)

    spark.stop()

    println("耗时：" + DateTime.now().minus(startTime.getMillis).getMillis / 1000 + " seconds")
  }


  def dirCnt(hdfs: FileSystem, dir: String): Long = {

    println("统计路径:" + dir)

    val filenamePath = new Path(dir)

    if (!hdfs.exists(filenamePath)) {
      return -1L;
    }

    // 显示实际的输出，例如这里显示 1G
    val contentSummary = hdfs.getContentSummary(filenamePath)

    //会根据集群的配置输出，例如我这里输出3G
    //println("SIZE OF THE HDFS DIRECTORY : " + hdfs.getContentSummary(filenamePath).getSpaceConsumed)

    val dir_size = contentSummary.getLength / 1024 / 1024

    //    println("子目录数：" + contentSummary.getDirectoryCount)
    //    println("子文件数：" + contentSummary.getFileCount)
    //    println("目录空间大小: " + dir_size + "MB")

    dir_size
  }

}
