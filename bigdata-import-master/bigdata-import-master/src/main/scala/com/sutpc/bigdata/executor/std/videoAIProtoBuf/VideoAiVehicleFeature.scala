package com.sutpc.bigdata.executor.std.videoAIProtoBuf

import java.util.UUID

import com.alibaba.fastjson.JSON
import com.sutpc.bigdata.job.batch.real.{BaseTask, EtlTask, TaskOutput}
import com.sutpc.bigdata.schema.hive.{STD_GPS_ORDER, s_vai_vehicle_feature}
import com.sutpc.bigdata.utils.{StrUtils, TimeTransform}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * <p>Description:TODO 视频AI 车辆特征识别 清洗到标准库-s_vai_vehicle_feature </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/5/13 9:53
  */

/**
  * wiki说明: http://wiki.sutpc.org/pages/viewpage.action?pageId=38961326#id-11-%E8%A7%86%E9%A2%91AI%E4%B8%BB%E9%A2%98-%E8%BD%A6%E8%BE%86%E7%89%B9%E5%BE%81%E8%AF%86%E5%88%ABs_vai_vehicle_feature
  * 原始数据存储目录: http://10.3.3.20:8889/hue/filebrowser/view=/data/origin/vai#/data/origin/vai/vehicleFeature
  * 数据格式样例: {"bayonet_direction":0,"drive_direction":0,"event_fid":"","id":"","lane":"5","license_color":"grn","score":0,"speed":61.87,"type":12,"v_violation_id":"0","vehicle_color":"uk","vehicle_id":"粤B-00001","vehicle_type":0,"violation":0}
  */
class VideoAiVehicleFeature202005(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER] {
  def map(x: String): String = {
    x.replaceAll("null", "\"\"")
  }

  override def filter(x: String): Boolean = {
    var flag = true
    try{
      val nObject = JSON.parseObject(x)
      if(nObject.getInteger("type") != 12) {
        flag = false
      }
    }catch {
      case e: Exception => {
        flag = false
      }
    }
    flag
  }

  override def execute(): Unit = {
    println("读取【" + task.input + "】数据")
    smartLoop(task.input: String, spark: SparkSession)
  }

  override def process(dir: String): Unit = {
    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask)
    import spark.implicits._
    val res = spark.createDataset(rdd)
      .filter(x => filter(x))
      .map(x => {map(x)})
      .as[String]
      .map(x => {
        val obj = JSON.parseObject(x)
        //使用json中的 流量统计结束时间 作为 分区
        val str = TimeTransform.unixTime2DateFormat(StrUtils.intIsNull(obj.getString("end_time")) + "", "yyyy-MM-dd HH:mm:ss")
        val dataTime = DateTime.parse(str, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

        //使用当前时间作为分区
        //val dataTime = new DateTime()

        val tmpTime2 = dataTime.toString("yyyyMMddHH")
        val year = tmpTime2.substring(0, 4)
        val month = tmpTime2.substring(4, 6)
        val day = tmpTime2.substring(6, 8)
        val hours = tmpTime2.substring(8, 10)

        s_vai_vehicle_feature(
          UUID.randomUUID().toString,
          StrUtils.stringIsNull(obj.getString("event_fid")),
          StrUtils.intIsNull(obj.getString("violation")),
          StrUtils.intIsNull(obj.getString("v_violation_id")),
          StrUtils.stringIsNull(obj.getString("score")),
          StrUtils.intIsNull(obj.getString("bayonet_direction")),
          StrUtils.intIsNull(obj.getString("drive_direction")),
          StrUtils.intIsNull(obj.getString("vehicle_type")),
          StrUtils.stringIsNull(obj.getString("lane")),
          StrUtils.doubleIsNull(obj.getString("speed")),
          StrUtils.stringIsNull(obj.getString("vehicle_id")),
          StrUtils.stringIsNull(obj.getString("license_color")),
          StrUtils.stringIsNull(obj.getString("vehicle_color")),
          440300,
          year,
          month,
          day
        )
      })
    res.show(10, false)
    TaskOutput.hive[s_vai_vehicle_feature](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq(""), isCopy2Tag = false)
  }
}
