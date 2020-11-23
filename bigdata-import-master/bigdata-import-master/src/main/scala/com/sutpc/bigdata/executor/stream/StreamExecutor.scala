package com.sutpc.bigdata.executor.stream

import com.alibaba.fastjson.JSON
import com.sutpc.bigdata.schema.hive.STD_GPS_DRIVE
import com.sutpc.bigdata.utils.SparkHiveUtil
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.joda.time.DateTime

import scala.collection.mutable.ArrayBuffer
import scala.math.BigDecimal.RoundingMode

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/11/8  17:36
  */
class GpsDriveBaiduSampleExecutor(spark: SparkSession) extends Serializable {

  def run(stream: DStream[String], outTables: String): Unit = {
    println("开始处理" + outTables)
    stream.foreachRDD(rdd => {
      process(rdd, outTables)
    })

  }


  def process(rdd: RDD[String], outTables: String) = {
    import spark.implicits._

    val result =
      rdd.filter(r => {
        val obj = JSON.parseObject(r)
        obj.containsKey("uid") && obj.containsKey("dataType") && obj.containsKey("gpsDatas")
      }).flatMap(x => {

        val obj = JSON.parseObject(x)

        val vehicle_id: String = obj.get("uid").toString

        val dataType = obj.get("dataType").toString.toInt

        val datas = if (obj.containsKey("gpsDatas")) obj.get("gpsDatas").toString else "[]"

        val jsonArr = JSON.parseArray(datas)

        val tmpArr = new ArrayBuffer[STD_GPS_DRIVE]()

        for (i <- Range(0, jsonArr.toArray.length)) {

          val obj = jsonArr.getJSONObject(i)

          val loc_time: Long = obj.getIntValue("timestamp")

          val dateTime = new DateTime(loc_time * 1000)

          val lng: BigDecimal = BigDecimal(obj.getDoubleValue("longitude")).setScale(6, RoundingMode.HALF_UP)
          val lat: BigDecimal = BigDecimal(obj.getDoubleValue("latitude")).setScale(6, RoundingMode.HALF_UP)
          val speed: BigDecimal = BigDecimal(obj.getDoubleValue("speed")).setScale(2, RoundingMode.HALF_UP)

          val angle: Int = obj.getIntValue("angle")
          val is_validate: Int = -99
          val company_code = "-99"
          val navigation_status: Int = dataType
          val source_company: String = "baidu"
          val city: Int = 440300
          val year: Int = dateTime.getYear
          val month: Int = dateTime.getMonthOfYear
          val day: Int = dateTime.getDayOfMonth

          val license_prefix: String = "-99"

          val result = STD_GPS_DRIVE(
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
          tmpArr.append(result)
        }

        val arr = tmpArr.sortBy(_.loc_time)

        val sampleTmp = new ArrayBuffer[Long]()

        var time = if (arr.nonEmpty) arr.head.loc_time else -1L

        for (i <- Range(0, arr.length)) {
          sampleTmp.append(time)
          time = time + 5
        }

        val res = arr
          .filter(x => sampleTmp.contains(x.loc_time))
          .filter(x => {
            x.speed < 900 && x.loc_time * 1000 <= DateTime.now().getMillis
          })

        res.toArray[STD_GPS_DRIVE]
      })

    val output = spark.createDataset[STD_GPS_DRIVE](result).filter(_.year >= 2019)

    //      output.cache()
    //      output.show(false)

    SparkHiveUtil.output[STD_GPS_DRIVE](spark, outTables.split(","), Array("source_company", "city", "year", "month", "day"), output, dupliCols = Seq("loc_time", "vehicle_id"))
  }
}


