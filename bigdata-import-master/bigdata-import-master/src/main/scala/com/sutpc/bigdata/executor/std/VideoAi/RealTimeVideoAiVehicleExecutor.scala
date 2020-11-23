package com.sutpc.bigdata.executor.std.VideoAi

import java.util.UUID

import com.alibaba.fastjson.{JSON, JSONObject}
import com.sutpc.bigdata.job.batch.real.{BaseTask, EtlTask, TaskOutput}
import com.sutpc.bigdata.schema.hive.{STD_GPS_ORDER, s_vai_vehicle_feature}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable.ListBuffer

/**
  * <p>Description:TODO 视频AI 车辆特征识别 清洗到标准库-s_vai_vehicle_feature</p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/3/14 16:47
  */
/**
  * 2020/03/14: http://wiki.sutpc.org/pages/viewpage.action?pageId=38961326#id-11-%E8%A7%86%E9%A2%91AI%E4%B8%BB%E9%A2%98-%E8%BD%A6%E8%BE%86%E7%89%B9%E5%BE%81%E8%AF%86%E5%88%ABs_vai_vehicle_feature
  *   数据格式: /data/origin/vai/qx/traffic_vehicles
  *     {"camera_id":"22104","event_cate":"traffic","event_date":"2020-03-05 00:01:40","event_refid":"98894d5ede39424ba9ff04725f4ad30e","event_type":"4","pictures":[],"server_id":"1","source_id":"3","traffic_vehicles":[{"bayonet_direction":0,"drive_direction":4,"end_date":null,"lane":3,"license_plate":"粤B-00000","license_plate_type":4,"start_date":null,"vehicle_color":1,"vehicle_type":1,"vehicle_violation_id":0,"velocity":36.6,"violation_code":0,"violation_score":0}],"version":"v1.7.4","videos":[]}
  */
class RealTimeVideoAiVehicleExecutor2020(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER]{
  override def filter(x: String): Boolean = {
    !JSON.parseObject(x).getJSONArray("traffic_vehicles").isEmpty
  }

  override def execute(): Unit = {
    println("读取【" + task.input + "】数据")
    smartLoop(task.input: String, spark: SparkSession)
  }

  override def process(dir: String): Unit = {
    val rdd = hdfs2RDD(dir: String, spark: SparkSession, task: EtlTask)
    import spark.implicits._
    val res = spark.createDataset(rdd).as[String]
      .flatMap(x => {
        val obj = JSON.parseObject(x)
        val event_refid = obj.getString("event_refid") //主事件全局唯一id
        val tmpTime = obj.getString("event_date").trim   //时间
        val dataTime = DateTime.parse(tmpTime, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
        val tmpTime2 = dataTime.toString("yyyyMMddHH")
        val year = tmpTime2.substring(0, 4)
        val month = tmpTime2.substring(4, 6)
        val day = tmpTime2.substring(6, 8)
        val hours = tmpTime2.substring(8, 10)
        val arr= obj.getJSONArray("traffic_vehicles")
        val it = arr.iterator()
        val buffer = ListBuffer[s_vai_vehicle_feature]()
        while (it.hasNext) {
          val trafficPedestriansObj = it.next.asInstanceOf[JSONObject]
          val violation_code = trafficPedestriansObj.getInteger("violation_code") //机动车违章
          val vehicle_violation_id = trafficPedestriansObj.getInteger("vehicle_violation_id") //机动车违章id
          val violation_score = trafficPedestriansObj.getString("violation_score") //事件置信度
          val bayonet_direction = trafficPedestriansObj.getInteger("bayonet_direction") //卡口方向
          val drive_direction = trafficPedestriansObj.getInteger("drive_direction") //形式方向
          val vehicle_type = trafficPedestriansObj.getInteger("vehicle_type") //车辆类型
          val lane = trafficPedestriansObj.getString("lane") //车道
          val velocity = trafficPedestriansObj.getDouble("velocity") //速度
          val license_plate = trafficPedestriansObj.getString("license_plate") //车牌号
          val license_plate_type = trafficPedestriansObj.getString("license_plate_type") //车牌颜色
          val vehicle_color = trafficPedestriansObj.getString("vehicle_color") //车身颜色
          buffer += s_vai_vehicle_feature(UUID.randomUUID().toString,event_refid,violation_code,vehicle_violation_id,
            violation_score,bayonet_direction,drive_direction,vehicle_type,lane,velocity,license_plate,
            license_plate_type,vehicle_color,440300, year,month,day)
        }
        buffer.toList
      })
    res.show(10, false)
    TaskOutput.hive[s_vai_vehicle_feature](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq(""), isCopy2Tag = false)
  }
}
