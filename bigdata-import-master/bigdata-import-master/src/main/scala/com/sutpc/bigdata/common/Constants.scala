package com.sutpc.bigdata.common


/**
  * <p>Title: Constants</p>
  * <p>Description:TODO 常量类  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/7/15  23:13
  */
object Constants {

  //视频AI ProtoBuf 类型
  val VIDEO_AI_TYPE = Map(
    "primary" -> 1,             //视频ai识别信息主表
    "flowRider" -> 2,           //骑行流量识别事件子表
    "flowPedestrian" -> 3,      //行人流量识别事件子表
    "countBicycle" -> 4,        //自行车数量识别事件子表
    "flowVehicle" -> 5,         //交通车辆流量识别事件子表
    "flowBusStop" -> 6,         //公交站台人流量识别事件子表
    "flowBusVehicle" -> 7,      //公交站台车辆上下车流量识别事件子表
    "flowMetro" -> 8,           //地铁出入口人流量识别事件子表
    "accident" -> 9,            //交通事件识别子表
    "accidentLane" -> 10,       //交通事件识别影响路段子表
    "pedestrianFeature" -> 11,  //行人特征识别子表
    "vehicleFeature" -> 12,     //车辆特征识别子表
    "roadBroken" -> 13,         //道路病害识别子表
    "pedestrianTrack" -> 14,    //行人轨迹识别子表
    "vehicleTrack" -> 15        //车辆轨迹识别子表
  )

  val MSIGNAL_AGG_TYPE = Map(
    "Normal" -> 1,
    "TrainStation" -> 2,
    "InterCity" -> 3,
    "Port" -> 4,
    "ScenicRegion" -> 5
  )

  val MSIGNAL_TRIP_MODE = Map(
    "W->H" -> 1,
    "H->W" -> 2,
    "W->O" -> 3,
    "O->W" -> 3,
    "H->O" -> 3,
    "O->H" -> 3,
    "O->O" -> 3
  )
  val MSIGNAL_TRIP_MODE2 = Map(
    "w2h" -> 1,
    "h2w" -> 2,
    "oth" -> 3,
    "oth" -> 3,
    "oth" -> 3,
    "oth" -> 3,
    "oth" -> 3
  )

  val IC_TRANS_TYPE_MAP = Map(
    "地铁入站" -> 1,
    "地铁出站" -> 2,
    "巴士" -> 3,
    "二维码巴士" -> 4
  )

  val CITY_VNO_PREFFIX_MAP = Map(
    "shenzhen" -> "粤B"
  )

  val CITY_CODE_MAP = Map {
    "shenzhen" -> 440300
  }

  //1-resident,2-tourist
  val MSIGNAL_USER_TYPE_MAP = Map(
    "RESIDENT" -> 1,
    "R" -> 1,
    "TOURIST" -> 2,
    "T" -> 2
  )

  val VEHICLE_TYPE = Map(
    "导航小汽车" -> 0,
    "班车客运" -> 1,
    "出租车" -> 2,
    "货运车" -> 4,
    "驾培车" -> 8,
    "包车客运" -> 16,
    "公交车" -> 32,
    "危险品车" -> 64,
    "其他车辆" -> 128,
    "泥头车" -> 256,
    "网约车" -> 512

  )


  val VEHICLE_TYPE2 = Map(
    "nav" -> 0,
    "coach" -> 1,
    "taxi" -> 2,
    "truck" -> 4,
    "driving" -> 8,
    "charter" -> 16,
    "bus" -> 32,
    "danger" -> 64,
    "others" -> 128,
    "dumper" -> 256,
    "order" -> 512

  )


  val BIKE_LOCK_STATUS_MAP = Map(
    "开锁" -> 1,
    "关锁" -> 2
  )


  //车辆类型 1-Mobike, 2-Mobike Lite, 999-红包车
  val BIKE_LOC_TYPE_MAP = Map(
    1 -> "Mobike",
    2 -> "Mobike Lite",
    999 -> "红包车"
  )

  //  vehicle_type
  //  1小汽车
  //    2,面包车
  //  3,货车
  //  4,客车
  //  5,SUV
  //  6,大货车
  //  7,大客车
  //  8,未知


  //  plate_type
  //  0,未知车牌
  //  1,蓝牌小汽车
  //  2,黑牌小汽车
  //  3,单排黄牌
  //  4,双排黄牌
  //  5,警车车牌
  //  6,武警车牌
  //  7,个性化车牌
  //  8,单排军车牌
  //  9,双排军车牌
  //  10,使馆车牌
  //  11,香港车牌
  //  12,农用车牌
  //  13,澳门牌
  //  14,厂内牌
  //  15,大汽车
  //  16,领馆汽车
  //  17,境外汽车
  //  18,外籍汽车
  //  19,普通摩托车
  //  20,轻便摩托车
  //  21,使馆摩托车
  //  22,领馆摩托车
  //  23,境外摩托车
  //  24,外籍摩托车
  //  25,低速车
  //  26,拖拉机
  //
  //  plate_color
  //  0未知 1蓝色 2黄色 3白色 4黑色 5绿色 6红色 7灰色 8紫色 9黄绿色

}
