
--命令行连接方式： /var/lib/hadoop-hdfs/phoenix/bin/sqlline.py node2.sutpc.cc:2181

------------------------------指标库----------------------
--网约车GPS
drop table t_vehicle_gps_order;
CREATE TABLE  IF NOT EXISTS  t_vehicle_gps_order
(
  vehicle_id VARCHAR NOT NULL,
  encry_id VARCHAR NOT NULL,
  fdate INTEGER NOT NULL,
  ftime VARCHAR NOT NULL,
  loc_time VARCHAR  NOT NULL,
  lng   decimal(10,6)  NOT NULL,
  lat   decimal(10,6)  NOT NULL,
  speed  decimal(4,1)  NOT NULL,
  angle  INTEGER  NOT NULL,
  company_code VARCHAR  NOT NULL,
  operation_status INTEGER  NOT NULL,
  position_type VARCHAR  NOT NULL,
  order_id VARCHAR  NOT NULL,
  altitude INTEGER  NOT NULL,
  city  INTEGER  NOT NULL
  CONSTRAINT PK PRIMARY KEY (encry_id,fdate,ftime,loc_time)
)   VERSIONS=1,BLOOMFILTER='ROW',immutable_rows=true;


--出租车GPS
--字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
--loc_time	定位日期	整型	10	否		unix10位时间戳
--fdate	日期	整型		否		20190708
--ftime	时间	整型		否		190802
--vehicle_id	车辆唯一标识（车牌）	字符串		否
--lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
--lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
--speed	瞬时速度（km/h）	浮点	4,1	否
--angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
--operation_status	运营状态，出租车预留字段	整型	1 	否		1重载，0空载
--company_code	所属公司代码	字符串		否		公司代码编码
--altitude	海拔高度，单位m	整型		否
--vehicle_color	车辆颜色	整型	1	是		1-蓝；2-黄；3-黑；4-白；5-其他
--total_mileage	车辆总里程数，单位千米	整型	10	是
--city	城市名称	字符串		否	1	参照城市编码对照表

drop table t_vehicle_gps_taxi;
CREATE TABLE  IF NOT EXISTS  t_vehicle_gps_taxi
(
  vehicle_id VARCHAR NOT NULL,
  encry_id VARCHAR NOT NULL,
  fdate INTEGER NOT NULL,
  ftime VARCHAR NOT NULL,
  loc_time BIGINT NOT NULL,
  lng   decimal(10,6) NOT NULL,
  lat   decimal(10,6) NOT NULL,
  speed  decimal(4,1) NOT NULL,
  angle  INTEGER NOT NULL,
  operation_status  INTEGER NOT NULL,
  company_code  VARCHAR NOT NULL,
  altitude  INTEGER NOT NULL,
  vehicle_color  INTEGER,
  total_mileage  INTEGER,
  city  INTEGER NOT NULL,
CONSTRAINT PK PRIMARY KEY (encry_id,fdate,ftime,loc_time)
) VERSIONS=1,BLOOMFILTER='ROW',immutable_rows=true;

--,SALT_BUCKETS=16


drop index  IDX_GPS_TAXI_VEHICLE_ID ON t_vehicle_gps_taxi;
CREATE INDEX if not exists IDX_GPS_TAXI_VEHICLE_ID ON t_vehicle_gps_taxi (fdate,ftime,vehicle_id)  VERSIONS=1,BLOOMFILTER = 'ROW', SALT_BUCKETS=16;
--CREATE INDEX if not exists IDX_GPS_TAXI_VEHICLE_ID ON t_vehicle_gps_taxi (vehicle_id) INCLUDE (lon,lat,speed) ASYNC VERSIONS=1,BLOOMFILTER = 'ROW', SALT_BUCKETS=16;




--公交车
--字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
--loc_time	定位日期	整型	10	否		unix10位时间戳
--fdate	日期	整型		否		20190708表示2019年7月8日
--ftime	时间	字符串		否		000321表示3分21秒
--vehicle_id	车辆唯一标识（车牌）	字符串		否
--encry_id	不可逆加密车牌	字符串		否		采用MD5进行加密
--lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
--lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
--speed	瞬时速度（km/h）	浮点	4,1	否
--angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
--company_code	所属公司代码	字符串		否		公司代码编码
--busline_name	公交线路名称	字符串		否
--stop_announce	报站状态	整型		是		报站状态：0位区间，1为进站
--busline_dir	方向 	整型		是		0为上行，1为下行，2偏离路线，3为上行场区，4为下行场区
--bus_station_order	站序	整型		是
--city	城市名称	字符串		否	1	参照城市编码对照表

--/data/gps/bus/2017/10/01
drop table t_vehicle_gps_bus;
CREATE TABLE  IF NOT EXISTS  t_vehicle_gps_bus
(
  vehicle_id VARCHAR NOT NULL  ,
  encry_id VARCHAR NOT NULL,
  fdate INTEGER NOT NULL,
  ftime VARCHAR NOT NULL,
  loc_time BIGINT(10) NOT NULL,
  lng   decimal(10,6) NOT NULL,
  lat   decimal(10,6) NOT NULL,
  speed  decimal(4,1) NOT NULL,
  angle  INTEGER(3) NOT NULL,
  company_code  VARCHAR NOT NULL,
  busline_name  VARCHAR NOT NULL,
  stop_announce  INTEGER,
  busline_dir  INTEGER,
  bus_station_order  INTEGER,
  city  INTEGER NOT NULL,
CONSTRAINT PK PRIMARY KEY (encry_id,fdate,ftime,loc_time)
) VERSIONS=1,BLOOMFILTER='ROW',immutable_rows=true;

drop index  IDX_GPS_BUS_VEHICLE_ID ON t_vehicle_gps_bus;
CREATE INDEX if not exists IDX_GPS_BUS_VEHICLE_ID ON t_vehicle_gps_bus (fdate,ftime,vehicle_id)  VERSIONS=1,BLOOMFILTER = 'ROW', SALT_BUCKETS=16;
--CREATE INDEX if not exists IDX_GPS_BUS_VEHICLE_ID ON t_vehicle_gps_bus (vehicle_id) INCLUDE (lon,lat,speed) ASYNC VERSIONS=1,BLOOMFILTER = 'ROW', SALT_BUCKETS=16;

--ic卡
--字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
--card_id	卡号	字符串		否
--trans_type	交易类型	整型		否		1-地铁进站；2-地铁出站；3-巴士；4-二维码巴士；
--trans_time	交易日期	整型	10	否		10位UNIX时间戳
--fdate	日期	整型		否		20190708表示2019年7月8日
--ftime	时间	字符串		否		000321表示3分21秒
--trans_value	交易金额	整型		否
--dev_no	设备编号	字符串		否
--company_line	公司名称/线路名称	字符串		否		trans_type=1或2时，线路名称；trans_type=3或4时，公司名称
--line_station	线路名称/站点名称	字符串		否		trans_type=1或2时，站点名称；trans_type=3或4时，线路名称
--vehicle_id	车辆编号	字符串		否
--encry_id	不可逆加密车牌	字符串		否		采用MD5进行加密
--city	城市名称	整型		否	1	参照城市编码对照表显示城市名称拼音
--create_date 入库日期	整型		否		20190708表示2019年7月8日

drop table t_transit_people_ic;
CREATE TABLE  IF NOT EXISTS  t_transit_people_ic
(
card_id VARCHAR not null,
vehicle_id VARCHAR not null,
encry_id VARCHAR not null,
trans_type INTEGER not null,
trans_time BIGINT not null,
fdate INTEGER not null,
ftime VARCHAR not null,
trans_value INTEGER ,
dev_no VARCHAR ,
company_line VARCHAR not null,
line_station VARCHAR not null,
city INTEGER not null,
create_date INTEGER not null,
CONSTRAINT PK PRIMARY KEY (card_id,encry_id,fdate,ftime)
) VERSIONS=1,BLOOMFILTER='ROW',immutable_rows=true ;


CREATE INDEX if not exists IDX_TRANSIT_IC_TIME ON t_transit_people_ic (fdate,ftime,vehicle_id)  VERSIONS=1,BLOOMFILTER = 'ROW', SALT_BUCKETS=12;

--天气

--字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
--grid_fid	栅格id	字符串		否		网格编号
--period	时间片	整型		否		5分钟为一个时间片，一天288个时间片（编号从1至288）
--t	温度	浮点		否		单位是摄氏度
--slp	海平面气压	浮点		否		单位：百帕
--wspd	风速	浮点		否		单位：米/秒
--wdir	风向	浮点		否		单位：度
--rhsfc	相对湿度	浮点		否		百分比%
--rain01m	当前分钟的降雨量	浮点		否		单位：毫米
--rain06m	6分钟累计降雨量	浮点		否		单位：毫米
--rain12m	12分钟累计降雨量	浮点		否		单位：毫米
--rain30m	30分钟累计降雨量	浮点		否		单位：毫米
--rain01h	1小时累计降雨量	浮点		否		单位：毫米
--rain02h	2小时累计降雨量	浮点		否		单位：毫米
--rain03h	3小时累计降雨量	浮点		否		单位：毫米
--rain06h	6小时累计降雨量	浮点		否		单位：毫米
--rain12h	12小时累计降雨量	浮点		否		单位：毫米
--rain24h	24小时累计降雨量	浮点		否		单位：毫米
--rain48h	48小时累计降雨量	浮点		否		单位：毫米
--rain72h	72小时累计降雨量	浮点		否		单位：毫米
--v	能见度	浮点		否		单位：公里
--city	城市名称	字符串		否	1	参照城市编码对照表
--fdate	日期	整型		否	2	示例20190808表示2019年8月8日

drop table t_weather_grid_his;
CREATE TABLE  IF NOT EXISTS  t_weather_grid_his
(
grid_fid VARCHAR not null,
fdate INTEGER not null,
period INTEGER not null,
t double not null,
slp double not null,
wspd double not null,
wdir double not null,
rhsfc double not null,
rain01m double not null,
rain06m double not null,
rain12m double not null,
rain30m double not null,
rain01h double not null,
rain02h double not null,
rain03h double not null,
rain06h double not null,
rain12h double not null,
rain24h double not null,
rain48h double not null,
rain72h double not null,
v double not null,
city INTEGER not null,
CONSTRAINT PK PRIMARY KEY (grid_fid,fdate,period,city)
) VERSIONS=1,BLOOMFILTER='ROW',SALT_BUCKETS=8,immutable_rows=true;


--地磁数据

--字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
--fdate	日期	整型		否
--period	时间片	整型		否		5分钟为一个时间片
--link_fid	linkid	整型	 	否 道路link的编号
--from_node	起点id	整型	 	否
--to_node	终点id	整型	 	否
--detect_volume	检测流量（辆）	整型	 	否
--speed	速度(km/h)	浮点	6,2	否
--statistic_lane_num	有统计数据的车道数	整型	 	否
--volume_factor	流量折算系数	整型	4,2	否
--modified_volume	扩样后流量(辆)	整型	 	否
--big_vehicle	大型车数量(辆)	整型	 	否
--mid_vehicle	中型车数量(辆)	整型	 
--small_vehicle	面包车数量(辆)	整型	 
--mini_vehicle	小汽车数量(辆)	整型	 
--city	城市名称	整型		否	1	参照城市编码对照表
drop table t_tpi_section_flow;
CREATE TABLE  IF NOT EXISTS  t_tpi_section_flow
(
fdate	INTEGER not null,
period	INTEGER not null,
link_fid	INTEGER not null,
from_node	INTEGER not null,
to_node	INTEGER not null,
detect_volume	INTEGER not null,
speed		decimal(6,2) 	not null,
statistic_lane_num	INTEGER not null,
volume_factor	decimal(4,2) 	not null,
modified_volume	INTEGER not null,
big_vehicle	INTEGER not null,
mid_vehicle	INTEGER,
small_vehicle	INTEGER,
mini_vehicle	INTEGER,
city INTEGER not null,
CONSTRAINT PK PRIMARY KEY (link_fid,fdate,period,city)
) VERSIONS=1,BLOOMFILTER='ROW',SALT_BUCKETS=16,immutable_rows=true;

----------------------------------------------------
--手机信令

--1、s_phone_inter_trip和t_phone_trip_distribute，删除province_city字段，由原来的start _city和end_city判断城市归属；增加start_utype和end_utype字段
--2、t_phone_home_work_rela和s_phone_inter_home_work的，删除province_city字段，由原来的home_city
--和work_city 区分城市归属；
--3、s_phone_inter_corridor_flow和t_phone_corridor_flow，删除province_city字段，增加start _city和end_city字段



--字段	描述	类型	长度	是否为空	分区键	特殊说明
--grid_id	居住地栅格ID	字符串	32	否
--user_type	用户类型	字符串	10	否		resident-常住人口；tourist-流动人口
--age	年龄	整型		否		999-未知
--sex	性别	整型		否		1-男；2-女，999-未知
--adm_reg	用户归属地	字符串	32	否		用户归属地，参照城市编码对照表
--has_auto	有无小汽车	整型		否		1-有；0-没有；999-0未知；
--user_qty	人口数	整型		否
--fdate	日期	整型		否		示例20190800表示2019年8月份的数据
--create_time	数据入库	整型		否		示例20190808
--city	城市名称	字符串		否	1	参照城市编码对照表
--source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信

drop table t_phone_home_distribute;
CREATE TABLE  IF NOT EXISTS  t_phone_home_distribute
(
grid_id  VARCHAR(32) not null,
user_type  INTEGER not null,
age  INTEGER ,
sex  INTEGER ,
adm_reg  INTEGER,
has_auto  INTEGER ,
user_qty  INTEGER not null,
fdate  INTEGER not null,
create_time  INTEGER not null,
city  INTEGER not null,
source  VARCHAR(2),
CONSTRAINT PK PRIMARY KEY (grid_id,fdate,city,source)
) VERSIONS=1,BLOOMFILTER='ROW',SALT_BUCKETS=2,immutable_rows=true;


--字段	描述	类型	长度	是否为空	分区键	特殊说明
--grid_id	居住地栅格id	字符串	32	否
--age	年龄	整型
--sex	性别	整型				1-男；2-女；999-未知
--adm_reg	用户归属地	字符串	32			用户归属地，参照城市编码对照表
--has_auto	有无小汽车	整型				1-有；0-没有；999-未知
--work_qty	岗位数	整型
--fdate	日期	整型		否		示例20190800表示2019年8月份的数据
--create_time	数据入库	整型		否		示例20190808
--city	城市名称	字符串		否	1	参照城市编码对照表
--source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信

drop table t_phone_work_distribute;
CREATE TABLE  IF NOT EXISTS  t_phone_work_distribute
(
grid_id  VARCHAR(32) not null,
age  INTEGER ,
sex  INTEGER ,
adm_reg  INTEGER,
has_auto  INTEGER ,
work_qty  INTEGER not null,
fdate  INTEGER not null,
create_time  INTEGER not null,
city  INTEGER not null,
source  VARCHAR(2),
CONSTRAINT PK PRIMARY KEY (grid_id,fdate,city,source)
) VERSIONS=1,BLOOMFILTER='ROW',SALT_BUCKETS=2,immutable_rows=true;

--字段	描述	类型	长度	是否为空	分区键	特殊说明
--home_grid_id	居住地栅格id	字符串	32	否
--work_grid_id	工作地栅格id	字符串	32	否
--home_city	居住地城市	字符串		否		参照城市编码对照表
--work_city	工作地城市	字符串		否		参照城市编码对照表
--age	年龄	整型
--sex	性别	整型				1-男；2-女；999-未知
--adm_reg	用户归属地	字符串	32			用户归属地，参照城市编码对照表
--has_auto	有无小汽车	整型				1-有；0-没有；999-未知
--user_qty	人口数	整型		否
--fdate	日期	整型		否		示例20190800表示2019年8月份的数据
--create_time	数据入库	整型		否		示例20190808
--source	数据来源	字符串	2	否	2	cm-移动；cu-联通；ct-电信

drop table t_phone_home_work_rela;
CREATE TABLE  IF NOT EXISTS  t_phone_home_work_rela
(
home_grid_id  VARCHAR(32) not null,
work_grid_id  VARCHAR(32) not null,
fdate  INTEGER not null,
home_city  INTEGER not null,
work_city  INTEGER not null,
age  INTEGER ,
sex  INTEGER ,
adm_reg  INTEGER,
has_auto  INTEGER ,
user_qty  INTEGER not null,
create_time  INTEGER not null,
source  VARCHAR(2),
CONSTRAINT PK PRIMARY KEY (home_grid_id,work_grid_id,fdate,home_city,work_city,source)
) VERSIONS=1,BLOOMFILTER='ROW',SALT_BUCKETS=4,immutable_rows=true;


--出行OD分布t_phone_trip_distribute

--字段名称	备注	类型	长度	是否为空	分区/索引键	特殊说明
--start_grid_id	出发位置栅格id	字符串	32	否
--end_grid_id	到达位置栅格id	字符串	32	否
--start_utype	出发城市用户类型	整型	1	否		1-常住人口；2-流动人口
--end_utype	到达城市用户类型	整型	1	否		1-常住人口；2-流动人口
--start_ptype	起点类型	整型	1	否		0-来访；1-工作地；2-家庭
--end_ptype	终点类型	整型	1	否		0-来访；1-工作地；2-家庭
--start_time	出发时间	整型		否
--end_time	到达时间	整型		否
--time_dim	时间粒度	整型		否		1-5分钟时间片；2-15分钟时间片；3-小时
--trip_mode	出行目的	整型	1	否		1-工作地到家；2-家到工作地；3-其他
--aggtype	出行类型	整型	1	否		1-正常；2-枢纽；3-景点；
--age	年龄	整型		是		999为未知
--sex	性别	整型		是		1-男；2-女；999-未知
--trip_qty	出行量	整型		否
--adm_reg	用户归属地	整型		是		参照城市编码对照表
--create_time	数据入库	整型		否		示例20190808
--start_city	省内出发城市	整型		否	1	参照城市编码对照表
--end_city	省内到达城市	整型		否	2	参照城市编码对照表
--source	数据来源	字符串	2	否	3	cm-移动、cu-联通、ct-电信
--fdate	日期	整型	8	否	4	示例20190808

drop table t_phone_trip_distribute;
CREATE TABLE  IF NOT EXISTS  t_phone_trip_distribute
(
start_grid_id  VARCHAR(32) not null,
end_grid_id  VARCHAR(32) not null,
fdate  INTEGER not null,
start_city  INTEGER not null,
end_city  INTEGER not null,
start_ptype  INTEGER not null,
end_ptype  INTEGER not null,
start_utype  INTEGER not null,
end_utype  INTEGER not null,
age  INTEGER ,
sex  INTEGER ,
adm_reg  INTEGER,
start_time  INTEGER not null,
end_time  INTEGER not null,
trip_mode INTEGER not null,
aggtype INTEGER not null,
trip_qty INTEGER not null,
create_time INTEGER not null,
source  VARCHAR(2),
time_dim INTEGER not null,
CONSTRAINT PK PRIMARY KEY (start_grid_id,end_grid_id,fdate,start_time,end_time,source)
) VERSIONS=1,BLOOMFILTER='ROW',SALT_BUCKETS=12,immutable_rows=true;

drop index  IDX_PHONE_TRIP_DIST_CITY ON t_phone_trip_distribute;
CREATE INDEX if not exists IDX_PHONE_TRIP_DIST_CITY ON t_phone_trip_distribute (start_city,end_city,start_time,end_time)  VERSIONS=1,BLOOMFILTER = 'ROW', SALT_BUCKETS=12;

------------

--字段名称	备注	类型	长度	是否为空	分区/索引键	特殊说明
--grid_id	活动栅格id	字符串	32	否
--user_type	用户类型	整型	10	否		1-常住人口；2-流动人口
--act_time	活动时间，小时	整型		否		时间粒度请看time_dim时间粒度字段
--time_dim	时间粒度	整型		否		1-5分钟时间片；2-15分钟时间片；3-小时
--fdate	日期	整型		否		示例20190802；20190900表示2019年9月份的统计数据
--age	年龄	整型		否		999-未知
--sex	性别	整型		否		1-男；2-女，999-未知
--user_qty	活动人数	整型		否
--adm_reg	用户归属地	整型	32	否		参照城市编码对照表
--create_time	数据入库	整型		否		示例20190808
--city	城市名称	整型		否	1	参照城市编码对照表
--source	数据来源	字符串	2	否	2	cm-移动、cu-联通、ct-电信

CREATE TABLE  IF NOT EXISTS  t_phone_activity (
    grid_id	  VARCHAR(32) not null,
    user_type	 INTEGER not null,
    act_time	活动时间，小时	整型		否		时间粒度请看time_dim时间粒度字段
    time_dim	时间粒度	整型		否		1-5分钟时间片；2-15分钟时间片；3-小时
    fdate	 INTEGER not null,
    age	 INTEGER not null,
    sex	 INTEGER not null,
    user_qty	 INTEGER not null,
    adm_reg	 INTEGER not null,
    create_time	 INTEGER not null,
    city	 INTEGER not null,
    source	 VARCHAR(2)
)



--字段名称	字段描述	类型	长度	是否为空	分区键	特殊说明
--loc_time	定位日期	整型	10	否		unix10位时间戳
--vehicle_id	车辆唯一标识（车牌）	字符串	20	否
--lng	经度，小数点后取6位	浮点	10,6	否		WGS坐标系
--lat	纬度，小数点后取6位	浮点	10,6	否		WGS坐标系
--speed	瞬时速度（km/h）	浮点	4,1	否
--angle	方向角，与正北方向的顺时针夹角（0-359）	整型	3 	否
--is_validate	数据是否有效，	整型	1	否		0表示异常，1表示有效
--company_code	所属公司代码	字符串	10	否		公司代码编码
--altitude	海拔高度，单位m	整型	2	否
--vehicle_color	车辆颜色	整型	1	是		1-蓝；2-黄；3-黑；4-白；5-其他
--total_mileage	车辆总里程数，单位千米	整型	10	是
--alarm_status	报警状态	字符串		是		存储原始数据，保留以后使用
--vehicle_status	车辆状态	字符串		是		存储原始数据，保留以后使用
--city	城市名称	字符串	10	否	1	参照城市编码对照表
--vehicle_type	车辆类型	整型	9	否	2	1-班车客运-scheduled;
--4-货运车-truck;
--8-驾培车-train_car;
--16-包车客运-rented;
--64-危险品车辆-chemical;
--128-其他-other;
--256-泥头车-dump
drop table t_vehicle_gps_truck;
CREATE TABLE  IF NOT EXISTS  t_vehicle_gps_truck
(
  vehicle_id VARCHAR NOT NULL,
  encry_id VARCHAR NOT NULL,
  fdate INTEGER NOT NULL,
  ftime VARCHAR NOT NULL,
  loc_time BIGINT NOT NULL,
  lng   decimal(10,6) NOT NULL,
  lat   decimal(10,6) NOT NULL,
  speed  decimal(4,1) NOT NULL,
  angle  INTEGER NOT NULL,
  company_code  VARCHAR NOT NULL,
  altitude  INTEGER NOT NULL,
  vehicle_color  INTEGER,
  total_mileage  BIGINT,
  city  INTEGER NOT NULL,
  vehicle_status VARCHAR
CONSTRAINT PK PRIMARY KEY (encry_id,fdate,ftime,loc_time)
) VERSIONS=1,BLOOMFILTER='ROW',immutable_rows=true;


drop index  IDX_GPS_TRUCK_VEHICLE_ID ON t_vehicle_gps_truck;
CREATE INDEX if not exists IDX_GPS_TRUCK_VEHICLE_ID ON t_vehicle_gps_truck (fdate,ftime,city,vehicle_color,vehicle_id)  VERSIONS=1,BLOOMFILTER = 'ROW', SALT_BUCKETS=18;

/*
Navicat PGSQL Data Transfer

Source Server         : sutpc-transpass
Source Server Version : 110200
Source Host           : 10.10.201.28:5432
Source Database       : postgres
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 110200
File Encoding         : 65001

Date: 2019-08-31 14:15:19
*/


-- ----------------------------
-- Table structure for t_weather_grid_met
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_weather_grid_met";
CREATE TABLE "public"."t_weather_grid_met" (
"grid_fid" int4,
"fdate" int4,
"period" numeric(20,1),
"t" numeric(20,2),
"slp" numeric(20,1),
"wspd" numeric(20,1),
"wdir" numeric(20,1),
"rhsfc" numeric(20,1),
"rain01m" numeric(20,1),
"rain06m" numeric(20,1),
"rain12m" numeric(20,1),
"rain30m" numeric(20,1),
"rain01h" numeric(20,1),
"rain02h" numeric(20,1),
"rain03h" numeric(20,1),
"rain06h" numeric(20,1),
"rain12h" numeric(20,1),
"rain24h" numeric(20,1),
"rain48h" numeric(20,1),
"rain72h" numeric(20,1),
"v" numeric(20,1),
"city" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_weather_grid_met" IS '网格化的历史天气数据，实时数据的备份';
COMMENT ON COLUMN "public"."t_weather_grid_met"."grid_fid" IS '栅格id';
COMMENT ON COLUMN "public"."t_weather_grid_met"."fdate" IS '日期';
COMMENT ON COLUMN "public"."t_weather_grid_met"."period" IS '时间片|1-288';
COMMENT ON COLUMN "public"."t_weather_grid_met"."t" IS '温度';
COMMENT ON COLUMN "public"."t_weather_grid_met"."slp" IS '海平面气压';
COMMENT ON COLUMN "public"."t_weather_grid_met"."wspd" IS '风速';
COMMENT ON COLUMN "public"."t_weather_grid_met"."wdir" IS '风向';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rhsfc" IS '相对湿度';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain01m" IS '当前分钟的降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain06m" IS '6分钟累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain12m" IS '12分钟累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain30m" IS '30分钟累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain01h" IS '1小时累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain02h" IS '2小时累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain03h" IS '3小时累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain06h" IS '6小时累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain12h" IS '12小时累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain24h" IS '24小时累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain48h" IS '48小时累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."rain72h" IS '72小时累计降雨量';
COMMENT ON COLUMN "public"."t_weather_grid_met"."v" IS '能见度';
COMMENT ON COLUMN "public"."t_weather_grid_met"."city" IS '城市名称|参照城市编码对照表';

-- ----------------------------
-- Alter Sequences Owned By
-- ----------------------------

-- ----------------------------
-- Indexes structure for table t_weather_grid_met
-- ----------------------------
CREATE INDEX "city_id_index" ON "public"."t_weather_grid_met" USING btree ("city");





/*
Navicat PGSQL Data Transfer

Source Server         : sutpc-transpass
Source Server Version : 110200
Source Host           : 10.10.201.28:5432
Source Database       : postgres
Source Schema         : public
Target Server Type    : PGSQL
Target Server Version : 110200
File Encoding         : 65001
Date: 2019-08-31 14:15:01
*/
-- ----------------------------
-- Table structure for t_carbon_em_link
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_carbon_em_link";
CREATE TABLE "public"."t_carbon_em_link" (
"linkfid" int4 NOT NULL,
"fromnode" int4 NOT NULL,
"tonode" int4 NOT NULL,
"co2" numeric,
"co" numeric,
"hc" numeric,
"nox" numeric,
"pm" numeric,
"fc" numeric,
"car_co2" numeric NOT NULL,
"car_co" numeric NOT NULL,
"car_hc" numeric NOT NULL,
"car_nox" numeric NOT NULL,
"car_pm" numeric NOT NULL,
"car_fc" numeric NOT NULL,
"bus_co2" numeric,
"bus_co" numeric,
"bus_hc" numeric,
"bus_nox" numeric,
"bus_pm" numeric,
"bus_fc" numeric,
"truck_co2" numeric,
"truck_co" numeric,
"truck_hc" numeric,
"truck_nox" numeric,
"truck_pm" numeric,
"truck_fc" numeric,
"car_elec_co2" numeric NOT NULL,
"bus_elec_co2" numeric,
"truck_elec_co2" numeric,
"elec_co2" numeric,
"flow" int4,
"hh" int4,
"city" int4,
"fdate" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_carbon_em_link" IS '路段排放计算表';
COMMENT ON COLUMN "public"."t_carbon_em_link"."linkfid" IS '路段编号';
COMMENT ON COLUMN "public"."t_carbon_em_link"."fromnode" IS '起点编号';
COMMENT ON COLUMN "public"."t_carbon_em_link"."tonode" IS '终点编号';
COMMENT ON COLUMN "public"."t_carbon_em_link"."co2" IS 'co2排放指数';
COMMENT ON COLUMN "public"."t_carbon_em_link"."co" IS 'co排放指数';
COMMENT ON COLUMN "public"."t_carbon_em_link"."hc" IS 'hc排放指数';
COMMENT ON COLUMN "public"."t_carbon_em_link"."nox" IS 'nox排放指数';
COMMENT ON COLUMN "public"."t_carbon_em_link"."pm" IS 'pm排放指数';
COMMENT ON COLUMN "public"."t_carbon_em_link"."fc" IS '燃油总量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."car_co2" IS '小汽车co2总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."car_co" IS '小汽车co总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."car_hc" IS '小汽车hc总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."car_nox" IS '小汽车nox总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."car_pm" IS '小汽车pm总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."car_fc" IS '小汽车燃油总量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."bus_co2" IS '公交车co2总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."bus_co" IS '公交车co总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."bus_hc" IS '公交车hc总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."bus_nox" IS '公交车nox总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."bus_pm" IS '公交车pm总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."bus_fc" IS '公交车燃油总量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."truck_co2" IS '公交车co2总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."truck_co" IS '公交车co总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."truck_hc" IS '公交车hc总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."truck_nox" IS '公交车nox总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."truck_pm" IS '公交车pm总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."truck_fc" IS '公交车燃油总量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."car_elec_co2" IS '新能源小汽车co2排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."bus_elec_co2" IS '新能源公交车co2排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."truck_elec_co2" IS '新能源货车co2排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."elec_co2" IS '新能源q汽车co2总排放量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."flow" IS '路段车流量';
COMMENT ON COLUMN "public"."t_carbon_em_link"."hh" IS '小时';
COMMENT ON COLUMN "public"."t_carbon_em_link"."city" IS '城市编号';
COMMENT ON COLUMN "public"."t_carbon_em_link"."fdate" IS '日期';

-- ----------------------------
-- Alter Sequences Owned By
-- ----------------------------

-- ----------------------------
-- Indexes structure for table t_carbon_em_link
-- ----------------------------
CREATE INDEX "city_link" ON "public"."t_carbon_em_link" USING btree ("city");




