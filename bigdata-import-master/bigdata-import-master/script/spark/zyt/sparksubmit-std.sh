#!/usr/bin/env bash

#####################标准库 hive##################---

nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 6  --executor-memory 6g --driver-memory 1g --conf spark.defalut.parallelism=90 --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/vehicle/gps/bus/440300/jiaowei/2019/07 s_vehicle_gps_bus UTF-8"  >>/root/spark-logs/s_vehicle_gps_bus.log 2>&1  &

30 00 * * * nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 1g --conf spark.defalut.parallelism=90 --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/vehicle/gps/bus/440300/jiaowei/`date -d "1 days ago" +\%Y/\%m/\%d` s_vehicle_gps_bus_realtime UTF-8"  >>/root/spark-logs/s_vehicle_gps_bus_realtime.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 1g --conf spark.defalut.parallelism=90 --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/vehicle/gps/bus/440300/jiaowei/2019/07/18 s_vehicle_gps_bus_realtime UTF-8"  >>/root/spark-logs/s_vehicle_gps_bus_realtime.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/vehicle/gps/taxi/440300/jiaowei/2019/03  s_vehicle_gps_taxi_jiaowei UTF-8"  >>/root/spark-logs/s_vehicle_gps_taxi.log 2>&1  &

nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/vehicle/gps/taxi/440300/jiaowei/`date -d "1 days ago" +\%Y/\%m/\%d` s_vehicle_gps_taxi_realtime UTF-8"  >>/root/spark-logs/s_vehicle_gps_taxi_realtime.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 7g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/gps/baidu/shenzhen/2019/07/01 s_vehicle_gps_drive UTF-8"  >>/root/spark-logs/s_vehicle_gps_drive.log 2>&1  &

#复制到指标库
nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 8g --driver-memory 1g  --conf spark.yarn.executor.memoryOverhead=1g  --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/gps/baidu/shenzhen/2019/07/01 s_vehicle_gps_drive UTF-8"  >>/root/spark-logs/s_vehicle_gps_drive.log 2>&1  &

nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 7g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/vehicle/gps/drive/guangdong/careland/2015-5min/01 s_vehicle_gps_drive_careland UTF-8"  >>/root/spark-logs/s_vehicle_gps_drive_careland.log 2>&1  &



nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 6  --executor-memory 5g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/gps/order/shenzhen/2019/06 s_vehicle_gps_order UTF-8"  >>/root/spark-logs/s_vehicle_gps_order.log 2>&1  &

10 00 * * * nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 6  --executor-memory 5g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/vehicle/gps/order/440300/jiaowei/`date -d "1 days ago" +\%Y/\%m/\%d` s_vehicle_gps_order_realtime UTF-8"  >>/root/spark-logs/s_vehicle_gps_order_realtime.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 3g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/gps/truck/shenzhen/2019/06 s_vehicle_gps_others_truck UTF-8"  >>/root/spark-logs/s_vehicle_gps_others_truck.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 3g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/ic/metro/shenzhen/2017/10 s_transit_people_ic GBK"  >>/root/spark-logs/s_transit_people_ic.log 2>&1  &

nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 1g --conf spark.defalut.parallelism=90 --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/share_bike/shenzhen/2018/10/a_share_data.sql s_bike_switch_lock UTF-8"  >>/root/spark-logs/s_bike_switch_lock.log 2>&1  &

#复制到指标库
nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 1g --conf spark.defalut.parallelism=90 --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/share_bike/switch_lock/shenzhen/2018/10  t_bike_switch_lock UTF-8"  >>/root/spark-logs/t_bike_switch_lock.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 4  --executor-memory 3g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/gps/truck/shenzhen/2019/06  s_vehicle_gps_truck UTF-8"  >>/root/spark-logs/s_vehicle_gps_truck.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 4  --executor-memory 3g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/gps/charter/shenzhen/2019/06,/data/gps/coach/shenzhen/2019/06,/data/gps/danger/shenzhen/2019/06,/data/gps/driving/shenzhen/2019/06,/data/gps/dumper/shenzhen/2019/06,/data/gps/others/shenzhen/2019/06  s_vehicle_gps_others UTF-8"  >>/root/spark-logs/s_vehicle_gps_others.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 4  --executor-memory 3g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/share_bike/loc/mobike/shenzhen/2017/04  s_bike_loc UTF-8"  >>/root/spark-logs/s_bike_loc.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/msignal/guangdong/activity/cu/2017/12  s_phone_inter_activity UTF-8"  >>/root/spark-logs/s_phone_inter_activity.log 2>&1  &

nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/activity/cm/2019/03    s_phone_inter_activity_cm UTF-8"  >>/root/spark-logs/s_phone_inter_activity_cm.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/road/recognition/440300/2018/10-11   s_road_plate_recognition2018 UTF-8"  >>/root/spark-logs/s_road_plate_recognition2018.log 2>&1  &

nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/road/recognition/440300/2019/09   s_road_plate_recognition2019 UTF-8"  >>/root/spark-logs/s_road_plate_recognition2019.log 2>&1  &

nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 3  --executor-memory 5g --driver-memory 1g --conf spark.defalut.parallelism=75 --conf spark.sql.adaptive.enabled=true --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h  --conf spark.blacklist.enabled=false   --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/home_count/cu/2017/12  s_phone_inter_resident_cu  UTF-8"  >>/root/spark-logs/s_phone_inter_resident_cu.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 3  --executor-memory 5g --driver-memory 1g --conf spark.defalut.parallelism=75 --conf spark.sql.adaptive.enabled=true --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h  --conf spark.blacklist.enabled=false   --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/home_count/cu/2017/12  s_phone_inter_tourist_cu  UTF-8"  >>/root/spark-logs/s_phone_inter_tourist.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 3  --executor-memory 6g --driver-memory 2g --conf spark.defalut.parallelism=75 --conf spark.sql.adaptive.enabled=true --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h  --conf spark.blacklist.enabled=false   --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/home_count/cm/2019/03  s_phone_inter_resident_cm  UTF-8"  >>/root/spark-logs/s_phone_inter_resident_cm.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 3  --executor-memory 6g --driver-memory 2g --conf spark.defalut.parallelism=75 --conf spark.sql.adaptive.enabled=true --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h  --conf spark.blacklist.enabled=false   --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/home_count/cm/2019/03  s_phone_inter_tourist_cm  UTF-8"  >>/root/spark-logs/s_phone_inter_tourist_cm.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 3  --executor-memory 5g --driver-memory 1g --conf spark.defalut.parallelism=75 --conf spark.sql.adaptive.enabled=true --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h  --conf spark.blacklist.enabled=false   --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/work_count/cu/2017/12  s_phone_inter_work_cu  UTF-8"  >>/root/spark-logs/s_phone_inter_work_cu.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 1  --executor-memory 1g --driver-memory 1g --conf spark.sql.adaptive.enabled=true --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h  --conf spark.blacklist.enabled=false   --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/work_count/cm/2019/03 s_phone_inter_work_cm  UTF-8"  >>/root/spark-logs/s_phone_inter_work_cm.log 2>&1  &


#手机信令 /data/origin/phone/msignal/guangdong/work_count/cu/2017/12

nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 1  --executor-memory 1g --driver-memory 1g --conf spark.sql.adaptive.enabled=true --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h  --conf spark.blacklist.enabled=false   --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/home_work_count/cu/2017/12 s_phone_inter_home_work_cu  UTF-8"  >>/root/spark-logs/s_phone_inter_home_work_cu.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 1  --executor-memory 1g --driver-memory 1g --conf spark.sql.adaptive.enabled=true --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h  --conf spark.blacklist.enabled=false   --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/home_work_count/cm/2019/03 s_phone_inter_home_work_cm  UTF-8"  >>/root/spark-logs/s_phone_inter_home_work_cm.log 2>&1  &



nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 3  --executor-memory 5g --driver-memory 1g --conf spark.defalut.parallelism=75 --conf spark.sql.adaptive.enabled=true --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h  --conf spark.blacklist.enabled=false   --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/trip/cu/2017/12 s_phone_inter_trip_cu  UTF-8"  >>/root/spark-logs/s_phone_inter_trip_cu.log 2>&1  &


nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 4  --executor-memory 6g --driver-memory 2g  --conf spark.sql.adaptive.enabled=true --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h  --conf spark.blacklist.enabled=false   --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar '10.10.201.8,10.10.201.11,10.10.201.12:2181' /data/origin/phone/msignal/guangdong/trip/cm/2019/03 s_phone_inter_trip_cm  UTF-8"  >>/root/spark-logs/s_phone_inter_trip_cm.log 2>&1  &

# 地磁清洗脚本
nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.Hdfs2HiveJob2 --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 6  --executor-memory 5g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  /var/lib/hadoop-hdfs/jars-kino/spark-sutpc-1.0-SNAPSHOT.jar s_road_geomagnetic_detection  /data/origin/road/flow/dici/440300/ transpaas_440300_std.s_road_geomagnetic_detection UTF-8 2017/10/01"  >>/var/lib/hadoop-hdfs/s_road_geomagnetic_detection.log 2>&1  &