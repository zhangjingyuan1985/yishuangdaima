package com.sutpc.flume.interceptor

import java.util.UUID

import scala.collection.JavaConverters
import scala.collection.mutable.ArrayBuffer

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author raomin
  * @version 1.0.0
  * @date date 2019/12/19 10:28
  */
class DisableRegion(spark: SparkSession, brokers: String, zkUrl: String) extends Serializable {
    def run(stream: DStream[GPSRecord],
            config: Array[DISABLE_REGION],
            moterValue: mutable.Map[String, String],
            driverValue: mutable.Map[String, String],
            companyValue: mutable.Map[String, String],
            vehtypeValue: mutable.Map[String, String],
            vehicleColorValue: mutable.Map[String, String],
            vehicleManageDepartmentValue: mutable.Map[String, String],
            companyNameValue: mutable.Map[String, String],
            companyScopeValue: mutable.Map[String, String],
            driverNameValue: mutable.Map[String, String],
            companyDataValue: mutable.Map[String, Timestamp],
            geoDataValue: mutable.Map[String, String]
           ): Unit = {
        //创建广播变量
        var duaration = 30;
        //创建累加器
        val acc = new accumlator
        spark.sparkContext.register(acc)

        //创建轨迹累加器
        val trace = new traceaccumlator(spark)
        spark.sparkContext.register(trace)
        //创建事故累加器
        val accident = new traceaccumlator(spark)
        spark.sparkContext.register(accident)


        var currentUnixTime = com.sutpc.bigdata.job.timeTransForm.timeTransform.time2UnixTime
        stream.foreachRDD(row => {
            currentUnixTime = com.sutpc.bigdata.job.timeTransForm.timeTransform.time2UnixTime
            //println(currentUnixTime)
        })
        var filterDstream = filterData(stream, currentUnixTime, config)

        val transformDstream = getVehicleGps(filterDstream, acc)

        getRes(transformDstream,
            currentUnixTime,
            acc, trace, accident,
            moterValue, driverValue, companyValue,
            vehtypeValue, vehicleColorValue, vehicleManageDepartmentValue,
            companyNameValue, companyScopeValue, driverNameValue,
            companyDataValue, geoDataValue
        )


    }

    def getRes(transformDstram: DStream[String],
               currentUnixTime: Long,
               acc: accumlator,
               trace: traceaccumlator,
               accident: traceaccumlator,
               moterValue: mutable.Map[String, String],
               driverValue: mutable.Map[String, String],
               companyValue: mutable.Map[String, String],
               vehtypeValue: mutable.Map[String, String],
               vehicleColorValue: mutable.Map[String, String],
               vehicleManageDepartmentValue: mutable.Map[String, String],
               companyNameValue: mutable.Map[String, String],
               companyScopeValue: mutable.Map[String, String],
               driverNameValue: mutable.Map[String, String],
               companyDataValue: mutable.Map[String, Timestamp],
               geoDataValue: mutable.Map[String, String]
              ): Unit = {
        transformDstram.foreachRDD(x => {
            println(acc.value.size + "累加器大小")
            val vehicleSeq = JavaConverters.asScalaIteratorConverter(acc.value.iterator).asScala.toSeq
            val vehRdd = spark.sparkContext.makeRDD(vehicleSeq)
            //得到车辆信息表
            val vehInfDataFrame = vehRdd.map(x => {
                val strs = x.toString.split(",")
                var tempList = List[String]()
                tempList = strs(4) +: tempList
                tempList = strs(3) +: tempList
                (strs(0), strs(1), strs(2).toLong, tempList, strs(5), strs(6))
            }).toDF("vehNo", "regionId", "localtime", "lngAndLat", "vehType", "speed").distinct()
            //过滤车辆类型

            //TODO 触发shuffle
            val timeFrame = vehInfDataFrame.groupBy("vehNo", "regionId").agg(("localtime", "min"), ("localtime", "max"), ("speed", "max")).toDF("vehNo", "regionId", "minTime", "maxTime", "maxSpeed")

            val sortvehInfDataFrame = vehInfDataFrame.orderBy("vehNo", "regionId", "localtime")

            //TODO 触发shuffle
            val traceFrame = sortvehInfDataFrame.groupBy("vehNo", "regionId").agg(collect_list("lngAndLat") as "lngAndLat")
            //得到当前数据流下的车辆轨迹和时间表
            //TODO 触发shuffle
            val traceAndTimeFrame = timeFrame.join(traceFrame, List("vehNo", "regionId"))

            //开始拼接轨迹
            val hisTraceValue = trace.value

            val unionFrame = unionTrace(traceAndTimeFrame, hisTraceValue)


            //待确认是否每批数据的时间都是大于前一段时间，如果不是此步骤存在问题
            val sortUnionFrame = unionFrame.orderBy("vehNo", "regionId", "maxTime")


            sortUnionFrame.cache()
            //TODO 触发shuffle
            val maxTimeValueFrame = sortUnionFrame.groupBy().max("maxTime")


            val size = maxTimeValueFrame.count()

            var maxValue = spark.sparkContext.broadcast(0.toLong)
            val value = maxTimeValueFrame.collect().array(0).get(0)
            if (value != null) {
                maxValue = spark.sparkContext.broadcast(maxTimeValueFrame.collect().array(0).getLong(0))
            }

            //TODO 触发shuffle
            val timeInfFrame = sortUnionFrame.groupBy("vehNo", "regionId")
              .agg(("minTime", "min"), ("maxTime", "max"), ("maxSpeed", "max")).toDF("vehNo", "regionId", "minTime", "maxTime", "maxSpeed")

            //TODO 触发shuffle
            val lngAndLatFrame = sortUnionFrame.groupBy("vehNo", "regionId").agg(collect_list("lngAndLat") as "lngAndLat")


            val delLngAndLatFrame = joinTrace(lngAndLatFrame)

            val delTimeFrame = timeInfFrame.rdd.map(row => {
                var diffTime = row.getLong(3) - row.getLong(2)
                var duaration = maxValue.value - row.getLong(3)
                (row.getString(0), row.getString(1), row.getLong(2), row.getLong(3), row.getString(4).toInt, diffTime, duaration, currentUnixTime)
            }).toDF("vehNo", "regionId", "minTime", "maxTime", "maxSpeed", "diffTime", "duarationTime", "currentTime")




            val filterTimeFrame = delTimeFrame.filter(row => {
                row.getLong(5).toInt > 30 & row.getLong(6).toInt > 60 //筛选停留时间和持续时间超过规定时间数据，用于输出结果
            })

            val filterCacheTimeFrame = delTimeFrame.filter(row => {
                //row.getLong(6).toInt <60 //筛选持续时间小于规定时间数据用于用于缓存数据
                if (row.getLong(5).toInt > 30 & row.getLong(6).toInt > 60) {
                    false
                } else {
                    true
                }
            })


            //得到结果预警信息
            //TODO 触发shuffle
            val cacheFrame = filterCacheTimeFrame.join(delLngAndLatFrame, Seq("vehNo", "regionId"))
            //TODO 触发shuffle
            val resFrame = filterTimeFrame.join(delLngAndLatFrame, Seq("vehNo", "regionId"))

            //写入数据库。。。。
            val accHis = accident.value
            accident.reset()

            val currentTime = com.sutpc.bigdata.job.timeTransForm.timeTransform.unixTime2DateFormat(currentUnixTime.toString)
            if (accHis.count() == 0) {
                var outputFrame = resFrame.rdd.map(row => {
                    var vehNo = row.getString(0)
                    var regionID = row.getString(1)
                    var minTime = row.getLong(2).toString
                    var waringTime = com.sutpc.bigdata.job.timeTransForm.timeTransform.unixTime2DateFormat(minTime)

                    var lngAndLatList = row.getList(8)
                    var lngAndLatArr = lngAndLatList.toArray()

                    var lng = lngAndLatArr(0).toString.split(",")(0).split("\\(")(1).toDouble
                    var lat = lngAndLatArr(0).toString.split(",")(1).split("\\)")(0).toDouble

                    var tr = ""
                    lngAndLatArr.foreach(row => {
                        val strs = row.toString.split(",")
                        tr = tr + strs(0).split("\\(")(1) + "," + strs(1).split("\\)")(0) + "|"
                    })
                    var track = tr.dropRight(1)
                    var sig = moterValue.contains(vehNo)
                    var motor = null.asInstanceOf[String]
                    var vehtypeid = null.asInstanceOf[String]
                    var dirver = null.asInstanceOf[String]
                    var company = null.asInstanceOf[String]

                    var companyName = null.asInstanceOf[String]
                    var driverName = null.asInstanceOf[String]
                    var busscope = null.asInstanceOf[String]
                    var lincesePeriod = null.asInstanceOf[Timestamp]
                    var plateColor = null.asInstanceOf[String]
                    var manageDepartment = null.asInstanceOf[String]
                    var templincesePeriod: Date = null.asInstanceOf[Date]

                    if (sig == true) {
                        motor = moterValue.get(vehNo).getOrElse(null.asInstanceOf[String])
                        vehtypeid = vehtypeValue.get(vehNo).getOrElse(null.asInstanceOf[String])
                        company = companyValue.get(vehNo).getOrElse(null.asInstanceOf[String])
                        dirver = driverValue.get(company).getOrElse(null.asInstanceOf[String])


                        busscope = companyScopeValue.get(company).getOrElse(null.asInstanceOf[String])
                        companyName = companyNameValue.get(company).getOrElse(null.asInstanceOf[String])
                        driverName = driverNameValue.get(dirver).getOrElse(null.asInstanceOf[String])

                        lincesePeriod = companyDataValue.get(company).getOrElse(null.asInstanceOf[Timestamp])

                        plateColor = vehicleColorValue.get(vehNo).getOrElse(null.asInstanceOf[String])
                        manageDepartment = vehicleManageDepartmentValue.get(vehNo).getOrElse(null.asInstanceOf[String])
                    }
                    val detectionContent = geoDataValue.get(regionID).getOrElse(null.asInstanceOf[String])
                    var uuid = UUID.randomUUID().toString
                    var date = com.sutpc.bigdata.job.timeTransForm.timeTransform.unixTime2Date(currentUnixTime.toString)
                    var hh = com.sutpc.bigdata.job.timeTransForm.timeTransform.unixTime2Hh(currentUnixTime.toString)
                    tblDynamicWarning(regionID, uuid, "WARN_TYPE#DISABLE_REGION", waringTime, vehNo,
                        lng, lat, vehtypeid, "LOWEST", track, company, companyName,
                        manageDepartment, currentUnixTime, currentTime, dirver,
                        driverName, row.getLong(6).toInt, date, hh, motor,
                        busscope, row.getInt(4).toDouble, lincesePeriod, plateColor, detectionContent,false)
                }).toDF("addition", "id", "type", "warningtime", "vehicleno",
                    "longitude", "latitude", "vehicletype", "severity", "track", "companyid",
                    "companyName", "manageDepartment", "timestamp", "createtime",
                    "driverId", "driverName", "duration", "date", "hour",
                    "motorId", "businessScope", "speed", "licensePeriod", "plateColor", "detectionContent","isDispose")
                val filterRes = outputFrame.filter(row => {
                    //com.sutpc.bigdata.job.identifyConfig.identifyConfig.identifyDisableRegionConfig(row)
                    true
                })
                var res = filterRes.distinct()

                res.show(20)
                //com.sutpc.bigdata.job.dbutil.mysqlWrite.writeMysql(res, "tbl_dynamic_warning")
                //var df = res.as[tblDynamicWarning]
                //new SparkKafkaUtil("10.10.190.27:9092,10.10.190.28:9092,10.10.190.29:9092", "T_SUTPC_SV_TOPIC_WARN").output[String](spark, df.drop($"track").toJSON)
                sortUnionFrame.unpersist()
            }
            //更新缓存的轨迹信息

            val filterCacheTime = cacheFrame.select("vehNo", "regionId", "minTime", "maxTime", "maxSpeed", "lngAndLat")
            trace.reset()
            println("轨迹大小" + trace.value.count())
            trace.add(filterCacheTime)
            trace.value.show()
            acc.reset()
            println("事故数据大小" + acc.value.size())
        })
    }

    def getVehicleGps(filterDstream: DStream[String], acc: accumlator): DStream[String] = {
        val treansformDstream = filterDstream.map(x => {
            var regionId = x.toString.split("\\*")(1)
            var strs = x.toString.split("\\*")(0).split(",")
            var localtime = strs(1)
            var vehNo = strs(2)
            var vehType = strs(12).split("\\)")(0).split("#")(1)
            var lng = strs(4)
            var lat = strs(5)
            var speed = strs(6)
            var str = vehNo + "," + regionId + "," + localtime + "," + lng + "," + lat + "," + vehType + "," + speed
            str
        })
        treansformDstream.foreachRDD(x => {
            x.foreach(row => {
                acc.add(row)
            })
        })
        treansformDstream
    }

    def filterData(stream: DStream[GPSRecord], unixTime: Long, config: Array[DISABLE_REGION]): DStream[String] = {
        val filterUnixTimeDstream = stream.filter(x => {
            val localtime = x.toString.split(",")(1).toLong
            localtime < unixTime
        })

        //    filterUnixTimeDstream.filter(row=>{
        //      var vehtypeConfig = config(0).vehicleType.split("#")(1)
        //      val strings = row.toString.split("\\*")(0).split(",")
        //      var vehicleType = strings(12).split("\\)")(0).split("#")(1)
        //      vehtypeConfig.contains(vehicleType)
        //    })

        val filterGeometryData = filterUnixTimeDstream.map(x => {
            var strs = x.toString.split(",")
            var lng = strs(4).toDouble
            var lat = strs(5).toDouble
            var sig = estimateRegions(lng, lat, config)
            if (sig == "null") {
                sig
            } else {
                x.toString + "*" + sig
            }
        }).filter(_ != "null")
        filterGeometryData
    }

    def estimateRegions(lng: Double, lat: Double, config: Array[DISABLE_REGION]): String = {
        val geometryFactory = new GeometryFactory()
        val coord = new Coordinate(lng, lat)
        var point = geometryFactory.createPoint(coord)
        val disableConfig = config
        var sig = "null"
        disableConfig.foreach(row => {
            val regions = row.regionsData
            if (regions.intersection(point).toString != "POINT EMPTY") {
                sig = row.regionId
            }
        })
        sig
    }

    def unionTrace(currentFrame: DataFrame, hisFrame: DataFrame): DataFrame = {
        if (hisFrame.count() == 0) {
            currentFrame
        } else {
            currentFrame.union(hisFrame)
        }
    }

    def joinTrace(lngAndLatFrame: DataFrame): DataFrame = {
        lngAndLatFrame.rdd.map(row => {

            var totalList = List[List[String]]()
            val list = row.get(2).asInstanceOf[mutable.WrappedArray[mutable.WrappedArray[List[String]]]].toArray
            list.foreach(x => {
                val ls = x.toList
                totalList = totalList ++: ls
            })

            val javaList = totalList.asJava.toArray


            //var array = new ArrayBuffer[String]()
            var array = new ArrayBuffer[List[String]]()
            val it = javaList.iterator

            while (it.hasNext) {
                //array = it.next().toString +:array
                //x新增
                var templist = List[String]()
                val strs = it.next().toString.split(",")
                var lng = strs(0).split("\\(")(1)
                var lat = strs(1).split("\\)")(0)
                templist = lat +: templist
                templist = lng +: templist
                array = templist +: array
            }

            (row.get(0).toString, row.get(1).toString, array)
        }).toDF("vehNo", "regionId", "lngAndLat")
    }
}

