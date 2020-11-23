package com.sutpc.bigdata.job.batch.tag

import com.sutpc.bigdata.executor.tag.phoenix._
import com.sutpc.bigdata.jdbc.DimSource
import com.sutpc.bigdata.utils.StdUtils
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime

/**
  * <p>Title: Hdfs2PhoenixJob</p>
  * <p>Description:TODO 清洗原始数据到Hbase指标库 </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  *         date 2019/7/15  20:46
  * @version V1.0
  */
object Hdfs2PhoenixJob {

  def main(args: Array[String]): Unit = {

    val startTime = DateTime.now()

    val app = s"${this.getClass.getSimpleName}".filter(!_.equals('$'))

    Logger.getLogger("org").setLevel(Level.ERROR)

    //TODO:参数验证
    require(args.length >= 2, s"Usage: $app " + "zkUrl<ip:port>  <inputPath>  <outputTable:t_transit_ic> <encode>")
    val zkUrl = args(0)
    val inputPath = args(1)
    val outputTable = args(2)
    //中文编码方式 GBK或UTF-8
    var encode = "#"
    if (args.length == 4) encode = args(3)

    println("参数")
    args.foreach(println)


    //TODO:spark初始化
    val spark = SparkSession.builder().master("yarn")
      .config("spark.sql.parquet.writeLegacyFormat", true)
      .enableHiveSupport()
      .appName(app).getOrCreate()
    //    val spark = SparkSession.builder().master("local[*]").appName(app).getOrCreate()

    //    val tagExecutor = new TagExecutor(inputPath, zkUrl, spark, outputTable)


    println("注册广播变量")
    lazy val psource = new DimSource(spark)

    lazy val cityPinyinCodeMapBR = spark.sparkContext.broadcast(psource.getCityPinyinCodeMap())

    lazy val map = psource.getCityVheadMap()
    lazy val cityVheadMapBR = spark.sparkContext.broadcast(map)

    lazy val gridCityCuMapBR = spark.sparkContext.broadcast(psource.getCuGridCityMap())

    lazy val gridCityCmMapBR = spark.sparkContext.broadcast(psource.getCmGridCityMap())

    outputTable match {

      case "local_t_vehicle_gps_taxi" => new TagGpsTaxiExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "t_vehicle_gps_taxi" => Range(1, 32).foreach(date => new TagGpsTaxiExecutor(inputPath + "/" + StdUtils.fillZero(2, date.toString), zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute())


      case "t_vehicle_gps_bus" => Range(1, 32).foreach(date => new TagGpsBusExecutor(inputPath + "/" + StdUtils.fillZero(2, date.toString), zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute())
      case "local_t_vehicle_gps_bus" => new TagGpsBusExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "t_transit_people_ic" => new TagICExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, cityVheadMapBR, encode).execute()

      case "t_weather_grid_his" => new TagWeatherGridExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "t_phone_home_distribute" => new TagHomeDistExecutor(inputPath, zkUrl, spark, outputTable, gridCityCuMapBR, encode).execute()

      case "t_phone_work_distribute" => new TagWorkDistExecutor(inputPath, zkUrl, spark, outputTable, gridCityCuMapBR, encode).execute()

      case "t_phone_home_work_rela" => new TagHomeWorkDistExecutor(inputPath, zkUrl, spark, outputTable, gridCityCuMapBR, encode).execute()

      case "t_phone_trip_distribute" => new TagTripDistExecutor(inputPath, zkUrl, spark, outputTable, gridCityCuMapBR, encode).execute()

      case "t_phone_home_distribute_cm" => new TagHomeDistCMExecutor(inputPath, zkUrl, spark, outputTable, gridCityCmMapBR, encode).execute()

      case "t_phone_work_distribute_cm" => new TagWorkDistCMExecutor(inputPath, zkUrl, spark, outputTable, gridCityCmMapBR, encode).execute()

      case "t_phone_home_work_rela_cm" => new TagHomeWorkDistCMExecutor(inputPath, zkUrl, spark, outputTable, gridCityCmMapBR, encode).execute()

      case "t_phone_trip_distribute_cm" => new TagTripDistCMExecutor(inputPath, zkUrl, spark, outputTable, gridCityCmMapBR, encode).execute()

      case "t_phone_activity" => new TagActivityExecutor(inputPath, zkUrl, spark, outputTable, gridCityCuMapBR, encode).execute()

      case "t_phone_activity_cm" => new TagActivityCMExecutor(inputPath, zkUrl, spark, outputTable, gridCityCuMapBR, encode).execute()

      case "local_vehicle_gps_truck" => new TagGpsTruckExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "t_vehicle_gps_truck" => Range(1, 32).foreach(date => new TagGpsTruckExecutor(inputPath + "/" + StdUtils.fillZero(2, date.toString), zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute())


      case _ => println("找不到目标表")
    }


    spark.stop()

    println("耗时：" + DateTime.now().minus(startTime.getMillis).getMillis / 1000 + " seconds")
  }


  //  private def batchGpsTaxi(zkUrl: String, inputPath: String, outputTable: String, spark: SparkSession) = {
  //    val path = new Path(inputPath)
  //    val hadoopConf = spark.sparkContext.hadoopConfiguration
  //    val hdfs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)
  //    if (hdfs.exists(path)) {
  //      val arr = hdfs.listFiles(path, false)
  //      while (arr.hasNext) {
  //        val dir = arr.next().getPath.getName
  //        TagExecutor.processGpsTaxi(dir, zkUrl, spark, outputTable)
  //      }
  //    }
  //  }
}

