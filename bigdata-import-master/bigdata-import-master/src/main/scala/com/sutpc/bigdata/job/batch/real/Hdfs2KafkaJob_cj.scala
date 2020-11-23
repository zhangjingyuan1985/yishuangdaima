package com.sutpc.bigdata.job.batch.real

import java.text.SimpleDateFormat
import java.util.Date

import com.sutpc.bigdata.schema.hive.{STD_GPS_ORDER, STD_GPS_OTHER, s_vehicle_gps_taxi}
import com.sutpc.bigdata.utils.{SparkKafkaUtil, StdUtils, TimeTransform}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.math.BigDecimal.RoundingMode

/**
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Description:TODO 川警项目模拟实时标准库ETL入口 </p>
  * <p>Company: </p>
  *
  * @author zhangyongtian
  */
object Hdfs2HiveJob_cj {

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

    val sparkConf = new SparkConf()
      .setAppName(s"${taleName}-APP")
      .setMaster("yarn")
//      .setMaster("local[*]")
      .set("spark.sql.parquet.writeLegacyFormat", "true")
      .set("spark.app.name", s"${taleName}-APP")
      .set("spark.sql.parquet.writeLegacyFormat", "true")
      .set("spark.shuffle.consolidateFiles", "true")
      .set("spark.network.timeout", "600")
      .set("spark.streaming.kafka.consumer.poll.ms", "60000")
      .set("spark.core.connection.ack.wait.timeout", "900")
      .set("spark.akka.timeout", "900")
      .set("spark.streaming.backpressure.enabled", "true")
      .set("spark.rpc.message.maxSize", "1000")
      .set("spark.streaming.receiver.maxRate", "1000")
      .set("spark.streaming.backpressure.initialRate", "2000")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryoserializer.buffer.max", "128m")

    val spark = SparkSession
      .builder()
      .enableHiveSupport()
      .config(sparkConf)
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
          //出租车GPS(空重车) #/data/origin/vehicle/gps/taxi/440300/jiaowei
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

//两客一危清洗写入 Kafka
class RealTimeGpsOthersTask_cj(spark: SparkSession, task: EtlTask, batch: String) extends BaseTask[STD_GPS_OTHER] {
  def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 3))
    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val c2 = dataTime.getMillis <= DateTime.now().getMillis
    val c3 = arr(3).length > 2
    lazy val today = DateTime.parse(task.input.substring(task.input.lastIndexOf("/") - 7), DateTimeFormat.forPattern("yyyy/MM/dd")).plusDays(1)
    val c4 = dataTime.getMillis >= today.minusDays(1).getMillis && dataTime.getMillis < today.getMillis
    c1 && !x.contains("grid_id") && c2 && c3 && c4
  }

  def map(x: String, dir: String) = {
    val arr = x.split(",")

    //20170930,235953,H,粤BT2046,113.902152,34.702252,0.0,1,1,1,2,0,837601,0,2,0
    //20180930,235857,H,粤BW7325,114.242953,22.598443,0.0,87,1,1,2,0,93526,0,3,134217728,4


    //20171001, 23,      H,          粤BX3340, 113.885809,  22.503325, 0.0,  0,     1,       1,         2,       0,      9390700,13,     786434,  0
    //定位日期,  定位时刻,所属公司代码, 车牌号码,  经度，       纬度，      速度，方向角，运营状态，数据可用性，车辆颜色，行车速度 总里程数 海拔高度 车辆状态 报警状态 车辆类型

    //    loc_time	定位日期	整型	10	否		unix10位时间戳
    //    vehicle_id	车辆唯一标识（车牌）	字符串	20	否
    //      lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    //    lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    //    speed	瞬时速度（km/h）	浮点	4,1	否
    //      angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否

    //    is_validate	数据是否有效，	整型	1	否		0表示异常，1表示有效
    val is_validate = arr(9).toInt

    //      alarm_status	报警状态	字符串		是		存储原始数据，保留以后使用
    val alarm_status = arr(15)

    //    vehicle_status	车辆状态	字符串		是		存储原始数据，保留以后使用
    val vehicle_status = arr(14)

    val date: String = arr(0)
    val time_tmp = date + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val loc_time = dataTime.getMillis / 1000

    //fdate	日期	整型		否		20190708
    val fdate = date.toInt

    //ftime	时间	字符串		否		190802
    val ftime = dataTime.toString("HHmmss")

    //vehicle_id	车辆唯一标识（车牌）	字符串		否
    val vehicle_id = arr(3)

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(4)).setScale(6, RoundingMode.HALF_UP)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5)).setScale(6, RoundingMode.HALF_UP)

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = BigDecimal(arr(5)).setScale(1, RoundingMode.HALF_UP)

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = arr(7).toInt

    //operation_status	运营状态，出租车预留字段	整型	1 	否		1重载，0空载
    val operation_status = arr(8).toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = arr(2)

    //altitude	海拔高度，单位m	整型		否
    val altitude = arr(13).toInt

    //vehicle_color	车辆颜色	整型	1	是		1-蓝；2-黄；3-黑；4-白；5-其他
    val vehicle_color = arr(10).toInt

    //total_mileage	车辆总里程数，单位千米	整型	10	是
    val total_mileage = arr(12).toLong

    //city	城市名称	字符串		否	1	参照城市编码对照表
    // 深圳
    val city = "440300".toInt

    //    0-导航小汽车；1-班车客运；2-出租车，4-货运车；8-驾培车；16-包车客运；32-公交车；64-危险品车；128-其他车辆；256-泥头车；512-网约车
    var vehicle_type = -99
    if(arr.length == 16) {
      if(dir.contains("charterbus")){//包车客运
        vehicle_type = 16
      } else if(dir.contains("coachbus")){ //班车客运
        vehicle_type = 1
      } else if(dir.contains("danger")){ //危险品运输
        vehicle_type = 64
      } else if(dir.contains("driving")){ //驾培
        vehicle_type = 8
      } else if(dir.contains("dumper")){ //泥头
        vehicle_type = 256
      } else if(dir.contains("freight")){ //货运
        vehicle_type = 4
      } else if(dir.contains("others")){ //其他
        vehicle_type = 128
      }
    } else {
      vehicle_type = arr(16).toInt
    }

    val year: String = arr(0).substring(0, 4)
    val month: String = arr(0).substring(4, 6)
    val day: String = arr(0).substring(6, 8)

    val license_prefix = vehicle_id.substring(0, 2)

    STD_GPS_OTHER(
      loc_time: Long,
      vehicle_id: String,
      lng: BigDecimal,
      lat: BigDecimal,
      speed: BigDecimal,
      angle: Int,
      is_validate: Int,
      vehicle_color: Int,
      total_mileage: Long,
      altitude: Int,
      company_code: String,
      alarm_status: String,
      vehicle_status: String,
      vehicle_type: Int,
      city: Int,
      year: String,
      month: String,
      day: String,
      license_prefix: String
    )

  }

  def execute() = {
    println("读取【" + task.input + "】数据")
    smartLoop(task.input: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask)

    val now: Date = new Date()
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = dateFormat.format(now)
    val year = date.substring(0, 4)
    val month = date.substring(4, 6)
    val day = date.substring(6, 8)
    val hours = date.substring(8, 10)

    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => {
        val arr = x.split(",")
        arr.length >= 16
      })
      .filter(x => filter(x))
      .map(x => map(x, dir))
      .filter(_.speed < 900)
      .filter(x => {
        val nowUnixDate: Int = TimeTransform.time2UnixTime - batch.toInt //当前时间戳 - 60s
        val nowStrDate: String = TimeTransform.unixTime2DateFormat(nowUnixDate+"", "HH:mm:ss") //当前时间
        val hh = nowStrDate.substring(0, 2).toInt
        val mm = nowStrDate.substring(3, 5).toInt
        val ss = nowStrDate.substring(6, 8).toInt
        val nowateLow = hh * 60 * 60 + mm * 60 + ss

        val nowUnixDate1: Int = TimeTransform.time2UnixTime + batch.toInt //当前时间戳 - 60s
        val nowStrDate1: String = TimeTransform.unixTime2DateFormat(nowUnixDate1+"", "HH:mm:ss") //当前时间
        val hh1 = nowStrDate1.substring(0, 2).toInt
        val mm1 = nowStrDate1.substring(3, 5).toInt
        val ss1 = nowStrDate1.substring(6, 8).toInt
        val nowDateUp = hh1 * 60 * 60 + mm1 * 60 + ss1


        val nowStrDate2: String = TimeTransform.unixTime2DateFormat(x.loc_time+"", "HH:mm:ss") //日志时间
        val hh2 = nowStrDate2.substring(0, 2).toInt
        val mm2 = nowStrDate2.substring(3, 5).toInt
        val ss2 = nowStrDate2.substring(6, 8).toInt
        val logDate = hh2 * 60 * 60 + mm2 * 60 + ss2
        logDate >= nowateLow && logDate <= nowDateUp
      })
      .map(x => {
          val str = (x.loc_time + "000").toLong
          s"""[{"altitude":${x.altitude},"color":${x.vehicle_color},"direction":${x.angle},"driveSpeed":${x.speed},"eff":-99,"encrypted":false,"id":"${x.vehicle_id}","latitude":${x.lat},"locationTime":"${new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(str)}","longitude":${x.lng},"recvtime":"${date}","run":-99,"satelliteSpeed":0,"totalMileage":${x.total_mileage},"vehicle_type":${x.vehicle_type}}]""".stripMargin
      })

    new SparkKafkaUtil("10.5.10.18:9092,10.5.10.19:9092,10.5.10.20:9092",
      "T_SUTPC_SV_TOPIC_GIS")
      .output[String](spark, res)
    JobLock.isDropPartition = false
  }
}

////公交车GPS清洗写入 Kafka
//class RealTimeGpsBusTask_cj(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_BUS] {
//
//  def filter(x: String): Boolean = {
//    val arr = x.split(",")
//    val c1 = StdUtils.allNotEmpty(arr.slice(2, 6)) && arr.length == 13
//
//    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
//    //loc_time	定位日期	整型	10	否		unix10位时间戳
//    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
//
//    //    val c3 = dataTime.getMillis <= DateTime.now().getMillis
//
//    val today = DateTime.parse(task.input.substring(task.input.lastIndexOf("/") - 7), DateTimeFormat.forPattern("yyyy/MM/dd")).plusDays(1)
//
//    c1 && dataTime.getMillis >= today.minusDays(1).getMillis && dataTime.getMillis < today.getMillis
//
//  }
//
//  def map(x: String, routeBC: Broadcast[Map[String, String]], vnoBC: Broadcast[Map[String, String]]) = {
//    //    20191118,235958,H,53075,113.92556,22.669336,0,0,1,1,5668,0,20191118 23:59:58
//
//    val arr = x.split(",")
//
//    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
//
//    //loc_time	定位日期/**/	整型	10	否		unix10位时间戳
//    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
//    val loc_time = dataTime.getMillis / 1000
//
//    //vehicle_id	车辆唯一标识（车牌）	字符串		否
//    val vehicle_id = vnoBC.value.getOrElse(arr(3).trim, "-99")
//
//    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
//    val lng = BigDecimal(arr(4).trim).setScale(6)
//
//    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
//    val lat = BigDecimal(arr(5).trim).setScale(6)
//
//    //speed	瞬时速度（km/h）	浮点	4,1	否
//    val speed = BigDecimal(arr(6).trim).setScale(1)
//
//    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
//    val angle = arr(7).trim.toInt
//
//    //company_code	所属公司代码	字符串		否		公司代码编码
//    val company_code = arr(2)
//
//    //busline_name	公交线路名称	字符串		否
//    val busline_name = routeBC.value.getOrElse(arr(10).trim, "-99")
//
//    // busline_dir	方向 	整型		是		0为上行，1为下行，2偏离路线，3为上行场区，4为下行场区
//    val busline_dir = null
//
//    //bus_station_order	站序	整型		是
//    val bus_station_order = null
//
//    //city	城市名称	字符串		否	1	参照城市编码对照表
//    val city_name = "shenzhen"
//
//    //深圳
//    val city = 440300
//    //    val city = "-99".toInt
//
//    val is_validate = arr(9).toInt
//
//    val bus_position = null
//
//    val alarm_status: String = null
//    val vehicle_status: String = null
//
//    val year = arr(0).substring(0, 4).toInt
//    val month = arr(0).substring(4, 6).toInt
//    val day = arr(0).substring(6, 8).toInt
//
//    STD_GPS_BUS(
//      loc_time: Long,
//      vehicle_id: String,
//      lng: BigDecimal,
//      lat: BigDecimal,
//      speed: BigDecimal,
//      angle: Int,
//      is_validate: Int,
//      company_code: String,
//      busline_name: String,
//      bus_position: Integer,
//      busline_dir: Integer,
//      bus_station_order: Integer,
//      alarm_status: String,
//      vehicle_status: String,
//      city: Int,
//      year: Int,
//      month: Int,
//      day: Int
//    )
//  }
//
//  def execute() = {
//
//    smartLoop(task.input: String, spark: SparkSession)
//  }
//
//  def process(dir: String) = {
//    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask)
//    val now: Date = new Date()
//    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//    val date = dateFormat.format(now)
//    val year = date.substring(0, 4)
//    val month = date.substring(4, 6)
//    val day = date.substring(6, 8)
//    val hours = date.substring(8, 10)
//
//    import spark.implicits._
//    val dimSource = new DimSource(spark)
//    val routeBC = spark.sparkContext.broadcast(dimSource.getRouteId2NameMap())
//    val vnoBC = spark.sparkContext.broadcast(dimSource.getVId2VnoMap())
//
//    val res = spark.createDataset(rdd).as[String]
//      .filter(x => {
//        val strings: Array[String] = x.split(",")
//        strings.length == 13
//      })
//      .filter(x => filter(x))
//      .map(x => map(x, routeBC, vnoBC))
//      .filter(x =>
//        x.vehicle_id.nonEmpty && x.lng != null && x.lat != null && x.speed < 900
//      )
//      .map((x: STD_GPS_BUS) => {
//        val str = (x.loc_time + "000").toLong
//        s"""[{"altitude":-99,"color":-99,"direction":${x.angle},"driveSpeed":${x.speed},"eff":-99,"encrypted":false,"id":"${x.vehicle_id}","latitude":${x.lat},"locationTime":"${new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(str)}","longitude":${x.lng},"recvtime":"${date}","run":-99,"satelliteSpeed":-99,"totalMileage":-99,"vehicle_type":32}]""".stripMargin
//      })
//
//    println("过滤后还剩: "+res.count())
//    res.show(10,false)
//
////    new SparkKafkaUtil("10.3.4.41:9092,10.3.4.42:9092,10.3.4.43:9092",
////      "T_SUTPC_SV_TOPIC_GIS")
////      .output[String](spark, res)
//  }
//}
//
//class RealTimeGpsOrderTask_cj(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER] {
//  def filter(x: String): Boolean = {
//    val arr = x.split(",")
//    val c1 = StdUtils.allNotEmpty(arr.slice(0, 7)) && arr.length == 17
//
//    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
//    //loc_time	定位日期	整型	10	否		unix10位时间戳
//    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
//
//    val today = DateTime.parse(task.input.substring(task.input.lastIndexOf("/") - 7), DateTimeFormat.forPattern("yyyy/MM/dd")).plusDays(1)
//
//    c1 //&& dataTime.getMillis >= today.minusDays(1).getMillis && dataTime.getMillis < today.getMillis
//
//  }
//
//  def map(x: String) = {
//    val arr = x.replaceAll("null", "-99").split(",")
//
//
//    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
//
//    //loc_time	定位日期	整型	10	否		unix10位时间戳
//    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
//    val loc_time = dataTime.getMillis / 1000
//
//    //vehicle_id	车辆唯一标识（车牌）	字符串		否
//    val vehicle_id = if (arr(3).startsWith("B")) "粤" + arr(3) else arr(3)
//
//    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
//    val lng = BigDecimal(arr(4)).setScale(6, RoundingMode.HALF_UP)
//
//    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
//    val lat = BigDecimal(arr(5)).setScale(6, RoundingMode.HALF_UP)
//
//    //speed	瞬时速度（km/h）	浮点	4,1	否
//    val speed = BigDecimal(arr(6)).setScale(2, RoundingMode.HALF_UP)
//
//    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
//    val angle = if (arr(7).isEmpty) -99 else arr(7).toInt
//
//    //company_code	所属公司代码	字符串		否		公司代码编码
//    val company_code = arr(2)
//
//
//    //operation_status	营运状态	整型	1 	否		1-载客，2-接单，3-空驶，4-停运
//    val operation_status = arr(8).toInt
//
//    //order_id	订单id	字符串	20	否		订单编号
//    val order_id = arr(16)
//
//    //altitude	海拔高度，单位m	整型	2	否
//    val altitude = if (arr(13).isEmpty) -99 else arr(13).toDouble.toInt
//
//    //position_type	位置信息类型	字符串	10	否		JYSX-经营上线、JYXX经营下线、CKSC乘客上车、CKXC-乘客下车、DDJD-订单派单
//    val position_type = arr(15)
//
//
//    //city	城市名称	字符串		否	1	参照城市编码对照表
//    val city_name = "shenzhen"
//
//    //深圳
//    val city = 440300
//
//    val year: String = arr(0).substring(0, 4)
//    val month: String = arr(0).substring(4, 6)
//    val day: String = arr(0).substring(6, 8)
//
//    STD_GPS_ORDER(
//      loc_time: Long,
//      vehicle_id: String,
//      lng: BigDecimal,
//      lat: BigDecimal,
//      speed: BigDecimal,
//      angle: Int,
//      operation_status: Int,
//      position_type: String,
//      order_id: String,
//      altitude: Int,
//      company_code: String,
//      city: Int,
//      year: String,
//      month: String,
//      day: String
//    )
//  }
//
//  def execute() = {
//    println("读取【" + task.input + "】数据")
//    println("数据格式：定位日期 定位时刻 用户ID 导航状态 经度 纬度 瞬时速度 方位角")
//    println("20170930,235958,M338,粤BN6601,114.029805,22.670012,37,135,1,1")
//    smartLoop(task.input: String, spark: SparkSession)
//  }
//
//  def process(dir: String) = {
//    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask)
//    import spark.implicits._
//    val res = spark.createDataset(rdd).as[String]
//      .filter(x => {
//        x.split(",").length == 17
//      })
//      .filter(x => filter(x))
//      .map(x => map(x)).filter(_.speed < 900)
//
//    //    cache(res)
//
//    TaskOutput.hive[STD_GPS_ORDER](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_id", "loc_time"), isCopy2Tag = true)
//
//  }
//}

//出租车(空重车)清洗写入kafka
class RealTimeGpsTaxiTask_cj(spark: SparkSession, task: EtlTask, batch: String) extends BaseTask[STD_GPS_ORDER]   {
  override def filter(x: String): Boolean = {
    val strs = x.split(",")
    strs.length match {
      case 10=>
        val speed = strs(6).toDouble.toLong
        val lng = strs(4).toDouble
        val lat = strs(5).toDouble
        speed>=0&&speed<900//&&StdUtils.isCnLngLat(lng,lat)
      case _=>false
    }
  }

  override def execute(): Unit = {
    println("读取【" + task.input + "】数据")
    smartLoop(task.input: String, spark: SparkSession)
  }
  def  map(row:String): s_vehicle_gps_taxi ={
    val strs = row.split(",")
    val vehNo = strs(3)
    val date = strs(0)
    var time = strs(1)
    val companyCode = strs(2)
    while (time.length < 6) {
      time = "0" + time
    }
    val loctime = TimeTransform.time2UnixTime2(date + time)
    val lng = strs(4).toDouble
    val lat = strs(5).toDouble
    val speed = strs(6).toDouble.toLong
    val angle = strs(7).toInt
    val operation_status = strs(8).toInt
    val isAvailable = strs(9).toInt
    val city = 440300
    val year = date.substring(0, 4).toInt
    val month = date.substring(4, 6).toInt
    val day = date.substring(6, 8).toInt
    val altitude = -99
    val vehicle_color = -99
    val total_mileage = -99
    val alarm_status = "-99"
    val vehicle_staus = "-99"
    s_vehicle_gps_taxi(
      vehNo,loctime,lng,lat,speed,angle,operation_status,isAvailable,
      companyCode,altitude,vehicle_color,total_mileage,alarm_status,vehicle_staus,
      city,year,month,day
    )
  }
  override def process(dir: String): Unit = {
    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask).repartition(100)
    val now: Date = new Date()
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = dateFormat.format(now)
    val year = date.substring(0, 4)
    val month = date.substring(4, 6)
    val day = date.substring(6, 8)
    val hours = date.substring(8, 10)

    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        val nowUnixDate: Int = TimeTransform.time2UnixTime - batch.toInt //当前时间戳 - 60s
        val nowStrDate: String = TimeTransform.unixTime2DateFormat(nowUnixDate+"", "HH:mm:ss") //当前时间
        val nowday: String = TimeTransform.unixTime2DateFormat(nowUnixDate+"", "dd") //当前时间
        val hh = nowStrDate.substring(0, 2).toInt
        val mm = nowStrDate.substring(3, 5).toInt
        val ss = nowStrDate.substring(6, 8).toInt
        val nowateLow = hh * 60 * 60 + mm * 60 + ss

        val nowUnixDate1: Int = TimeTransform.time2UnixTime + batch.toInt //当前时间戳
        val nowStrDate1: String = TimeTransform.unixTime2DateFormat(nowUnixDate1+"", "HH:mm:ss") //当前时间
        val hh1 = nowStrDate1.substring(0, 2).toInt
        val mm1 = nowStrDate1.substring(3, 5).toInt
        val ss1 = nowStrDate1.substring(6, 8).toInt
        val nowDateUp = hh1 * 60 * 60 + mm1 * 60 + ss1

        val logday: String = TimeTransform.unixTime2DateFormat(x.loc_time+"", "dd")
        val nowStrDate2: String = TimeTransform.unixTime2DateFormat(x.loc_time+"", "HH:mm:ss") //日志时间
        val hh2 = nowStrDate2.substring(0, 2).toInt
        val mm2 = nowStrDate2.substring(3, 5).toInt
        val ss2 = nowStrDate2.substring(6, 8).toInt
        val logDate = hh2 * 60 * 60 + mm2 * 60 + ss2
        logDate >= nowateLow && logDate <= nowDateUp && nowday == logday
      })
      .map(x => {


        s"${x.vehicle_id}, ${x.loc_time}, ${x.lng}, ${x.lat}, ${x.speed}, ${x.angle}, ${x.operation_status}, ${x.is_validate}, ${x.company_code}, ${x.altitude}, ${x.vehicle_color}, ${x.total_mileage}, ${x.alarm_status}, ${x.vehicle_status}, ${x.city}, ${x.year}, ${x.month}, ${x.day}"


//        val str = (x.loc_time + "000").toLong
//        s"""[{"altitude":${x.altitude},"color":${x.vehicle_color},"direction":${x.angle},"driveSpeed":${x.speed},"eff":-99,"encrypted":false,"id":"${x.vehicle_id}","latitude":${x.lat},"locationTime":"${new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(str)}","longitude":${x.lng},"recvtime":"${date}","run":-99,"satelliteSpeed":0,"totalMileage":${x.total_mileage},"vehicle_type":2}]""".stripMargin
      })

    new SparkKafkaUtil("10.5.10.18:9092,10.5.10.19:9092,10.5.10.20:9092",
      "T_SUTPC_SV_TOPIC_GIS_TAXI_EW")
      .output[String](spark, res)
    JobLock.isDropPartition = false

  }
}