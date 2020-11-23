package com.sutpc.bigdata.executor.tag.phoenix

import com.sutpc.bigdata.common.Constants
import com.sutpc.bigdata.executor.BaseExecutor
import com.sutpc.bigdata.schema.phoenix._
import com.sutpc.bigdata.utils.{MD5Utils, StdUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class TagGpsTaxiExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_GPS_TAXI] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = !arr(3).isEmpty && arr.length == 10
    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val c2 = dataTime.getMillis <= DateTime.now().getMillis
    c1 && c2

  }

  override def map(x: String) = {
    val arr = x.split(",")

    //20170501,555,H,��B5V4P9,113.897903,22.570900,0.0,0,0,1

    //    val date: String = StdUtils.replaceYear(arr(0))
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
    val lng = BigDecimal(arr(4))

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5))

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = arr(6).toDouble

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = arr(7).toInt

    //operation_status	运营状态，出租车预留字段	整型	1 	否		1重载，0空载
    val operation_status = arr(8).toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = arr(2)

    //altitude	海拔高度，单位m	整型		否
    val altitude = -99

    //vehicle_color	车辆颜色	整型	1	是		1-蓝；2-黄；3-黑；4-白；5-其他
    val vehicle_color = null

    //total_mileage	车辆总里程数，单位千米	整型	10	是
    val total_mileage = null

    //city	城市名称	字符串		否	1	参照城市编码对照表
    // 深圳
    val city_name = "shenzhen"
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    val license_prefix: String = vehicle_id.substring(0, 2)

      TAG_GPS_TAXI(
        loc_time: Long,
        fdate: Int,
        ftime: String,
        vehicle_id: String,
        MD5Utils.encode16(vehicle_id): String,
        lng: BigDecimal,
        lat: BigDecimal,
        speed: Double,
        angle: Int,
        operation_status: Int,
        company_code: String,
        altitude: Int,
        vehicle_color: Integer,
        total_mileage: Integer,
        city: Int,
        license_prefix: String
      )
  }

  def execute() = {
    println("读取【" + inputPath + "】数据")
    println("数据格式：定位日期 定位时刻 所属公司代码 车牌号码 经度 纬度 速度 方向角 运营状态 数据可用性")
    println("20170501,555,H,��B5V4P9,113.897903,22.570900,0.0,0,0,1")
    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.speed < 900)

    cache(res)

    outputHBase(zkUrl, outputTable, res)
  }
}

class TagGpsBusExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_GPS_BUS] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(2, 6))
    val c2 = (arr.length == 10)

    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val c3 = dataTime.getMillis <= DateTime.now().getMillis

    c1 && c2 && c3
  }

  override def map(x: String) = {
    val arr = x.split(",")

    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val loc_time = dataTime.getMillis / 1000

    //fdate	日期	整型		否		20190708表示2019年7月8日
    val fdate = arr(0).toInt

    // ftime	时间	字符串		否		000321表示3分21秒
    val ftime = dataTime.toString("HHmmss")

    //vehicle_id	车辆唯一标识（车牌）	字符串		否
    val vehicle_id = arr(3)

    //encry_id	不可逆加密车牌	字符串		否		采用MD5进行加密
    val encry_id = MD5Utils.encode16(vehicle_id)

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(4))

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5))

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = arr(6).toDouble

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = arr(7).toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = "-99"

    //busline_name	公交线路名称	字符串		否
    val busline_name = arr(2)

    //stop_announce	报站状态	整型		是		报站状态：0位区间，1为进站
    val stop_announce = null

    // busline_dir	方向 	整型		是		0为上行，1为下行，2偏离路线，3为上行场区，4为下行场区
    val busline_dir = null

    //bus_station_order	站序	整型		是
    val bus_station_order = null

    //city	城市名称	字符串		否	1	参照城市编码对照表
    val city_name = "shenzhen"

    //深圳
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    val license_prefix: String = vehicle_id.substring(0, 2)

    TAG_GPS_BUS(
      loc_time: Long,
      fdate: Int,
      ftime: String,
      vehicle_id: String,
      encry_id: String,
      lng: BigDecimal,
      lat: BigDecimal,
      speed: Double,
      angle: Int,
      company_code: String,
      busline_name: String,
      stop_announce: Integer,
      busline_dir: Integer,
      bus_station_order: Integer,
      city: Int,
      license_prefix: String
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
    outputHBase(zkUrl, outputTable, res)
  }
}

class TagICExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], cityVheadMapBR: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_IC] {

  override def filter(x: String): Boolean = {

    val arr = x.split(",")

    lazy val dataTime = DateTime.parse(arr(1), DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    lazy val c3 = dataTime.getMillis <= DateTime.now().getMillis

    !x.startsWith("卡号") && StdUtils.allNotEmpty(Array(arr(0), arr(1), arr(2), arr(6), arr(7), arr(8))) && arr.length == 11 && c3
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
    var vehicle_id = if (trans_type == 3 || trans_type == 4) v_head + arr(8) else arr(8)

    //        vehicle_id = vehicle_id.replaceAll("进", "").replaceAll("出", "")

    // encry_id	不可逆加密车牌	字符串		否		采用MD5进行加密
    val encry_id = MD5Utils.encode16(vehicle_id)

    // create_date 入库日期	整型		否		20190708表示2019年7月8日
    val create_date = DateTime.now().toString("yyyyMMdd").toInt

    val license_prefix: String = if (trans_type == 3 || trans_type == 4) v_head  else ""

    TAG_IC(
      card_id: String,
      trans_type: Int,
      trans_time: Long,
      fdate: Int,
      ftime: String,
      trans_value: Int,
      dev_no: String,
      company_line: String,
      line_station: String,
      vehicle_id: String,
      encry_id: String,
      city: Int,
      create_date: Int,
      license_prefix: String
    )
  }

  def execute() = {
    println("读取IC卡HDFS数据")
    println("数据格式：卡号,交易日期时间,交易类型,交易金额,交易值,设备编码,公司名称,线路站点,车牌号,联程标记,结算日期")
    println("292375203,20171029221200,地铁入站,0,0,260036109,地铁二号线,大剧院,AGM-109,0,20171030")
    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
    outputHBase(zkUrl, outputTable, res)
  }
}

class TagWeatherGridExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_WEATHER_GRID] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")

    val dataTime = DateTime.parse(arr(1), DateTimeFormat.forPattern("yyyyMMddHH"))
    val c3 = dataTime.getMillis <= DateTime.now().getMillis

    StdUtils.allNotEmpty(arr) && arr.length == 21 && c3
  }

  override def map(x: String) = {
    val arr = x.split(",")

    //1187,20171101,1,21.9,1017.6,4.1,33.1,70,0,0,0,0,0,0,0,0,0,0,0,0,18.418

    //网格化降雨数据
    // grid_fid	栅格id	字符串		否		网格编号
    val grid_fid = arr(0)

    // period	时间片	整型		否		5分钟为一个时间片，一天288个时间片（编号从1至288）
    val period = arr(2).toInt

    // t	温度	浮点		否		单位是摄氏度
    val t = arr(3).toDouble

    // slp	海平面气压	浮点		否		单位：百帕
    val slp = arr(4).toDouble

    // wspd	风速	浮点		否		单位：米/秒
    val wspd = arr(5).toDouble

    // wdir	风向	浮点		否		单位：度
    val wdir = arr(6).toDouble

    // rhsfc	相对湿度	浮点		否		百分比%
    val rhsfc = arr(7).toDouble

    //rain01m	当前分钟的降雨量	浮点		否		单位：毫米
    val rain01m = arr(8).toDouble

    // rain06m	6分钟累计降雨量	浮点		否		单位：毫米
    val rain06m = arr(9).toDouble

    // rain12m	12分钟累计降雨量	浮点		否		单位：毫米
    val rain12m = arr(10).toDouble

    // rain30m	30分钟累计降雨量	浮点		否		单位：毫米
    val rain30m = arr(11).toDouble

    // rain01h	1小时累计降雨量	浮点		否		单位：毫米
    val rain01h = arr(12).toDouble

    // rain02h	2小时累计降雨量	浮点		否		单位：毫米
    val rain02h = arr(13).toDouble

    // rain03h	3小时累计降雨量	浮点		否		单位：毫米
    val rain03h = arr(14).toDouble

    // rain06h	6小时累计降雨量	浮点		否		单位：毫米
    val rain06h = arr(15).toDouble

    // rain12h	12小时累计降雨量	浮点		否		单位：毫米
    val rain12h = arr(16).toDouble

    // rain24h	24小时累计降雨量	浮点		否		单位：毫米
    val rain24h = arr(17).toDouble

    // rain48h	48小时累计降雨量	浮点		否		单位：毫米
    val rain48h = arr(18).toDouble

    // rain72h	72小时累计降雨量	浮点		否		单位：毫米
    val rain72h = arr(19).toDouble

    // v	能见度	浮点		否		单位：公里
    val v = arr(20).toDouble

    val city_name = "shenzhen"

    // city	城市名称	字符串		否	1	参照城市编码对照表
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    //fdate	日期	整型		否	2	示例20190808表示2019年8月8日
    val fdate = arr(1).toInt


    TAG_WEATHER_GRID(
      grid_fid: String,
      period: Int,
      t: Double,
      slp: Double,
      wspd: Double,
      wdir: Double,
      rhsfc: Double,
      rain01m: Double,
      rain06m: Double,
      rain12m: Double,
      rain30m: Double,
      rain01h: Double,
      rain02h: Double,
      rain03h: Double,
      rain06h: Double,
      rain12h: Double,
      rain24h: Double,
      rain48h: Double,
      rain72h: Double,
      v: Double,
      city: Int,
      fdate: Int
    )
  }

  def execute() = {
    println("读取降雨量HDFS数据")
    println("数据格式：gridid time period t slp wspd wdir rhsfc rain01m rain06m rain12m rain30m rain01h rain02h rain03h rain06h rain12h rain24h rain48h rain72h v")
    println("432,20171024,1,21.7,1014.8,0.6,26.5,74.8,0,0,0,0,0,0,0,0,0,0,0,0,11.051")

    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
    outputHBase(zkUrl, outputTable, res)
  }
}

class TagHomeDistExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_PHONE_HOME_DIST] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 3))
    val c2 = (arr.length == 4)
    c1 && c2
  }

  override def map(x: String) = {
    val arr = x.split(",")

    // user_type home_grid_id user_count expansion_rate

    //grid_id	居住地栅格ID	字符串	32	否
    val grid_id = arr(1)

    //user_type	用户类型	字符串	10	否
    // resident-常住人口；tourist-流动人口
    //resident-常住人口；tourist-流动人口
    val user_type = Constants.MSIGNAL_USER_TYPE_MAP.get(arr(0)).getOrElse(-99)

    // age	年龄	整型		否		999-未知
    val age = -99

    // sex	性别	整型		否		1-男；2-女，999-未知
    val sex = -99

    // adm_reg	用户归属地	字符串	32	否		用户归属地，参照城市编码对照表
    val adm_reg = -99

    // has_auto	有无小汽车	整型		否		1-有；0-没有；999-0未知；
    val has_auto = -99

    // user_qty	人口数	整型		否
    val user_qty = arr(2).toInt

    //fdate	日期	整型		否		示例20190800表示2019年8月份的数据
    val fdate = 20171200

    // create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt

    // city	城市名称	字符串		否	1	参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val city_code = gridCityMapBR.get(grid_id).getOrElse("-99")
    val city = if (StringUtils.isEmpty(city_code)) -99 else city_code.toInt

    //source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cu"

    TAG_PHONE_HOME_DIST(
      grid_id: String,
      user_type: Int,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      city: Int,
      source: String

    )
  }

  def execute() = {
    println("user_type home_grid_id user_count expansion_rate")
    println("RESIDENT,782494,18,null")

    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        x.city != (-99) && !x.grid_id.equals("0")
      })
    outputHBase(zkUrl, outputTable, res)
  }
}

class TagWorkDistExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_PHONE_WORK_DIST] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 2))
    val c2 = (arr.length == 3)
    c1 && c2
  }

  override def map(x: String) = {
    val arr = x.split(",")

    // user_type home_grid_id user_count expansion_rate

    //grid_id	居住地栅格ID	字符串	32	否
    val grid_id = arr(0)

    // age	年龄	整型		否		999-未知
    val age = -99

    // sex	性别	整型		否		1-男；2-女，999-未知
    val sex = -99

    // adm_reg	用户归属地	字符串	32	否		用户归属地，参照城市编码对照表
    val adm_reg = -99

    // has_auto	有无小汽车	整型		否		1-有；0-没有；999-0未知；
    val has_auto = -99

    // user_qty	人口数	整型		否
    val user_qty = arr(1).toInt

    //fdate	日期	整型		否		示例20190800表示2019年8月份的数据
    val fdate = 20171200

    // create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt

    // city	城市名称	字符串		否	1	参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val city_code = gridCityMapBR.get(grid_id).getOrElse("-99")
    val city = if (StringUtils.isEmpty(city_code)) -99 else city_code.toInt

    //source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cu"

    TAG_PHONE_WORK_DIST(
      grid_id: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      city: Int,
      source: String

    )
  }

  def execute() = {
    println("user_type home_grid_id user_count expansion_rate")
    println("RESIDENT,782494,18,null")

    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        x.city != (-99) && !x.grid_id.equals("0")
      })
    outputHBase(zkUrl, outputTable, res)
  }
}

class TagHomeWorkDistExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_PHONE_HOME_WORK_DIST] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 2))
    val c2 = (arr.length == 4)
    c1 && c2
  }

  override def map(x: String) = {
    val arr = x.split(",")

    //    home_grid_id	居住地栅格id	字符串	32	否
    val home_grid_id = arr(0)

    //      work_grid_id	工作地栅格id	字符串	32	否
    val work_grid_id = arr(1)

    //      home_city	居住地城市	字符串		否		参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val home_city_code = gridCityMapBR.get(home_grid_id).getOrElse("-99")
    val home_city = if (StringUtils.isEmpty(home_city_code)) -99 else home_city_code.toInt

    //    work_city	工作地城市	字符串		否		参照城市编码对照表
    val work_city_code = gridCityMapBR.get(work_grid_id).getOrElse("-99")
    val work_city = if (StringUtils.isEmpty(work_city_code)) -99 else work_city_code.toInt


    // age	年龄	整型		否		999-未知
    val age = -99

    // sex	性别	整型		否		1-男；2-女，999-未知
    val sex = -99

    // adm_reg	用户归属地	字符串	32	否		用户归属地，参照城市编码对照表
    val adm_reg = -99

    // has_auto	有无小汽车	整型		否		1-有；0-没有；999-0未知；
    val has_auto = -99

    // user_qty	人口数	整型		否
    val user_qty = arr(2).toInt

    //fdate	日期	整型		否		示例20190800表示2019年8月份的数据
    val fdate = 20171200

    // create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt

    //source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cu"

    TAG_PHONE_HOME_WORK_DIST(
      home_grid_id: String,
      work_grid_id: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      home_city: Int,
      work_city: Int,
      source: String
    )
  }

  def execute() = {
    println("home_grid_id work_grid_id user_count expansion_rate")
    println("865149 865149 60 null")

    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        x.home_city != (-99) && !x.home_grid_id.equals("0") && x.work_city != (-99) && !x.work_grid_id.equals("0")
      })
    outputHBase(zkUrl, outputTable, res)
  }
}

class TagHomeDistCMExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_PHONE_HOME_DIST] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr)
    val c2 = (arr.length == 2)
    c1 && c2 && !x.contains("user_qty")
  }

  override def map(x: String) = {
    val arr = x.split(",")

    // user_type home_grid_id user_count expansion_rate

    //grid_id	居住地栅格ID	字符串	32	否
    val grid_id = arr(0)

    //user_type	用户类型	字符串	10	否
    // resident-常住人口；tourist-流动人口
    //resident-常住人口；tourist-流动人口
    val user_type = 1

    // age	年龄	整型		否		999-未知
    val age = -99

    // sex	性别	整型		否		1-男；2-女，999-未知
    val sex = -99

    // adm_reg	用户归属地	字符串	32	否		用户归属地，参照城市编码对照表
    val adm_reg = -99

    // has_auto	有无小汽车	整型		否		1-有；0-没有；999-0未知；
    val has_auto = -99

    // user_qty	人口数	整型		否
    val user_qty = arr(1).toInt

    //fdate	日期	整型		否		示例20190800表示2019年8月份的数据
    val fdate = 20190300

    // create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt

    // city	城市名称	字符串		否	1	参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val city_code = gridCityMapBR.get(grid_id).getOrElse("-99")
    val city = if (StringUtils.isEmpty(city_code)) -99 else city_code.toInt

    //source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cm"

    TAG_PHONE_HOME_DIST(
      grid_id: String,
      user_type: Int,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      city: Int,
      source: String

    )
  }

  def map2(x: String) = {
    val arr = x.split(",")

    // user_type home_grid_id user_count expansion_rate

    //grid_id	居住地栅格ID	字符串	32	否
    val grid_id = arr(0)

    //user_type	用户类型	字符串	10	否
    // resident-常住人口；tourist-流动人口
    //resident-常住人口；tourist-流动人口
    val user_type = 2

    // age	年龄	整型		否		999-未知
    val age = -99

    // sex	性别	整型		否		1-男；2-女，999-未知
    val sex = -99

    // adm_reg	用户归属地	字符串	32	否		用户归属地，参照城市编码对照表
    val adm_reg = -99

    // has_auto	有无小汽车	整型		否		1-有；0-没有；999-0未知；
    val has_auto = -99

    // user_qty	人口数	整型		否
    val user_qty = arr(1).toInt

    //fdate	日期	整型		否		示例20190800表示2019年8月份的数据
    val fdate = 20190300

    // create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt

    // city	城市名称	字符串		否	1	参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val city_code = gridCityMapBR.get(grid_id).getOrElse("-99")
    val city = if (StringUtils.isEmpty(city_code)) -99 else city_code.toInt

    //source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cm"

    TAG_PHONE_HOME_DIST(
      grid_id: String,
      user_type: Int,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      city: Int,
      source: String

    )
  }

  def execute() = {
    println("user_type home_grid_id user_count expansion_rate")
    println("RESIDENT,782494,18,null")

    process(inputPath)
  }

  def process(dir: String) = {
    val rdd1 = getRDD(inputPath + "/resident*": String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res1 = spark.createDataset(rdd1).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        x.city != (-99) && !x.grid_id.equals("0")
      })

    val rdd2 = getRDD(inputPath + "/tourist_*": String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res2 = spark.createDataset(rdd2).as[String]
      .filter(x => filter(x))
      .map(x => map2(x))
      .filter(x => {
        x.city != (-99) && !x.grid_id.equals("0")
      })

    val table = outputTable.substring(0, outputTable.lastIndexOf("_cm"))
    println("输出表格" + table)
    outputHBase(zkUrl, table, res1.union(res2))
  }
}

class TagWorkDistCMExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_PHONE_WORK_DIST] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr)
    val c2 = (arr.length == 2)
    c1 && c2 && !x.contains("user_qty")
  }

  override def map(x: String) = {
    val arr = x.split(",")

    // user_type home_grid_id user_count expansion_rate

    //grid_id	居住地栅格ID	字符串	32	否
    val grid_id = arr(0)

    // age	年龄	整型		否		999-未知
    val age = -99

    // sex	性别	整型		否		1-男；2-女，999-未知
    val sex = -99

    // adm_reg	用户归属地	字符串	32	否		用户归属地，参照城市编码对照表
    val adm_reg = -99

    // has_auto	有无小汽车	整型		否		1-有；0-没有；999-0未知；
    val has_auto = -99

    // user_qty	人口数	整型		否
    val user_qty = arr(1).toInt

    //fdate	日期	整型		否		示例20190800表示2019年8月份的数据
    val fdate = 20171200

    // create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt

    // city	城市名称	字符串		否	1	参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val city_code = gridCityMapBR.get(grid_id).getOrElse("-99")
    val city = if (StringUtils.isEmpty(city_code)) -99 else city_code.toInt

    //source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cm"

    TAG_PHONE_WORK_DIST(
      grid_id: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      city: Int,
      source: String

    )
  }

  def execute() = {
    println("user_type home_grid_id user_count expansion_rate")
    println("RESIDENT,782494,18,null")

    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        x.city != (-99) && !x.grid_id.equals("0")
      })

    outputHBase(zkUrl, outputTable.substring(0, outputTable.lastIndexOf("_cm")), res)
  }
}

class TagHomeWorkDistCMExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_PHONE_HOME_WORK_DIST] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 2))
    val c2 = (arr.length == 3)
    c1 && c2 && !x.contains("user_qty")
  }

  override def map(x: String) = {
    val arr = x.split(",")

    //    home_grid_id	居住地栅格id	字符串	32	否
    val home_grid_id = arr(0)

    //      work_grid_id	工作地栅格id	字符串	32	否
    val work_grid_id = arr(1)

    //      home_city	居住地城市	字符串		否		参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val home_city_code = gridCityMapBR.get(home_grid_id).getOrElse("-99")
    val home_city = if (StringUtils.isEmpty(home_city_code)) -99 else home_city_code.toInt

    //    work_city	工作地城市	字符串		否		参照城市编码对照表
    val work_city_code = gridCityMapBR.get(work_grid_id).getOrElse("-99")
    val work_city = if (StringUtils.isEmpty(work_city_code)) -99 else work_city_code.toInt


    // age	年龄	整型		否		999-未知
    val age = -99

    // sex	性别	整型		否		1-男；2-女，999-未知
    val sex = -99

    // adm_reg	用户归属地	字符串	32	否		用户归属地，参照城市编码对照表
    val adm_reg = -99

    // has_auto	有无小汽车	整型		否		1-有；0-没有；999-0未知；
    val has_auto = -99

    // user_qty	人口数	整型		否
    val user_qty = arr(2).toInt

    //fdate	日期	整型		否		示例20190800表示2019年8月份的数据
    val fdate = 20190300

    // create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt

    //source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cm"

    TAG_PHONE_HOME_WORK_DIST(
      home_grid_id: String,
      work_grid_id: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      home_city: Int,
      work_city: Int,
      source: String
    )
  }

  def execute() = {
    println("home_grid_id work_grid_id user_count expansion_rate")
    println("865149 865149 60 null")

    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        x.home_city != (-99) && !x.home_grid_id.equals("0") && x.work_city != (-99) && !x.work_grid_id.equals("0")
      })
    outputHBase(zkUrl, outputTable.substring(0, outputTable.lastIndexOf("_cm")), res)
  }
}

class TagTripDistExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_PHONE_TRIP_DIST] {

  //时间维度 1: 5 分钟 时间片 2: 15分钟时间片 3 :小时
  val time_dim = spark.sql(s"select time_dim from dim_phone_city_time where area='广东省' and source = '联通' and date = '201712' and tname = '$outputTable'").head.getInt(0)


  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 7))
    val c2 = (arr.length == 11)
    c1 && c2
  }

  override def map(x: String) = {
    val arr = x.split(",")

    //TOURIST,20171214,20171214,820012,886422,Normal,250,250,H->O,1,null

    //    start_grid_id	出发位置栅格id	字符串	32	否
    val start_grid_id = arr(3)

    //      end_grid_id	到达位置栅格id	字符串	32	否
    val end_grid_id = arr(4)

    //      start_city	省内出发城市	字符串	32	否		参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val home_city_code = gridCityMapBR.get(start_grid_id).getOrElse("-99")
    val start_city = if (StringUtils.isEmpty(home_city_code)) -99 else home_city_code.toInt

    //    end_city	省内到达城市	字符串	32	否		参照城市编码对照表
    val work_city_code = gridCityMapBR.get(end_grid_id).getOrElse("-99")
    val end_city = if (StringUtils.isEmpty(work_city_code)) -99 else work_city_code.toInt

    //start_utype	出发城市用户类型	整型	1	否		1-常住人口；2-流动人口
    val start_utype = Constants.MSIGNAL_USER_TYPE_MAP.get(arr(0)).getOrElse(-99)

    //end_utype	到达城市用户类型	整型	1	否		1-常住人口；2-流动人口
    val end_utype = Constants.MSIGNAL_USER_TYPE_MAP.get(arr(0)).getOrElse(-99)

    //start_ptype	起点类型	整型	1	否		0-来访；1-工作地；2-家庭
    val start_ptype = -99

    //end_ptype	终点类型	整型	1	否		0-来访；1-工作地；2-家庭
    val end_ptype = -99

    //start_time	出发小时	整型		否		1-24小时
    val start_time = arr(1).substring(6, 8).toInt

    // end_time	到达小时	整型		否		1-24小时
    val end_time = arr(2).substring(6, 8).toInt

    //trip_mode	出行目的	字符串	10	否		w2h-工作地到家；h2w-家到工作地；oth-其他
    val trip_mode = Constants.MSIGNAL_TRIP_MODE.get(arr(8)).getOrElse(-99)

    //    aggtype	出行类型	字符串	32	否		normal-正常；trainStation-枢纽；scenicRegion-景点；
    val aggtype = Constants.MSIGNAL_AGG_TYPE.get(arr(5)).getOrElse(-99)

    //    age	年龄	整型		否		999为未知
    val age = -99

    //      sex	性别	整型		否		1-男；2-女；999-未知
    val sex = -99

    // trip_qty	出行量	整型		否
    val trip_qty = arr(9).toInt

    //adm_reg	用户归属地	字符串	32	否		参照城市编码对照表
    val adm_reg = -99

    //    create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt

    //    source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cu"

    //fdate	日期	整型		否		示例20190800表示2019年8月份的数据
    val fdate = arr(1).toInt


    TAG_PHONE_TRIP_DIST(
      start_grid_id: String,
      end_grid_id: String,
      fdate: Int,
      start_city: Int,
      end_city: Int,
      start_ptype: Int,
      end_ptype: Int,
      start_utype: Int,
      end_utype: Int,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      start_time: Int,
      end_time: Int,
      trip_mode: Int,
      aggtype: Int,
      trip_qty: Int,
      create_time: Int,
      source: String,
      time_dim: Int

    )
  }

  def execute() = {
    println("home_grid_id work_grid_id user_count expansion_rate")
    println("TOURIST,20171214,20171214,820012,886422,Normal,250,250,H->O,1,null")

    process(inputPath)

  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        x.start_city != (-99) && !x.start_grid_id.equals("0") && x.end_city != (-99) && !x.end_grid_id.equals("0")
      })
    outputHBase(zkUrl, outputTable, res)
  }
}

class TagTripDistCMExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_PHONE_TRIP_DIST] {

  val table = outputTable.substring(0, outputTable.lastIndexOf("_cm"))


  val time_dim = spark.sql(s"select time_dim from dim_phone_city_time where area='广东省' and source = '移动' and date = '201903' and tname = '$table'").head.getInt(0)


  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 7))
    c1 && !x.contains("o_grid_id")
  }

  override def map(x: String) = {
    val arr = x.split(",")
    //    o_grid_id,d_grid_id,o_city_user_type,d_city_user_type,o_hour,d_hour,trip_purpose,user_qty,trip_data
    //  1861401,1863013,R,R,10,12,H2W,1,20190324

    //    start_grid_id	出发位置栅格id	字符串	32	否
    val start_grid_id = arr(0)

    //      end_grid_id	到达位置栅格id	字符串	32	否
    val end_grid_id = arr(1)

    //      start_city	省内出发城市	字符串	32	否		参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val home_city_code = gridCityMapBR.get(start_grid_id).getOrElse("-99")
    val start_city = if (StringUtils.isEmpty(home_city_code)) -99 else home_city_code.toInt

    //    end_city	省内到达城市	字符串	32	否		参照城市编码对照表
    val work_city_code = gridCityMapBR.get(end_grid_id).getOrElse("-99")
    val end_city = if (StringUtils.isEmpty(work_city_code)) -99 else work_city_code.toInt


    //start_utype	出发城市用户类型	整型	1	否		1-常住人口；2-流动人口
    val start_utype = Constants.MSIGNAL_USER_TYPE_MAP.get(arr(2)).getOrElse(-99)

    //end_utype	到达城市用户类型	整型	1	否		1-常住人口；2-流动人口
    val end_utype = Constants.MSIGNAL_USER_TYPE_MAP.get(arr(3)).getOrElse(-99)

    //start_ptype	起点类型	整型	1	否		0-来访；1-工作地；2-家庭
    val start_ptype = -99

    //end_ptype	终点类型	整型	1	否		0-来访；1-工作地；2-家庭
    val end_ptype = -99

    //start_time	出发小时	整型		否		1-24小时
    val start_time = arr(4).toInt

    // end_time	到达小时	整型		否		1-24小时
    val end_time = arr(5).toInt

    //trip_mode	出行目的	字符串	10	否		w2h-工作地到家；h2w-家到工作地；oth-其他
    val trip_mode = Constants.MSIGNAL_TRIP_MODE2.get(arr(6).toLowerCase).getOrElse(-99)

    //    aggtype	出行类型	字符串	32	否		normal-正常；trainStation-枢纽；scenicRegion-景点；
    val aggtype = -99

    //    age	年龄	整型		否		999为未知
    val age = -99

    //      sex	性别	整型		否		1-男；2-女；999-未知
    val sex = -99

    // trip_qty	出行量	整型		否
    val trip_qty = arr(7).toInt

    //adm_reg	用户归属地	字符串	32	否		参照城市编码对照表
    val adm_reg = -99

    //    create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt

    //    source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cm"

    //fdate	日期	整型		否		示例20190800表示2019年8月份的数据
    val fdate = arr(8).toInt


    TAG_PHONE_TRIP_DIST(
      start_grid_id: String,
      end_grid_id: String,
      fdate: Int,
      start_city: Int,
      end_city: Int,
      start_ptype: Int,
      end_ptype: Int,
      start_utype: Int,
      end_utype: Int,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      start_time: Int,
      end_time: Int,
      trip_mode: Int,
      aggtype: Int,
      trip_qty: Int,
      create_time: Int,
      source: String,
      time_dim: Int

    )
  }

  def execute() = {
    println(" o_grid_id,d_grid_id,o_city_user_type,d_city_user_type,o_hour,d_hour,trip_purpose,user_qty,trip_data")
    println("TOURIST,20171214,20171214,820012,886422,Normal,250,250,H->O,1,null")

    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        x.start_city != (-99) && !x.start_grid_id.equals("0") && x.end_city != (-99) && !x.end_grid_id.equals("0")
      })
    outputHBase(zkUrl, table, res)
  }
}

class TagActivityExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_PHONE_ACTIVITY] {

  //时间维度 1: 5 分钟 时间片 2: 15分钟时间片 3 :小时
  val time_dim = spark.sql(s"select time_dim from dim_phone_city_time where area='广东省' and source = '联通' and date = '201712' and tname = '$outputTable'").head.getInt(0)


  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 7))
    val c2 = (arr.length == 8)
    c1 && c2
  }

  override def map(x: String) = {
    val arr = x.split(",")

    //RESIDENT,20171213,20171213,768881,Port,129,169,1,null

    //--字段名称	备注	类型	长度	是否为空	分区/索引键	特殊说明
    //--grid_id	活动栅格id	字符串	32	否
    val grid_id = arr(3)

    //--user_type	用户类型	整型	10	否		1-常住人口；2-流动人口
    val user_type = Constants.MSIGNAL_USER_TYPE_MAP.get(arr(0)).getOrElse(-99)

    //--act_time	活动时间，小时	整型		否		时间粒度请看time_dim时间粒度字段
    val act_time = arr(5).toInt

    //--fdate	日期	整型		否		示例20190802；20190900表示2019年9月份的统计数据
    val fdate = arr(1).toInt

    //--age	年龄	整型		否		999-未知
    val age = -99

    //--sex	性别	整型		否		1-男；2-女，999-未知
    val sex = -99

    //--adm_reg	用户归属地	整型	32	否		参照城市编码对照表
    val adm_reg = -99

    //--user_qty	活动人数	整型		否
    val user_qty = arr(7).toInt


    //--create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt


    //--city	城市名称	整型		否	1	参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val home_city_code = gridCityMapBR.get(grid_id).getOrElse("-99")
    val city = if (StringUtils.isEmpty(home_city_code)) -99 else home_city_code.toInt

    //--source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cu"

    TAG_PHONE_ACTIVITY(
      grid_id: String,
      user_type: Int,
      act_time: Int,
      time_dim: Int,
      fdate: Int,
      age: Int,
      sex: Int,
      user_qty: Int,
      adm_reg: Int,
      create_time: Int,
      city: Int,
      source: String
    )
  }

  def execute() = {
    println("home_grid_id work_grid_id user_count expansion_rate")
    println("TOURIST,20171214,20171214,820012,886422,Normal,250,250,H->O,1,null")

    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        !x.grid_id.equals("0")
      })
    outputHBase(zkUrl, outputTable, res)
  }
}

class TagActivityCMExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_PHONE_ACTIVITY] {

  val table = outputTable.substring(0, outputTable.lastIndexOf("_cm"))


  val time_dim = spark.sql(s"select time_dim from dim_phone_city_time where area='广东省' and source = '移动' and date = '201903' and tname = '$table'").head.getInt(0)


  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 3))
    c1 && !x.contains("grid_id")
  }

  override def map(x: String) = {
    val arr = x.split(",")

    //user_type,grid_id,act_hour,user_qty
    //R,109967,13,91
    //R,110007,11,913

    //--字段名称	备注	类型	长度	是否为空	分区/索引键	特殊说明
    //--grid_id	活动栅格id	字符串	32	否
    val grid_id = arr(1)

    //--user_type	用户类型	整型	10	否		1-常住人口；2-流动人口
    val user_type = Constants.MSIGNAL_USER_TYPE_MAP.get(arr(0)).getOrElse(-99)

    //--act_time	活动时间，小时	整型		否		时间粒度请看time_dim时间粒度字段
    val act_time = arr(2).toInt

    //--fdate	日期	整型		否		示例20190802；20190900表示2019年9月份的统计数据
    val fdate = arr(1).toInt

    //--age	年龄	整型		否		999-未知
    val age = -99

    //--sex	性别	整型		否		1-男；2-女，999-未知
    val sex = -99

    //--adm_reg	用户归属地	整型	32	否		参照城市编码对照表
    val adm_reg = -99

    //--user_qty	活动人数	整型		否
    val user_qty = arr(3).toInt


    //--create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt


    //--city	城市名称	整型		否	1	参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val home_city_code = gridCityMapBR.get(grid_id).getOrElse("-99")
    val city = if (StringUtils.isEmpty(home_city_code)) -99 else home_city_code.toInt

    //--source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cm"

    TAG_PHONE_ACTIVITY(
      grid_id: String,
      user_type: Int,
      act_time: Int,
      time_dim: Int,
      fdate: Int,
      age: Int,
      sex: Int,
      user_qty: Int,
      adm_reg: Int,
      create_time: Int,
      city: Int,
      source: String
    )
  }

  def execute() = {
    println(" o_grid_id,d_grid_id,o_city_user_type,d_city_user_type,o_hour,d_hour,trip_purpose,user_qty,trip_data")
    println("TOURIST,20171214,20171214,820012,886422,Normal,250,250,H->O,1,null")

    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        !x.grid_id.equals("0")
      })
    outputHBase(zkUrl, table, res)
  }
}

class TagGpsTruckExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[TAG_GPS_Truck] {

  val table = outputTable

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

    //20171001,28,H,粤BV0235,123.124816,41.114633,0.0,35,1,1,2, 0,0,0,33554432,0
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
    val lng = BigDecimal(arr(4))

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5))

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = arr(6).toDouble

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

    val license_prefix: String = vehicle_id.substring(0, 2)

    TAG_GPS_Truck(
      loc_time: Long,
      fdate: Int,
      ftime: String,
      vehicle_id: String,
      MD5Utils.encode16(vehicle_id): String,
      lng: BigDecimal,
      lat: BigDecimal,
      speed: Double,
      angle: Int,
      company_code: String,
      altitude: Int,
      vehicle_color: Integer,
      total_mileage: Long,
      city: Int,
      vehicle_status: String,
      license_prefix: String
    )
  }

  def execute() = {
    println(" o_grid_id,d_grid_id,o_city_user_type,d_city_user_type,o_hour,d_hour,trip_purpose,user_qty,trip_data")
    println("TOURIST,20171214,20171214,820012,886422,Normal,250,250,H->O,1,null")

    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.speed < 900)

    outputHBase(zkUrl, table, res)
  }
}


//TODO: class TagSectionFLowExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String)
//println("读取地磁流量 HDFS数据")
//println("数据格式：serial_id intersection_fid lane_fid record_time  vehicle_num  big_vehicle  mid_vehicle  small_vehicle  mini_vehicle  motor_vehicle  avg_headway  max_headway  min_headway  accum_headway  avg_speed  max_speed  min_speed  accum_speed  avg_occupancy  max_occupancy  min_occupancy  accum_occupancy  avg_interval  max_interval  min_interval  accum_interval  avg_vehicle_length  max_vehicle_length  min_vehicle_length  accum_vehicle_length  max_queue_length  min_queue_length  accum_queue_length  accum_queue_length  run_redlight_num  preceding_wayfull_time  city  year  month  day")
//println("2939131247,19980077,-15093343,201,2017-01-01 15:34:23,1,0,0,1,0,0,0,0,0,0,0,0,131856,131856,131856,131856,16.987055,16.987055,16.987055,16.987055,866,866,866,866,130896,130896,130896,130896,4.746764,4.746764,4.746764,4.746764,0,0,0,0,0,0,1")
//  val arr = x.split(",")
//StdUtils.allNotEmpty(arr.slice(0, 4)) && arr.length == 21 && arr(1).substring(0, 4).toInt <= DateTime.now().getYear






