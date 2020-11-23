package com.sutpc.bigdata.job.batch.std

import com.sutpc.bigdata.executor.std.dici.DiCiExecutor201710
import com.sutpc.bigdata.executor.std.taxi.TaxiGpsExecutor201710
import com.sutpc.bigdata.job.batch.real.EtlTask
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/3/24 16:14
  */
object Hdfs2HiveJob2 {
  def main(args: Array[String]): Unit = {

    System.setProperty("HADOOP_USER_NAME", "hdfs")

    val startTime = DateTime.now()

    val app = s"${this.getClass.getSimpleName}".filter(!_.equals('$'))

    Logger.getLogger("org").setLevel(Level.ERROR)

    //TODO:参数验证
    require(args.length >= 5, s"Usage: $app " + "<taleName> <inputPath>  <outputTable> <encode>  <date>")

    val Array(taleName: String, inputPath: String, outputTable: String, encode: String, date: String) = args

    println("参数")
    args.foreach(println)

    //TODO:spark初始化
    val spark = SparkSession
      .builder()
      .master("yarn")
//            .master("local[*]")
      .appName(app)
      .config("spark.sql.parquet.writeLegacyFormat", true)
      .enableHiveSupport()
      .getOrCreate()


    inputPath.split(",").foreach(input => {

      val task = EtlTask(
        taleName,
        input.trim + "/" + date,
        outputTable,
        encode = "UTF-8",
        date
      )

      task.task_name match {
        //地磁
        case "s_road_geomagnetic_detection" => new DiCiExecutor201710(spark, task).execute()

        //出租车GPS(空重)
        case "s_vehicle_gps_taxi" => new TaxiGpsExecutor201710(spark, task).execute()

        case _ => println("无效的任务表名")
      }
    })
    spark.stop()
    println("耗时：" + DateTime.now().minus(startTime.getMillis).getMillis / 1000 + " seconds")
  }
}
