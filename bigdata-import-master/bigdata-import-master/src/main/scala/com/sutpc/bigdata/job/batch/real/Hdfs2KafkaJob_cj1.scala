package com.sutpc.bigdata.job.batch.real

import java.text.SimpleDateFormat
import java.util.Date

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
object Hdfs2HiveJob_cj1 {

  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME", "hdfs")
    val startTime = DateTime.now()

    val app = s"${this.getClass.getSimpleName}".filter(!_.equals('$'))

    Logger.getLogger("org").setLevel(Level.ERROR)

    //TODO:参数验证
    require(args.length >= 5, s"Usage: $app " + "<taleName> <inputPath> <outputTable> <encode> <date> <batchSize>")

    var Array(taleName: String, inputPath: String, outputTable: String, encode: String, date: String, batchSize: String) = args

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

    inputPath.split(",").foreach(input => {
      var task = EtlTask(taleName, input.trim + "/" + date, outputTable, encode = "UTF-8", date)

      /**
        * 1. 死循环读取 当天 数据, 然后根据 时间戳 的 时分, 过滤出对应时间点的数据, 写入到 Kafka, 再由 Spark 进行消费处理
        *   当前时间戳:
        *     例如: 当前时间为  13:30分, 30s 一个批次, 那么就应该过滤出来的数据为: 30s-13:30+30s
        *
        * 1. s_vehicle_gps_bus：	公交车GPS	        #/data/origin/vehicle/gps/bus/440300/jiaowei    2019整年都有
        * 2. s_vehicle_gps_drive:	导航数据	          找不到目录
        * 3. s_vehicle_gps_order:	网约车GPS	        #/data/origin/vehicle/gps/order/440300/jiaowei  2019/09、2019/11比较齐全
        * 4. s_vehicle_gps_others:两客一危GPS
        * 5. s_vehicle_gps_taix:	出租车GPS(空重车)	#/data/origin/vehicle/gps/taxi/440300/jiaowei    2019年没有整月的数据
        * 6. s_vehicle_gps_truck: 货运GPS		        找不到目录
        */
      while (true){
        //根据传进来的日期获取 年、月、日
        val year = date.toInt

        if(year != 2017 && year != 2018 && year != 2019) {
          println("年份输入有误, 仅支持 2017/2018/2019三年, 请重新输入....")
          System.exit(0)
        }
        if(year == 2017){
          date = s"$year/11/01"
          println("暂不支持2017年的数据源, 请重新输入....")
          System.exit(0)
        } else if(year == 2018){
          date = s"$year/11/01"
          println("暂不支持2018年的数据源, 请重新输入....")
          System.exit(0)
        } else if(year == 2019){
          val now: Date = new Date()
          val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
          val nowDate = dateFormat.format(now)
          val day = nowDate.substring(8, 10)
          task = EtlTask(taleName, input.trim + "/" + s"$year/11/$day", outputTable, encode = "UTF-8", s"$year/11/$day")
        }
        task.task_name match {
          //两客一危GPS #
          case "s_vehicle_gps_others" => new RealTimeGpsOthersTask_cj(spark, task, batchSize).execute()
          //出租车GPS(空重车) #/data/origin/vehicle/gps/taxi/440300/jiaowei    2019年没有整月的数据
          case "s_vehicle_gps_taix" => new RealTimeGpsTaxiTask_cj(spark, task, batchSize).execute()
          case _ =>
            println("原始数据名不正确, 请重新输入....")
            System.exit(0)
        }
        Thread.sleep(batchSize.toInt * 1000)
      }
    })
    spark.stop()
    println("耗时：" + DateTime.now().minus(startTime.getMillis).getMillis / 1000 + " seconds")
  }
}
