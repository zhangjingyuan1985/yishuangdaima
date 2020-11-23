package com.sutpc.bigdata.tool

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/8/7  14:25
  */
object FileTool {

  def main(args: Array[String]): Unit = {

    val startTime = DateTime.now()

    val app = s"${this.getClass.getSimpleName}".filter(!_.equals('$'))

    Logger.getLogger("org").setLevel(Level.ERROR)

    //TODO:spark初始化
    //    val spark = SparkSession.builder().master("yarn").appName(app).getOrCreate()
    val spark = SparkSession.builder().master("local[*]").appName(app).getOrCreate()
    spark.conf.set("spark.sql.inMemoryColumnarStorage.batchSize", "10000")

    import spark.implicits._

    //    spark.read.parquet("data-meta/index_metadata1*").show(false)


    val df = spark.read.textFile("data/gps_order_20190807_11.1565149018964.log")

    df.cache()
    println("数据量：" + df.count())

    val df2 = df.filter(_.startsWith("2019"))

    df2.cache()
    println("数据量：" + df2.count())

    df2.coalesce(1).write.text("data/order")

    spark.stop()


  }
}
