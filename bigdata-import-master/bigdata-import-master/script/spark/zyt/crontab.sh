#!/usr/bin/env bash
#两客一危
40 00 * * * nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.real.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 6  --executor-memory 5g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  /var/lib/hadoop-hdfs/jars-kino/spark-sutpc-1.0-SNAPSHOT.jar  s_vehicle_gps_others_real  /data/gps_key_realtime/charterbus/440300,/data/gps_key_realtime/danger/440300,/data/gps_key_realtime/CoachBus/440300,/data/gps_key_realtime/driving/440300,/data/gps_key_realtime/dumper/440300,/data/gps_key_realtime/freight/440300,/data/gps_key_realtime/others/440300   transpaas_440300_std_dev.s_vehicle_gps_others_real,transpaas_440300_std.s_vehicle_gps_others UTF-8 `date -d "1 days ago" +\%Y/\%m/\%d`"  >>/var/lib/hadoop-hdfs/s_vehicle_gps_others_real.log 2>&1  &


#网约车
10 00 * * * nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.real.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 6 --executor-memory 5g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4 --conf spark.task.maxFailures=8 --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC" /var/lib/hadoop-hdfs/jars-kino/spark-sutpc-1.0-SNAPSHOT.jar s_vehicle_gps_order_real /data/origin/vehicle/gps/order/440300/jiaowei transpaas_440300_std_dev.s_vehicle_gps_order_real,transpaas_440300_std.s_vehicle_gps_order UTF-8 `date -d "1 days ago" +\%Y/\%m/\%d`"  >>/var/lib/hadoop-hdfs/s_vehicle_gps_order_real.log 2>&1  &



#出租车
20 00 * * * nohup su - hdfs -c "spark-submit --master yarn --deploy-mode client --class com.sutpc.bigdata.job.batch.real.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 6  --executor-memory 5g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  /var/lib/hadoop-hdfs/jars-kino/spark-sutpc-1.0-SNAPSHOT.jar  s_vehicle_gps_taxi_real  /data/origin/vehicle/gps/taxi/440300/jiaowei transpaas_440300_std_dev.s_vehicle_gps_taxi_real,transpaas_440300_std.s_vehicle_gps_taxi UTF-8 `date -d "1 days ago" +\%Y/\%m/\%d`"  >>/var/lib/hadoop-hdfs/s_vehicle_gps_taxi_real.log 2>&1  &



#公交车
30 00 * * * nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.real.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 6  --executor-memory 5g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  /var/lib/hadoop-hdfs/jars-kino/spark-sutpc-1.0-SNAPSHOT.jar  s_vehicle_gps_bus_real  /data/origin/vehicle/gps/bus/440300/jiaowei transpaas_440300_std_dev.s_vehicle_gps_bus_real,transpaas_440300_std.s_vehicle_gps_bus UTF-8 `date -d "1 days ago" +\%Y/\%m/\%d`"  >>/var/lib/hadoop-hdfs/s_vehicle_gps_bus_real.log 2>&1  &





#视频AI-行人特征识别
10 * * * * nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.real.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 6  --executor-memory 5g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  /var/lib/hadoop-hdfs/jars-kino/spark-sutpc-1.0-SNAPSHOT.jar  s_vai_pedestrian_feature  /data/origin/vai/qx/traffic_pedestrians  transpaas_440300_std.s_vai_pedestrian_feature UTF-8 `date -d "-1 hours" +\%Y/\%m/\%d/\%H`"  >>/var/lib/hadoop-hdfs/s_vai_pedestrian_feature.log 2>&1  &


#视频AI-车辆特征识别
10 * * * * nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.real.Hdfs2HiveJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 6  --executor-memory 5g --driver-memory 1g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  /var/lib/hadoop-hdfs/jars-kino/spark-sutpc-1.0-SNAPSHOT.jar  s_vai_vehicle_feature  /data/origin/vai/qx/traffic_vehicles transpaas_440300_std.s_vai_vehicle_feature UTF-8 `date -d "-1 hours" +\%Y/\%m/\%d/\%H`"  >>/var/lib/hadoop-hdfs/s_vai_vehicle_feature.log 2>&1  &


