package com.sutpc.bigdata.executor.std.videoAIProtoBuf

import java.util.UUID

import com.alibaba.fastjson.JSON
import com.sutpc.bigdata.job.batch.real.{BaseTask, EtlTask, TaskOutput}
import com.sutpc.bigdata.schema.hive.{STD_GPS_ORDER, s_vai_flow_rider}
import com.sutpc.bigdata.utils.{StrUtils, TimeTransform}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * <p>Description:TODO 视频AI 骑行流量识别事件子表 清洗到标准库-s_vai_flow_rider </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/5/12 17:06
  */

/**
  * wiki说明: http://wiki.sutpc.org/pages/viewpage.action?pageId=38961326#id-11-%E8%A7%86%E9%A2%91AI%E4%B8%BB%E9%A2%98-%E9%AA%91%E8%A1%8C%E6%B5%81%E9%87%8F%E8%AF%86%E5%88%AB%E4%BA%8B%E4%BB%B6%E5%AD%90%E8%A1%A8s_vai_flow_rider
  * 原始数据存储目录: http://10.3.3.20:8889/hue/filebrowser/view=/data/origin/vai/qx#/data/origin/vai/flowRider
  * 数据格式样例: {"area_num":0,"bayonet_direction":1,"end_time":1589183625,"event_fid":"","flow_direction":2,"id":null,"in_num":2,"out_num":1,"sample_dura":25,"speed":0.72,"start_time":1589183600,"type":"flowRider","wait_num":3}
  */
class VideoAiFlowRider202005(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER] {
  def map(x: String): String = {
    x.replaceAll("null", "\"\"")
  }

  override def filter(x: String): Boolean = {
    var flag = true
    try{
      val nObject = JSON.parseObject(x)
      if(nObject.getInteger("type") != 2) {
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
        val str = TimeTransform.unixTime2DateFormat(StrUtils.intIsNull(obj.getString("end_time"))+"", "yyyy-MM-dd HH:mm:ss")
        val dataTime = DateTime.parse(str, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

        //使用当前时间作为分区
        //val dataTime = new DateTime()

        val tmpTime2 = dataTime.toString("yyyyMMddHH")
        val year = tmpTime2.substring(0, 4)
        val month = tmpTime2.substring(4, 6)
        val day = tmpTime2.substring(6, 8)

        s_vai_flow_rider(
          UUID.randomUUID().toString,
          StrUtils.stringIsNull(obj.getString("event_fid")),
          StrUtils.intIsNull(obj.getString("start_time")),
          StrUtils.intIsNull(obj.getString("end_time")),
          StrUtils.intIsNull(obj.getString("bayonet_direction")),
          StrUtils.intIsNull(obj.getString("flow_direction")),
          StrUtils.intIsNull(obj.getString("sample_dura")),
          StrUtils.intIsNull(obj.getString("area_num")),
          StrUtils.doubleIsNull(obj.getString("speed")),
          StrUtils.stringIsNull(obj.getString("speed_unit")),
          StrUtils.intIsNull(obj.getString("out_num")),
          StrUtils.intIsNull(obj.getString("in_num")),
          StrUtils.intIsNull(obj.getString("wait_num")),
          440300,
          year,
          month,
          day
        )
      })
    res.show(10, false)
    TaskOutput.hive[s_vai_flow_rider](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq(""), isCopy2Tag = false)
  }
}
