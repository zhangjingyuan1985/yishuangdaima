package com.sutpc.bigdata.job.batch.real

import com.sutpc.bigdata.executor.real._
import com.sutpc.bigdata.executor.std.VideoAi._
import com.sutpc.bigdata.executor.std.videoAIProtoBuf._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime

/**
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Description:TODO 实时标准库ETL入口 </p>
  * <p>Company: </p>
  *
  * @author zhangyongtian
  */
object Hdfs2HiveJob {

  def main(args: Array[String]): Unit = {

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
      //      .master("yarn")
      .master("local[*]")
      .appName(app)
      .config("spark.sql.parquet.writeLegacyFormat", true)
      .enableHiveSupport()
      .getOrCreate()

    //    val spark = SparkSession.builder().master("local[*]").appName(app).getOrCreate()

    inputPath.split(",").foreach(input => {

      val task = EtlTask(
        taleName,
        input.trim + "/" + date,
        outputTable,
        encode = "UTF-8",
        date
      )

      task.task_name match {

        case "s_vehicle_gps_others_real" => new RealTimeGpsOthersTask(spark, task).execute()

        case "s_vehicle_gps_taxi_real" => new RealTimeGpsTaxiExecutor(spark, task).execute()

        case "s_vehicle_gps_bus_real" => new RealTimeGpsBusExecutor(spark, task).execute()

        case "s_vehicle_gps_order_real" => new RealTimeGpsOrderExecutor(spark, task).execute()

        //视频AI 行人特征识别
        case "s_vai_pedestrian_feature" => new RealTimeVideoAiPedestrianExecutor2020(spark, task).execute()

        //视频AI 车辆特征识别
        case "s_vai_vehicle_feature" => new RealTimeVideoAiVehicleExecutor2020(spark, task).execute()

        //视频AI 车辆特征识别
        case "s_vai_primary" => new RealTimeVideoAiPrimaryExecutor2020(spark, task).execute()

        //protobuf 协议 - 1- 视频ai识别信息主表
        case "s_vai_primary_protobuf" => new VideoAiPrimary202005(spark, task).execute()

        //protobuf 协议 - 2- 骑行流量识别事件子表
        case "s_vai_flowrider_protobuf" => new VideoAiFlowRider202005(spark, task).execute()

        //protobuf 协议 - 3- 行人流量识别事件子表
        case "s_vai_flowpedestrian_protobuf" => new VideoAiFlowPedestrian202005(spark, task).execute()

        //protobuf 协议 - 4- 自行车量识别事件子表
        case "s_vai_countbicycle_protobuf" => new VideoAiCountBicycle202005(spark, task).execute()

        //protobuf 协议 - 5- 交通车辆流量识别事件子表
        case "s_vai_flowvehicle_protobuf" => new VideoAiFlowVehicle202005(spark, task).execute()

        //protobuf 协议 - 6- 公交站台人流量识别事件子表
        case "s_vai_flowbusstop_protobuf" => new VideoAiFlowBusStop202005(spark, task).execute()

        //protobuf 协议 - 7- 公交站台车辆上下车流量识别事件子表
        case "s_vai_flowbusvehicle_protobuf" => new VideoAiFlowBusVehicle202005(spark, task).execute()

        //protobuf 协议 - 8- 交通事件识别影响路段子表
        case "s_vai_flowmetro_protobuf" => new VideoAiFlowMetro202005(spark, task).execute()

        //protobuf 协议 - 9- 交通事件识别子表
        case "s_vai_accident_protobuf" => new VideoAiAccident202005(spark, task).execute()

        //protobuf 协议 - 10- 地铁出入口人流量识别事件子表
        case "s_vai_accidentlane_protobuf" => new VideoAiAccidentLane202005(spark, task).execute()

        //protobuf 协议 - 11- 行人特征识别
        case "s_vai_pedestrianfeature_protobuf" => new VideoAiPedestrianFeature202005(spark, task).execute()

        //protobuf 协议 - 12- 车辆特征识别
        case "s_vai_vehiclefeature_protobuf" => new VideoAiVehicleFeature202005(spark, task).execute()

        case _ => println("无效的任务表名")

      }
    })


    spark.stop()

    println("耗时：" + DateTime.now().minus(startTime.getMillis).getMillis / 1000 + " seconds")
  }
}



