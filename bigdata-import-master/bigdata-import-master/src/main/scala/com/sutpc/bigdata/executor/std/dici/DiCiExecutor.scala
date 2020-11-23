package com.sutpc.bigdata.executor.std.dici

import java.util.UUID

import com.sutpc.bigdata.job.batch.real.{BaseTask, EtlTask, TaskOutput}
import com.sutpc.bigdata.schema.hive.{STD_GPS_ORDER, s_road_geomagnetic_detection}
import com.sutpc.bigdata.utils.DateTimeUtils
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable.ListBuffer

/**
  * <p>Description:TODO 地磁数据清洗到标准库-s_road_geomagnetic_detection </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/3/24 9:47
  */

/**
  * 此类为2017年10月的数据格式
  *   数据格式: /data/origin/road/flow/dici/440300/2017/10
  *   数据格式说明:
  *     年、月、日、时、分、秒|车道             |检测器id  |车辆型号    |扩展字段     |车头时距       |速度                         |占有时间    |间隔时间          |车长
  *     2017-10-01 00:02:36,19980021,2690337,102,      1,0,1,0,0,0, 0,0,0,0,0,0,6000,6000,6000,63.436123,63.436123,63.436123,440,440,440,30501,30501,30501,7.753304,7.753304,7.753304,0,0,0,0,0,0
  * 清洗规则:
  *   1. 车速小于 0 的过滤掉
  *   2. 占有时间大于 10万 的过滤掉
  */
class DiCiExecutor201710(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER]  {
  override def filter(x: String): Boolean = {
    val splits = x.split(",")
    splits.length match {
      case 37 =>
        val speed = splits(19).toDouble //平均速度
        val occupancy = splits(22).toInt //平均占有时间
        speed >= 0 && occupancy < 100000 && DateTimeUtils.validateDf(splits(0), "yyyy-MM-dd HH:mm:ss")
      case _ => false
    }
  }

  override def execute(): Unit = {
    println("读取【" + task.input + "】数据")
    smartLoop(task.input: String, spark: SparkSession)
  }

  def map(x: String) = {
    val splits = x.split(",")
    splits.length match {
      case 37 => {
        val buffer = ListBuffer[s_road_geomagnetic_detection]()

        val dateTime: String = splits(0) //时间 年-月-日 时:分:秒
        val lane_fid: String = splits(3) //车道id
        val detector_fid: String = splits(1) //检测器id

        val dache = splits(5) //车流量 - 大车
        val zhognche  = splits(6) //车流量 - 中车
        val xiaoche = splits(7) //车流量 - 小车
        val weixinche = splits(8) //车流量 - 微型车
        val motuoche = splits(9) //车流量 - 摩托车

        var array = Array[Int]()
        if(dache.toInt > 0) array :+= 1
        if(zhognche.toInt > 0) array :+= 2
        if(xiaoche.toInt > 0) array :+= 3
        if(weixinche.toInt > 0) array :+= 4
        if(motuoche.toInt > 0) array :+= 5

        val headway = splits(16).toInt // 车头时距
        val speed = splits(19).toDouble //平均速度
        val occupancy = splits(22).toInt //平均占有时间
        val interval = splits(25).toInt //平均间隔时间
        val length = splits(28).toDouble // 平局车长

        val dataTime = DateTime.parse(dateTime, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
        val tmpTime2 = dataTime.toString("yyyyMMddHHmm")
        val year = tmpTime2.substring(0, 4).toInt
        val month = tmpTime2.substring(4, 6).toInt
        val day = tmpTime2.substring(6, 8).toInt
        val hh = tmpTime2.substring(8, 10).toInt
        val mm = tmpTime2.substring(10, 12).toInt

        array.length match {
          case 0 => {
            buffer += s_road_geomagnetic_detection(
              UUID.randomUUID().toString,detector_fid,year,month,day,hh,mm,lane_fid,
              -1, headway, speed, occupancy, interval, length, dataTime.getMillis / 1000, 440300)
          }
          case 1 => {
            buffer += s_road_geomagnetic_detection(
              UUID.randomUUID().toString,detector_fid,year,month,day,hh,mm,lane_fid,
              array(0), headway, speed, occupancy, interval, length, dataTime.getMillis / 1000, 440300)
          }
          case _ => {
            for (i <- 1 to array.length) {
              buffer += s_road_geomagnetic_detection(
                UUID.randomUUID().toString,detector_fid,year,month,day,hh,mm,lane_fid,
                array(i-1), 0, 0, 0, 0, 0, dataTime.getMillis / 1000, 440300)
            }
          }
        }
        buffer
      }
    }
  }

  override def process(dir: String): Unit = {
    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .filter(x => filter(x))
      .flatMap(x => map(x)).coalesce(2)

//    val res1 = spark.createDataset(rdd).as[String]
//      .filter(x => filter(x))
//        .filter(x => {
//          val splits: Array[String] = x.split(",")
//          val vehicleFlow = splits(4).toInt //车流量 - 检测车流量
//          vehicleFlow != 1
//        }).map(x => map(x)).coalesce(2)
//
//    println("没有被过滤掉的数据共有: "+res.count())
//
//    println("dir 被过滤掉的数据如下: ")
//    println("总共被过滤调了: "+res1.count() + " 条记录..., 占比重: " + res1.count() / res.count())
//    println("明细如下: ")
//    res1.show(1000000,false)
    TaskOutput.hive[s_road_geomagnetic_detection](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq(""), isCopy2Tag = false)
  }
}