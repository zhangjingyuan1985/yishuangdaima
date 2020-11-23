package com.sutpc.bigdata.executor.std.VideoAi

import java.util.UUID

import com.alibaba.fastjson.{JSON, JSONObject}
import com.sutpc.bigdata.job.batch.real.{BaseTask, EtlTask, TaskOutput}
import com.sutpc.bigdata.schema.hive.{STD_GPS_ORDER, s_vai_pedestrian_feature}
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable.ListBuffer

/**
  * <p>Description:TODO 视频AI 行人特征识别 清洗到标准库-s_vai_pedestrian_feature</p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2020/3/14 16:41
  */
/**
  * 2020/03/14: http://wiki.sutpc.org/pages/viewpage.action?pageId=38961326#id-11-%E8%A7%86%E9%A2%91AI%E4%B8%BB%E9%A2%98-%E8%BD%A6%E8%BE%86%E7%89%B9%E5%BE%81%E8%AF%86%E5%88%ABs_vai_vehicle_feature
  *   数据格式为: /data/origin/vai/qx/traffic_pedestrians
  *   {"camera_id":"22059","event_cate":"traffic","event_date":"2020-03-05 00:01:50","event_refid":"7e0ef0532f6f4ab68bd60e5fba8f5d41","event_type":"3","pictures":[],"server_id":"1","source_id":"3","traffic_pedestrians":[{"bayonet_direction":2,"drive_direction":2,"pedestrians":[{"age_stage":3,"age_stage_score":0,"bag":1,"bag_score":0,"bike":2,"bike_score":99,"cap":2,"cap_score":0,"company":0,"end_date":"2020-03-05 00:01:50","hair":2,"hair_score":0,"lobody_clothes":0,"lobody_color":0,"lobody_score":0,"nonvehicle_violation_id":0,"pedestrian_id":0,"rect":{"bottom":0.0,"left":0.0,"right":0.0,"top":0.0},"sex":1,"speed":6.2,"start_date":"2020-03-05 00:01:50","stature":3,"stature_score":0,"upbody_clothes":0,"upbody_color":1,"upbody_score":0,"violation_code":0,"violation_score":0}]}],"version":"v1.7.4","videos":[]}
  */
class RealTimeVideoAiPedestrianExecutor2020(spark: SparkSession, task: EtlTask) extends BaseTask[STD_GPS_ORDER] {
  override def filter(x: String): Boolean = !JSON.parseObject(x).getJSONArray("traffic_pedestrians").isEmpty

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
        val arr= obj.getJSONArray("traffic_pedestrians")
        //          if(!arr.isEmpty) {
        val it = arr.iterator()
        val buffer = ListBuffer[s_vai_pedestrian_feature]()
        while (it.hasNext) {
          val trafficPedestriansObj = it.next.asInstanceOf[JSONObject]
          val bayonet_direction = trafficPedestriansObj.getInteger("bayonet_direction") //卡口方向
          val drive_direction = trafficPedestriansObj.getInteger("drive_direction") //形式方向
          val pedestriansArr = trafficPedestriansObj.getJSONArray("pedestrians")
          if(!pedestriansArr.isEmpty) {
            val arrIt = pedestriansArr.iterator()
            while (arrIt.hasNext) {
              val arrObj = arrIt.next.asInstanceOf[JSONObject]
              val speed = arrObj.getDouble("speed")//速度
              val start_date = DateTime.parse(arrObj.getString("start_date").trim, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))//开始时间
              val end_date = DateTime.parse(arrObj.getString("end_date").trim, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))//结束时间
              val violation_code = arrObj.getInteger("violation_code") //非机动车违章
              val nonvehicle_violation_id = arrObj.getInteger("nonvehicle_violation_id") //非机动车违章id
              val violation_score = arrObj.getDouble("violation_score") //特征得分
              var rect_top = 0
              var rect_left = 0
              var rect_bottom = 0
              var rect_right = 0
              val rectObj = arrObj.getJSONObject("rect")
              rect_top = rectObj.getInteger("top")
              rect_left = rectObj.getInteger("left")
              rect_bottom = rectObj.getInteger("bottom")
              rect_right = rectObj.getInteger("right")
              val sex = arrObj.getInteger("sex") //性别
              val upbody_color = arrObj.getString("upbody_color")//上半身颜色
              val upbody_clothes = arrObj.getString("upbody_clothes")//上半身衣服类型
              val upbody_score = arrObj.getInteger("upbody_score")//上半身衣服可信度
              val lobody_color = arrObj.getString("lobody_color") //下半身颜色
              val lobody_clothes = arrObj.getString("lobody_clothes") //下半身衣服类型
              val lobody_score = arrObj.getInteger("lobody_score") //下半身可信度
              val cap = arrObj.getInteger("cap")  //是否戴帽子
              val cap_score = arrObj.getInteger("cap_score") //是否戴帽子可信度
              val stature = arrObj.getInteger("stature")    //身高类型
              val stature_score = arrObj.getInteger("stature_score")//身高类型可信度
              val bag = arrObj.getInteger("bag")      // 背包类型
              val bag_score  = arrObj.getInteger("bag_score")  //背包可信度
              val hair = arrObj.getInteger("hair") //头发类型
              val hair_score = arrObj.getInteger("hair_score")//头发类型可信度
              val age_stage = arrObj.getInteger("age_stage")    // 年龄类型
              val age_stage_score = arrObj.getInteger("age_stage_score") // 年龄可信度
              val bike = arrObj.getInteger("bike")   //是否骑行
              val bike_score = arrObj.getInteger("bike_score") //是否骑行可信度
              val company = arrObj.getInteger("company") //运营商所属编码

              arrObj.getInteger("pedestrian_id") // 行人id

              val tmpTime = arrObj.getString("start_date").trim
              val dataTime = DateTime.parse(tmpTime, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
              val tmpTime2 = dataTime.toString("yyyyMMddHH")
              val year = tmpTime2.substring(0, 4)
              val month = tmpTime2.substring(4, 6)
              val day = tmpTime2.substring(6, 8)

              buffer += s_vai_pedestrian_feature(UUID.randomUUID().toString, event_refid, speed,start_date.getMillis / 1000, end_date.getMillis / 1000,
                violation_code, nonvehicle_violation_id, violation_score, bayonet_direction, drive_direction, rect_top, rect_left, rect_bottom, rect_right, sex,
                upbody_color, upbody_clothes, upbody_score, lobody_color, lobody_clothes, lobody_score, cap, cap_score, stature, stature_score,
                bag, bag_score, hair, hair_score, age_stage, age_stage_score, bike, bike_score, company, 440300, year,month,day)
            }
          }
        }
        buffer.toList
        //          }
      })

    res.show(10, false)
    TaskOutput.hive[s_vai_pedestrian_feature](spark, task, Array("city", "year", "month", "day"), res, isPersist = false, dupliCols = Seq(""), isCopy2Tag = false)
  }
}

