package com.sutpc.bigdata.executor.std.videoAIProtoBuf

import java.util.UUID

import com.alibaba.fastjson.JSON
import com.sutpc.bigdata.job.batch.real.{BaseTask, EtlTask, TaskOutput}
import com.sutpc.bigdata.schema.hive.{STD_GPS_ORDER, s_vai_pedestrian_feature}
import com.sutpc.bigdata.utils.{StrUtils, TimeTransform}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * <p>Description:TODO 视频AI 行人特征识别 清洗到标准库-s_vai_pedestrian_feature </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/5/13 9:14
  */

/**
  * wiki说明: http://wiki.sutpc.org/pages/viewpage.action?pageId=38961326#id-11-%E8%A7%86%E9%A2%91AI%E4%B8%BB%E9%A2%98-%E8%A1%8C%E4%BA%BA%E7%89%B9%E5%BE%81%E8%AF%86%E5%88%ABs_vai_pedestrian_feature
  * 原始数据存储目录: http://10.3.3.20:8889/hue/filebrowser/view=/data/origin/vai#/data/origin/vai/pedestrianFeature
  * 数据格式样例: {"age_stage":2,"age_stage_score":99,"bag":0,"bag_score":99,"bayonet_direction":1,"bike_score":null,"cap":0,"cap_score":99,"company":0,"drive_direction":2,"end_time":1589295958,"event_fid":"","hair":1,"hair_score":99,"id":"","is_bike":1,"lobody_clothes":"CLOTHES_TYPE_UNKNOWN","lobody_color":"uk","lobody_score":0,"nv_violation_id":"3461","pedestrianId":6867,"rect_bottom":0,"rect_left":0,"rect_right":0,"rect_top":0,"score":99,"sex":1,"speed":2.09,"start_time":1589295918,"stature":0,"stature_score":0,"type":11,"upbody_clothes":"CLOTHES_TYPE_UNKNOWN","upbody_color":"uk","upbody_score":0,"violation":2}
  */
class VideoAiPedestrianFeature202005(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER] {
  def map(x: String): String = {
    x.replaceAll("null", "\"\"")
  }

  override def filter(x: String): Boolean = {
    var flag = true
    try{
      val nObject = JSON.parseObject(x)
      if(nObject.getInteger("type") != 11) {
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

        s_vai_pedestrian_feature(
          UUID.randomUUID().toString,
          StrUtils.stringIsNull(obj.getString("event_fid")),
          StrUtils.doubleIsNull(obj.getString("speed")),
          StrUtils.longIsNull(obj.getString("start_time")),
          StrUtils.longIsNull(obj.getString("end_time")),
          StrUtils.intIsNull(obj.getString("violation")),
          StrUtils.intIsNull(obj.getString("nv_violation_id")),
          StrUtils.doubleIsNull(obj.getString("score")),
          StrUtils.intIsNull(obj.getString("bayonet_direction")),
          StrUtils.intIsNull(obj.getString("drive_direction")),
          StrUtils.intIsNull(obj.getString("rect_top")),
          StrUtils.intIsNull(obj.getString("rect_left")),
          StrUtils.intIsNull(obj.getString("rect_bottom")),
          StrUtils.intIsNull(obj.getString("rect_right")),
          StrUtils.intIsNull(obj.getString("sex")),
          StrUtils.stringIsNull(obj.getString("upbody_color")),
          StrUtils.stringIsNull(obj.getString("upbody_clothes")),
          StrUtils.intIsNull(obj.getString("upbody_score")),
          StrUtils.stringIsNull(obj.getString("lobody_color")),
          StrUtils.stringIsNull(obj.getString("lobody_clothes")),
          StrUtils.intIsNull(obj.getString("lobody_score")),
          StrUtils.intIsNull(obj.getString("cap")),
          StrUtils.intIsNull(obj.getString("cap_score")),
          StrUtils.intIsNull(obj.getString("stature")),
          StrUtils.intIsNull(obj.getString("stature_score")),
          StrUtils.intIsNull(obj.getString("bag")),
          StrUtils.intIsNull(obj.getString("bag_score")),
          StrUtils.intIsNull(obj.getString("hair")),
          StrUtils.intIsNull(obj.getString("hair_score")),
          StrUtils.intIsNull(obj.getString("age_stage")),
          StrUtils.intIsNull(obj.getString("age_stage_score")),
          StrUtils.intIsNull(obj.getString("is_bike")),
          StrUtils.intIsNull(obj.getString("bike_score")),
          StrUtils.intIsNull(obj.getString("company")),
          440300,
          year,
          month,
          day
        )
      })
    res.show(10, false)
    TaskOutput.hive[s_vai_pedestrian_feature](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq(""), isCopy2Tag = false)
  }
}
