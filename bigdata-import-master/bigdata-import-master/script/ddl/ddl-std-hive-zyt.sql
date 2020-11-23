DROP table  IF  EXISTS s_vehicle_gps_bus;
CREATE TABLE  IF NOT EXISTS  s_vehicle_gps_bus(
    loc_time	BIGINT COMMENT '定位日期',
    vehicle_id	STRING COMMENT '车辆唯一标识（车牌）',
    lng decimal(10,6)	COMMENT '经度WGS坐标系',
    lat	decimal(10,6) COMMENT '纬度WGS坐标系',
    speed decimal(4,1)	COMMENT '瞬时速度（km/h）',
    angle int	COMMENT '方向角|与正北方向的顺时针夹角（0-359）',
    is_validate int	COMMENT '数据是否有效|0表示异常，1表示有效',
    company_code STRING	COMMENT '公司代码编码',
    busline_name STRING	COMMENT '公交线路名称',
    bus_position int	COMMENT '报站状态，公交车预留字段，报站状态：0位区间，1为进站',
    busline_dir int	COMMENT '方向，公交车预留字段，0为上行，1为下行，2偏离路线，3为上行场区，4为下行场区',
    bus_station_order int	COMMENT '站序',
    alarm_status STRING	COMMENT '报警状态 存储原始数据，保留以后使用',
    vehicle_status STRING	COMMENT '车辆状态 存储原始数据，保留以后使用'
 )
 COMMENT '公交车辆GPS数据表'
  partitioned by (city int	COMMENT '城市名称|参照城市编码对照表', year int COMMENT '年', month int COMMENT '月', day  int COMMENT '日')
CLUSTERED BY(vehicle_id) SORTED BY(loc_time) INTO 32 BUCKETS
STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");


drop table s_vehicle_gps_taxi;
CREATE TABLE  IF NOT EXISTS  s_vehicle_gps_taxi
(
  vehicle_id STRING  COMMENT '车辆唯一标识（车牌）',
  loc_time BIGINT  COMMENT '定位日期	unix10位时间戳',
  lng   decimal(10,6) COMMENT '经度WGS坐标系',
  lat   decimal(10,6) COMMENT '纬度WGS坐标系',
  speed  decimal(4,1) COMMENT '瞬时速度（km/h）',
  angle  int COMMENT '方向角，与正北方向的顺时针夹角（0-359）',
  operation_status  int COMMENT '运营状态，出租车预留字段，1重载，0空载',
  is_validate int	COMMENT '数据是否有效，0表示异常，1表示有效',
  company_code  STRING COMMENT '所属公司代码',
  altitude  int COMMENT '海拔高度，单位m',
  vehicle_color  int COMMENT '车辆颜色	整型	1	是		1-蓝；2-黄；3-黑；4-白；5-其他',
  total_mileage  int COMMENT '车辆总里程数，单位千米	整型	10	是',
  alarm_status STRING	COMMENT '报警状态 存储原始数据，保留以后使用',
  vehicle_status STRING	COMMENT '车辆状态 存储原始数据，保留以后使用'
)  COMMENT '出租车GPS数据表'
    partitioned by (city  int COMMENT '城市名称 1	参照城市编码对照表', year int COMMENT '年', month int COMMENT '月', day  int COMMENT '日')
   CLUSTERED BY(vehicle_id) SORTED BY(loc_time) INTO 32 BUCKETS
  STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");

drop table IF  EXISTS s_vehicle_gps_drive;
CREATE TABLE  IF NOT EXISTS  s_vehicle_gps_drive
(
  vehicle_id STRING  COMMENT '车辆唯一标识（车牌）',
  loc_time BIGINT  COMMENT '定位日期	unix10位时间戳',
  lng   decimal(10,6) COMMENT '经度WGS坐标系',
  lat   decimal(10,6) COMMENT '纬度WGS坐标系',
  speed  decimal(4,1) COMMENT '瞬时速度（km/h）',
  angle  int COMMENT '方向角，与正北方向的顺时针夹角（0-359）',
  is_validate int	COMMENT '数据是否有效，0表示异常，1表示有效',
  navigation_status int COMMENT '导航状态 	整型	1 	否		11为导航状态、10为非导航状态',
  company_code  STRING COMMENT '所属公司代码',
  license_prefix STRING COMMENT '车牌前缀|如：深圳市车牌前缀为粤B'
)  COMMENT '百度导航GPS数据表'
    partitioned by (source_company string COMMENT '数据来源公司',city string COMMENT '城市编号|参照城市编码对照表', year int COMMENT '年', month int COMMENT '月', day int COMMENT '日')
   CLUSTERED BY(vehicle_id) SORTED BY(loc_time) INTO 32 BUCKETS
  STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");

drop table s_vehicle_gps_order;
CREATE TABLE  IF NOT EXISTS  s_vehicle_gps_order
(
  vehicle_id STRING  COMMENT '车辆唯一标识（车牌）',
  loc_time BIGINT  COMMENT '定位日期	unix10位时间戳',
  lng   decimal(10,6) COMMENT '经度WGS坐标系',
  lat   decimal(10,6) COMMENT '纬度WGS坐标系',
  speed  decimal(4,1) COMMENT '瞬时速度（km/h）',
  angle  int COMMENT '方向角，与正北方向的顺时针夹角（0-359）',
  company_code  STRING COMMENT '所属公司代码',
  operation_status int COMMENT '营运状态	整型	1 	否		1-载客，2-接单，3-空驶，4-停运',
  position_type STRING COMMENT	'位置信息类型	字符串	10	否		JYSX-经营上线、JYXX经营下线、CKSC乘客上车、CKXC-乘客下车、DDJD-订单派单',
  order_id STRING COMMENT	'订单id	字符串 订单编号',
  altitude int COMMENT '海拔高度，单位m'
)  COMMENT '网约车GPS数据表'
  partitioned by (city  int COMMENT '城市名称|参照城市编码对照表', year int COMMENT '年', month int COMMENT '月', day int COMMENT '日')
   CLUSTERED BY(vehicle_id) SORTED BY(loc_time) INTO 32 BUCKETS
  STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");

DROP TABLE  IF EXISTS s_vehicle_gps_others;
CREATE TABLE  IF NOT EXISTS  s_vehicle_gps_others
(
  vehicle_id STRING  COMMENT '车辆唯一标识（车牌）',
  loc_time BIGINT  COMMENT '定位日期	unix10位时间戳',
  lng   decimal(10,6) COMMENT '经度WGS坐标系',
  lat   decimal(10,6) COMMENT '纬度WGS坐标系',
  speed  decimal(4,1) COMMENT '瞬时速度（km/h）',
  angle  int COMMENT '方向角，与正北方向的顺时针夹角（0-359）',
  is_validate int COMMENT '数据是否有效 0表示异常，1表示有效',
  company_code  STRING COMMENT '所属公司代码',
  vehicle_color int COMMENT '车辆颜色 1-蓝；2-黄；3-黑；4-白；5-其他',
  total_mileage BIGINT COMMENT '车辆总里程数，单位千米	整型	10	是',
  alarm_status int COMMENT '报警状态 存储原始数据，保留以后使用',
  vehicle_status STRING COMMENT '车辆状态 存储原始数据，保留以后使用',
  altitude int COMMENT '海拔高度，单位m',
  license_prefix STRING COMMENT '车牌前缀|如：深圳市车牌前缀为粤B'
)  COMMENT '客运班车、客运包车、泥头车、危险品运输车、货运、驾培车GPS数据'
    partitioned by (vehicle_type int COMMENT '车辆类型|参照车辆类型对照表',city  int COMMENT '城市名称|参照城市编码对照表', year string COMMENT '年', month string COMMENT '月', day string COMMENT '日')
   CLUSTERED BY(vehicle_id) SORTED BY(loc_time) INTO 32 BUCKETS
  STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");

DROP TABLE  IF EXISTS s_transit_people_ic;
CREATE TABLE  IF NOT EXISTS  s_transit_people_ic
(
 card_id STRING  COMMENT '卡号	字符串',
 vehicle_id STRING  COMMENT '车辆编号',
 trans_type int COMMENT '交易类型	整型		否		1-地铁进站；2-地铁出站；3-巴士；4-二维码巴士',
 trans_time BIGINT COMMENT '交易日期	整型	10	否		10位UNIX时间戳',
 trans_value int COMMENT '交易金额	整型		否' ,
 dev_no STRING  COMMENT '设备编号' ,
 company_line STRING  COMMENT '公司名称/线路名称	字符串		否		trans_type=1或2时，线路名称；trans_type=3或4时，公司名称',
 line_station STRING  COMMENT '线路名称/站点名称	字符串		否		trans_type=1或2时，站点名称；trans_type=3或4时，线路名称'
)  COMMENT 'IC卡数据'
   partitioned by (vehicle_type int COMMENT '车辆类型|参照车辆类型对照表',city  int COMMENT '城市名称|参照城市编码对照表', year string COMMENT '年', month string COMMENT '月', day string COMMENT '日')
   CLUSTERED BY(vehicle_id) SORTED BY(trans_time) INTO 32 BUCKETS
  STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");

DROP TABLE  IF EXISTS s_bike_switch_lock;
CREATE TABLE  IF NOT EXISTS  s_bike_switch_lock
(
  bike_id STRING  COMMENT '单车编号',
  loc_time BIGINT  COMMENT '定位时间	unix10位时间戳',
  ftime STRING   COMMENT '时间 格式HHmmss',
  lng   decimal(10,6) COMMENT '经度WGS坐标系',
  lat   decimal(10,6) COMMENT '纬度WGS坐标系',
  order_no  int COMMENT '序号(按行数自增)',
  lock_status int COMMENT '单车状态(1-开锁；2-关锁)'
)  COMMENT '共享单车开锁或关锁发送一次定位数据'
partitioned by (city string COMMENT '按照城市编码对照表填写城市编码', year string COMMENT '年', month string COMMENT '月', day string COMMENT '日')
CLUSTERED BY(bike_id) SORTED BY(loc_time) INTO 32 BUCKETS
STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");




DROP TABLE IF  EXISTS transpass_std.s_road_plate_recognition;
CREATE TABLE IF NOT EXISTS transpass_std.s_road_plate_recognition
(
id   string   COMMENT "编号",
vehicle_fid   string   COMMENT "车牌号码",
 encry_id STRING  COMMENT '加密车牌|采用"<车牌前缀>_<是否电动车>_<加密串>"的形式表示。粤BD12345加密后的形式为"粤B_D_<12345的md5加密>"。非电动车用符号"o"表示',
recognition_time   int   COMMENT "10为UNIX时间戳|识别时间",
upload_time   int   COMMENT "10为UNIX时间戳|上传时间",
detector_id   string   COMMENT "不足8为左边补0|监测设备id",
lane_fid   string   COMMENT "待补充|车道编号",
speed   int   COMMENT "单位km/h|行驶速度",
vehicle_type   int   COMMENT "待补|车辆类型",
plate_type   int   COMMENT "待补充|车牌类型",
illegal_type   int   COMMENT "待补充|违法类型",
degree_of_confidence   int   COMMENT "0-未知，1-可信任，2-不可信任|置信度",
plate_color  int    COMMENT "待补充|号牌颜色",
drive_dir int     COMMENT "待补充|0-其他；1-由东向西；2-由西向东；3-由南向北；4-由北向南；5-由东南向西北；6-由西北向东南；7-由东北向西南；8-由西南向东北|出行方向",
crossing_type   int   COMMENT "待补充|1-卡口；2-电警；3-RFID卡口；4-互联停车场；5-分局卡口；6-加油站；7-违停球；9-移动卡口|卡口类型",
capture_address   string   COMMENT "待补充|抓拍地址"
) COMMENT "卡口电警车牌识别数据"
partitioned by (city string COMMENT '城市编号|参照城市编码对照表', year int COMMENT '示例2019|年份', month int COMMENT '示例8|月份', day int COMMENT '示例2|日')
 CLUSTERED BY(vehicle_fid) SORTED BY(recognition_time) INTO 32 BUCKETS
STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");


DROP TABLE IF  EXISTS transpass_std.s_road_plate_recognition;
CREATE TABLE IF NOT EXISTS transpass_std.s_road_plate_recognition
(
id   string   COMMENT "编号",
vehicle_fid   string   COMMENT "车牌号码",
recognition_time   int   COMMENT "10为UNIX时间戳|识别时间",
upload_time   int   COMMENT "10为UNIX时间戳|上传时间",
detector_id   string   COMMENT "不足8为左边补0|监测设备id",
lane_fid   string   COMMENT "待补充|车道编号",
speed   int   COMMENT "单位km/h|行驶速度",
vehicle_type      COMMENT "车辆类型",
plate_type      COMMENT "待补充|车牌类型",
illegal_type      COMMENT "待补充|违法类型",
degree_of_confidence      COMMENT "0-未知，1-可信任，2-不可信任|置信度",
plate_color      COMMENT "待补充|号牌颜色",
drive_dir      COMMENT "待补充0-其他；1-由东向西；2-由西向东；3-由南向北；4-由北向南；5-由东南向西北；6-由西北向东南；7-由东北向西南；8-由西南向东北|出行方向",
crossing_type   int   COMMENT "1-卡口；2-电警；3-RFID卡口；4-互联停车场；5-分局卡口；6-加油站；7-违停球；9-移动卡口|卡口类型",
capture_address   string   COMMENT "|抓拍地址",
city   int   COMMENT "参照城市编码对照表|城市编码",
year   int   COMMENT "示例2019|年份",
month   int   COMMENT "示例8|月份",
day   int   COMMENT "示例2|日",
) COMMENT "卡口电警车牌识别数据"
partitioned by (city string COMMENT '城市编码（参照城市编码对照表）', year int COMMENT '年', month int COMMENT '月', day int COMMENT '日')
CLUSTERED BY(vehicle_fid) SORTED BY(record_time) INTO 32 BUCKETS STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");




DROP TABLE  IF EXISTS s_bike_loc;
CREATE TABLE  IF NOT EXISTS  s_bike_loc
(
  bike_id STRING  COMMENT '单车编号',
  operator int  COMMENT '运营商 1-Mobike；2-ofo；3-其他',
  bike_type int  COMMENT '车辆类型 1-Mobike, 2-Mobike Lite, 999-红包车',
  loc_time BIGINT  COMMENT '定位时间|unix10位时间戳',
  lng   decimal(10,6)  COMMENT '经度|WGS坐标系',
  lat   decimal(10,6)  COMMENT '纬度|WGS坐标系'
)  COMMENT '单车定位数据'
    partitioned by (city string COMMENT '城市编码|参照城市编码对照表', year string COMMENT '年', month string COMMENT '月', day string COMMENT '日')
   CLUSTERED BY(bike_id) SORTED BY(loc_time) INTO 32 BUCKETS
  STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");

drop table  IF  EXISTS  s_vehicle_gps_truck;
CREATE TABLE  IF NOT EXISTS  s_vehicle_gps_truck
(
  vehicle_id STRING  COMMENT '车辆唯一标识（车牌）',
  loc_time BIGINT  COMMENT '定位日期',
  lng   decimal(10,6)  COMMENT '经度',
  lat   decimal(10,6)  COMMENT '纬度',
  speed  decimal(4,1)  COMMENT '瞬时速度（km/h）',
  angle  int  COMMENT '方向角，与正北方向的顺时针夹角（0-359）',
  company_code  STRING  COMMENT '所属公司代码(公司代码编码)',
  altitude  int  COMMENT '海拔高度，单位m',
  vehicle_color  int COMMENT '车辆颜色|1-蓝；2-黄；3-黑；4-白；5-其他',
  total_mileage  BIGINT COMMENT '车辆总里程数|单位千米',
  is_validate int  COMMENT '数据是否有效 0表示异常，1表示有效',
  alarm_status STRING  COMMENT '报警状态',
  vehicle_status STRING  COMMENT '车辆状态'
) COMMENT '货运车GPS数据表'
partitioned by (city string COMMENT '城市编码|参照城市编码对照表', year string COMMENT '年', month string COMMENT '月', day string COMMENT '日')
  CLUSTERED BY(vehicle_id) SORTED BY(loc_time) INTO 32 BUCKETS
  STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");

drop table  IF  EXISTS  s_phone_inter_activity;
CREATE TABLE  IF NOT EXISTS  s_phone_inter_activity
(
    grid_id	  string    COMMENT '活动栅格id',
    user_type	 int   COMMENT '用户类型|1-常住人口；2-流动人口',
    act_time	int COMMENT '活动时间|小时 时间粒度请看time_dim时间粒度字段',
    time_dim	int COMMENT '时间粒度|1-5分钟时间片；2-15分钟时间片；3-小时',
    fdate	 int COMMENT '日期|示例20190802；20190900表示2019年9月份的统计数据',
    age	 int COMMENT '年龄|999-未知',
    sex	 int COMMENT '性别|1-男；2-女，999-未知',
    user_qty	 int COMMENT '活动人数',
    adm_reg	 int COMMENT '用户归属地|参照城市编码对照表',
    create_time	 int COMMENT '数据入库时间|示例20190808',
    creator	STRING COMMENT '入库人员|入库人员姓名拼音（liangjiaxian）'
) COMMENT '24小时人员活动'
PARTITIONED by (source string COMMENT '数据来源(m-移动、cu-联通、ct-电信)',city string COMMENT '城市编码|参照城市编码对照表', year string COMMENT '年', month string COMMENT '月', day string COMMENT '日')
CLUSTERED BY(grid_id) SORTED BY(create_time) INTO 16 BUCKETS
STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");


drop table  IF  EXISTS  s_road_epolice;
CREATE TABLE  IF NOT EXISTS  s_road_epolice
(
    id	  string    COMMENT '编号',
    district	string   COMMENT '行政区',
    address	string COMMENT '地址',
    lng_bd	decimal(10,6) COMMENT '百度坐标系经度|浮点(10,6)',
    lat_bd	 decimal(10,6) COMMENT '百度坐标系纬度|浮点(10,6)',
    lng	 decimal(10,6) COMMENT '年龄|999-未知',
    lat	 decimal(10,6) COMMENT '性别|1-男；2-女，999-未知',
    cameraType	 int COMMENT '摄像头类型| 1-标清,2-高清',
    shotType	 int COMMENT '抓拍类型| 1-超速；2-冲红灯；3-逆行；4-冲红灯、逆行、压线、不按道行驶'
) COMMENT '电子警察数据'
PARTITIONED by (city string COMMENT '城市编码|参照城市编码对照表', year int COMMENT '年', month int COMMENT '月', day int COMMENT '日')
 ROW format delimited fields terminated by ',' STORED AS TEXTFILE;

load data  inpath '/data/epolice/shenzhen/PoliceVideo-all.csv' into table s_road_epolice partition(city='shenzhen',year=2019,month=5,day=1);



CREATE TABLE IF NOT EXISTS `transpaas_440300_std.s_vai_pedestrian_feature`(
  `uuid`                  string 			COMMENT '唯一编号',
  `event_fid`           string 			COMMENT '主事件全局唯一id',
  `speed`               decimal(10,2) 	COMMENT '速度',
  `start_time`          Int 			COMMENT '开始时间',
  `end_time`            Int 			COMMENT '结束时间',
  `violation`           int 			COMMENT '非机动车违章',
  `nv_violation_id`     int 			COMMENT '非机动车违章id',
  `score`               int 			COMMENT '特征得分',
  `bayonet_direction`   int 			COMMENT '卡口方向',
  `drive_direction`     int 			COMMENT '行驶方向',
  `rect_top`            int 			COMMENT '识别位置-上',
  `rect_left`           int 			COMMENT '识别位置坐标-左',
  `rect_bottom`         int 			COMMENT '识别位置坐标-底部',
  `rect_right`          int 			COMMENT '识别位置坐标-右',
  `sex`                 int 			COMMENT '性别',
  `upbody_color`        string 			COMMENT '上半身颜色',
  `upbody_clothes`      string 			COMMENT '上半身衣服类型',
  `upbody_score`        int 			COMMENT '可信度',
  `lobody_lobody`       string 			COMMENT '下半身颜色',
  `lobody_clothes`      string 			COMMENT '下半身衣服类型',
  `lobody_score`        int 			COMMENT '可信度',
  `cap`                 int 			COMMENT '是否戴帽子',
  `cap_score`           int 			COMMENT '可信度',
  `stature`             int 			COMMENT '身高类型',
  `stature_score`       int 			COMMENT '可信度',
  `bag`                 int 			COMMENT '背包类型',
  `bag_score`           int 			COMMENT '可信度',
  `hair`                int 			COMMENT '头发类型',
  `hair_score`          int 			COMMENT '可信度',
  `age_stage`           int 			COMMENT '年龄类型',
  `age_stage_score`     int 			COMMENT '可信度',
  `is_bike`             int 			COMMENT '是否骑行',
  `bike_score`          int 			COMMENT '可信度',
  `company`             int 			COMMENT '运营商所属编码'
) COMMENT '记录视频识别的每一位行人的特征信息，包括身高、衣着颜色、是否骑行等'
partitioned by (year string COMMENT '年', month string COMMENT '月', day string COMMENT '日', hours string COMMENT '小时')
STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");


CREATE TABLE IF NOT EXISTS `transpaas_440300_std.s_vai_vehicle_feature`(
  `id`                  string          COMMENT '唯一编号',
  `event_fid`           string 			COMMENT '主事件全局唯一id',
  `violation`			Int				COMMENT '机动车违章',
  `v_violation_id`		Int				COMMENT '机动车违章id',
  `score`				string			COMMENT '事件置信度',
  `bayonet_direction`	Int				COMMENT '卡扣方向',
  `drive_direction`		Int				COMMENT '行驶方向',
  `vehicle_type`		Int				COMMENT '车辆类型',
  `lane`				string			COMMENT '车道',
  `speed`               decimal(10,2) 	COMMENT '速度',
  `vehicle_id`			string			COMMENT '车牌号',
  `license_color`		string			COMMENT '车牌颜色',
  `vehicle_color`		string			COMMENT '车身颜色'
) COMMENT '记录交叉口人行道的路网位置及与视频关联信息'
partitioned by (year string COMMENT '年', month string COMMENT '月', day string COMMENT '日', hours string COMMENT '小时')
STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");


//地磁建表语句
CREATE TABLE IF NOT EXISTS `transpaas_440300_std.s_road_geomagnetic_detection`(
  `uuid`                string          COMMENT '唯一编号',
  `detector_fid`        string 			COMMENT '检测器ID',
  `hh`					Int				COMMENT '小时',
  `mm`					Int 			COMMENT '分钟',
  `lane_fid`			String			COMMENT '车道id',
  `vehicle_size`		int			COMMENT '车辆型号',
  `headway`				Int				COMMENT '车头时距,单位毫秒',
  `speed`               decimal(10,2) 	COMMENT '速度,单位km/h',
  `occupancy`			Int				COMMENT '平均占有时间,单位毫秒',
  `interval`			Int				COMMENT '平均间隔时间,单位毫秒',
  `length`				decimal(10,2)	COMMENT '平均车长,单位米',
  `record_time`			Int				COMMENT '流水时间'
) COMMENT '地磁检测数据,深圳市平均一天约800M数据量'
partitioned by (city Int COMMENT '城市编码', year string COMMENT '年份', month string COMMENT '月份', day string COMMENT '日')
STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");




CREATE TABLE IF NOT EXISTS `s_road_geomagnetic_detection`(
`uuid` string COMMENT '唯一编号',
`detector_fid`  string COMMENT '检测器ID',
`hh` Int COMMENT '小时',
`mm` Int COMMENT '分钟',
`lane_fid` String COMMENT '车道id',
`vehicle_size` int COMMENT '车辆型号',
`headway` Int COMMENT '车头时距,单位毫秒',
`speed`               decimal(10,2)  COMMENT '速度,单位km/h',
`occupancy` Int COMMENT '平均占有时间,单位毫秒',
`interval` Int COMMENT '平均间隔时间,单位毫秒',
`length` decimal(10,2) COMMENT '平均车长,单位米',
`record_time` Int COMMENT '流水时间'
) COMMENT '地磁检测数据,深圳市平均一天约800M数据量'
partitioned by (city Int COMMENT '城市编码', year string COMMENT '年份', month string COMMENT '月份', day string COMMENT '日')
STORED AS PARQUET TBLPROPERTIES ("PARQUET.compress"="SNAPPY");