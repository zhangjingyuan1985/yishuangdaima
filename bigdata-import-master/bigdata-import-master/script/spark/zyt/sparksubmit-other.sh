#!/usr/bin/env bash


###############################数据量统计
10 12 * * * nohup su - hdfs -c "spark-submit --master yarn  --deploy-mode client --class com.sutpc.bigdata.job.batch.std.HdfsStatJob --files /etc/hbase/conf/hbase-site.xml --executor-cores 5 --num-executors 5  --executor-memory 6g --driver-memory 3g --conf spark.yarn.maxAppAttempts=4  --conf spark.task.maxFailures=8     --conf spark.yarn.am.attemptFailuresValidityInterval=1h --conf spark.yarn.executor.failuresValidityInterval=1h --conf spark.sql.adaptive.enabled=true    --conf spark.yarn.max.executor.failures=24 --conf spark.blacklist.enabled=false    --conf "spark.executor.extraJavaOptions=-XX:+UseG1GC"  ./jars_zyt/spark-sutpc-1.0-SNAPSHOT.jar `date -d "1 days ago" +\%Y\%m\%d`"  >>/root/spark-logs/HdfsStatJob.log 2>&1  &

