package com.sutpc.bigdata.executor.std.taxi

import com.sutpc.bigdata.job.batch.real.{BaseTask, EtlTask, TaskOutput}
import com.sutpc.bigdata.schema.hive.{STD_GPS_ORDER, s_vehicle_gps_taxi}
import com.sutpc.bigdata.utils.{StdUtils, TimeTransform}
import org.apache.spark.sql.SparkSession

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/3/24 20:10
  */
class TaxiGpsExecutor201710(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER]   {
  override def filter(x: String): Boolean = {
    val strs = x.split(",")
    strs.length match {
      case 10=>
        var speed = strs(6).toDouble.toLong
        var lng = strs(4).toDouble
        var lat = strs(5).toDouble
        speed>=0&&speed<900&&StdUtils.isCnLngLat(lng,lat)
      case _=>false
    }
  }

  override def execute(): Unit = {
    println("读取【" + task.input + "】数据")
    smartLoop(task.input: String, spark: SparkSession)
  }
  def  map(row:String): s_vehicle_gps_taxi ={
    val strs = row.split(",")
    val vehNo = s"粤${strs(3).substring(strs(3).length - 6, strs(3).length)}"


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
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .map(x => map(x))//.coalesce(3)
    println("结果输出....")
    res.show(10, false)

    val res2txt = res.map(x => {
      s"${x.vehicle_id}, ${x.loc_time}, ${x.lng}, ${x.lat}, ${x.speed}, ${x.angle}, ${x.operation_status}, ${x.is_validate}, ${x.company_code}, ${x.altitude}, ${x.vehicle_color}, ${x.total_mileage}, ${x.alarm_status}, ${x.vehicle_status}, ${x.city}, ${x.year}, ${x.month}, ${x.day}"
    })

    TaskOutput.hive[s_vehicle_gps_taxi](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq(""), isCopy2Tag = false)
  }
}
