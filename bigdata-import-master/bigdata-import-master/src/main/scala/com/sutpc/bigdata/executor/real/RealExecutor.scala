package com.sutpc.bigdata.executor.real

import com.alibaba.fastjson.JSON
import com.sutpc.bigdata.jdbc.DimSource
import com.sutpc.bigdata.job.batch.real.{BaseTask, EtlTask, JobLock, TaskOutput}
import com.sutpc.bigdata.schema.hive._
import com.sutpc.bigdata.utils.StdUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{Dataset, SparkSession}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.math.BigDecimal.RoundingMode

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/10/28  19:38
  */

//20191015,3348,H,粤BS4773,110.758354,21.409887,0.0,174,1,1,2,0,0,0,3,0,1
//20191028,150937,H,粤BU2458,113.800848,22.686768,0.0,318,1,1,2,0,25913,0,3,0,64
//两客一危
class RealTimeGpsOthersTask(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_OTHER] {

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

    import spark.implicits._
    val res: Dataset[STD_GPS_OTHER] = spark.createDataset(rdd).as[String]
      .filter(x => {
        val arr = x.split(",")
        arr.length >= 16
      })
      .filter(x => filter(x))
      .map(x => map(x, dir)).filter(_.speed < 900)

    //    cache(res)

    res.show(10,false)

    TaskOutput.hive[STD_GPS_OTHER](spark, task, Array("vehicle_type", "city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_id", "loc_time"), isCopy2Tag = true)
    JobLock.isDropPartition = false

  }

}

//        [{"acc":false,"aiLock":false,"altitude":0.0,"carLock":false,"carState":false,"circuitry":false,"color":1,"direction":39,"driveSpeed":0.0,"emptyToHeavy":false,"encrypted":false,"heavyToEmpty":false,"id":"粤BDK0187","latDirection":false,"latitude":22.574913,"lngDirection":false,"location":false,"locationTime":"2019-09-24 23:59:22","longitude":114.06607,"obligate":false,"oil":false,"otherState":false,"price":false,"satelliteSpeed":0.0,"state":false,"totalMileage":1161890.0,"wieldNumberTime":false}]
class RealTimeGpsTaxiExecutor(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_TAXI] {

  def filter(x: String): Boolean = {

    //    var isOk = true

    //    Try(JSON.parseArray(x)) match {
    //      case Success(i) => isOk = true
    //      case Failure(s) => isOk = false
    //    }

//    val arr = x.split(",")

        lazy val jsonArr = JSON.parseArray(x)
        lazy val jsonObj = jsonArr.getJSONObject(0)

        lazy val time_tmp = jsonObj.getString("locationTime").trim
//    val time_tmp = arr(0).trim
    //loc_time	定位日期	整型	10	否		unix10位时间戳
        lazy val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
//    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMdd"))

    val today = DateTime.parse(task.input.substring(task.input.lastIndexOf("/") - 7), DateTimeFormat.forPattern("yyyy/MM/dd")).plusDays(1)

    //true &&
    dataTime.getMillis >= today.minusDays(1).getMillis && dataTime.getMillis < today.getMillis

  }

  def map(x: String) = {

    val jsonArr = JSON.parseArray(x)
    val jsonObj = jsonArr.getJSONObject(0)


    //      [{"acc":false,"aiLock":false,"altitude":0.0,"carLock":false,"carState":false,"circuitry":false,"color":1,"direction":39,"driveSpeed":0.0,"emptyToHeavy":false,"encrypted":false,"heavyToEmpty":false,"id":"粤BDK0187","latDirection":false,"latitude":22.574913,"lngDirection":false,"location":false,"locationTime":"2019-09-24 23:59:22","longitude":114.06607,"obligate":false,"oil":false,"otherState":false,"price":false,"satelliteSpeed":0.0,"state":false,"totalMileage":1161890.0,"wieldNumberTime":false}]


    val tmpTime = jsonObj.getString("locationTime").trim
    val dataTime = DateTime.parse(tmpTime, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
    val loc_time = dataTime.getMillis / 1000

    val vehicle_id: String = jsonObj.getString("id")
    val lng: BigDecimal = BigDecimal(jsonObj.getString("longitude")).setScale(6)
    val lat: BigDecimal = BigDecimal(jsonObj.getString("latitude")).setScale(6)
    val speed: BigDecimal = BigDecimal(jsonObj.getString("driveSpeed")).setScale(1)
    val angle: Int = jsonObj.getIntValue("direction")
    val is_validate: Int = 1
    val operation_status: Int = if (jsonObj.getBooleanValue("carState")) 1 else 0
    val company_code: String = "-99"
    val altitude: Int = jsonObj.getDouble("altitude").toInt
    val vehicle_color: Integer = jsonObj.getInteger("color")
    val total_mileage: Integer = (jsonObj.getDouble("totalMileage").toInt) / 1000

    val alarm_status: String = null
    val vehicle_status: String = null

    //city	城市名称	字符串		否	1	参照城市编码对照表
    val city = 440300

    val tmpTime2 = dataTime.toString("yyyyMMdd")
    val year = tmpTime2.substring(0, 4).toInt
    val month = tmpTime2.substring(4, 6).toInt
    val day = tmpTime2.substring(6, 8).toInt


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

    smartLoop(task.input: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x =>
        x.vehicle_id.nonEmpty && x.lng != null && x.lat != null && StdUtils.isCnLngLat(x.lng.toDouble, x.lat.toDouble) && x.speed < 900
      )

    //    cache(res)
    res.show(10, false)
    TaskOutput.hive[STD_GPS_TAXI](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_id", "loc_time"), isCopy2Tag = true)


  }
}

class RealTimeGpsBusExecutor(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_BUS] {

  def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(2, 6)) && arr.length == 13

    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))

    //    val c3 = dataTime.getMillis <= DateTime.now().getMillis

    val today = DateTime.parse(task.input.substring(task.input.lastIndexOf("/") - 7), DateTimeFormat.forPattern("yyyy/MM/dd")).plusDays(1)

    c1 && dataTime.getMillis >= today.minusDays(1).getMillis && dataTime.getMillis < today.getMillis

  }

  def map(x: String, routeBC: Broadcast[Map[String, String]], vnoBC: Broadcast[Map[String, String]]) = {
    //    20191118,235958,H,53075,113.92556,22.669336,0,0,1,1,5668,0,20191118 23:59:58

    val arr = x.split(",")

    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期/**/	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val loc_time = dataTime.getMillis / 1000

    //vehicle_id	车辆唯一标识（车牌）	字符串		否
    val vehicle_id = vnoBC.value.getOrElse(arr(3).trim, "-99")

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(4).trim).setScale(6)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5).trim).setScale(6)

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = BigDecimal(arr(6).trim).setScale(1)

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = arr(7).trim.toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = arr(2)

    //busline_name	公交线路名称	字符串		否
    val busline_name = routeBC.value.getOrElse(arr(10).trim, "-99")


    // busline_dir	方向 	整型		是		0为上行，1为下行，2偏离路线，3为上行场区，4为下行场区
    val busline_dir = null

    //bus_station_order	站序	整型		是
    val bus_station_order = null

    //city	城市名称	字符串		否	1	参照城市编码对照表
    val city_name = "shenzhen"

    //深圳
    val city = 440300
    //    val city = "-99".toInt

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
    println("读取【" + task.input + "】数据")
    smartLoop(task.input: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask)
    import spark.implicits._
    val dimSource = new DimSource(spark)
    val routeBC = spark.sparkContext.broadcast(dimSource.getRouteId2NameMap())
    val vnoBC = spark.sparkContext.broadcast(dimSource.getVId2VnoMap())

    val res = spark.createDataset(rdd).as[String]
      .filter(x => {
        val strings: Array[String] = x.split(",")
        strings.length == 13
      })
      .filter(x => filter(x))
      .map(x => map(x, routeBC, vnoBC))
      .filter(x =>
        x.vehicle_id.nonEmpty && x.lng != null && x.lat != null && StdUtils.isCnLngLat(x.lng.toDouble, x.lat.toDouble) && x.speed < 900
      )

    //    cache(res)
    res.show(10, false)
//    TaskOutput.hive[STD_GPS_BUS](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_id", "loc_time"), isCopy2Tag = true)


  }
}

class RealTimeGpsOrderExecutor(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER] {
  //20190531,235925,H,粤BDE4690,114.199640,22.647543,29.0,26,1,1,1,29,0,0,0,0,512

  //20191009,235959,WANSHUNJIAOCHE,B19EM6,114.050983,22.636803,32.0,359,0,1,9000042793,362531196310061216,1,63.0,3,XS,0


  //定位日期 定位时刻 公司代码 车牌号 经度 纬度 速度 方向角 运营状态 数据可用性 车辆颜色 行车速度 总里程 海拔高度 车辆状态 报警状态 车辆类型
  //20190531,235920,H,    粤BFX015,  115.910216,  23.003230,  0.0,  0, 1, 1, 2,  0,131673600,18,786434,262144,512

  def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 7)) && arr.length == 17

    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))

    val today = DateTime.parse(task.input.substring(task.input.lastIndexOf("/") - 7), DateTimeFormat.forPattern("yyyy/MM/dd")).plusDays(1)

    c1 && dataTime.getMillis >= today.minusDays(1).getMillis && dataTime.getMillis < today.getMillis

  }

  def map(x: String) = {
    val arr = x.replaceAll("null", "-99").split(",")


    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val loc_time = dataTime.getMillis / 1000

    //vehicle_id	车辆唯一标识（车牌）	字符串		否
    val vehicle_id = if (arr(3).startsWith("B")) "粤" + arr(3) else arr(3)

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(4)).setScale(6, RoundingMode.HALF_UP)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5)).setScale(6, RoundingMode.HALF_UP)

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = BigDecimal(arr(6)).setScale(2, RoundingMode.HALF_UP)

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = if (arr(7).isEmpty) -99 else arr(7).toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = arr(2)


    //operation_status	营运状态	整型	1 	否		1-载客，2-接单，3-空驶，4-停运
    val operation_status = arr(8).toInt

    //order_id	订单id	字符串	20	否		订单编号
    val order_id = arr(16)

    //altitude	海拔高度，单位m	整型	2	否
    val altitude = if (arr(13).isEmpty) -99 else arr(13).toDouble.toInt

    //position_type	位置信息类型	字符串	10	否		JYSX-经营上线、JYXX经营下线、CKSC乘客上车、CKXC-乘客下车、DDJD-订单派单
    val position_type = arr(15)


    //city	城市名称	字符串		否	1	参照城市编码对照表
    val city_name = "shenzhen"

    //深圳
    val city = 440300

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
    println("读取【" + task.input + "】数据")
    println("数据格式：定位日期 定位时刻 用户ID 导航状态 经度 纬度 瞬时速度 方位角")
    println("20170930,235958,M338,粤BN6601,114.029805,22.670012,37,135,1,1")
    smartLoop(task.input: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => {
        x.split(",").length == 17
      })
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.speed < 900)

    //    cache(res)

    TaskOutput.hive[STD_GPS_ORDER](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_id", "loc_time"), isCopy2Tag = true)

  }
}
