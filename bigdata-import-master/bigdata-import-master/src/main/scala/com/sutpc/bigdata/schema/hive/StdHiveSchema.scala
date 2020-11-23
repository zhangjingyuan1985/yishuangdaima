package com.sutpc.bigdata.schema.hive

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/7/31  14:29
  */

/**
  *
  * @param loc_time          定位日期
  * @param vehicle_id        车牌
  * @param lng               经度
  * @param lat               纬度
  * @param speed             瞬时速度（km/h）
  * @param angle             方向角
  * @param is_validate       数据是否有效
  * @param company_code      公司代码编码
  * @param busline_name      公交线路名称
  * @param bus_position      报站状态
  * @param busline_dir       方向
  * @param bus_station_order 站序
  * @param alarm_status      报警状态
  * @param vehicle_status    车辆状态
  * @param city              城市编码
  * @param year              年份
  * @param month             月份
  * @param day               日份
  */
case class STD_GPS_BUS(loc_time: Long, vehicle_id: String, lng: BigDecimal, lat: BigDecimal, speed: BigDecimal, angle: Int, is_validate: Int, company_code: String, busline_name: String, bus_position: Integer, busline_dir: Integer, bus_station_order: Integer, alarm_status: String, vehicle_status: String, city: Int, year: Int, month: Int, day: Int)


case class STD_GPS_TAXI(
                         loc_time: Long,
                         vehicle_id: String,
                         lng: BigDecimal,
                         lat: BigDecimal,
                         speed: BigDecimal,
                         angle: Int,
                         is_validate: Int,
                         operation_status: Int,
                         company_code: String,
                         altitude: Int,
                         vehicle_color: Integer,
                         total_mileage: Integer,
                         alarm_status: String,
                         vehicle_status: String,
                         city: Int,
                         year: Int,
                         month: Int,
                         day: Int
                       )

case class STD_GPS_DRIVE(
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


/**
  *
  * @param loc_time 定位日期
  * @param vehicle_id 车牌
  * @param lng 经度
  * @param lat
  * @param speed
  * @param angle
  * @param operation_status
  * @param position_type
  * @param order_id
  * @param altitude
  * @param company_code
  * @param city
  * @param year
  * @param month
  * @param day
  */
case class STD_GPS_ORDER(loc_time: Long, vehicle_id: String, lng: BigDecimal, lat: BigDecimal, speed: BigDecimal, angle: Int, operation_status: Int, position_type: String,  order_id: String, altitude: Int, company_code: String, city: Int, year: String, month: String, day: String)

case class STD_GPS_OTHER(
                          loc_time: Long,
                          vehicle_id: String,
                          lng: BigDecimal,
                          lat: BigDecimal,
                          speed: BigDecimal,
                          angle: Int,
                          is_validate: Int,
                          vehicle_color: Int,
                          total_mileage: Long,
                          altitude: Int,
                          company_code: String,
                          alarm_status: String,
                          vehicle_status: String,
                          vehicle_type: Int,
                          city: Int,
                          year: String,
                          month: String,
                          day: String,
                          license_prefix: String
                        )

case class STD_IC(
                   card_id: String,
                   trans_type: Int,
                   trans_time: Long,
                   trans_value: Int,
                   dev_no: String,
                   company_line: String,
                   line_station: String,
                   vehicle_id: String,
                   city: Int,
                   year: Int,
                   month: Int,
                   day: Int
                 )


case class STD_BIKE_SWITCH(
                            bike_id: String,
                            loc_time: Long,
                            ftime: String,
                            lng: BigDecimal,
                            lat: BigDecimal,
                            order_no: Int,
                            lock_status: Int,
                            city: Int,
                            year: String,
                            month: String,
                            day: String
                          )


case class STD_GPS_TRUCK(
                          loc_time: Long,
                          vehicle_id: String,
                          lng: BigDecimal,
                          lat: BigDecimal,
                          speed: BigDecimal,
                          angle: Int,
                          company_code: String,
                          altitude: Int,
                          vehicle_color: Integer,
                          total_mileage: Long,
                          is_validate: Int,
                          alarm_status: String,
                          vehicle_status: String,
                          city: Int,
                          year: String,
                          month: String,
                          day: String
                        )


case class STD_BIKE_LOC(
                         bike_id: String,
                         operator: Int,
                         bike_type: Int,
                         loc_time: Long,
                         lng: BigDecimal,
                         lat: BigDecimal,
                         city: Int,
                         year: String,
                         month: String,
                         day: String
                       )

case class S_ROAD_PLATE_RECOGNITION(
                                     id: String, // ""
                                     vehicle_fid: String, // ""
                                     encry_id: String, // ""
                                     recognition_time: Long, // "10为UNIX时间戳"
                                     upload_time: Long, // "10为UNIX时间戳"
                                     detector_id: String, // "不足8为左边补0"
                                     lane_fid: String, // "待补充"
                                     speed: BigDecimal, // "单位km/h"
                                     vehicle_type: Int, // ""
                                     plate_type: Int, // "待补充"
                                     illegal_type: Int, // "待补充"
                                     degree_of_confidence: Int, // "0-未知，1-可信任，2-不可信任"
                                     plate_color: Int, // "待补充"
                                     drive_dir: Int, // "待补充0-其他；1-由东向西；2-由西向东；3-由南向北；4-由北向南；5-由东南向西北；6-由西北向东南；7-由东北向西南；8-由西南向东北"
                                     crossing_type: Int, // "1-卡口；2-电警；3-RFID卡口；4-互联停车场；5-分局卡口；6-加油站；7-违停球；9-移动卡口"
                                     capture_address: String, // ""
                                     city: Int, // "参照城市编码对照表"
                                     year: Int, // "示例2019"
                                     month: Int, // "示例8"
                                     day: Int // "示例2"
                                   )


//case class S_ROAD_PLATE_RECOGNITION(
//                                     id: String, // ""
//                                     vehicle_fid: String, // ""
//                                     recognition_time: Long, // "10为UNIX时间戳"
//                                     upload_time: Int, // "10为UNIX时间戳"
//                                     detector_id: String, // "不足8为左边补0"
//                                     lane_fid: String, // "待补充"
//                                     speed: Int, // "单位km/h"
//                                     vehicle_type: Int, // ""
//                                     plate_type: Int, // "待补充"
//                                     illegal_type: Int, // "待补充"
//                                     degree_of_confidence: Int, // "0-未知，1-可信任，2-不可信任"
//                                     plate_color: Int, // "待补充"
//                                     drive_dir: Int, // "待补充0-其他；1-由东向西；2-由西向东；3-由南向北；4-由北向南；5-由东南向西北；6-由西北向东南；7-由东北向西南；8-由西南向东北"
//                                     crossing_type: Int, // "1-卡口；2-电警；3-RFID卡口；4-互联停车场；5-分局卡口；6-加油站；7-违停球；9-移动卡口"
//                                     capture_address: String, // ""
//                                     city: Int, // "参照城市编码对照表"
//                                     year: Int, // "示例2019"
//                                     month: Int, // "示例8"
//                                     day: Int // "示例2"
//                                   )


case class S_PHONE_ACTIVITY(
                             grid_id: String,
                             user_type: Int,
                             act_time: Int,
                             time_dim: Int,
                             fdate: Int,
                             age: Int,
                             sex: Int,
                             user_qty: Int,
                             adm_reg: Int,
                             create_time: Int,
                             city: Int,
                             source: String,
                             creator: String,
                             year: String,
                             month: String,
                             day: String
                           )


case class S_PHONE_INTER_RESIDENT(
                                   grid_id: String,
                                   age: Integer,
                                   sex: Integer,
                                   adm_reg: Integer,
                                   has_auto: Integer,
                                   user_qty: Integer,
                                   fdate: Int,
                                   create_time: Int,
                                   creator: String,
                                   city: Int,
                                   source: String
                                 )

case class S_PHONE_INTER_TOURIST(

                                  grid_id: String,
                                  age: Integer,
                                  sex: Integer,
                                  adm_reg: Integer,
                                  stay_day: Integer,
                                  user_qty: Integer,
                                  fdate: Int,
                                  create_time: Int,
                                  creator: String,
                                  city: Int,
                                  source: String
                                )

//grid_id  STRING COMMENT '居住地栅格ID',
//age  int COMMENT '年龄',
//sex  int COMMENT '性别',
//adm_reg  int COMMENT '用户归属地（参照城市编码对照表）',
//has_auto  int COMMENT '有无小汽车 1-有；0-没有；999-0未知',
//work_qty  int COMMENT '人口数',
//fdate  int COMMENT '日期 示例20190800表示2019年8月份的数据',
//create_time  int COMMENT '数据入库时间 示例20190808',
//creator STRING COMMENT '入库人员|入库人员姓名拼音（liangjiaxian）'
case class S_PHONE_INTER_WORK(

                               grid_fid: String,
                               age: Integer,
                               sex: Integer,
                               adm_reg: Integer,
                               has_auto: Integer,
                               work_qty: Integer,
                               fdate: Int,
                               create_time: Int,
                               creator: String,
                               city: Int,
                               source: String
                             )

case class S_PHONE_INTER_HOME_WORK(
                                    home_grid_fid: String,
                                    work_grid_fid: String,
                                    age: Integer,
                                    sex: Integer,
                                    adm_reg: Integer,
                                    has_auto: Integer,
                                    user_qty: Integer,
                                    fdate: Int,
                                    create_time: Int,
                                    home_city: Int,
                                    work_city: Int,
                                    source: String,
                                    creator: String

                                  )

case class S_PHONE_TRIP_DIST(
                              start_grid_fid: String,
                              end_grid_fid: String,
                              start_city: Int,
                              end_city: Int,
                              start_ptype: Int,
                              end_ptype: Int,
                              start_utype: Int,
                              end_utype: Int,
                              age: Integer,
                              sex: Integer,
                              adm_reg: Integer,
                              start_time: Int,
                              end_time: Int,
                              trip_mode: Int,
                              aggtype: Int,
                              trip_qty: Int,
                              create_time: Int,
                              source: String,
                              time_dim: Int,
                              year: String,
                              month: String,
                              day: String,
                              creator: String
                            )

/**
  * 视频AI-行人特征识别s_vai_pedestrian_feature
  * @param uuid              唯一编号
  * @param event_fid         主事件全局唯一id
  * @param speed             速度
  * @param start_time        开始时间
  * @param end_time          结束时间
  * @param violation         非机动车违章
  * @param nv_violation_id   非机动车违章id
  * @param score             特征得分
  * @param bayonet_direction 卡口方向
  * @param drive_direction   行驶方向
  * @param rect_top          识别位置-上
  * @param rect_left         识别位置坐标-左
  * @param rect_bottom       识别位置坐标-底部
  * @param rect_right        识别位置坐标-右
  * @param sex               性别
  * @param upbody_color      上半身颜色
  * @param upbody_clothes    上半身衣服类型
  * @param upbody_score      可信度
  * @param lobody_lobody     下半身颜色
  * @param lobody_clothes    下半身衣服类型
  * @param lobody_score      可信度
  * @param cap               是否戴帽子
  * @param cap_score         可信度
  * @param stature           身高类型
  * @param stature_score     可信度
  * @param bag               背包类型
  * @param bag_score         可信度
  * @param hair              头发类型
  * @param hair_score        可信度
  * @param age_stage         年龄类型
  * @param age_stage_score   可信度
  * @param is_bike           是否骑行
  * @param bike_score        可信度
  * @param company           运营商所属编码
  * @param city              城市编码
  * @param year 年
  * @param month 月
  * @param day 日
  */
case class s_vai_pedestrian_feature(uuid: String, event_fid: String, speed: Double, start_time: Long,
                                    end_time: Long, violation: Int, nv_violation_id: Int, score: Double,
                                    bayonet_direction: Int, drive_direction: Int,
                                    rect_top: Int, rect_left: Int, rect_bottom: Int, rect_right: Int, sex: Int,
                                    upbody_color: String, upbody_clothes: String, upbody_score: Int,
                                    lobody_lobody: String, lobody_clothes: String, lobody_score: Int,
                                    cap: Int, cap_score: Int, stature: Int, stature_score: Int, bag: Int,
                                    bag_score: Int, hair: Int, hair_score: Int, age_stage: Int, age_stage_score: Int,
                                    is_bike: Int, bike_score: Int, company: Int,
                                    city: Int, year: String, month: String, day: String)

/**
  * 视频AI-车辆特征识别s_vai_vehicle_feature
  * @param uuid              唯一编号
  * @param event_fid         主事件全局唯一id
  * @param violation         机动车违章
  * @param v_violation_id    机动车违章id
  * @param score             事件置信度
  * @param bayonet_direction 卡口方向
  * @param drive_direction   行驶方向
  * @param vehicle_type      车辆类型
  * @param lane              车道
  * @param speed             速度
  * @param vehicle_id        车牌号
  * @param license_color     车牌颜色
  * @param vehicle_color     车身颜色
  * @param city              城市编码
  * @param year 年
  * @param month 月
  * @param day 日
  */
case class s_vai_vehicle_feature(uuid: String, event_fid: String, violation: Int, v_violation_id: Int,
                                 score: String, bayonet_direction: Int, drive_direction: Int, vehicle_type: Int,
                                 lane: String, speed: Double, vehicle_id: String, license_color: String,
                                 vehicle_color: String, city: Int, year: String, month: String, day: String)

/**
  * 视频AI - 识别信息主表s_vai_primary
  * @param uuid        记录唯一编号
  * @param event_refid 事件全局唯一id
  * @param event_cate  事件大类
  * @param event_type  事件小类
  * @param server_fid  分析主机的id
  * @param event_date  事件上传时间
  * @param camera_fid  关联的摄像头id
  * @param pole_fid    杆件id
  * @param vai_source  视频算法供应商编码
  * @param version     协议编码
  * @param city        城市编码
  * @param year        年
  * @param month       月
  * @param day         日
  */
case class s_vai_primary(
                          uuid: String, event_refid: String, event_cate: String, event_type: String, server_fid: String,
                          event_date: String, camera_fid: Int, pole_fid: String, vai_source: Int, version: String,
                          city: Int, year: String, month: String, day: String)

/**
  * 视频AI 骑行流量识别事件子表s_vai_flow_rider
  *
  * @param uuid              唯一编号
  * @param event_fid         主事件唯一id
  * @param start_time        流量统计开始时间
  * @param end_time          流量统计结束时间
  * @param bayonet_direction 卡口方向
  * @param flow_direction    视频内车辆行驶方向
  * @param sample_dura       采样周期
  * @param area_num          区域流量
  * @param speed             目标平均速度
  * @param speed_unit        速度单位
  * @param out_num           流出量
  * @param in_num            流入量
  * @param wait_num          等候数量
  * @param city              城市编码
  * @param year              年
  * @param month             月
  * @param day               日
  */
case class s_vai_flow_rider(
                             uuid: String, event_fid: String, start_time: Int, end_time: Int, bayonet_direction: Int,
                             flow_direction: Int, sample_dura: Int, area_num: Int, speed: Double, speed_unit: String,
                             out_num: Int, in_num: Int, wait_num: Int, city: Int, year: String, month: String, day: String)

/**
  * 行人流量识别事件子表s_vai_flow_pedestrian
  *
  * @param uuid              唯一编号
  * @param event_fid         主事件全局唯一id
  * @param start_time        流量统计开始时间
  * @param end_time          流量统计结束时间
  * @param bayonet_direction 卡口方向
  * @param flow_direction    视频内车辆行驶方向
  * @param sample_dura       采样周期
  * @param area_num          区域流量
  * @param speed             目标平均速度
  * @param speed_unit        速度单位
  * @param out_num           流出量
  * @param in_num            流入量
  * @param wait_num          等候数量
  * @param city 城市
  * @param year 年
  * @param month 月
  * @param day 日
  */
case class s_vai_flow_pedestrian(
                                  uuid: String, event_fid: String, start_time: Int, end_time: Int, bayonet_direction: Int,
                                  flow_direction: Int, sample_dura: Int, area_num: Int, speed: Double, speed_unit: String,
                                  out_num: Int,in_num: Int, wait_num: Int,city: Int, year: String, month: String, day: String)

/**
  * 自行车量识别事件子表s_vai_flow_bicycle
  * @param uuid        唯一编号
  * @param event_fid   主事件全局唯一id
  * @param start_time  流量统计开始时间
  * @param end_time    流量统计结束时间
  * @param sample_dura 采样周期
  * @param company     运营商编码
  * @param area_num    区域停放该品牌车辆数
  * @param density     密度
  * @param city 城市
  * @param year 年
  * @param month 月
  * @param day 日
  */
case class s_vai_flow_bicycle(uuid: String, event_fid: String, start_time: Int, end_time: Int, sample_dura: Int, company: Int,
                               area_num: Int, density: Double,city: Int, year: String, month: String, day: String)

/**
  * 交通车辆流量识别事件子表s_vai_flow_vehicle
  * @param uuid 唯一编号
  * @param event_fid 主事件全局唯一id
  * @param start_time 流量统计开始时间
  * @param end_time 流量统计结束时间
  * @param bayonet_direction 卡口方向
  * @param flow_direction 视频内车辆行驶方向
  * @param sample_dura 采样周期
  * @param area_num 区域流量
  * @param speed 目标平均速度
  * @param speed_unit 速度单位
  * @param out_num 流出量
  * @param in_num 流入量
  * @param wait_num 等候数量
  * @param queue_num 排队数量
  * @param lane 车道编号
  * @param delta_time 车头时距
  * @param occupancy 占有率
  * @param vehicle_type 车辆类型
  * @param city 城市
  * @param year 年
  * @param month 月
  * @param day 日
  */
case class s_vai_flow_vehicle(uuid: String, event_fid: String, start_time: Int, end_time: Int, bayonet_direction: Int,
                               flow_direction: Int, sample_dura: Int, area_num: Int, speed: Double, speed_unit: String,
                               out_num: Int, in_num: Int, wait_num: Int, queue_num: Int, lane: Int, delta_time: Double,
                               occupancy: Double, vehicle_type: Int,city: Int, year: String, month: String, day: String)

/**
  * 公交站台人流量识别事件子表s_vai_flow_bus_stop
  *
  * @param uuid 唯一编号
  * @param event_fid 主事件全局唯一id
  * @param start_time 流量统计开始时间
  * @param end_time 流量统计结束时间
  * @param sample_dura 采样周期
  * @param area_num 区域流量
  * @param density 区域等候人数密度
  * @param speed 目标平均速度
  * @param speed_unit 速度单位
  * @param out_num 流出量
  * @param in_num 流入量
  * @param wait_num 等候数量
  * @param city 城市
  * @param year 年
  * @param month 月
  * @param day 日
  */
case class s_vai_flow_bus_stop(uuid: String, event_fid: String, start_time: Int, end_time: Int, sample_dura: Int,
                                area_num: Int, density: Double, speed: Double, speed_unit: String, out_num: Int,
                                in_num: Int, wait_num: Int,city: Int, year: String, month: String, day: String)

/**
  * 公交站台车辆上下车流量识别事件子表s_vai_flow_bus_vehicle
  *
  * @param uuid 唯一编号
  * @param event_fid 主事件全局唯一id
  * @param start_time 流量统计开始时间
  * @param end_time 流量统计结束时间
  * @param sample_dura 采样周期
  * @param vehicle_id 车辆车牌
  * @param up_num 上车人数
  * @param down_num 下车人数
  * @param city 城市
  * @param year 年
  * @param month 月
  * @param day 日
  */
case class s_vai_flow_bus_vehicle(uuid: String, event_fid: String, start_time: Int, end_time: Int, sample_dura: Int,
                                   vehicle_id: String, up_num: Int, down_num: Int,city: Int, year: String, month: String, day: String)

/**
  * 地铁出入口人流量识别事件子表s_vai_flow_metro
  * @param uuid 唯一编号
  * @param event_fid 主事件全局唯一id
  * @param start_time 流量统计开始时间
  * @param end_time 流量统计结束时间
  * @param sample_dura 采样周期
  * @param speed 目标平均速度
  * @param speed_unit 速度单位
  * @param out_num 流出量
  * @param in_num 流入量
  * @param wait_num 等候数量
  * @param density 行人密度
  * @param city 城市
  * @param year 年
  * @param month 月
  * @param day 日
  */
case class s_vai_flow_metro(uuid: String, event_fid: String, start_time: Int, end_time: Int, sample_dura: Int,
                            speed: Double, speed_unit: String, out_num: Int, in_num: Int, wait_num: Int, density: Double,
                            city: Int, year: String, month: String, day: String)

/**
  * 交通事件识别子表s_vai_accident
  * @param uuid 唯一编号
  * @param event_fid 主事件全局唯一id
  * @param accident_type 事件类型
  * @param accident_id 机动车异常事件id
  * @param score 事件置信度
  * @param start_time 统计开始时间
  * @param end_time 统计结束时间
  * @param bayonet_direction 卡口方向
  * @param affect_lane_num 受影响车道数
  * @param city 城市
  * @param year 年
  * @param month 月
  * @param day 日
  */
case class s_vai_accident(
                           uuid: String, event_fid: String, accident_type: Int, accident_id: String, score: Double,
                           start_time: Int, end_time: Int, bayonet_direction: Int, affect_lane_num: Int,
                           city: Int, year: String, month: String, day: String)

/**
  * s_vai_accident_lane
  *
  * @param uuid 唯一编号
  * @param accident_fid 事件主表唯一id
  * @param lane_fid 车道编号
  * @param affect_vehicle_num 受影响的车辆数
  * @param speed 受影响车道内车辆平均速度
  * @param queue_length 车辆排队长度
  * @param visual_range 可视范围
  * @param city 城市
  * @param year 年
  * @param month 月
  * @param day 日
  */
case class s_vai_accident_lane(
                                uuid: String, accident_fid: String, lane_fid: Int, affect_vehicle_num: Int,
                                speed: Double, queue_length: Int, visual_range: Int,
                                city: Int, year: String, month: String, day: String)

/**
  * 地磁
  * @param uuid         记录唯一编号
  * @param detector_fid 检测器ID
  * @param year         年
  * @param month        月
  * @param day          日
  * @param hh           小时
  * @param mm           分钟
  * @param lane_fid     车道id
  * @param vehicle_size 车辆型号
  * @param headway      车头时距
  * @param speed        平均速度
  * @param occupancy    平均占有时间
  * @param interval     平均间隔时间
  * @param length       平均车长
  * @param record_time  流水时间
  * @param city         城市编码
  */
case class s_road_geomagnetic_detection(
                                         uuid: String,
                                         detector_fid: String, year: Int, month: Int, day: Int, hh: Int, mm: Int,
                                         lane_fid: String, vehicle_size: Int, headway: Int, speed: Double,
                                         occupancy: Int, interval: Int, length: Double, record_time: Long, city: Int
                                       )

// .toDF("vehicle_id","loc_time","lng","lat","speed","angle","operation_status","is_validate","company_code","altitude","vehicle_color","total_mileage","alarm_status","vehicle_status","city","year","month","day")
case class s_vehicle_gps_taxi(vehicle_id: String, loc_time: Long, lng: Double, lat: Double, speed: Double, angle: Int, operation_status: Int, is_validate: Int, company_code: String, altitude: Int, vehicle_color: Int, total_mileage: Int, alarm_status: String, vehicle_status: String, city: Int, year: Int, month: Int, day: Int)