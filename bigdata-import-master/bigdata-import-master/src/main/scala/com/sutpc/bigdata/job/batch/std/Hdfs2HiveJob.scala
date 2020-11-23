package com.sutpc.bigdata.job.batch.std

import com.sutpc.bigdata.common.Constants
import com.sutpc.bigdata.executor.std._
import com.sutpc.bigdata.jdbc.DimSource
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
object Hdfs2HiveJob {

  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME", "hdfs")
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
    val spark = SparkSession
      .builder()
      .master("yarn")
//      .master("local[*]")
      .appName(app)
      .config("spark.sql.parquet.writeLegacyFormat", true)
      .enableHiveSupport()
      .getOrCreate()

    //    val spark = SparkSession.builder().master("local[*]").appName(app).getOrCreate()

    println("注册广播变量")
    lazy val psource = new DimSource(spark)

    lazy val cityPinyinCodeMapBR = spark.sparkContext.broadcast(psource.getCityPinyinCodeMap())

    lazy val map = psource.getCityVheadMap()
    lazy val cityVheadMapBR = spark.sparkContext.broadcast(map)

    lazy val gridCityCuMapBR = spark.sparkContext.broadcast(psource.getCuGridCityMap())

    lazy val gridCityCmMapBR = spark.sparkContext.broadcast(psource.getCmGridCityMap())

    //读取mysql 配置


    outputTable match {

      case "s_vehicle_gps_bus" => new StdGpsBusExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "s_vehicle_gps_taxi" => new StdGpsTaxiExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "s_vehicle_gps_taxi_jiaowei" => new StdGpsTaxiJiaoweiExecutor(inputPath, zkUrl, spark, "s_vehicle_gps_taxi", cityPinyinCodeMapBR, encode).execute()

      case "s_vehicle_gps_drive" => new StdGpsDriveBaiduExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "s_vehicle_gps_drive_careland" => new StdGpsDriveCarelandExecutor(inputPath, zkUrl, spark, "s_vehicle_gps_drive", cityPinyinCodeMapBR, encode).execute()

      case "s_vehicle_gps_order" => new StdGpsOrderExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "s_vehicle_gps_truck" => new StdGpsTruckExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "s_vehicle_gps_others" => inputPath.split(",").foreach(p => {
        new StdGpsOthersExecutor(p, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode, {
          var r = -99
          Constants.VEHICLE_TYPE2.keySet.foreach(k => {
            if (p.contains(k)) {
              r = Constants.VEHICLE_TYPE2.get(k).get
            }
          })
          if (r == -99) System.exit(1)
          r
        }).execute()
      })

      case "s_transit_people_ic" => new StdGpsICExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, cityVheadMapBR, encode).execute()

      case "s_bike_switch_lock" => new StdBikeSwitchExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "t_bike_switch_lock" => new StdBikeSwitchExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "s_bike_loc" => new StdBikeLocExecutor(inputPath, zkUrl, spark, outputTable, cityPinyinCodeMapBR, encode).execute()

      case "s_phone_inter_activity" => new StdActivityCuExecutor(inputPath, zkUrl, spark, outputTable, gridCityCuMapBR, encode).execute()

      case "s_phone_inter_activity_cm" => new StdActivityCMExecutor(inputPath, zkUrl, spark, "s_phone_inter_activity", gridCityCmMapBR, encode).execute()

      case "s_road_plate_recognition2018" => {

        for (i <- Range(1, 27)) {
          val a = (96 + i).toChar
          for (j <- Range(1, 27)) {
            val b = (96 + j).toChar
            val c = a + "" + b
            new StdRecognitionExecutor2018(inputPath + "/0" + c + "*", zkUrl, spark, "s_road_plate_recognition", cityPinyinCodeMapBR, encode).execute()
          }

        }
      }

      case "s_road_plate_recognition2019" => new StdRecognitionExecutor2019(inputPath, zkUrl, spark, "s_road_plate_recognition", cityPinyinCodeMapBR, encode).execute()


      case "s_phone_inter_resident_cu" => new StdResidentCuExecutor(inputPath, zkUrl, spark, "s_phone_inter_resident", gridCityCuMapBR, encode).execute()

      case "s_phone_inter_tourist_cu" => new StdTouristCuExecutor(inputPath, zkUrl, spark, "s_phone_inter_tourist", gridCityCuMapBR, encode).execute()

      case "s_phone_inter_resident_cm" => new StdResidentCMExecutor(inputPath, zkUrl, spark, "s_phone_inter_resident", gridCityCmMapBR, encode).execute()

      case "s_phone_inter_tourist_cm" => new StdTouristCMExecutor(inputPath, zkUrl, spark, "s_phone_inter_tourist", gridCityCmMapBR, encode).execute()

      case "s_phone_inter_work_cu" => {
        new StdWorkDistCuExecutor(inputPath, zkUrl, spark, "s_phone_inter_work", gridCityCuMapBR, encode).execute()
      }

      case "s_phone_inter_work_cm" => {
        new StdWorkDistCmExecutor(inputPath, zkUrl, spark, "s_phone_inter_work", gridCityCuMapBR, encode).execute()
      }

      case "s_phone_inter_home_work_cu" => new StdHomeWorkDistCuExecutor(inputPath, zkUrl, spark, "s_phone_inter_home_work", gridCityCuMapBR, encode).execute()
      case "s_phone_inter_home_work_cm" => new StdHomeWorkDistCMExecutor(inputPath, zkUrl, spark, "s_phone_inter_home_work", gridCityCuMapBR, encode).execute()

      case "s_phone_inter_trip_cu" => new StdTripDistCuExecutor(inputPath, zkUrl, spark, "s_phone_inter_trip", gridCityCuMapBR, encode).execute()

      case "s_phone_inter_trip_cm" => new StdTripDistCMExecutor(inputPath, zkUrl, spark, "s_phone_inter_trip", gridCityCmMapBR, encode).execute()



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

