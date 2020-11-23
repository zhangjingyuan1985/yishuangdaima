package com.sutpc.bigdata.jdbc

import org.apache.spark.sql.SparkSession

/**
  * <p>Description:TODO 字典维度表缓存 </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/7/23  20:53
  */
class DimSource(spark: SparkSession) {

  spark.conf.set("spark.sql.inMemoryColumnarStorage.batchSize", "10000")

//  lazy val cityDict = spark.read.jdbc(transpass_std_url, "public.t_common_dict_city", transpass_prop).cache()
  lazy val cityDict =  spark.table("transpaas_440300_std.dim_city_vhead").cache() //transpaas_440300_std.dim_city_vhead

//  lazy val gridCityCuDict = spark.read.jdbc(phone_url, "public.space_dim", phone_prop).cache()
//  lazy val gridCityCmDict = spark.read.jdbc(phone_url, "public.space_dim_cm", phone_prop).cache()

  lazy val gridCityCuDict = spark.table("transpaas_440300_std.s_common_space_grid_dim").cache()
  lazy val gridCityCmDict = spark.table("transpaas_440300_std.s_common_space_grid_dim").cache()

//  val timeDimDict = spark.read.jdbc(transpass_std_url, "public.tmp_city_time_dim", transpass_prop).cache().createOrReplaceTempView("tmp_city_time_dim")
  val timeDimDict =  spark.table("transpaas_440300_std.dim_phone_city_time").cache().createOrReplaceTempView("dim_phone_city_time")


//  val vehicleTypeDict = spark.read.jdbc(transpass_std_url, "public.s_common_dict_vehicletype", transpass_prop).cache()
  val vehicleTypeDict = spark.table("transpaas_440300_std.dim_vehicle_type").cache()

  //获取公交线路
  lazy val routeId2NameDict =spark.read.jdbc(DBUrls.bus_info_url, "route_bd", DBUrls.bus_info_prop).cache()

  lazy val vId2VnoDict =spark.read.jdbc(DBUrls.bus_info_url, "bus_bd", DBUrls.bus_info_prop).cache()


  def getVId2VnoMap() = {
    vId2VnoDict.select("PRODUCTID", "CARDID").collect().map(x => (x.get(0).toString, x.get(1).toString)).toMap
  }

  def getRouteId2NameMap() = {
    routeId2NameDict.select("ROUTEID", "ROUTENAME").collect().map(x => (x.get(0).toString, x.get(1).toString)).toMap
  }

  def getCityPinyinCodeMap() = {
    cityDict.select("city_pinyin", "adcode").collect().map(x => (x.get(0).toString, x.get(1).toString)).toMap
  }

  def getCityVheadMap() = {
    cityDict.select("city_pinyin", "vehicle_id_head").collect().map(x => (x.get(0).toString, x.get(1).toString)).toMap
  }

  def getCuGridCityMap() = {

    val map1 = cityDict.select("city", "adcode").collect().map(x => (x.get(0).toString, x.get(1).toString)).toMap

//    val map2 = gridCityCuDict.select("grid_id_500m", "city_name").collect().map(x => (x.get(0).toString, Option(x.get(1)).getOrElse("#").toString)).toMap
    val map2 = gridCityCuDict.select("grid_fid", "city_name").where("create_time = 20190905").collect().map(x => (x.get(0).toString, Option(x.get(1)).getOrElse("#").toString)).toMap

    map2.map(x => (x._1, map1.get(x._2).getOrElse("")))

  }

  def getCmGridCityMap() = {

    val map1 = cityDict.select("city", "adcode").collect().map(x => (x.get(0).toString, x.get(1).toString)).toMap

    val map2 = gridCityCmDict.select("grid_fid", "city_name").where("create_time = 20190906").collect().map(x => (x.get(0).toString, Option(x.get(1)).getOrElse("#").toString)).toMap

    map2.map(x => (x._1, map1.get(x._2).getOrElse("")))

  }

  def getVehicleTypeDict() = {

    vehicleTypeDict.select("fid", "fname").collect().map(x => (x.get(0).toString, x.get(1).toString)).toMap

  }


}
