package com.sutpc.bigdata.executor.std.videoAIProtoBuf

import java.util.UUID

import com.alibaba.fastjson.JSON
import com.sutpc.bigdata.job.batch.real.{BaseTask, EtlTask, TaskOutput}
import com.sutpc.bigdata.schema.hive.{STD_GPS_ORDER, s_vai_primary}
import com.sutpc.bigdata.utils.StrUtils
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * <p>Description:TODO 视频AI 识别信息主表 清洗到标准库-s_vai_primary </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/5/12 14:07
  */

/**
  * wiki说明: http://wiki.sutpc.org/pages/viewpage.action?pageId=38961326#id-11-%E8%A7%86%E9%A2%91AI%E4%B8%BB%E9%A2%98-%E8%A7%86%E9%A2%91AI%E8%AF%86%E5%88%AB%E4%BF%A1%E6%81%AF%E4%B8%BB%E8%A1%A8s_vai_primary
  * 原始数据存储目录: http://10.3.3.20:8889/hue/filebrowser/view=/data/origin/vai#/data/origin/vai/primary
  * 数据格式样例: {"camera_fid":1021,"city":null,"event_cate":"behavior","event_date":"2020-05-11 15:53:21","event_refid":"","event_type":1,"id":null,"pole_fid":null,"server_fid":null,"type":"primary","vai_source":3,"version":"1.0.6"}
  */
class VideoAiPrimary202005(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER]  {
  def map(x: String): String = {
    x.replaceAll("null", "\"\"")
  }

  override def filter(x: String): Boolean = {
    var flag = true
    try{
      val nObject = JSON.parseObject(x)
      if(nObject.getInteger("type") != 1) {
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
        val tmpTime = obj.getString("event_date").trim //时间
        val dataTime = DateTime.parse(tmpTime, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
        val tmpTime2 = dataTime.toString("yyyyMMddHH")
        val year = tmpTime2.substring(0, 4)
        val month = tmpTime2.substring(4, 6)
        val day = tmpTime2.substring(6, 8)

        s_vai_primary(
          UUID.randomUUID().toString,
          StrUtils.stringIsNull(obj.getString("event_refid")),
          StrUtils.stringIsNull(obj.getString("event_cate")),
          StrUtils.stringIsNull(obj.getString("event_type")),
          StrUtils.stringIsNull(obj.getString("server_fid")),
          StrUtils.stringIsNull(obj.getString("event_date")),
          StrUtils.intIsNull(obj.getString("camera_fid")),
          StrUtils.stringIsNull(obj.getString("pole_fid")),
          StrUtils.intIsNull(obj.getString("vai_source")),
          StrUtils.stringIsNull(obj.getString("version")),
          if(obj.getString("city").trim.isEmpty) 400300 else obj.getString("city").toInt,
          year,
          month,
          day)
      })
    res.show(10, false)
    TaskOutput.hive[s_vai_primary](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq(""), isCopy2Tag = false)
  }
}


