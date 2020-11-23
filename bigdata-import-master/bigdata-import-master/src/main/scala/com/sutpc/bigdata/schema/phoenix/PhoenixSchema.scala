package com.sutpc.bigdata.schema.phoenix

/**
  * 字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
  * card_id	卡号	字符串		否
  * trans_type	交易类型	整型		否		1-地铁进站；2-地铁出站；3-巴士；4-二维码巴士；
  * trans_time	交易日期	整型	10	否		10位UNIX时间戳
  * fdate	日期	整型		否		20190708表示2019年7月8日
  * ftime	时间	字符串		否		000321表示3分21秒
  * trans_value	交易金额	整型		否
  * dev_no	设备编号	字符串		否
  * company_line	公司名称/线路名称	字符串		否		trans_type=1或2时，线路名称；trans_type=3或4时，公司名称
  * line_station	线路名称/站点名称	字符串		否		trans_type=1或2时，站点名称；trans_type=3或4时，线路名称
  * vehicle_id	车辆编号	字符串		否
  * encry_id	不可逆加密车牌	字符串		否		采用MD5进行加密
  * city	城市名称	整型		否	1	参照城市编码对照表显示城市名称拼音
  * create_date 入库日期	整型		否		20190708表示2019年7月8日
  */
case class TAG_IC(
                   card_id: String,
                   trans_type: Int,
                   trans_time: Long,
                   fdate: Int,
                   ftime: String,
                   trans_value: Int,
                   dev_no: String,
                   company_line: String,
                   line_station: String,
                   vehicle_id: String,
                   encry_id: String,
                   city: Int,
                   create_date: Int,
                   license_prefix: String
                 )


/**
  *
  * 字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
  * loc_time	定位日期	整型	10	否		unix10位时间戳
  * fdate	日期	整型		否		20190708
  * ftime	时间	整型		否		190802
  * vehicle_id	车辆唯一标识（车牌）	字符串		否
  * lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
  * lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
  * speed	瞬时速度（km/h）	浮点	4,1	否
  * angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
  * operation_status	运营状态，出租车预留字段	整型	1 	否		1重载，0空载
  * company_code	所属公司代码	字符串		否		公司代码编码
  * altitude	海拔高度，单位m	整型		否
  * vehicle_color	车辆颜色	整型	1	是		1-蓝；2-黄；3-黑；4-白；5-其他
  * total_mileage	车辆总里程数，单位千米	整型	10	是
  * city	城市名称	字符串		否	1	参照城市编码对照表
  *
  */
case class TAG_GPS_TAXI(
                         loc_time: Long,
                         fdate: Int,
                         ftime: String,
                         vehicle_id: String,
                         encry_id: String,
                         lng: BigDecimal,
                         lat: BigDecimal,
                         speed: Double,
                         angle: Int,
                         operation_status: Int,
                         company_code: String,
                         altitude: Int,
                         vehicle_color: Integer,
                         total_mileage: Integer,
                         city: Int,
                         license_prefix: String
                       )


/**
  * 字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
  * loc_time	定位日期	整型	10	否		unix10位时间戳
  * fdate	日期	整型		否		20190708表示2019年7月8日
  * ftime	时间	字符串		否		000321表示3分21秒
  * vehicle_id	车辆唯一标识（车牌）	字符串		否
  * encry_id	不可逆加密车牌	字符串		否		采用MD5进行加密
  * lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
  * lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
  * speed	瞬时速度（km/h）	浮点	4,1	否
  * angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
  * company_code	所属公司代码	字符串		否		公司代码编码
  * busline_name	公交线路名称	字符串		否
  * stop_announce	报站状态	整型		是		报站状态：0位区间，1为进站
  * busline_dir	方向 	整型		是		0为上行，1为下行，2偏离路线，3为上行场区，4为下行场区
  * bus_station_order	站序	整型		是
  * city	城市名称	字符串		否	1	参照城市编码对照表
  * vehicle_id VARCHAR NOT NULL,
  * encry_id VARCHAR NOT NULL,
  * fdate INTEGER NOT NULL,
  * ftime VARCHAR NOT NULL,
  * loc_time BIGINT NOT NULL,
  * lng   decimal(10,6) NOT NULL,
  * lat   decimal(10,6) NOT NULL,
  * speed  decimal(4,1) NOT NULL,
  * angle  INTEGER NOT NULL,
  * operation_status  INTEGER NOT NULL,
  * company_code  VARCHAR NOT NULL,
  * busline_name  VARCHAR NOT NULL,
  * stop_announce  INTEGER NOT NULL,
  * busline_dir  INTEGER NOT NULL,
  * bus_station_order  INTEGER NOT NULL,
  * city  VARCHAR NOT NULL,
  */
case class TAG_GPS_BUS(
                        loc_time: Long,
                        fdate: Int,
                        ftime: String,
                        vehicle_id: String,
                        encry_id: String,
                        lng: BigDecimal,
                        lat: BigDecimal,
                        speed: Double,
                        angle: Int,
                        company_code: String,
                        busline_name: String,
                        stop_announce: Integer,
                        busline_dir: Integer,
                        bus_station_order: Integer,
                        city: Int,
                        license_prefix: String
                      )


//字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
//grid_fid	栅格id	字符串		否		网格编号
//period	时间片	整型		否		5分钟为一个时间片，一天288个时间片（编号从1至288）
//t	温度	浮点		否		单位是摄氏度
//slp	海平面气压	浮点		否		单位：百帕
//wspd	风速	浮点		否		单位：米/秒
//wdir	风向	浮点		否		单位：度
//rhsfc	相对湿度	浮点		否		百分比%
//rain01m	当前分钟的降雨量	浮点		否		单位：毫米
//rain06m	6分钟累计降雨量	浮点		否		单位：毫米
//rain12m	12分钟累计降雨量	浮点		否		单位：毫米
//rain30m	30分钟累计降雨量	浮点		否		单位：毫米
//rain01h	1小时累计降雨量	浮点		否		单位：毫米
//rain02h	2小时累计降雨量	浮点		否		单位：毫米
//rain03h	3小时累计降雨量	浮点		否		单位：毫米
//rain06h	6小时累计降雨量	浮点		否		单位：毫米
//rain12h	12小时累计降雨量	浮点		否		单位：毫米
//rain24h	24小时累计降雨量	浮点		否		单位：毫米
//rain48h	48小时累计降雨量	浮点		否		单位：毫米
//rain72h	72小时累计降雨量	浮点		否		单位：毫米
//v	能见度	浮点		否		单位：公里
//city	城市名称	字符串		否	1	参照城市编码对照表
//fdate	日期	整型		否	2	示例20190808表示2019年8月8日
case class TAG_WEATHER_GRID(
                             grid_fid: String,
                             period: Int,
                             t: Double,
                             slp: Double,
                             wspd: Double,
                             wdir: Double,
                             rhsfc: Double,
                             rain01m: Double,
                             rain06m: Double,
                             rain12m: Double,
                             rain30m: Double,
                             rain01h: Double,
                             rain02h: Double,
                             rain03h: Double,
                             rain06h: Double,
                             rain12h: Double,
                             rain24h: Double,
                             rain48h: Double,
                             rain72h: Double,
                             v: Double,
                             city: Int,
                             fdate: Int
                           )

//字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
//fdate	日期	整型		否
//period	时间片	整型		否		5分钟为一个时间片
//link_fid	linkid	整型	 	否 道路link的编号
//from_node	起点id	整型	 	否
//to_node	终点id	整型	 	否
//detect_volume	检测流量（辆）	整型	 	否
//speed	速度(km/h)	浮点	6,2	否
//statistic_lane_num	有统计数据的车道数	整型	 	否
//volume_factor	流量折算系数	整型	4,2	否
//modified_volume	扩样后流量(辆)	整型	 	否
//big_vehicle	大型车数量(辆)	整型	 	否
//mid_vehicle	中型车数量(辆)	整型	 
//small_vehicle	面包车数量(辆)	整型	 
//mini_vehicle	小汽车数量(辆)	整型	 
//city	城市名称	整型		否	1	参照城市编码对照表
case class TAG_SECTION_FLOW(
                             fdate: Int,
                             period: Int,
                             link_fid: Int,
                             from_node: Int,
                             to_node: Int,
                             detect_volume: Int,
                             speed: Double,
                             statistic_lane_num: Int,
                             volume_factor: Int,
                             modified_volume: Int,
                             big_vehicle: Integer,
                             mid_vehicle: Integer,
                             small_vehicle: Integer,
                             mini_vehicle: Integer,
                             city: Int
                           )

//grid_id	居住地栅格ID	字符串	32	否
//user_type	用户类型	字符串	10	否		resident-常住人口；tourist-流动人口
//age	年龄	整型		否		999-未知
//sex	性别	整型		否		1-男；2-女，999-未知
//adm_reg	用户归属地	字符串	32	否		用户归属地，参照城市编码对照表
//has_auto	有无小汽车	整型		否		1-有；0-没有；999-0未知；
//user_qty	人口数	整型		否
//fdate	日期	整型		否		示例20190800表示2019年8月份的数据
//create_time	数据入库	整型		否		示例20190808
//city	城市名称	字符串		否	1	参照城市编码对照表
//source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信

case class TAG_PHONE_HOME_DIST(
                                grid_id: String,
                                user_type: Int,
                                age: Integer,
                                sex: Integer,
                                adm_reg: Integer,
                                has_auto: Integer,
                                user_qty: Integer,
                                fdate: Int,
                                create_time: Int,
                                city: Int,
                                source: String
                              )


//字段	描述	类型	长度	是否为空	分区键	特殊说明
//grid_id	居住地栅格id	字符串	32	否
//age	年龄	整型
//sex	性别	整型				1-男；2-女；999-未知
//adm_reg	用户归属地	字符串	32			用户归属地，参照城市编码对照表
//has_auto	有无小汽车	整型				1-有；0-没有；999-未知
//work_qty	岗位数	整型
//fdate	日期	整型		否		示例20190800表示2019年8月份的数据
//create_time	数据入库	整型		否		示例20190808
//city	城市名称	字符串		否	1	参照城市编码对照表
//source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信

case class TAG_PHONE_WORK_DIST(
                                grid_id: String,
                                age: Integer,
                                sex: Integer,
                                adm_reg: Integer,
                                has_auto: Integer,
                                work_qty: Integer,
                                fdate: Int,
                                create_time: Int,
                                city: Int,
                                source: String

                              )

//home_grid_id	居住地栅格id	字符串	32	否
//work_grid_id	工作地栅格id	字符串	32	否
//home_city	居住地城市	字符串		否		参照城市编码对照表
//work_city	工作地城市	字符串		否		参照城市编码对照表
//age	年龄	整型
//sex	性别	整型				1-男；2-女；999-未知
//adm_reg	用户归属地	字符串	32			用户归属地，参照城市编码对照表
//has_auto	有无小汽车	整型				1-有；0-没有；999-未知
//user_qty	人口数	整型		否
//fdate	日期	整型		否		示例20190800表示2019年8月份的数据
//create_time	数据入库	整型		否		示例20190808
//province_city	省份/城市	整型		否	1	城市数据填写城市编码，全省数据填写省份编码（参照城市编码对照表）
//source	数据来源	字符串	2	否	2	cm-移动；cu-联通；ct-电信
case class TAG_PHONE_HOME_WORK_DIST(
                                     home_grid_id: String,
                                     work_grid_id: String,
                                     age: Integer,
                                     sex: Integer,
                                     adm_reg: Integer,
                                     has_auto: Integer,
                                     user_qty: Integer,
                                     fdate: Int,
                                     create_time: Int,
                                     home_city: Int,
                                     work_city: Int,
                                     source: String
                                   )


//字段名称	备注	类型	长度	是否为空	分区/索引键	特殊说明
//start_grid_id	出发位置栅格id	字符串	32	否
//end_grid_id	到达位置栅格id	字符串	32	否
//start_utype	出发城市用户类型	整型	1	否		1-常住人口；2-流动人口
//end_utype	到达城市用户类型	整型	1	否		1-常住人口；2-流动人口
//start_ptype	起点类型	整型	1	否		0-来访；1-工作地；2-家庭
//end_ptype	终点类型	整型	1	否		0-来访；1-工作地；2-家庭
//start _time	出发时间	整型		否
//end_ time	到达时间	整型		否
//time_dim	时间粒度	整型		否		1-5分钟时间片；2-15分钟时间片；3-小时
//trip_mode	出行目的	整型	1	否		1-工作地到家；2-家到工作地；3-其他
//aggtype	出行类型	整型	1	否		1-正常；2-枢纽；3-景点；
//age	年龄	整型		是		999为未知
//sex	性别	整型		是		1-男；2-女；999-未知
//trip_qty	出行量	整型		否
//adm_reg	用户归属地	整型		是		参照城市编码对照表
//create_time	数据入库	整型		否		示例20190808
//start_city	省内出发城市	整型		否	1	参照城市编码对照表
//end_city	省内到达城市	整型		否	2	参照城市编码对照表
//source	数据来源	字符串	2	否	3	cm-移动、cu-联通、ct-电信
//fdate	日期	整型	8	否	4	示例20190808


case class TAG_PHONE_TRIP_DIST(
                                start_grid_id: String,
                                end_grid_id: String,
                                fdate: Int,
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
                                time_dim: Int
                              )

//--字段名称	备注	类型	长度	是否为空	分区/索引键	特殊说明
//--grid_id	活动栅格id	字符串	32	否
//--user_type	用户类型	整型	10	否		1-常住人口；2-流动人口
//--act_time	活动时间，小时	整型		否		时间粒度请看time_dim时间粒度字段
//--time_dim	时间粒度	整型		否		1-5分钟时间片；2-15分钟时间片；3-小时
//--fdate	日期	整型		否		示例20190802；20190900表示2019年9月份的统计数据
//--age	年龄	整型		否		999-未知
//--sex	性别	整型		否		1-男；2-女，999-未知
//--user_qty	活动人数	整型		否
//--adm_reg	用户归属地	整型	32	否		参照城市编码对照表
//--create_time	数据入库	整型		否		示例20190808
//--city	城市名称	整型		否	1	参照城市编码对照表
//--source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信

case class TAG_PHONE_ACTIVITY(
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
                               source: String
                             )

case class TAG_GPS_Truck(
                          loc_time: Long,
                          fdate: Int,
                          ftime: String,
                          vehicle_id: String,
                          encry_id: String,
                          lng: BigDecimal,
                          lat: BigDecimal,
                          speed: Double,
                          angle: Int,
                          company_code: String,
                          altitude: Int,
                          vehicle_color: Integer,
                          total_mileage: Long,
                          city: Int,
                          vehicle_status: String,
                          license_prefix: String
                        )
