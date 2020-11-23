package com.sutpc.bigdata.executor.std

import com.sutpc.bigdata.common.Constants
import com.sutpc.bigdata.executor.BaseExecutor
import com.sutpc.bigdata.schema.hive._
import com.sutpc.bigdata.utils.StdUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/7/31  14:28
  */
class StdGpsBusExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_GPS_BUS] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(2, 6))
    val c2 = (arr.length == 10)

    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val c3 = dataTime.getMillis <= DateTime.now().getMillis

    c1 && c2 && c3
  }

  override def map(x: String) = {
    val arr = x.split(",")

    //20171001,13,,粤BM7485,114.023085,22.755496,0,119,1,1
    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val loc_time = dataTime.getMillis / 1000

    //vehicle_id	车辆唯一标识（车牌）	字符串		否
    val vehicle_id = arr(3).trim

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(4).trim).setScale(6)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5).trim).setScale(6)

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = BigDecimal(arr(6).trim).setScale(1)

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = arr(7).trim.toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = "-99"

    //busline_name	公交线路名称	字符串		否
    val busline_name = arr(2).trim


    // busline_dir	方向 	整型		是		0为上行，1为下行，2偏离路线，3为上行场区，4为下行场区
    val busline_dir = null

    //bus_station_order	站序	整型		是
    val bus_station_order = null

    //city	城市名称	字符串		否	1	参照城市编码对照表
    val city_name = "shenzhen"

    //深圳
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt


    val is_validate = arr(9).toInt

    val bus_position = null

    val alarm_status: String = null
    val vehicle_status: String = null

    val year = arr(0).substring(0, 4).toInt
    val month = arr(0).substring(4, 6).toInt
    val day = arr(0).substring(6, 8).toInt


    STD_GPS_BUS(
      loc_time: Long,
      vehicle_id: String,
      lng: BigDecimal,
      lat: BigDecimal,
      speed: BigDecimal,
      angle: Int,
      is_validate: Int,
      company_code: String,
      busline_name: String,
      bus_position: Integer,
      busline_dir: Integer,
      bus_station_order: Integer,
      alarm_status: String,
      vehicle_status: String,
      city: Int,
      year: Int,
      month: Int,
      day: Int
    )
  }

  def execute() = {

    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.speed < 900)

    //        cache(res)


    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, dupliCols = Seq("vehicle_id", "loc_time"), isPersist = false)


  }
}

class StdGpsTaxiExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_GPS_TAXI] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = !arr(3).isEmpty

    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val c2 = dataTime.getMillis <= DateTime.now().getMillis

    c1 && c2

  }

  override def map(x: String) = {
    val arr = x.split(",")

    //20171001,13,,粤BM7485,114.023085,22.755496,0,119,1,1
    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val loc_time = dataTime.getMillis / 1000

    //vehicle_id	车辆唯一标识（车牌）	字符串		否
    val vehicle_id = arr(3)

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(4)).setScale(6)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5)).setScale(6)

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = BigDecimal(arr(6)).setScale(1)

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = arr(7).toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = "-99"

    //busline_name	公交线路名称	字符串		否
    val busline_name = arr(2)


    // busline_dir	方向 	整型		是		0为上行，1为下行，2偏离路线，3为上行场区，4为下行场区
    val busline_dir = null

    //bus_station_order	站序	整型		是
    val bus_station_order = null

    //city	城市名称	字符串		否	1	参照城市编码对照表
    val city_name = "shenzhen"

    //深圳
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt


    val is_validate = arr(9).toInt

    val bus_position = null

    val alarm_status: String = null
    val vehicle_status: String = null

    val year = arr(0).substring(0, 4).toInt
    val month = arr(0).substring(4, 6).toInt
    val day = arr(0).substring(6, 8).toInt

    //operation_status	运营状态，出租车预留字段	整型	1 	否		1重载，0空载
    val operation_status = arr(8).toInt

    //altitude	海拔高度，单位m	整型		否
    val altitude = -99

    //vehicle_color	车辆颜色	整型	1	是		1-蓝；2-黄；3-黑；4-白；5-其他
    val vehicle_color = null

    //total_mileage	车辆总里程数，单位千米	整型	10	是
    val total_mileage = null

    STD_GPS_TAXI(
      loc_time: Long,
      vehicle_id: String,
      lng: BigDecimal,
      lat: BigDecimal,
      speed: BigDecimal,
      angle: Int,
      is_validate: Int,
      operation_status: Int,
      company_code: String,
      altitude: Int,
      vehicle_color: Integer,
      total_mileage: Integer,
      alarm_status: String,
      vehicle_status: String,
      city: Int,
      year: Int,
      month: Int,
      day: Int
    )
  }

  def execute() = {
    println("读取【" + inputPath + "】数据")
    println("数据格式：定位日期 定位时刻 线路名称 车牌号码 经度 纬度 速度 方向角 运营状态 数据可用性")
    println("20170930,235958,M338,粤BN6601,114.029805,22.670012,37,135,1,1")
    smartLoop(inputPath: String, spark: SparkSession)
  }


  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.speed < 900)

    //    cache(res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, dupliCols = Seq("vehicle_id", "loc_time"))


  }
}

class StdGpsTaxiJiaoweiExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_GPS_TAXI] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = !arr(3).isEmpty
    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val c2 = dataTime.getMillis <= DateTime.now().getMillis

    c1 && c2

  }

  override def map(x: String) = {
    val arr = x.split(",")

    //20171001,13,,粤BM7485,114.023085,22.755496,0,119,1,1
    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val loc_time = dataTime.getMillis / 1000

    //vehicle_id	车辆唯一标识（车牌）	字符串		否
    val vehicle_id = arr(3)

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(4)).setScale(6)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5)).setScale(6)

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = BigDecimal(arr(6)).setScale(1)

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = arr(7).toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = arr(2)

    //city	城市名称	字符串		否	1	参照城市编码对照表
    val city_name = "shenzhen"

    //深圳
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    val is_validate = arr(9).toInt

    val alarm_status: String = null
    val vehicle_status: String = null

    val year = arr(0).substring(0, 4).toInt
    val month = arr(0).substring(4, 6).toInt
    val day = arr(0).substring(6, 8).toInt

    //operation_status	运营状态，出租车预留字段	整型	1 	否		1重载，0空载
    val operation_status = arr(8).toInt

    //altitude	海拔高度，单位m	整型		否
    val altitude = -99

    //vehicle_color	车辆颜色	整型	1	是		1-蓝；2-黄；3-黑；4-白；5-其他
    val vehicle_color = null

    //total_mileage	车辆总里程数，单位千米	整型	10	是
    val total_mileage = null

    STD_GPS_TAXI(
      loc_time: Long,
      vehicle_id: String,
      lng: BigDecimal,
      lat: BigDecimal,
      speed: BigDecimal,
      angle: Int,
      is_validate: Int,
      operation_status: Int,
      company_code: String,
      altitude: Int,
      vehicle_color: Integer,
      total_mileage: Integer,
      alarm_status: String,
      vehicle_status: String,
      city: Int,
      year: Int,
      month: Int,
      day: Int

    )
  }

  def execute() = {
    println("读取【" + inputPath + "】数据")
    println("数据格式：定位日期 定位时刻 线路名称 车牌号码 经度 纬度 速度 方向角 运营状态 数据可用性")
    println("20170930,235958,M338,粤BN6601,114.029805,22.670012,37,135,1,1")
    smartLoop(inputPath: String, spark: SparkSession)
  }


  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.speed < 900)

    //    cache(res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, dupliCols = Seq("vehicle_id", "loc_time"))

  }
}

class StdGpsDriveBaiduExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_GPS_DRIVE] {

  //20190101, 235924, 12207671782273809886, 11, 113.922647, 22.543030, 54, 274
  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 7)) && arr.length == 8

    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))

    val c2 = dataTime.getMillis <= DateTime.now().getMillis

    c1 && c2

  }

  override def map(x: String) = {
    val arr = x.split(",")

    //20190101, 235924, 12207671782273809886, 11, 113.922647, 22.543030, 54, 274
    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val loc_time = dataTime.getMillis / 1000

    //vehicle_id	车辆唯一标识（车牌）	字符串		否
    val vehicle_id = arr(2)

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(4)).setScale(6)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5)).setScale(6)

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = BigDecimal(arr(6)).setScale(1)

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = arr(7).toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = "-99"


    //city	城市名称	字符串		否	1	参照城市编码对照表
    val city_name = "shenzhen"

    //深圳
    val city = broadcast.value.get(city_name.trim).getOrElse("-99").toInt

    //is_validate	数据是否有效，	整型	 1	否		0表示异常，1表示有效
    val is_validate = -99

    val year = arr(0).substring(0, 4).toInt
    val month = arr(0).substring(4, 6).toInt
    val day = arr(0).substring(6, 8).toInt

    val source_company = "baidu"

    val navigation_status = arr(3).toInt

    val license_prefix: String = ""

    STD_GPS_DRIVE(
      loc_time: Long,
      vehicle_id: String,
      lng: BigDecimal,
      lat: BigDecimal,
      speed: BigDecimal,
      angle: Int,
      is_validate: Int,
      company_code: String,
      navigation_status: Int,
      source_company: String,
      city: Int,
      year: Int,
      month: Int,
      day: Int,
      license_prefix: String
    )
  }

  def execute() = {
    println("读取【" + inputPath + "】数据")
    println("数据格式：定位日期 定位时刻 用户ID 导航状态 经度 纬度 瞬时速度 方位角")
    println("20170930,235958,M338,粤BN6601,114.029805,22.670012,37,135,1,1")
    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(StdUtils.replaceBlank(_))
      .map(x => map(x)).filter(_.speed < 900)
    outputHiveStd(spark, outputTable, Array("source_company", "city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_id", "loc_time", "source_company"))


  }
}

//20190531,235925,H,粤BDE4690,114.199640,22.647543,29.0,26,1,1,1,29,0,0,0,0,512
class StdGpsOrderExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_GPS_ORDER] {

  //定位日期 定位时刻 公司代码 车牌号 经度 纬度 速度 方向角 运营状态 数据可用性 车辆颜色 行车速度 总里程 海拔高度 车辆状态 报警状态 车辆类型
  //20190531,235920,H,    粤BFX015,  115.910216,  23.003230,  0.0,  0, 1, 1, 2,  0,131673600,18,786434,262144,512
  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 7)) && arr.length == 17

    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val c2 = dataTime.getMillis <= DateTime.now().getMillis

    c1 && c2

  }

  override def map(x: String) = {
    val arr = x.split(",")

    //20190531,235925,H,粤BDE4690,114.199640,22.647543,29.0,26,1,1,1,29,0,0,0,0,512
    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val loc_time = dataTime.getMillis / 1000

    //vehicle_id	车辆唯一标识（车牌）	字符串		否
    val vehicle_id = arr(3)

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(4)).setScale(6)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5)).setScale(6)

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = BigDecimal(arr(6)).setScale(1)

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = arr(7).toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = arr(2)


    //operation_status	营运状态	整型	1 	否		1-载客，2-接单，3-空驶，4-停运
    val operation_status = arr(8).toInt

    //order_id	订单id	字符串	20	否		订单编号
    val order_id = "-99"

    //altitude	海拔高度，单位m	整型	2	否
    val altitude = arr(13).toInt

    //position_type	位置信息类型	字符串	10	否		JYSX-经营上线、JYXX经营下线、CKSC乘客上车、CKXC-乘客下车、DDJD-订单派单
    val position_type = "-99"


    //city	城市名称	字符串		否	1	参照城市编码对照表
    val city_name = "shenzhen"

    //深圳
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    val year: String = arr(0).substring(0, 4)
    val month: String = arr(0).substring(4, 6)
    val day: String = arr(0).substring(6, 8)

    STD_GPS_ORDER(
      loc_time: Long,
      vehicle_id: String,
      lng: BigDecimal,
      lat: BigDecimal,
      speed: BigDecimal,
      angle: Int,
      operation_status: Int,
      position_type: String,
      order_id: String,
      altitude: Int,
      company_code: String,
      city: Int,
      year: String,
      month: String,
      day: String
    )
  }

  def execute() = {
    println("读取【" + inputPath + "】数据")
    println("数据格式：定位日期 定位时刻 用户ID 导航状态 经度 纬度 瞬时速度 方位角")
    println("20170930,235958,M338,粤BN6601,114.029805,22.670012,37,135,1,1")
    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.speed < 900)

    cache(res)

    //    outputHive(zkUrl, outputTable, res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, dupliCols = Seq("vehicle_id", "loc_time"))

  }
}

class StdGpsTruckExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_GPS_TRUCK] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 3))

    lazy val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
    lazy val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    lazy val c2 = dataTime.getMillis <= DateTime.now().getMillis

    c1 && !x.contains("grid_id") && c2

  }

  override def map(x: String) = {
    val arr = x.split(",")

    //20170930,235953,H,粤BT2046,113.902152,34.702252,0.0,1,1,1,2,0,837601,0,2,0
    //20180930,235857,H,粤BW7325,114.242953,22.598443,0.0,87,1,1,2,0,93526,0,3,134217728,4

    //    20171001,23,H,粤BX3340,113.885809,22.503325,0.0,0,1,1,2,0,9390700,13,786434,0
    //定位日期,定位时刻,所属公司代码,车牌号码,经度，纬度，速度，方向角，运营状态，数据可用性，车辆颜色，行车速度 总里程数 海拔高度 车辆状态 报警状态 车辆类型

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
    val lng = BigDecimal(arr(4)).setScale(6)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5)).setScale(6)

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = BigDecimal(arr(6)).setScale(1)

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
    val city_name = "shenzhen"
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    //    0-导航小汽车；1-班车客运；2-出租车，4-货运车；8-驾培车；16-包车客运；32-公交车；64-危险品车；128-其他车辆；256-泥头车；512-网约车
    val vehicle_type = 4

    val year: String = arr(0).substring(0, 4)
    val month: String = arr(0).substring(4, 6)
    val day: String = arr(0).substring(6, 8)

    STD_GPS_TRUCK(
      loc_time: Long,
      vehicle_id: String,
      lng: BigDecimal,
      lat: BigDecimal,
      speed: BigDecimal,
      angle: Int,
      company_code: String,
      altitude: Int,
      vehicle_color: Integer,
      total_mileage: Long,
      is_validate: Int,
      alarm_status: String,
      vehicle_status: String,
      city: Int,
      year: String,
      month: String,
      day: String
    )

  }

  def execute() = {
    println("读取【" + inputPath + "】数据")
    println("数据格式：定位日期 定位时刻 用户ID 导航状态 经度 纬度 瞬时速度 方位角")
    println("20170930,235958,M338,粤BN6601,114.029805,22.670012,37,135,1,1")
    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.speed < 900)

    //    cache(res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_id", "loc_time"))

  }
}

class StdGpsICExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], cityVheadMapBR: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_IC] {

  override def filter(x: String): Boolean = {

    val arr = x.split(",")

    lazy val dataTime = DateTime.parse(arr(1), DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    lazy val c3 = dataTime.getMillis <= DateTime.now().getMillis

    !x.startsWith("卡号") && !x.contains("交易日期") && StdUtils.isTime(arr(1), Array("yyyyMMddHHmmss")) && StdUtils.allNotEmpty(Array(arr(0), arr(1), arr(2), arr(6), arr(7), arr(8))) && arr.length == 11 && c3
  }

  override def map(x: String) = {

    val arr = x.split(",")

    //card_id	卡号	字符串		否
    val card_id = arr(0)
    //trans_type	交易类型	整型		否		1-地铁进站；2-地铁出站；3-巴士；4-二维码巴士；
    // val local_encoding = System.getProperty("file.encoding")
    // val trans_type_tmp = new String(arr(2).getBytes(local_encoding), "UTF-8")
    var trans_type = Constants.IC_TRANS_TYPE_MAP.get(arr(2)).getOrElse(-99)
    // if (trans_type == -99) {
    //trans_type = Constants.IC_TRANS_TYPE_MAP.get(arr(2)).getOrElse(-99)
    // }

    val dateTime = DateTime.parse(arr(1), DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    // trans_time	交易日期	整型	10	否		10位UNIX时间戳
    val trans_time = dateTime.getMillis / 1000


    // fdate	日期	整型		否		20190708表示2019年7月8日
    val fdate = dateTime.toString("yyyyMMdd").toInt

    // ftime	时间	字符串		否		000321表示3分21秒
    val ftime = dateTime.toString("HHmmss")

    //trans_value	交易金额	整型		否 单位分
    val trans_value = arr(3).toInt

    //dev_no	设备编号	字符串		否
    val dev_no = arr(5)

    //company_line	公司名称/线路名称	字符串		否		trans_type=1或2时，线路名称；trans_type=3或4时，公司名称
    val company_line = arr(6)

    //line_station	线路名称/站点名称	字符串		否		trans_type=1或2时，站点名称；trans_type=3或4时，线路名称
    val line_station = arr(7)

    //city	城市名称	整型		否	1	参照城市编码对照表显示城市名称拼音
    //深圳
    val city_name = "shenzhen"

    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    //  vehicle_id	车辆编号	字符串		否
    val v_head = cityVheadMapBR.value.get(city_name).getOrElse("#").trim

    var vehicle_id = if ((trans_type == 3 || trans_type == 4) && (!arr(8).startsWith(v_head))) v_head + arr(8) else arr(8)


    //vehicle_id = vehicle_id.replaceAll("进", "").replaceAll("出", "")

    val year = arr(1).substring(0, 4).toInt
    val month = arr(1).substring(4, 6).toInt
    val day = arr(1).substring(6, 8).toInt

    STD_IC(
      card_id: String,
      trans_type: Int,
      trans_time: Long,
      trans_value: Int,
      dev_no: String,
      company_line: String,
      line_station: String,
      vehicle_id: String,
      city: Int,
      year: Int,
      month: Int,
      day: Int
    )

  }

  def execute() = {
    println("读取【" + inputPath + "】数据")
    println("数据格式：定位日期 定位时刻 用户ID 导航状态 经度 纬度 瞬时速度 方位角")
    println("20170930,235958,M338,粤BN6601,114.029805,22.670012,37,135,1,1")

    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))

    //    cache(res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, dupliCols = Seq("card_id", "trans_time", "vehicle_id", "trans_type"))

  }
}

class StdBikeSwitchExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_BIKE_SWITCH] {

  override def filter(x: String): Boolean = {
    true
  }

  override def map(x: String) = {

    //    数据时间：2018年10月1日至2018年10月31日期间监管平台中所有车辆开关锁数据
    //      数据字段：vehicle_no（统一处理过的车辆编码）,location_time（车辆定位时间）,lat（车辆定位经度）,lng（车辆定位纬度）,locak_status（车辆状态：开锁，关锁）
    //    数据量：61,033,439条数据
    //      数据采集说明：共享单车关锁时发送一次位置信息

    //    INSERT INTO `a_share` (`vehicle_num`, `location_time`, `lat`, `lng`, `id`, `lockstatus`) VALUES ('8c432e4aae24b2025b5e3620b3974630', '2018-10-1 00:00:00', '22.633789', '113.840496', 1, '开锁');

    val arr = x
      .replaceAll("INSERT INTO `a_share` \\(`vehicle_num`, `location_time`, `lat`, `lng`, `id`, `lockstatus`\\) VALUES \\(",
        "".stripMargin)
      .replaceAll("\\);", "")
      .replaceAll("'", "")
      .split(",")

    //8c432e4aae24b2025b5e3620b3974630, 2018-10-1 00:00:00, 22.633789, 113.840496, 1, 开锁

    //    --bike_id	单车编号	字符串	32	否
    val bike_id = arr(0).trim
    //      --loc_time	定位时间	整型		否		10位UNIX时间戳

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(arr(1).trim, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
    val loc_time = dataTime.getMillis / 1000

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(3).trim).setScale(6)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(2).trim).setScale(6)

    //    --lock_status	单车状态	字符串	10	否		1-开锁；2-关锁
    val lock_status = Constants.BIKE_LOCK_STATUS_MAP.getOrElse(arr(5).trim, "-99").toString.toInt

    //    --order_no	序号	整型		否		按行数自增
    val order_no = arr(4).trim.toInt

    //city	城市名称	字符串		否	1	参照城市编码对照表
    // 深圳
    val city_name = "shenzhen"
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt


    val year: String = dataTime.getYear.toString
    val month: String = StdUtils.fillZero(2, dataTime.getMonthOfYear.toString)
    val day: String = StdUtils.fillZero(2, dataTime.getDayOfMonth.toString)


    //ftime	时间	字符串		否		190802
    val ftime = dataTime.toString("HHmmss")


    STD_BIKE_SWITCH(
      bike_id: String,
      loc_time: Long,
      ftime: String,
      lng: BigDecimal,
      lat: BigDecimal,
      order_no: Int,
      lock_status: Int,
      city: Int,
      year: String,
      month: String,
      day: String
    )


  }

  def execute() = {
    println("读取【" + inputPath + "】数据")
    println("数据格式：定位日期 定位时刻 用户ID 导航状态 经度 纬度 瞬时速度 方位角")
    println("20170930,235958,M338,粤BN6601,114.029805,22.670012,37,135,1,1")

    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))

    cache(res)


    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, dupliCols = Seq("bike_id", "loc_time"))

  }
}


