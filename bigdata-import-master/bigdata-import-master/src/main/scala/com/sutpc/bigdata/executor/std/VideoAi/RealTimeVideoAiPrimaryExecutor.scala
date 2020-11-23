package com.sutpc.bigdata.executor.std.VideoAi

import java.util.UUID

import com.alibaba.fastjson.JSON
import com.sutpc.bigdata.job.batch.real.{BaseTask, EtlTask, TaskOutput}
import com.sutpc.bigdata.schema.hive.{STD_GPS_ORDER, s_vai_primary}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * <p>Description:TODO 视频AI 主表 清洗到标准库-s_vai_primary </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/3/16 16:08
  */


/**
  * 2020/03/14: http://wiki.sutpc.org/pages/viewpage.action?pageId=38961326#id-11-%E8%A7%86%E9%A2%91AI%E4%B8%BB%E9%A2%98-%E8%BD%A6%E8%BE%86%E7%89%B9%E5%BE%81%E8%AF%86%E5%88%ABs_vai_vehicle_feature
  *   数据格式: /data/origin/vai/qx/所有
  *     只用JSONObject的第一层数据
  */

class RealTimeVideoAiPrimaryExecutor2020(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER] {
  override def filter(x: String): Boolean = ???

  override def execute(): Unit = {
    println("读取【" + task.input + "】数据")
    smartLoop(task.input: String, spark: SparkSession)
  }

  override def process(dir: String): Unit = {
    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .map(x => {
        val obj = JSON.parseObject(x)
        val event_refid = obj.getString("event_refid")  //事件全局唯一id
        val event_cate = obj.getString("event_cate") //事件大类
        val event_type = obj.getString("event_type") //事件小类
        val server_fid = obj.getString("server_id")  //分析主机的id
        val event_date = obj.getString("event_date") //事件上传时间
        val camera_fid = obj.getInteger("camera_id")  //关联的摄像头id
        val pole_fid = "-99" //杆件ID  通过 camera_fid 关联  s_common_base_camera 表 获取
        val vai_source = obj.getInteger("source_id")  //视频算法供应商编码
        val version = obj.getString("version")  //协议编码
        val city = 440300 //通过 camera_fid 关联  s_common_base_camera 表 获取

        val tmpTime = obj.getString("event_date").trim //时间
        val dataTime = DateTime.parse(tmpTime, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
        val tmpTime2 = dataTime.toString("yyyyMMddHH")
        val year = tmpTime2.substring(0, 4)
        val month = tmpTime2.substring(4, 6)
        val day = tmpTime2.substring(6, 8)

        s_vai_primary(UUID.randomUUID().toString, event_refid, event_cate, event_type, server_fid, event_date,
          camera_fid, pole_fid, vai_source, version, city, year, month, day)
      })
    res.show(10, false)
    TaskOutput.hive[s_vai_primary](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq(""), isCopy2Tag = false)
  }
}
