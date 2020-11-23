package com.sutpc.bigdata.executor.std

import com.sutpc.bigdata.common.Constants
import com.sutpc.bigdata.executor.BaseExecutor
import com.sutpc.bigdata.schema.hive._
import com.sutpc.bigdata.utils.{CoordinateTransformUtil, MD5Utils, StdUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession
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
  *          date 2019/8/28  14:01
  */
class StdGpsOthersExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String, category: Int) extends BaseExecutor[STD_GPS_OTHER] {

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
    val speed = BigDecimal(arr(5)).setScale(1)

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
    val vehicle_type = category

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

    //    outputHBase(zkUrl, outputTable, res)

    outputHiveStd(spark, "s_vehicle_gps_others", Array("vehicle_type", "city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_id", "loc_time"))

  }
}

class StdBikeLocExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_BIKE_LOC] {

  override def filter(x: String): Boolean = {
    true
  }

  override def map(x: String) = {

    //    2017-04-02T00:11:01,7556086534#,2,7556086534,1,0,113.884867,22.857553

    //    Mobike 数据格式说明
    //
    //    本数据是通过网络爬虫爬取的Mobike位置，每次可以获取一个小矩形窗格内的单车，大约每12分钟完整遍历一次深圳。由于爬取的矩形窗格相互之间有一定重叠，在一组数据内，每辆单车会在不同时间被爬取多次，在分析数据的时候，需要进行去重处理。
    //    数据的格式如下表：
    //    时间	单车ID	单车类型	单车ID（同）	null	null	经度	纬度
    //    2017-04-02T16:47:53	7310026486#	999	7310026486	1	0	112.92599	28.411629
    //    2017-04-02T16:47:53	7316504360#	1	7316504360	1	0	112.926665	28.413342
    //    2017-04-02T16:47:53	7316510237#	2	7316510237	1	0	112.926909	28.413196
    //    有两个字段记录了单车ID，除#号外，二者在数字部分一致
    //    单车类型：1-Mobike, 2-Mobike Lite, 999-红包车
    //    坐标为火星坐标系（GCJ02）
    //
    //    文件夹内含有多个压缩文件，解压后为CSV格式
    //
    //    每一个文件为一次遍历的数据，且该次遍历的时间起点即为该文件名时间(如2017/04/02 00:23:13)，终点即为下一个文件的起始时间(如2017/04/04 00:34:17)
    //
    //    如有问题请联系：
    //    杨源譞（xuan）
    //    gyyy@leeds.ac.uk

    val arr = x.split(",")

    //    --bike_id	单车编号	字符串	32	否
    val bike_id = arr(3).trim

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(arr(0).trim.replaceAll("T", ""), DateTimeFormat.forPattern("yyyy-MM-ddHH:mm:ss"))
    val loc_time = dataTime.getMillis / 1000

    //坐标转换
    val gps84 = CoordinateTransformUtil.gcj02towgs84(arr(6).trim.toDouble, arr(7).trim.toDouble)

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(gps84(0)).setScale(6)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(gps84(1)).setScale(6)

    //    运营商 1-Mobike；2-ofo；3-其他'
    val operator = 1

    //   车辆类型 1-Mobike, 2-Mobike Lite, 999-红包车
    val bike_type = arr(2).trim.toInt

    //city	城市名称	字符串		否	1	参照城市编码对照表
    // 深圳
    val city_name = "shenzhen"
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt


    val year: String = dataTime.getYear.toString
    val month: String = StdUtils.fillZero(2, dataTime.getMonthOfYear.toString)
    val day: String = StdUtils.fillZero(2, dataTime.getDayOfMonth.toString)


    //ftime	时间	字符串		否		190802
    val ftime = dataTime.toString("HHmmss")

    STD_BIKE_LOC(
      bike_id: String,
      operator: Int,
      bike_type: Int,
      loc_time: Long,
      lng: BigDecimal,
      lat: BigDecimal,
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

    //    outputHBase(zkUrl, outputTable, res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, dupliCols = Seq("bike_id", "loc_time"))

  }
}

//class StdBikeLoc2Executor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_BIKE_LOC] {
//
//  override def filter(x: String): Boolean = {
//    true
//  }
//
//  override def map(x: String) = {
//
////    2017-04-02T00:11:01,7556086534#,2,7556086534,1,0,113.884867,22.857553
//
//
//    //    2017年11月份龙岗区摩拜单车的出行OD数据，抽样率约1%，约12万条记录。
//    //    包含用户ID、车辆ID、开始时间、结束时间、起终点坐标等信息
//
//    val arr = x
//      .replaceAll("INSERT INTO `a_share` \\(`vehicle_num`, `location_time`, `lat`, `lng`, `id`, `lockstatus`\\) VALUES \\(",
//        "".stripMargin)
//      .replaceAll("\\);", "")
//      .replaceAll("'", "")
//      .split(",")
//
//    //8c432e4aae24b2025b5e3620b3974630, 2018-10-1 00:00:00, 22.633789, 113.840496, 1, 开锁
//
//    //    --bike_id	单车编号	字符串	32	否
//    val bike_id = arr(0).trim
//    //      --loc_time	定位时间	整型		否		10位UNIX时间戳
//
//    //loc_time	定位日期	整型	10	否		unix10位时间戳
//    val dataTime = DateTime.parse(arr(1).trim, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
//    val loc_time = dataTime.getMillis / 1000
//
//    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
//    val lng = BigDecimal(arr(3).trim)
//
//    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
//    val lat = BigDecimal(arr(2).trim)
//
//    //    --lock_status	单车状态	字符串	10	否		1-开锁；2-关锁
//    val lock_status = Constants.BIKE_LOCK_STATUS_MAP.getOrElse(arr(5).trim, "-99").toString.toInt
//
//    //    --order_no	序号	整型		否		按行数自增
//    val order_no = arr(4).trim.toInt
//
//    //city	城市名称	字符串		否	1	参照城市编码对照表
//    // 深圳
//    val city_name = "shenzhen"
//    val city = broadcast.value.get(city_name).getOrElse("-99").toInt
//
//
//    val year: String = dataTime.getYear.toString
//    val month: String = StdUtils.fillZero(2, dataTime.getMonthOfYear.toString)
//    val day: String = StdUtils.fillZero(2, dataTime.getDayOfMonth.toString)
//
//
//    //ftime	时间	字符串		否		190802
//    val ftime = dataTime.toString("HHmmss")
//
//
//    S_BIKE_LOC(
//      bike_id: String,
//      operator: String,
//      bike_type: String,
//      loc_time: Long,
//      lng: BigDecimal,
//      lat: BigDecimal,
//      city: Int,
//      year: String,
//      month: String,
//      day: String
//    )
//
//
//  }
//
//  def execute() = {
//    println("读取【" + inputPath + "】数据")
//    println("数据格式：定位日期 定位时刻 用户ID 导航状态 经度 纬度 瞬时速度 方位角")
//    println("20170930,235958,M338,粤BN6601,114.029805,22.670012,37,135,1,1")
//
//    smartLoop(inputPath: String, spark: SparkSession)
//  }
//
//  def process(dir: String) = {
//    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
//    import spark.implicits._
//    val res = spark.createDataset(rdd).as[String]
//      .filter(x => filter(x))
//      .map(x => map(x))
//
//    cache(res)
//
//    //    outputHBase(zkUrl, outputTable, res)
//
//    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res)
//
//  }
//}

class StdActivityCuExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_ACTIVITY] {

  //时间维度 1: 5 分钟 时间片 2: 15分钟时间片 3 :小时
  val time_dim = spark.sql(s"select time_dim from dim_phone_city_time where area='广东省' and source = '联通' and date = '201712' and tname = '$outputTable'").head.getInt(0)


  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 7))
    val c2 = (arr.length == 9)
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

    //--fdate	日期	整型		否		示例20190802；20190900表示2019年9月份的统计数据 20190312
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

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    val creator = "zhangyongtian"

    S_PHONE_ACTIVITY(
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
      source: String,
      creator: String,
      year: String,
      month: String,
      day: String
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

    //    cache(res)

    outputHiveStd(spark, outputTable, Array("source", "city", "year", "month", "day"), res, isCopy2Tag = true, tag_table = "t_phone_activity", dupliCols = Seq("grid_id", "act_time", "user_type", "source"))
  }
}

class StdActivityCMExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_ACTIVITY] {

  val table = outputTable

  val time_dim = spark.sql(s"select time_dim from dim_phone_city_time where area='广东省' and source = '移动' and date = '201903' and tname = '$table'").head.getInt(0)

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 3))
    c1 && !x.contains("grid_id") && !x.contains("\\N")
  }

  override def map(x: String) = {
    val arr = x.split(",")

    //user_type,grid_id,act_hour,user_qty
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
    //    val fdate = 20190300
    val fdate = 20190312

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

    val creator = "zhangyongtian"

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_ACTIVITY(
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
      source: String,
      creator: String,
      year: String,
      month: String,
      day: String
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

    cache(res)

    outputHiveStd(spark, outputTable, Array("source", "city", "year", "month", "day"), res, isCopy2Tag = true, tag_table = "t_phone_activity", dupliCols = Seq("grid_id", "act_time", "user_type", "source"))
  }
}


//�ֶκ���˵�������ƺ���,������ɫ(0���ơ�1���ơ�2���ơ�3�»��ơ�4��ɫ���ơ�5������6������8�侯��7�»�ɫ����),����ID,��������,����,ͨ��ʱ��,����,�ٶ�,ͼƬ1,ͼƬ2,ͼƬ3,ͼƬ4,ͼƬ5
//��B76A79,0,10100206#,���������������������,3,2015-09-16 09:00:00,,-1,20150916090000131010020631_40#,20150916090000131010020632_40#,#,#,#
//��B2MY06,0,10100206#,���������������������,2,2015-09-16 09:00:00,,-1,20150916090000481010020621_28#,20150916090000481010020622_28#,#,#,#
//��B7HG48,0,10100205#,���������������������,4,2015-09-16 09:00:00,,-1,20150916090000981010020541_29#,20150916090000981010020542_29#,#,#,#
//��B162LQ,0,10100406#,���Ӵ����������������,7,2015-09-16 09:00:00,,-1,20150916090000501010040671_44#,20150916090000501010040672_44#,#,#,#
//��B1G3W2,0,10100406#,���Ӵ����������������,8,2015-09-16 09:00:00,,-1,20150916090000491010040681_43#,20150916090000491010040682_43#,#,#,#
//字段含义说明：车牌号码	车牌颜色(0蓝牌、1黑牌、2黄牌、3新黄牌、4黄色后牌、5警车、6军车、8武警、7新黄色后牌)	监测点ID	监测点名称	车道	通过时间	车标	速度	图片1	图片2	图片3	图片4	图片5
//粤B76A79	0	10100206#	北环大道新洲立交西往东	3	2015/9/16 9:00		-1	20150916090000131010020631_40#	20150916090000131010020632_40#	#	#	#
//粤B2MY06	0	10100206#	北环大道新洲立交西往东	2	2015/9/16 9:00		-1	20150916090000481010020621_28#	20150916090000481010020622_28#	#	#	#
//粤B7HG48	0	10100205#	北环大道新洲立交东往西	4	2015/9/16 9:00		-1	20150916090000981010020541_29#	20150916090000981010020542_29#	#	#	#
//粤B162LQ	0	10100406#	滨河大道彩田立交西往东	7	2015/9/16 9:00		-1	20150916090000501010040671_44#	20150916090000501010040672_44#	#	#	#
//粤B1G3W2	0	10100406#	滨河大道彩田立交西往东	8	2015/9/16 9:00		-1	20150916090000491010040681_43#	20150916090000491010040682_43#	#	#	#
//粤B9L6W5	0	10200904#	罗沙路罗芳人行天桥东往西	4	2015/9/16 9:00	其它	10	20150916090000741020090441_0_0_0_0_29#	20150916090000741020090442_0_0_0_0_29#	#	#	#

class StdRecognitionExecutor2015(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_ROAD_PLATE_RECOGNITION] {

  override def filter(x: String): Boolean = {

    val a = x.replaceAll("\\{.*?(?!\\{)(?<!\\})\\}", "").replaceAll("\n", "").trim

    val b =
      """
        |face_mask2,vehicle_color,plate_rect,logo_rect,pilot_safe_belt,abs_stime,image_url2,image_url1,vehicle_speed,location_x,i_annual_tags,image_url3,
        |location_y,pass_hour,use_phone,pilot_sunvisor,lane_no,vehicle_rect,location_z,relative_etime,vice_pilot_rect,lane_name,id,capture_user,whole_height,
        |relative_stime,hold_a_child,i_vice_sunvisor,pass_timestamp,car_body_small_type,plate_type,vehicle_model_name,vehicle_brand,plate_no,save_time,i_pendant,
        |tissue_box,whole_width,status,location_hashcode,i_tissuebox,marker_windows,vehicle_special_type,abs_etime,pass_date,data_src_type,pendant,vice_pilot_sunvisor,annual_tags_layout,
        |smoking,hold_a_child2,direction,vehicle_brightness,plate_color,pass_time,i_pilot_sunvisor,checkpoint_id,face_mask,vehicle_type,
        |smoking2,pilot_rect,capture_address,i_marker_windows,host_id,vice_pilot_safe_belt,vehicle_model_year,vehicle_sub_brand,annual_tags,plate_confidence
      """.stripMargin.replaceAll("\n", "").trim

    a.split(",").length == b.split(",").length
  }

  override def map(x: String) = {

    //    4a9dfhle445c026101950192010802018-10-01 07:51:582018-10-01 07:51:5800粤NNM755110008{   obj_bottom : 932,   obj_left : 1034,   obj_right : 1553,   obj_top : 499}{   obj_bottom : 884,   obj_left : 1241,   obj_right : 1355,   obj_top : 854}0003{   crossing_data_type : 0,   crossing_sub_type : 0,   crossingcode : 02610195,   crossingid : 2988,   crossingtype : 5,   device_time : 2018-10-01 00:00:25.416,   devicetime_confidence : 2,   directionindex : 0,   imagepath : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,   laneno : 1,   latitude : 0,   longitude : 0,   passid : 806096011990325145,   passtime : 2018-10-01 00:00:25.000,   picurl_num : 1,   pilotsafebelt : 0,   pilotsunvisor : 7,   platecolor : 2,   plateno : 车牌,   platestate : 0,   platetype : 1,   platetype_gb : 2,   platform_time : 2018-09-30 23:50:59.416,   tfsid : 101,   transfer_time : 2018-09-30 23:50:59.863,   veh_pic_type_1 : 1,   veh_pic_url_1 : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,   vehiclecolor : 4,   vehiclecolor_gb : J,   vehiclecolordepth : 1,   vehicleinfolevel : 1,   vehiclelen : 0,   vehiclelogo : 0,   vehiclemodel : 20122014,   vehiclespeed : 0,   vehiclestate : 0,   vehiclesublogo : 0,   vehicletype : 0,   vehicletype_gb : 1,   video_bucket_name : ,   video_cloud_id : 0,   video_object_key : }1车牌002018-10-01 00:00:2520181001252018-10-01 07:51:58http://190.192.2.20:6120//pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i9061011111100

    //    obj_bottom : 932,

    //    obj_left : 1034,
    //    obj_right : 1553,
    //    obj_top : 499}
    //  {
    //    obj_bottom : 884,
    //    obj_left : 1241,
    //    obj_right : 1355,
    //    obj_top : 854}
    //  0003{
    //    crossing_data_type : 0,
    //    crossing_sub_type : 0,
    //    crossingcode : 02610195,//卡口编号
    //    crossingid : 2988, //卡口 ID
    //    crossingtype : 5,
    //    device_time : 2018-10-01 00:00:25.416,//数据上传时前端设备/平台时间 //字符串，格式 2015-02-02 19:30:00.123
    //    devicetime_confidence : 2,//该前端设备/平台时间是否可信任 整型
    //取值：0-未知，1-可信任，2-不可信任
    //    directionindex : 0,//方向编号 默认值 0
    //    imagepath : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,
    //    laneno : 1,//车道编号 默认值 0
    //    latitude : 0,
    //    longitude : 0,
    //    passid : 806096011990325145,//过车流水号
    //    passtime : 2018-10-01 00:00:25.000,//过车时间 格式：字符串  //格式形式 2015-02-02 19:30:00.123
    //    picurl_num : 1,
    //    pilotsafebelt : 0,
    //    pilotsunvisor : 7,
    //    platecolor : 2,
    //    plateno : 车牌,
    //    platestate : 0,
    //    platetype : 1,
    //    platetype_gb : 2,
    //    platform_time : 2018-09-30 23:50:59.416,//数据接入高清平台时，高清平台时间
    //字符串，格式 2015-02-02 19:30:00.123
    //    tfsid : 101,
    //    transfer_time : 2018-09-30 23:50:59.863,//高清平台转发该数据的时间
    //字符串，格式 2015-02-02 19:30:00.123
    //    veh_pic_type_1 : 1,
    //    veh_pic_url_1 : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,
    //    vehiclecolor : 4,
    //    vehiclecolor_gb : J,
    //    vehiclecolordepth : 1,
    //    vehicleinfolevel : 1,
    //    vehiclelen : 0,
    //    vehiclelogo : 0,
    //    vehiclemodel : 20122014,
    //    vehiclespeed : 0,
    //    vehiclestate : 0,
    //    vehiclesublogo : 0,
    //    vehicletype : 0,
    //    vehicletype_gb : 1,
    //    video_bucket_name : ,
    //    video_cloud_id : 0,
    //    video_object_key :

    val a = x.replaceAll("\\{.*?(?!\\{)(?<!\\})\\}", "").replaceAll("\n", "").trim

    val b =
      """
        |face_mask2,vehicle_color,plate_rect,logo_rect,pilot_safe_belt,abs_stime,image_url2,image_url1,vehicle_speed,location_x,i_annual_tags,image_url3,
        |location_y,pass_hour,use_phone,pilot_sunvisor,lane_no,vehicle_rect,location_z,relative_etime,vice_pilot_rect,lane_name,id,capture_user,whole_height,
        |relative_stime,hold_a_child,i_vice_sunvisor,pass_timestamp,car_body_small_type,plate_type,vehicle_model_name,vehicle_brand,plate_no,save_time,i_pendant,
        |tissue_box,whole_width,status,location_hashcode,i_tissuebox,marker_windows,vehicle_special_type,abs_etime,pass_date,data_src_type,pendant,vice_pilot_sunvisor,annual_tags_layout,
        |smoking,hold_a_child2,direction,vehicle_brightness,plate_color,pass_time,i_pilot_sunvisor,checkpoint_id,face_mask,vehicle_type,
        |smoking2,pilot_rect,capture_address,i_marker_windows,host_id,vice_pilot_safe_belt,vehicle_model_year,vehicle_sub_brand,annual_tags,plate_confidence
      """.stripMargin.replaceAll("\n", "").trim

    val map = b.split(",").zip(a.split(",")).filter(r => {
      val arr = Array("id", "plate_no", "pass_timestamp", "save_time", "checkpoint_id"
        , "lane_no", "vehicle_speed", "vehicle_type", "plate_type", "plate_confidence", "plate_color", "direction", "status", "capture_address"
      )
      arr.contains(r._1)
    }).map(r => if (r._2.isEmpty) (r._1, "-99") else r).toMap

    //id String  COMMENT '编号',
    val id = map.get("id").get //UUID

    //    val id = map2.getOrElse("crossingcode", "-99").toInt

    //vehicle_fid String  COMMENT '车牌号码',
    val vehicle_fid = map.get("plate_no").get

    //lane_fid String  COMMENT '车道编号',//0
    val lane_fid = map.get("lane_no").get

    //speed decimal(4,1)	COMMENT '瞬时速度（km/h）',
    val speed = BigDecimal(map.get("vehicle_speed").get).setScale(1) //vehiclespeed

    //drive_dir String   COMMENT '出行方向',
    val drive_dir = map.get("direction").get.trim.toInt

    //    val dataTime = DateTime.parse(StdUtils.fillTime(arr1(5).trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

    val dataTime = DateTime.parse(StdUtils.fillTime(map.get("pass_timestamp").get.trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

    //record_time BIGINT  COMMENT '定位时间	unix10位时间戳',2018-10-01 00:00:25.000
    val recognition_time = dataTime.getMillis / 1000

    //city	城市名称	字符串		否	1	参照城市编码对照表
    // 深圳
    val city_name = "shenzhen"
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    val year = dataTime.getYear.toString.toInt
    val month = StdUtils.fillZero(2, dataTime.getMonthOfYear.toString).toInt
    val day = StdUtils.fillZero(2, dataTime.getDayOfMonth.toString).toInt

    val license_prefix: String = if (vehicle_fid.length < 3) "" else vehicle_fid.substring(0, 2)


    //    粤B_D_<12345的md5加密>
    //    6位车牌的是电动车
    //    5位的是燃油
    //      如：粤BD12345是电动车；粤B12345是燃油车
    val encry_id = vehicle_fid

    val upload_time = DateTime.parse(StdUtils.fillTime(map.getOrElse("save_time", "-99").trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis / 1000

    //device_fid String   COMMENT '卡口编号'
    val detector_id = StdUtils.fillZero(8, map.getOrElse("checkpoint_id", "-99").trim)

    val vehicle_type = map.getOrElse("vehicle_type", "-99").trim.toInt
    val plate_type = map.getOrElse("plate_type", "-99").trim.toInt
    val illegal_type = -99
    val degree_of_confidence = map.getOrElse("plate_confidence", "-99").trim.toInt
    val plate_color = map.getOrElse("plate_color", "-99").trim.toInt
    val crossing_type = map.getOrElse("status", "-99").trim.toInt
    val capture_address = map.getOrElse("capture_address", "-99").trim

    //    encry_id	加密车牌	字符串	 	否	 	采用"<车牌前缀>_<是否电动车>_<加密串>"的形式表示。粤BD12345加密后的形式为"粤B_D_<12345的md5加密>"。非电动车用符号"o"表示

    S_ROAD_PLATE_RECOGNITION(
      id: String, // ""
      vehicle_fid: String, // ""
      encry_id: String, // ""
      recognition_time: Long, // "10为UNIX时间戳"
      upload_time: Long, // "10为UNIX时间戳"
      detector_id: String, // "不足8为左边补0"
      lane_fid: String, // "待补充"
      speed: BigDecimal, // "单位km/h"
      vehicle_type: Int, // ""
      plate_type: Int, // "待补充"
      illegal_type: Int, // "待补充"
      degree_of_confidence: Int, // "0-未知，1-可信任，2-不可信任"
      plate_color: Int, // "待补充"
      drive_dir: Int, // "待补充0-其他；1-由东向西；2-由西向东；3-由南向北；4-由北向南；5-由东南向西北；6-由西北向东南；7-由东北向西南；8-由西南向东北"
      crossing_type: Int, // "1-卡口；2-电警；3-RFID卡口；4-互联停车场；5-分局卡口；6-加油站；7-违停球；9-移动卡口"
      capture_address: String, // ""
      city: Int, // "参照城市编码对照表"
      year: Int, // "示例2019"
      month: Int, // "示例8"
      day: Int // "示例2"
    )


  }

  def execute() = {
    println("读取【" + inputPath + "】数据")

    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.vehicle_fid.length > 5)

    //    cache(res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_fid", "recognition_time"))

  }
}

//车牌,,13,0,,2016-04-01 00:00:29,2016-04-01 00:01:23,30606705,0,3,,,,,,-1.00,,,2,20160401000029013060670531_40,20160401000029013060670532_40,,,,0,0,0,0,1,0,1,,Uf,,,
//NoPlate,,13,0,,2016-04-01 00:01:32,2016-04-01 00:01:26,30707901,0,2,,,,,,70.00,,,2,20160401000132003070790121_40,20160401000132003070790122_40,,,,0,0,1,0,0,0,1,,Uf,,,
//粤B4Q0Q8,,0,0,,2016-04-01 00:01:30,2016-04-01 00:01:29,20100301,0,2,,,,,,-1.00,,,2,20160401000130002010030121_40,20160401000130002010030122_40,,,,1,0,1,96,0,0,1,,Uf,,,
//粤B2W1M8,,0,0,,2016-04-01 00:01:30,2016-04-01 00:01:30,20504202,0,2,,,,,,-1.00,,,2,20160401000130002050420221_40,20160401000130002050420222_40,,,,1,0,1,95,0,0,1,,Uf,,,
//粤BN560P,,0,0,,2016-03-31 14:21:40,2016-04-01 00:01:30,30606703,0,1,,,,,,-1.00,,,2,20160331142140273060670311_40,20160331142140273060670312_40,,,,1,0,0,75,1,0,31,,Uf,,,
//粤B5S0G9,,0,0,,2016-03-31 12:16:50,2016-04-01 00:01:30,20507304,0,4,,,,,,-1.00,,,2,20160331121650302050730441_40,20160331121650302050730442_40,,,,1,0,0,100,0,0,31,,Uf,,,
//粤B772YK,,0,0,,2016-03-31 20:47:28,2016-04-01 00:01:30,10200104,0,3,,,,,,-1.00,,,2,20160331204728161020010431_40,20160331204728161020010432_40,,,,1,0,0,0,1,0,31,,Uf,,,
//粤BS01W5,,0,0,,2016-03-31 18:01:17,2016-04-01 00:01:30,30606701,0,6,,,,,,-1.00,,,2,20160331180117613060670161_40,20160331180117613060670162_40,,,,1,0,0,95,1,0,31,,Uf,,,
//粤B1N2Q7,,0,0,,2016-03-31 12:16:50,2016-04-01 00:01:30,20507304,0,2,,,,,,-1.00,,,2,20160331121650952050730421_40,20160331121650952050730422_40,,,,1,0,0,89,0,0,31,,Uf,,,
//粤SV6D65,,0,0,,2016-04-01 00:01:31,2016-04-01 00:01:30,10300503,0,2,,,,,,71.27,,,2,20160401000131001030050321_40,20160401000131001030050322_40,,,,1,0,1,691,0,0,1,,Uf,,,
//NoPlate,,13,0,,2016-04-01 00:00:52,2016-04-01 00:01:33,10100407,0,7,,,其它,,,44.00,,,2,20160401000052181010040771_0_0_0_0_40,20160401000052181010040772_0_0_0_0_40,,,,0,0,0,0,1,0,1,,,,,
class StdRecognitionExecutor2016(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_ROAD_PLATE_RECOGNITION] {

  override def filter(x: String): Boolean = {

    val a = x.replaceAll("\\{.*?(?!\\{)(?<!\\})\\}", "").replaceAll("\n", "").trim

    val b =
      """
        |face_mask2,vehicle_color,plate_rect,logo_rect,pilot_safe_belt,abs_stime,image_url2,image_url1,vehicle_speed,location_x,i_annual_tags,image_url3,
        |location_y,pass_hour,use_phone,pilot_sunvisor,lane_no,vehicle_rect,location_z,relative_etime,vice_pilot_rect,lane_name,id,capture_user,whole_height,
        |relative_stime,hold_a_child,i_vice_sunvisor,pass_timestamp,car_body_small_type,plate_type,vehicle_model_name,vehicle_brand,plate_no,save_time,i_pendant,
        |tissue_box,whole_width,status,location_hashcode,i_tissuebox,marker_windows,vehicle_special_type,abs_etime,pass_date,data_src_type,pendant,vice_pilot_sunvisor,annual_tags_layout,
        |smoking,hold_a_child2,direction,vehicle_brightness,plate_color,pass_time,i_pilot_sunvisor,checkpoint_id,face_mask,vehicle_type,
        |smoking2,pilot_rect,capture_address,i_marker_windows,host_id,vice_pilot_safe_belt,vehicle_model_year,vehicle_sub_brand,annual_tags,plate_confidence
      """.stripMargin.replaceAll("\n", "").trim

    a.split(",").length == b.split(",").length
  }

  override def map(x: String) = {

    //    4a9dfhle445c026101950192010802018-10-01 07:51:582018-10-01 07:51:5800粤NNM755110008{   obj_bottom : 932,   obj_left : 1034,   obj_right : 1553,   obj_top : 499}{   obj_bottom : 884,   obj_left : 1241,   obj_right : 1355,   obj_top : 854}0003{   crossing_data_type : 0,   crossing_sub_type : 0,   crossingcode : 02610195,   crossingid : 2988,   crossingtype : 5,   device_time : 2018-10-01 00:00:25.416,   devicetime_confidence : 2,   directionindex : 0,   imagepath : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,   laneno : 1,   latitude : 0,   longitude : 0,   passid : 806096011990325145,   passtime : 2018-10-01 00:00:25.000,   picurl_num : 1,   pilotsafebelt : 0,   pilotsunvisor : 7,   platecolor : 2,   plateno : 车牌,   platestate : 0,   platetype : 1,   platetype_gb : 2,   platform_time : 2018-09-30 23:50:59.416,   tfsid : 101,   transfer_time : 2018-09-30 23:50:59.863,   veh_pic_type_1 : 1,   veh_pic_url_1 : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,   vehiclecolor : 4,   vehiclecolor_gb : J,   vehiclecolordepth : 1,   vehicleinfolevel : 1,   vehiclelen : 0,   vehiclelogo : 0,   vehiclemodel : 20122014,   vehiclespeed : 0,   vehiclestate : 0,   vehiclesublogo : 0,   vehicletype : 0,   vehicletype_gb : 1,   video_bucket_name : ,   video_cloud_id : 0,   video_object_key : }1车牌002018-10-01 00:00:2520181001252018-10-01 07:51:58http://190.192.2.20:6120//pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i9061011111100

    //    obj_bottom : 932,

    //    obj_left : 1034,
    //    obj_right : 1553,
    //    obj_top : 499}
    //  {
    //    obj_bottom : 884,
    //    obj_left : 1241,
    //    obj_right : 1355,
    //    obj_top : 854}
    //  0003{
    //    crossing_data_type : 0,
    //    crossing_sub_type : 0,
    //    crossingcode : 02610195,//卡口编号
    //    crossingid : 2988, //卡口 ID
    //    crossingtype : 5,
    //    device_time : 2018-10-01 00:00:25.416,//数据上传时前端设备/平台时间 //字符串，格式 2015-02-02 19:30:00.123
    //    devicetime_confidence : 2,//该前端设备/平台时间是否可信任 整型
    //取值：0-未知，1-可信任，2-不可信任
    //    directionindex : 0,//方向编号 默认值 0
    //    imagepath : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,
    //    laneno : 1,//车道编号 默认值 0
    //    latitude : 0,
    //    longitude : 0,
    //    passid : 806096011990325145,//过车流水号
    //    passtime : 2018-10-01 00:00:25.000,//过车时间 格式：字符串  //格式形式 2015-02-02 19:30:00.123
    //    picurl_num : 1,
    //    pilotsafebelt : 0,
    //    pilotsunvisor : 7,
    //    platecolor : 2,
    //    plateno : 车牌,
    //    platestate : 0,
    //    platetype : 1,
    //    platetype_gb : 2,
    //    platform_time : 2018-09-30 23:50:59.416,//数据接入高清平台时，高清平台时间
    //字符串，格式 2015-02-02 19:30:00.123
    //    tfsid : 101,
    //    transfer_time : 2018-09-30 23:50:59.863,//高清平台转发该数据的时间
    //字符串，格式 2015-02-02 19:30:00.123
    //    veh_pic_type_1 : 1,
    //    veh_pic_url_1 : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,
    //    vehiclecolor : 4,
    //    vehiclecolor_gb : J,
    //    vehiclecolordepth : 1,
    //    vehicleinfolevel : 1,
    //    vehiclelen : 0,
    //    vehiclelogo : 0,
    //    vehiclemodel : 20122014,
    //    vehiclespeed : 0,
    //    vehiclestate : 0,
    //    vehiclesublogo : 0,
    //    vehicletype : 0,
    //    vehicletype_gb : 1,
    //    video_bucket_name : ,
    //    video_cloud_id : 0,
    //    video_object_key :

    val a = x.replaceAll("\\{.*?(?!\\{)(?<!\\})\\}", "").replaceAll("\n", "").trim

    val b =
      """
        |face_mask2,vehicle_color,plate_rect,logo_rect,pilot_safe_belt,abs_stime,image_url2,image_url1,vehicle_speed,location_x,i_annual_tags,image_url3,
        |location_y,pass_hour,use_phone,pilot_sunvisor,lane_no,vehicle_rect,location_z,relative_etime,vice_pilot_rect,lane_name,id,capture_user,whole_height,
        |relative_stime,hold_a_child,i_vice_sunvisor,pass_timestamp,car_body_small_type,plate_type,vehicle_model_name,vehicle_brand,plate_no,save_time,i_pendant,
        |tissue_box,whole_width,status,location_hashcode,i_tissuebox,marker_windows,vehicle_special_type,abs_etime,pass_date,data_src_type,pendant,vice_pilot_sunvisor,annual_tags_layout,
        |smoking,hold_a_child2,direction,vehicle_brightness,plate_color,pass_time,i_pilot_sunvisor,checkpoint_id,face_mask,vehicle_type,
        |smoking2,pilot_rect,capture_address,i_marker_windows,host_id,vice_pilot_safe_belt,vehicle_model_year,vehicle_sub_brand,annual_tags,plate_confidence
      """.stripMargin.replaceAll("\n", "").trim

    val map = b.split(",").zip(a.split(",")).filter(r => {
      val arr = Array("id", "plate_no", "pass_timestamp", "save_time", "checkpoint_id"
        , "lane_no", "vehicle_speed", "vehicle_type", "plate_type", "plate_confidence", "plate_color", "direction", "status", "capture_address"
      )
      arr.contains(r._1)
    }).map(r => if (r._2.isEmpty) (r._1, "-99") else r).toMap

    //id String  COMMENT '编号',
    val id = map.get("id").get //UUID

    //    val id = map2.getOrElse("crossingcode", "-99").toInt

    //vehicle_fid String  COMMENT '车牌号码',
    val vehicle_fid = map.get("plate_no").get

    //lane_fid String  COMMENT '车道编号',//0
    val lane_fid = map.get("lane_no").get

    //speed decimal(4,1)	COMMENT '瞬时速度（km/h）',
    val speed = BigDecimal(map.get("vehicle_speed").get).setScale(1) //vehiclespeed

    //drive_dir String   COMMENT '出行方向',
    val drive_dir = map.get("direction").get.trim.toInt

    //    val dataTime = DateTime.parse(StdUtils.fillTime(arr1(5).trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

    val dataTime = DateTime.parse(StdUtils.fillTime(map.get("pass_timestamp").get.trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

    //record_time BIGINT  COMMENT '定位时间	unix10位时间戳',2018-10-01 00:00:25.000
    val recognition_time = dataTime.getMillis / 1000

    //city	城市名称	字符串		否	1	参照城市编码对照表
    // 深圳
    val city_name = "shenzhen"
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    val year = dataTime.getYear.toString.toInt
    val month = StdUtils.fillZero(2, dataTime.getMonthOfYear.toString).toInt
    val day = StdUtils.fillZero(2, dataTime.getDayOfMonth.toString).toInt

    val license_prefix: String = if (vehicle_fid.length < 3) "" else vehicle_fid.substring(0, 2)


    //    粤B_D_<12345的md5加密>
    //    6位车牌的是电动车
    //    5位的是燃油
    //      如：粤BD12345是电动车；粤B12345是燃油车
    val encry_id = vehicle_fid

    val upload_time = DateTime.parse(StdUtils.fillTime(map.getOrElse("save_time", "-99").trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis / 1000

    //device_fid String   COMMENT '卡口编号'
    val detector_id = StdUtils.fillZero(8, map.getOrElse("checkpoint_id", "-99").trim)

    val vehicle_type = map.getOrElse("vehicle_type", "-99").trim.toInt
    val plate_type = map.getOrElse("plate_type", "-99").trim.toInt
    val illegal_type = -99
    val degree_of_confidence = map.getOrElse("plate_confidence", "-99").trim.toInt
    val plate_color = map.getOrElse("plate_color", "-99").trim.toInt
    val crossing_type = map.getOrElse("status", "-99").trim.toInt
    val capture_address = map.getOrElse("capture_address", "-99").trim

    //    encry_id	加密车牌	字符串	 	否	 	采用"<车牌前缀>_<是否电动车>_<加密串>"的形式表示。粤BD12345加密后的形式为"粤B_D_<12345的md5加密>"。非电动车用符号"o"表示

    S_ROAD_PLATE_RECOGNITION(
      id: String, // ""
      vehicle_fid: String, // ""
      encry_id: String, // ""
      recognition_time: Long, // "10为UNIX时间戳"
      upload_time: Long, // "10为UNIX时间戳"
      detector_id: String, // "不足8为左边补0"
      lane_fid: String, // "待补充"
      speed: BigDecimal, // "单位km/h"
      vehicle_type: Int, // ""
      plate_type: Int, // "待补充"
      illegal_type: Int, // "待补充"
      degree_of_confidence: Int, // "0-未知，1-可信任，2-不可信任"
      plate_color: Int, // "待补充"
      drive_dir: Int, // "待补充0-其他；1-由东向西；2-由西向东；3-由南向北；4-由北向南；5-由东南向西北；6-由西北向东南；7-由东北向西南；8-由西南向东北"
      crossing_type: Int, // "1-卡口；2-电警；3-RFID卡口；4-互联停车场；5-分局卡口；6-加油站；7-违停球；9-移动卡口"
      capture_address: String, // ""
      city: Int, // "参照城市编码对照表"
      year: Int, // "示例2019"
      month: Int, // "示例8"
      day: Int // "示例2"
    )


  }

  def execute() = {
    println("读取【" + inputPath + "】数据")

    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.vehicle_fid.length > 5)

    //    cache(res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_fid", "recognition_time"))

  }
}


//"gcxh","kdbh","cdbh","gcsj","hphm","hpys","hpzl","cthphm","cthpys","csys","tplx","tp","splx","sp","clsd","cwhphm","cwhpys","cllx","sbbh","fxbh","clpp","rksj","hpyz","clxs","xszt","clwx","byzd1","byzd2","byzd3","sys_filename","sys_pch"
//"4403000000011770605623","205A0615","","2017-07-20 17:19:29","粤B5T697","2","","粤B5T697","0","","2","2017072005192903205A061511_31","","","-1","","0","","","","","2017-04-20 17:20:22","0","0","","","","","","2017#4#20#rImport_ysb_201704201719_03_1.txt","2017-04-20 17:41:21"
//"4403000000011770623563","205A0615","","2017-07-20 17:19:23","粤B49V33","2","","粤B49V33","0","","2","2017072005192303205A061511_32","","","-1","","0","","","","","2017-04-20 17:20:18","0","0","","","","","","2017#4#20#rImport_ysb_201704201719_03_2.txt","2017-04-20 17:41:21"
class StdRecognitionExecutor2017(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_ROAD_PLATE_RECOGNITION] {

  override def filter(x: String): Boolean = {

    val a = x.replaceAll("\\{.*?(?!\\{)(?<!\\})\\}", "").replaceAll("\n", "").trim

    val b =
      """
        |face_mask2,vehicle_color,plate_rect,logo_rect,pilot_safe_belt,abs_stime,image_url2,image_url1,vehicle_speed,location_x,i_annual_tags,image_url3,
        |location_y,pass_hour,use_phone,pilot_sunvisor,lane_no,vehicle_rect,location_z,relative_etime,vice_pilot_rect,lane_name,id,capture_user,whole_height,
        |relative_stime,hold_a_child,i_vice_sunvisor,pass_timestamp,car_body_small_type,plate_type,vehicle_model_name,vehicle_brand,plate_no,save_time,i_pendant,
        |tissue_box,whole_width,status,location_hashcode,i_tissuebox,marker_windows,vehicle_special_type,abs_etime,pass_date,data_src_type,pendant,vice_pilot_sunvisor,annual_tags_layout,
        |smoking,hold_a_child2,direction,vehicle_brightness,plate_color,pass_time,i_pilot_sunvisor,checkpoint_id,face_mask,vehicle_type,
        |smoking2,pilot_rect,capture_address,i_marker_windows,host_id,vice_pilot_safe_belt,vehicle_model_year,vehicle_sub_brand,annual_tags,plate_confidence
      """.stripMargin.replaceAll("\n", "").trim

    a.split(",").length == b.split(",").length
  }

  override def map(x: String) = {

    //    4a9dfhle445c026101950192010802018-10-01 07:51:582018-10-01 07:51:5800粤NNM755110008{   obj_bottom : 932,   obj_left : 1034,   obj_right : 1553,   obj_top : 499}{   obj_bottom : 884,   obj_left : 1241,   obj_right : 1355,   obj_top : 854}0003{   crossing_data_type : 0,   crossing_sub_type : 0,   crossingcode : 02610195,   crossingid : 2988,   crossingtype : 5,   device_time : 2018-10-01 00:00:25.416,   devicetime_confidence : 2,   directionindex : 0,   imagepath : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,   laneno : 1,   latitude : 0,   longitude : 0,   passid : 806096011990325145,   passtime : 2018-10-01 00:00:25.000,   picurl_num : 1,   pilotsafebelt : 0,   pilotsunvisor : 7,   platecolor : 2,   plateno : 车牌,   platestate : 0,   platetype : 1,   platetype_gb : 2,   platform_time : 2018-09-30 23:50:59.416,   tfsid : 101,   transfer_time : 2018-09-30 23:50:59.863,   veh_pic_type_1 : 1,   veh_pic_url_1 : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,   vehiclecolor : 4,   vehiclecolor_gb : J,   vehiclecolordepth : 1,   vehicleinfolevel : 1,   vehiclelen : 0,   vehiclelogo : 0,   vehiclemodel : 20122014,   vehiclespeed : 0,   vehiclestate : 0,   vehiclesublogo : 0,   vehicletype : 0,   vehicletype_gb : 1,   video_bucket_name : ,   video_cloud_id : 0,   video_object_key : }1车牌002018-10-01 00:00:2520181001252018-10-01 07:51:58http://190.192.2.20:6120//pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i9061011111100

    //    obj_bottom : 932,

    //    obj_left : 1034,
    //    obj_right : 1553,
    //    obj_top : 499}
    //  {
    //    obj_bottom : 884,
    //    obj_left : 1241,
    //    obj_right : 1355,
    //    obj_top : 854}
    //  0003{
    //    crossing_data_type : 0,
    //    crossing_sub_type : 0,
    //    crossingcode : 02610195,//卡口编号
    //    crossingid : 2988, //卡口 ID
    //    crossingtype : 5,
    //    device_time : 2018-10-01 00:00:25.416,//数据上传时前端设备/平台时间 //字符串，格式 2015-02-02 19:30:00.123
    //    devicetime_confidence : 2,//该前端设备/平台时间是否可信任 整型
    //取值：0-未知，1-可信任，2-不可信任
    //    directionindex : 0,//方向编号 默认值 0
    //    imagepath : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,
    //    laneno : 1,//车道编号 默认值 0
    //    latitude : 0,
    //    longitude : 0,
    //    passid : 806096011990325145,//过车流水号
    //    passtime : 2018-10-01 00:00:25.000,//过车时间 格式：字符串  //格式形式 2015-02-02 19:30:00.123
    //    picurl_num : 1,
    //    pilotsafebelt : 0,
    //    pilotsunvisor : 7,
    //    platecolor : 2,
    //    plateno : 车牌,
    //    platestate : 0,
    //    platetype : 1,
    //    platetype_gb : 2,
    //    platform_time : 2018-09-30 23:50:59.416,//数据接入高清平台时，高清平台时间
    //字符串，格式 2015-02-02 19:30:00.123
    //    tfsid : 101,
    //    transfer_time : 2018-09-30 23:50:59.863,//高清平台转发该数据的时间
    //字符串，格式 2015-02-02 19:30:00.123
    //    veh_pic_type_1 : 1,
    //    veh_pic_url_1 : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,
    //    vehiclecolor : 4,
    //    vehiclecolor_gb : J,
    //    vehiclecolordepth : 1,
    //    vehicleinfolevel : 1,
    //    vehiclelen : 0,
    //    vehiclelogo : 0,
    //    vehiclemodel : 20122014,
    //    vehiclespeed : 0,
    //    vehiclestate : 0,
    //    vehiclesublogo : 0,
    //    vehicletype : 0,
    //    vehicletype_gb : 1,
    //    video_bucket_name : ,
    //    video_cloud_id : 0,
    //    video_object_key :

    val a = x.replaceAll("\\{.*?(?!\\{)(?<!\\})\\}", "").replaceAll("\n", "").trim

    val b =
      """
        |face_mask2,vehicle_color,plate_rect,logo_rect,pilot_safe_belt,abs_stime,image_url2,image_url1,vehicle_speed,location_x,i_annual_tags,image_url3,
        |location_y,pass_hour,use_phone,pilot_sunvisor,lane_no,vehicle_rect,location_z,relative_etime,vice_pilot_rect,lane_name,id,capture_user,whole_height,
        |relative_stime,hold_a_child,i_vice_sunvisor,pass_timestamp,car_body_small_type,plate_type,vehicle_model_name,vehicle_brand,plate_no,save_time,i_pendant,
        |tissue_box,whole_width,status,location_hashcode,i_tissuebox,marker_windows,vehicle_special_type,abs_etime,pass_date,data_src_type,pendant,vice_pilot_sunvisor,annual_tags_layout,
        |smoking,hold_a_child2,direction,vehicle_brightness,plate_color,pass_time,i_pilot_sunvisor,checkpoint_id,face_mask,vehicle_type,
        |smoking2,pilot_rect,capture_address,i_marker_windows,host_id,vice_pilot_safe_belt,vehicle_model_year,vehicle_sub_brand,annual_tags,plate_confidence
      """.stripMargin.replaceAll("\n", "").trim

    val map = b.split(",").zip(a.split(",")).filter(r => {
      val arr = Array("id", "plate_no", "pass_timestamp", "save_time", "checkpoint_id"
        , "lane_no", "vehicle_speed", "vehicle_type", "plate_type", "plate_confidence", "plate_color", "direction", "status", "capture_address"
      )
      arr.contains(r._1)
    }).map(r => if (r._2.isEmpty) (r._1, "-99") else r).toMap

    //id String  COMMENT '编号',
    val id = map.get("id").get //UUID

    //    val id = map2.getOrElse("crossingcode", "-99").toInt

    //vehicle_fid String  COMMENT '车牌号码',
    val vehicle_fid = map.get("plate_no").get

    //lane_fid String  COMMENT '车道编号',//0
    val lane_fid = map.get("lane_no").get

    //speed decimal(4,1)	COMMENT '瞬时速度（km/h）',
    val speed = BigDecimal(map.get("vehicle_speed").get).setScale(1) //vehiclespeed

    //drive_dir String   COMMENT '出行方向',
    val drive_dir = map.get("direction").get.trim.toInt

    //    val dataTime = DateTime.parse(StdUtils.fillTime(arr1(5).trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

    val dataTime = DateTime.parse(StdUtils.fillTime(map.get("pass_timestamp").get.trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

    //record_time BIGINT  COMMENT '定位时间	unix10位时间戳',2018-10-01 00:00:25.000
    val recognition_time = dataTime.getMillis / 1000

    //city	城市名称	字符串		否	1	参照城市编码对照表
    // 深圳
    val city_name = "shenzhen"
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    val year = dataTime.getYear.toString.toInt
    val month = StdUtils.fillZero(2, dataTime.getMonthOfYear.toString).toInt
    val day = StdUtils.fillZero(2, dataTime.getDayOfMonth.toString).toInt

    val license_prefix: String = if (vehicle_fid.length < 3) "" else vehicle_fid.substring(0, 2)


    //    粤B_D_<12345的md5加密>
    //    6位车牌的是电动车
    //    5位的是燃油
    //      如：粤BD12345是电动车；粤B12345是燃油车
    val encry_id = vehicle_fid

    val upload_time = DateTime.parse(StdUtils.fillTime(map.getOrElse("save_time", "-99").trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis / 1000

    //device_fid String   COMMENT '卡口编号'
    val detector_id = StdUtils.fillZero(8, map.getOrElse("checkpoint_id", "-99").trim)

    val vehicle_type = map.getOrElse("vehicle_type", "-99").trim.toInt
    val plate_type = map.getOrElse("plate_type", "-99").trim.toInt
    val illegal_type = -99
    val degree_of_confidence = map.getOrElse("plate_confidence", "-99").trim.toInt
    val plate_color = map.getOrElse("plate_color", "-99").trim.toInt
    val crossing_type = map.getOrElse("status", "-99").trim.toInt
    val capture_address = map.getOrElse("capture_address", "-99").trim

    //    encry_id	加密车牌	字符串	 	否	 	采用"<车牌前缀>_<是否电动车>_<加密串>"的形式表示。粤BD12345加密后的形式为"粤B_D_<12345的md5加密>"。非电动车用符号"o"表示

    S_ROAD_PLATE_RECOGNITION(
      id: String, // ""
      vehicle_fid: String, // ""
      encry_id: String, // ""
      recognition_time: Long, // "10为UNIX时间戳"
      upload_time: Long, // "10为UNIX时间戳"
      detector_id: String, // "不足8为左边补0"
      lane_fid: String, // "待补充"
      speed: BigDecimal, // "单位km/h"
      vehicle_type: Int, // ""
      plate_type: Int, // "待补充"
      illegal_type: Int, // "待补充"
      degree_of_confidence: Int, // "0-未知，1-可信任，2-不可信任"
      plate_color: Int, // "待补充"
      drive_dir: Int, // "待补充0-其他；1-由东向西；2-由西向东；3-由南向北；4-由北向南；5-由东南向西北；6-由西北向东南；7-由东北向西南；8-由西南向东北"
      crossing_type: Int, // "1-卡口；2-电警；3-RFID卡口；4-互联停车场；5-分局卡口；6-加油站；7-违停球；9-移动卡口"
      capture_address: String, // ""
      city: Int, // "参照城市编码对照表"
      year: Int, // "示例2019"
      month: Int, // "示例8"
      day: Int // "示例2"
    )


  }

  def execute() = {
    println("读取【" + inputPath + "】数据")

    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.vehicle_fid.length > 5)

    //    cache(res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_fid", "recognition_time"))

  }
}


class StdRecognitionExecutor2018(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_ROAD_PLATE_RECOGNITION] {

  override def filter(x: String): Boolean = {
    val y = x.replaceAll("", ";").replace("\\\n", "").trim
    y.contains("{") && y.contains("}")
  }

  override def map(x: String) = {

    //    4a9dfhle445c026101950192010802018-10-01 07:51:582018-10-01 07:51:5800粤NNM755110008{   obj_bottom : 932,   obj_left : 1034,   obj_right : 1553,   obj_top : 499}{   obj_bottom : 884,   obj_left : 1241,   obj_right : 1355,   obj_top : 854}0003{   crossing_data_type : 0,   crossing_sub_type : 0,   crossingcode : 02610195,   crossingid : 2988,   crossingtype : 5,   device_time : 2018-10-01 00:00:25.416,   devicetime_confidence : 2,   directionindex : 0,   imagepath : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,   laneno : 1,   latitude : 0,   longitude : 0,   passid : 806096011990325145,   passtime : 2018-10-01 00:00:25.000,   picurl_num : 1,   pilotsafebelt : 0,   pilotsunvisor : 7,   platecolor : 2,   plateno : 车牌,   platestate : 0,   platetype : 1,   platetype_gb : 2,   platform_time : 2018-09-30 23:50:59.416,   tfsid : 101,   transfer_time : 2018-09-30 23:50:59.863,   veh_pic_type_1 : 1,   veh_pic_url_1 : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,   vehiclecolor : 4,   vehiclecolor_gb : J,   vehiclecolordepth : 1,   vehicleinfolevel : 1,   vehiclelen : 0,   vehiclelogo : 0,   vehiclemodel : 20122014,   vehiclespeed : 0,   vehiclestate : 0,   vehiclesublogo : 0,   vehicletype : 0,   vehicletype_gb : 1,   video_bucket_name : ,   video_cloud_id : 0,   video_object_key : }1车牌002018-10-01 00:00:2520181001252018-10-01 07:51:58http://190.192.2.20:6120//pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i9061011111100

    //    obj_bottom : 932,

    //    obj_left : 1034,
    //    obj_right : 1553,
    //    obj_top : 499}
    //  {
    //    obj_bottom : 884,
    //    obj_left : 1241,
    //    obj_right : 1355,
    //    obj_top : 854}
    //  0003{
    //    crossing_data_type : 0,
    //    crossing_sub_type : 0,
    //    crossingcode : 02610195,//卡口编号
    //    crossingid : 2988, //卡口 ID
    //    crossingtype : 5,
    //    device_time : 2018-10-01 00:00:25.416,//数据上传时前端设备/平台时间 //字符串，格式 2015-02-02 19:30:00.123
    //    devicetime_confidence : 2,//该前端设备/平台时间是否可信任 整型
    //取值：0-未知，1-可信任，2-不可信任
    //    directionindex : 0,//方向编号 默认值 0
    //    imagepath : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,
    //    laneno : 1,//车道编号 默认值 0
    //    latitude : 0,
    //    longitude : 0,
    //    passid : 806096011990325145,//过车流水号
    //    passtime : 2018-10-01 00:00:25.000,//过车时间 格式：字符串  //格式形式 2015-02-02 19:30:00.123
    //    picurl_num : 1,
    //    pilotsafebelt : 0,
    //    pilotsunvisor : 7,
    //    platecolor : 2,
    //    plateno : 车牌,
    //    platestate : 0,
    //    platetype : 1,
    //    platetype_gb : 2,
    //    platform_time : 2018-09-30 23:50:59.416,//数据接入高清平台时，高清平台时间
    //字符串，格式 2015-02-02 19:30:00.123
    //    tfsid : 101,
    //    transfer_time : 2018-09-30 23:50:59.863,//高清平台转发该数据的时间
    //字符串，格式 2015-02-02 19:30:00.123
    //    veh_pic_type_1 : 1,
    //    veh_pic_url_1 : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,
    //    vehiclecolor : 4,
    //    vehiclecolor_gb : J,
    //    vehiclecolordepth : 1,
    //    vehicleinfolevel : 1,
    //    vehiclelen : 0,
    //    vehiclelogo : 0,
    //    vehiclemodel : 20122014,
    //    vehiclespeed : 0,
    //    vehiclestate : 0,
    //    vehiclesublogo : 0,
    //    vehicletype : 0,
    //    vehicletype_gb : 1,
    //    video_bucket_name : ,
    //    video_cloud_id : 0,
    //    video_object_key :

    val arr = x.split("").map(r => {
      if (StringUtils.isEmpty(r.trim)) "-99" else r.trim
    }).map(r => {
      if (r.isEmpty) "-99" else r
    })

    //id String  COMMENT '编号',
    val id = arr(0) //UUID

    //    val id = map2.getOrElse("crossingcode", "-99").toInt

    //vehicle_fid String  COMMENT '车牌号码',
    val vehicle_fid = arr(9)

    //lane_fid String  COMMENT '车道编号',//0
    val lane_fid = arr(34)

    //speed decimal(4,1)	COMMENT '瞬时速度（km/h）',
    val speed = BigDecimal(arr(36)).setScale(1) //vehiclespeed

    //drive_dir String   COMMENT '出行方向',
    val drive_dir = arr(13).trim.toInt

    //    val dataTime = DateTime.parse(StdUtils.fillTime(arr1(5).trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

    val dataTime = DateTime.parse(StdUtils.fillTime(arr(43).trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

    //record_time BIGINT  COMMENT '定位时间	unix10位时间戳',2018-10-01 00:00:25.000
    val recognition_time = dataTime.getMillis / 1000

    //city	城市名称	字符串		否	1	参照城市编码对照表
    // 深圳
    val city_name = "shenzhen"
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    val year = dataTime.getYear.toString.toInt
    val month = StdUtils.fillZero(2, dataTime.getMonthOfYear.toString).toInt
    val day = StdUtils.fillZero(2, dataTime.getDayOfMonth.toString).toInt

    val license_prefix: String = if (vehicle_fid.length < 3) "" else vehicle_fid.substring(0, 2)


    //    粤B_D_<12345的md5加密>
    //    6位车牌的是电动车
    //    5位的是燃油
    //      如：粤BD12345是电动车；粤B12345是燃油车
    val encry_id = if (vehicle_fid.length < 3) ""
    else if (vehicle_fid.length == 8) {
      vehicle_fid.substring(0, 2) + "_" + "D_" + MD5Utils.encode16(vehicle_fid)
    } else {
      vehicle_fid.substring(0, 2) + "_" + "O_" + MD5Utils.encode16(vehicle_fid)
    }

    val upload_time = DateTime.parse(StdUtils.fillTime(arr(46).trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis / 1000

    //device_fid String   COMMENT '卡口编号'
    val detector_id = StdUtils.fillZero(8, arr(1).trim)

    val vehicle_type = arr(54).trim.toInt
    val plate_type = arr(11).trim.toInt
    val illegal_type = -99
    val degree_of_confidence = arr(12).trim.toInt
    val plate_color = arr(10).trim.toInt
    val crossing_type = arr(69).trim.toInt
    val capture_address = arr(41).trim

    //    encry_id	加密车牌	字符串	 	否	 	采用"<车牌前缀>_<是否电动车>_<加密串>"的形式表示。粤BD12345加密后的形式为"粤B_D_<12345的md5加密>"。非电动车用符号"o"表示

    S_ROAD_PLATE_RECOGNITION(
      id: String, // ""
      vehicle_fid: String, // ""
      encry_id: String, // ""
      recognition_time: Long, // "10为UNIX时间戳"
      upload_time: Long, // "10为UNIX时间戳"
      detector_id: String, // "不足8为左边补0"
      lane_fid: String, // "待补充"
      speed: BigDecimal, // "单位km/h"
      vehicle_type: Int, // ""
      plate_type: Int, // "待补充"
      illegal_type: Int, // "待补充"
      degree_of_confidence: Int, // "0-未知，1-可信任，2-不可信任"
      plate_color: Int, // "待补充"
      drive_dir: Int, // "待补充0-其他；1-由东向西；2-由西向东；3-由南向北；4-由北向南；5-由东南向西北；6-由西北向东南；7-由东北向西南；8-由西南向东北"
      crossing_type: Int, // "1-卡口；2-电警；3-RFID卡口；4-互联停车场；5-分局卡口；6-加油站；7-违停球；9-移动卡口"
      capture_address: String, // ""
      city: Int, // "参照城市编码对照表"
      year: Int, // "示例2019"
      month: Int, // "示例8"
      day: Int // "示例2"
    )


  }

  def execute() = {
    println("读取【" + inputPath + "】数据")

    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.vehicle_fid.length > 5)

    //        cache(res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_fid", "recognition_time"))

  }
}

class StdRecognitionExecutor2019(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_ROAD_PLATE_RECOGNITION] {

  override def filter(x: String): Boolean = {

    val a = x.replaceAll("\\{.*?(?!\\{)(?<!\\})\\}", "").replaceAll("\n", "").trim

    val b =
      """
        |face_mask2,vehicle_color,plate_rect,logo_rect,pilot_safe_belt,abs_stime,image_url2,image_url1,vehicle_speed,location_x,i_annual_tags,image_url3,
        |location_y,pass_hour,use_phone,pilot_sunvisor,lane_no,vehicle_rect,location_z,relative_etime,vice_pilot_rect,lane_name,id,capture_user,whole_height,
        |relative_stime,hold_a_child,i_vice_sunvisor,pass_timestamp,car_body_small_type,plate_type,vehicle_model_name,vehicle_brand,plate_no,save_time,i_pendant,
        |tissue_box,whole_width,status,location_hashcode,i_tissuebox,marker_windows,vehicle_special_type,abs_etime,pass_date,data_src_type,pendant,vice_pilot_sunvisor,annual_tags_layout,
        |smoking,hold_a_child2,direction,vehicle_brightness,plate_color,pass_time,i_pilot_sunvisor,checkpoint_id,face_mask,vehicle_type,
        |smoking2,pilot_rect,capture_address,i_marker_windows,host_id,vice_pilot_safe_belt,vehicle_model_year,vehicle_sub_brand,annual_tags,plate_confidence
      """.stripMargin.replaceAll("\n", "").trim

    a.split(",").length == b.split(",").length
  }

  override def map(x: String) = {

    //    4a9dfhle445c026101950192010802018-10-01 07:51:582018-10-01 07:51:5800粤NNM755110008{   obj_bottom : 932,   obj_left : 1034,   obj_right : 1553,   obj_top : 499}{   obj_bottom : 884,   obj_left : 1241,   obj_right : 1355,   obj_top : 854}0003{   crossing_data_type : 0,   crossing_sub_type : 0,   crossingcode : 02610195,   crossingid : 2988,   crossingtype : 5,   device_time : 2018-10-01 00:00:25.416,   devicetime_confidence : 2,   directionindex : 0,   imagepath : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,   laneno : 1,   latitude : 0,   longitude : 0,   passid : 806096011990325145,   passtime : 2018-10-01 00:00:25.000,   picurl_num : 1,   pilotsafebelt : 0,   pilotsunvisor : 7,   platecolor : 2,   plateno : 车牌,   platestate : 0,   platetype : 1,   platetype_gb : 2,   platform_time : 2018-09-30 23:50:59.416,   tfsid : 101,   transfer_time : 2018-09-30 23:50:59.863,   veh_pic_type_1 : 1,   veh_pic_url_1 : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,   vehiclecolor : 4,   vehiclecolor_gb : J,   vehiclecolordepth : 1,   vehicleinfolevel : 1,   vehiclelen : 0,   vehiclelogo : 0,   vehiclemodel : 20122014,   vehiclespeed : 0,   vehiclestate : 0,   vehiclesublogo : 0,   vehicletype : 0,   vehicletype_gb : 1,   video_bucket_name : ,   video_cloud_id : 0,   video_object_key : }1车牌002018-10-01 00:00:2520181001252018-10-01 07:51:58http://190.192.2.20:6120//pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i9061011111100

    //    obj_bottom : 932,

    //    obj_left : 1034,
    //    obj_right : 1553,
    //    obj_top : 499}
    //  {
    //    obj_bottom : 884,
    //    obj_left : 1241,
    //    obj_right : 1355,
    //    obj_top : 854}
    //  0003{
    //    crossing_data_type : 0,
    //    crossing_sub_type : 0,
    //    crossingcode : 02610195,//卡口编号
    //    crossingid : 2988, //卡口 ID
    //    crossingtype : 5,
    //    device_time : 2018-10-01 00:00:25.416,//数据上传时前端设备/平台时间 //字符串，格式 2015-02-02 19:30:00.123
    //    devicetime_confidence : 2,//该前端设备/平台时间是否可信任 整型
    //取值：0-未知，1-可信任，2-不可信任
    //    directionindex : 0,//方向编号 默认值 0
    //    imagepath : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,
    //    laneno : 1,//车道编号 默认值 0
    //    latitude : 0,
    //    longitude : 0,
    //    passid : 806096011990325145,//过车流水号
    //    passtime : 2018-10-01 00:00:25.000,//过车时间 格式：字符串  //格式形式 2015-02-02 19:30:00.123
    //    picurl_num : 1,
    //    pilotsafebelt : 0,
    //    pilotsunvisor : 7,
    //    platecolor : 2,
    //    plateno : 车牌,
    //    platestate : 0,
    //    platetype : 1,
    //    platetype_gb : 2,
    //    platform_time : 2018-09-30 23:50:59.416,//数据接入高清平台时，高清平台时间
    //字符串，格式 2015-02-02 19:30:00.123
    //    tfsid : 101,
    //    transfer_time : 2018-09-30 23:50:59.863,//高清平台转发该数据的时间
    //字符串，格式 2015-02-02 19:30:00.123
    //    veh_pic_type_1 : 1,
    //    veh_pic_url_1 : /pic?0dd50537b-23eb711i4e39pa--625c0e892b9e4i1b2*=0d7*39d4i*s1d=i9p3t=0a*m5-e0=8686=2i906,
    //    vehiclecolor : 4,
    //    vehiclecolor_gb : J,
    //    vehiclecolordepth : 1,
    //    vehicleinfolevel : 1,
    //    vehiclelen : 0,
    //    vehiclelogo : 0,
    //    vehiclemodel : 20122014,
    //    vehiclespeed : 0,
    //    vehiclestate : 0,
    //    vehiclesublogo : 0,
    //    vehicletype : 0,
    //    vehicletype_gb : 1,
    //    video_bucket_name : ,
    //    video_cloud_id : 0,
    //    video_object_key :

    val a = x.replaceAll("\\{.*?(?!\\{)(?<!\\})\\}", "").replaceAll("\n", "").trim

    val b =
      """
        |face_mask2,vehicle_color,plate_rect,logo_rect,pilot_safe_belt,abs_stime,image_url2,image_url1,vehicle_speed,location_x,i_annual_tags,image_url3,
        |location_y,pass_hour,use_phone,pilot_sunvisor,lane_no,vehicle_rect,location_z,relative_etime,vice_pilot_rect,lane_name,id,capture_user,whole_height,
        |relative_stime,hold_a_child,i_vice_sunvisor,pass_timestamp,car_body_small_type,plate_type,vehicle_model_name,vehicle_brand,plate_no,save_time,i_pendant,
        |tissue_box,whole_width,status,location_hashcode,i_tissuebox,marker_windows,vehicle_special_type,abs_etime,pass_date,data_src_type,pendant,vice_pilot_sunvisor,annual_tags_layout,
        |smoking,hold_a_child2,direction,vehicle_brightness,plate_color,pass_time,i_pilot_sunvisor,checkpoint_id,face_mask,vehicle_type,
        |smoking2,pilot_rect,capture_address,i_marker_windows,host_id,vice_pilot_safe_belt,vehicle_model_year,vehicle_sub_brand,annual_tags,plate_confidence
      """.stripMargin.replaceAll("\n", "").trim

    val map = b.split(",").zip(a.split(",")).filter(r => {
      val arr = Array("id", "plate_no", "pass_timestamp", "save_time", "checkpoint_id"
        , "lane_no", "vehicle_speed", "vehicle_type", "plate_type", "plate_confidence", "plate_color", "direction", "status", "capture_address"
      )
      arr.contains(r._1)
    }).map(r => if (r._2.isEmpty) (r._1, "-99") else r).toMap

    //id String  COMMENT '编号',
    val id = map.get("id").get //UUID

    //    val id = map2.getOrElse("crossingcode", "-99").toInt

    //vehicle_fid String  COMMENT '车牌号码',
    val vehicle_fid = map.get("plate_no").get

    //lane_fid String  COMMENT '车道编号',//0
    val lane_fid = map.get("lane_no").get

    //speed decimal(4,1)	COMMENT '瞬时速度（km/h）',
    val speed = BigDecimal(map.get("vehicle_speed").get).setScale(1) //vehiclespeed

    //drive_dir String   COMMENT '出行方向',
    val drive_dir = map.get("direction").get.trim.toInt

    //    val dataTime = DateTime.parse(StdUtils.fillTime(arr1(5).trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

    val dataTime = DateTime.parse(StdUtils.fillTime(map.get("pass_timestamp").get.trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

    //record_time BIGINT  COMMENT '定位时间	unix10位时间戳',2018-10-01 00:00:25.000
    val recognition_time = dataTime.getMillis / 1000

    //city	城市名称	字符串		否	1	参照城市编码对照表
    // 深圳
    val city_name = "shenzhen"
    val city = broadcast.value.get(city_name).getOrElse("-99").toInt

    val year = dataTime.getYear.toString.toInt
    val month = StdUtils.fillZero(2, dataTime.getMonthOfYear.toString).toInt
    val day = StdUtils.fillZero(2, dataTime.getDayOfMonth.toString).toInt

    val license_prefix: String = if (vehicle_fid.length < 3) "" else vehicle_fid.substring(0, 2)


    //    粤B_D_<12345的md5加密>
    //    6位车牌的是电动车
    //    5位的是燃油
    //      如：粤BD12345是电动车；粤B12345是燃油车
    val encry_id = vehicle_fid

    val upload_time = DateTime.parse(StdUtils.fillTime(map.getOrElse("save_time", "-99").trim), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis / 1000

    //device_fid String   COMMENT '卡口编号'
    val detector_id = StdUtils.fillZero(8, map.getOrElse("checkpoint_id", "-99").trim)

    val vehicle_type = map.getOrElse("vehicle_type", "-99").trim.toInt
    val plate_type = map.getOrElse("plate_type", "-99").trim.toInt
    val illegal_type = -99
    val degree_of_confidence = map.getOrElse("plate_confidence", "-99").trim.toInt
    val plate_color = map.getOrElse("plate_color", "-99").trim.toInt
    val crossing_type = map.getOrElse("status", "-99").trim.toInt
    val capture_address = map.getOrElse("capture_address", "-99").trim

    //    encry_id	加密车牌	字符串	 	否	 	采用"<车牌前缀>_<是否电动车>_<加密串>"的形式表示。粤BD12345加密后的形式为"粤B_D_<12345的md5加密>"。非电动车用符号"o"表示

    S_ROAD_PLATE_RECOGNITION(
      id: String, // ""
      vehicle_fid: String, // ""
      encry_id: String, // ""
      recognition_time: Long, // "10为UNIX时间戳"
      upload_time: Long, // "10为UNIX时间戳"
      detector_id: String, // "不足8为左边补0"
      lane_fid: String, // "待补充"
      speed: BigDecimal, // "单位km/h"
      vehicle_type: Int, // ""
      plate_type: Int, // "待补充"
      illegal_type: Int, // "待补充"
      degree_of_confidence: Int, // "0-未知，1-可信任，2-不可信任"
      plate_color: Int, // "待补充"
      drive_dir: Int, // "待补充0-其他；1-由东向西；2-由西向东；3-由南向北；4-由北向南；5-由东南向西北；6-由西北向东南；7-由东北向西南；8-由西南向东北"
      crossing_type: Int, // "1-卡口；2-电警；3-RFID卡口；4-互联停车场；5-分局卡口；6-加油站；7-违停球；9-移动卡口"
      capture_address: String, // ""
      city: Int, // "参照城市编码对照表"
      year: Int, // "示例2019"
      month: Int, // "示例8"
      day: Int // "示例2"
    )


  }

  def execute() = {
    println("读取【" + inputPath + "】数据")

    smartLoop(inputPath: String, spark: SparkSession)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x)).filter(_.vehicle_fid.length > 5)

    //    cache(res)

    outputHiveStd(spark, outputTable, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq("vehicle_fid", "recognition_time"))

  }
}


//TODO:手机信令
class StdResidentCuExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_INTER_RESIDENT] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 3))
    val c2 = (arr.length == 4)
    c1 && c2 && Constants.MSIGNAL_USER_TYPE_MAP.get(arr(0)).getOrElse(-99) == 1
  }

  override def map(x: String) = {
    val arr = x.split(",")

    // user_type home_grid_id user_count expansion_rate

    //grid_id	居住地栅格ID	字符串	32	否
    val grid_id = arr(1)

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

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_INTER_RESIDENT(
      grid_id: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      creator = "zhangyongtian",
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
        !x.grid_id.equals("0")
      })
    outputHiveStd(spark, outputTable, Array("source", "city"), res, dupliCols = Seq("grid_id", "fdate", "source"))
  }
}

class StdTouristCuExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_INTER_TOURIST] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 3))
    val c2 = (arr.length == 4)
    c1 && c2 && Constants.MSIGNAL_USER_TYPE_MAP.get(arr(0)).getOrElse(-99) == 2
  }

  override def map(x: String) = {
    val arr = x.split(",")

    // user_type home_grid_id user_count expansion_rate

    //grid_id	居住地栅格ID	字符串	32	否
    val grid_id = arr(1)

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

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_INTER_TOURIST(
      grid_id: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      stay_day = -99,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      creator = "zhangyongtian",
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
        !x.grid_id.equals("0")
      })
    outputHiveStd(spark, outputTable, Array("source", "city"), res, dupliCols = Seq("grid_id", "fdate", "source"))
  }
}

class StdResidentCMExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_INTER_RESIDENT] {

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

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_INTER_RESIDENT(
      grid_id: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      creator = "zhangyongtian",
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
        !x.grid_id.equals("0")
      })

    outputHiveStd(spark, outputTable, Array("source", "city"), res1, dupliCols = Seq("grid_id", "fdate", "source"))

  }
}

class StdTouristCMExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_INTER_TOURIST] {

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

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_INTER_TOURIST(

      grid_id: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      stay_day = -99,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      creator = "zhangyongtian",
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
    val rdd2 = getRDD(inputPath + "/tourist*": String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res2 = spark.createDataset(rdd2).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        !x.grid_id.equals("0")
      })

    println("输出表格" + outputTable)
    outputHiveStd(spark, outputTable, Array("source", "city"), res2, dupliCols = Seq("grid_id", "fdate", "source"))

  }
}

class StdWorkDistCuExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_INTER_WORK] {

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
    val work_qty = arr(1).toInt

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

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_INTER_WORK(
      grid_id: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      work_qty: Integer,
      fdate: Int,
      create_time: Int,
      creator = "zhangyongtian",
      city: Int,
      source: String
    )
  }

  def execute() = {
    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        !x.grid_fid.equals("0")
      })
    outputHiveStd(spark, outputTable, Array("source", "city"), res, dupliCols = Seq("grid_fid", "fdate", "source"))
  }
}

class StdWorkDistCmExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_INTER_WORK] {

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
    val work_qty = arr(1).toInt

    //fdate	日期	整型		否		示例20190800表示2019年8月份的数据
    //    val fdate = 20171200
    val fdate = 20190300

    // create_time	数据入库	整型		否		示例20190808
    val create_time = DateTime.now().toString("yyyyMMdd").toInt

    // city	城市名称	字符串		否	1	参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val city_code = gridCityMapBR.get(grid_id).getOrElse("-99")
    val city = if (StringUtils.isEmpty(city_code)) -99 else city_code.toInt

    //source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信
    val source = "cm"

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_INTER_WORK(
      grid_id: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      work_qty: Integer,
      fdate: Int,
      create_time: Int,
      creator = "zhangyongtian",
      city: Int,
      source: String
    )
  }

  def execute() = {
    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        !x.grid_fid.equals("0")
      })

    //    cache(res)

    outputHiveStd(spark, outputTable, Array("source", "city"), res, dupliCols = Seq("grid_fid", "fdate", "source"))

  }
}

class StdHomeWorkDistCuExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_INTER_HOME_WORK] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 2))
    val c2 = (arr.length == 4)
    c1 && c2
  }

  override def map(x: String) = {
    val arr = x.split(",")

    //    home_grid_id	居住地栅格id	字符串	32	否
    val home_grid_fid = arr(0)

    //      work_grid_id	工作地栅格id	字符串	32	否
    val work_grid_fid = arr(1)

    //      home_city	居住地城市	字符串		否		参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val home_city_code = gridCityMapBR.get(home_grid_fid).getOrElse("-99")
    val home_city = if (StringUtils.isEmpty(home_city_code)) -99 else home_city_code.toInt

    //    work_city	工作地城市	字符串		否		参照城市编码对照表
    val work_city_code = gridCityMapBR.get(work_grid_fid).getOrElse("-99")
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

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_INTER_HOME_WORK(
      home_grid_fid: String,
      work_grid_fid: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      home_city: Int,
      work_city: Int,
      source: String,
      creator = "zhangyongtian"

    )
  }

  def execute() = {
    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(dir: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        !x.home_grid_fid.equals("0") && !x.work_grid_fid.equals("0")
      })
    outputHiveStd(spark, outputTable, Array("source", "home_city", "work_city"), res, dupliCols = Seq("home_grid_fid", "work_grid_fid", "fdate", "source"))
  }
}

class StdHomeWorkDistCMExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_INTER_HOME_WORK] {

  override def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 2))
    val c2 = (arr.length == 3)
    c1 && c2 && !x.contains("user_qty")
  }

  override def map(x: String) = {
    val arr = x.split(",")

    //    home_grid_id	居住地栅格id	字符串	32	否
    val home_grid_fid = arr(0)

    //      work_grid_id	工作地栅格id	字符串	32	否
    val work_grid_fid = arr(1)

    //      home_city	居住地城市	字符串		否		参照城市编码对照表
    val gridCityMapBR = broadcast.value
    val home_city_code = gridCityMapBR.get(home_grid_fid).getOrElse("-99")
    val home_city = if (StringUtils.isEmpty(home_city_code)) -99 else home_city_code.toInt

    //    work_city	工作地城市	字符串		否		参照城市编码对照表
    val work_city_code = gridCityMapBR.get(work_grid_fid).getOrElse("-99")
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

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_INTER_HOME_WORK(
      home_grid_fid: String,
      work_grid_fid: String,
      age: Integer,
      sex: Integer,
      adm_reg: Integer,
      has_auto: Integer,
      user_qty: Integer,
      fdate: Int,
      create_time: Int,
      home_city: Int,
      work_city: Int,
      source: String,
      creator = "zhangyongtian"

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
        //         !x.home_grid_id.equals("0") &&  !x.work_grid_id.equals("0")
        !x.home_grid_fid.equals("0") && !x.work_grid_fid.equals("0")
      })
    outputHiveStd(spark, outputTable, Array("source", "home_city", "work_city"), res, dupliCols = Seq("home_grid_fid", "work_grid_fid", "fdate", "source"))
  }
}

class StdTripDistCuExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_TRIP_DIST] {

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

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_TRIP_DIST(
      start_grid_id: String,
      end_grid_id: String,
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
      time_dim: Int,
      year: String,
      month: String,
      day: String,
      "zhangyongtian"
    )
  }

  def execute() = {
    process(inputPath)

  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        !x.start_grid_fid.equals("0") && !x.end_grid_fid.equals("0")
      })

    outputHiveStd(spark, outputTable, Array("source", "start_city", "year", "month", "day"), res, dupliCols = Seq("start_grid_fid", "end_grid_fid", "start_time", "end_time", "source"))
  }
}

class StdTripDistCMExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[S_PHONE_TRIP_DIST] {

  val table = outputTable


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

    val year: String = fdate.toString.substring(0, 4)
    val month: String = fdate.toString.substring(4, 6)
    val day: String = fdate.toString.substring(6, 8)

    S_PHONE_TRIP_DIST(
      start_grid_id: String,
      end_grid_id: String,
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
      time_dim: Int,
      year: String,
      month: String,
      day: String,
      "zhangyongtian"
    )
  }

  def execute() = {
    process(inputPath)
  }

  def process(dir: String) = {
    val rdd = getRDD(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, encode)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))
      .filter(x => {
        !x.start_grid_fid.equals("0") && !x.end_grid_fid.equals("0")
      })
    outputHiveStd(spark, table, Array("source", "start_city", "year", "month", "day"), res, dupliCols = Seq("start_grid_fid", "end_grid_fid", "start_time", "end_time", "source"))

  }
}

//TODO:凯立德导航
//1927800121,34411001718319412762,��BS3R99,2016-11-30 00:00:48,2016-11-30 00:04:07,3,1,00Сʱ01��14��,13,7,��ƽ��  ,1,2016-11-30 00:00:00
class StdGpsDriveCarelandExecutor(inputPath: String, zkUrl: String, spark: SparkSession, outputTable: String, broadcast: Broadcast[Map[String, String]], encode: String) extends BaseExecutor[STD_GPS_DRIVE] {

  def filter(x: String): Boolean = {
    val arr = x.split(",")
    val c1 = StdUtils.allNotEmpty(arr.slice(0, 7)) && arr.length == 8

    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))

    val c2 = dataTime.getMillis <= DateTime.now().getMillis

    c1 && c2

  }

  def map(x: String) = {
    val arr = x.split(",")

    //20190101, 235924, 12207671782273809886, 11, 113.922647, 22.543030, 54, 274
    val time_tmp = arr(0) + StdUtils.fillZero(6, arr(1))

    //loc_time	定位日期	整型	10	否		unix10位时间戳
    val dataTime = DateTime.parse(time_tmp, DateTimeFormat.forPattern("yyyyMMddHHmmss"))
    val loc_time = dataTime.getMillis / 1000

    //vehicle_id	车辆唯一标识（车牌）	字符串		否
    val vehicle_id = arr(2)

    //lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lng = BigDecimal(arr(4)).setScale(6, RoundingMode.HALF_UP)

    //lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
    val lat = BigDecimal(arr(5)).setScale(6, RoundingMode.HALF_UP)

    //speed	瞬时速度（km/h）	浮点	4,1	否
    val speed = BigDecimal(arr(6)).setScale(1, RoundingMode.HALF_UP)

    //angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
    val angle = arr(7).toInt

    //company_code	所属公司代码	字符串		否		公司代码编码
    val company_code = "-99"


    //city	城市名称	字符串		否	1	参照城市编码对照表
    val city_name = "shenzhen"

    //深圳
    val city = 440300

    //is_validate	数据是否有效，	整型	 1	否		0表示异常，1表示有效
    val is_validate = -99

    val year = arr(0).substring(0, 4).toInt
    val month = arr(0).substring(4, 6).toInt
    val day = arr(0).substring(6, 8).toInt

    val source_company = "careland"

    val navigation_status = arr(3).toInt

    val license_prefix = null

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



