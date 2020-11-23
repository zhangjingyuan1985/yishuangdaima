#!/bin/bash
cd  /var/lib/hadoop-hdfs
rm -rf hive_$1
cp -r hive_db hive_$1

cd /var/lib/hadoop-hdfs/hive_$1

sed -i "s/node2.sutpc.cc/bigdata-4.sutpc.com/g"  transpass_tag.hql
sed -i "s/node2.sutpc.cc/bigdata-4.sutpc.com/g"  transpass_std.hql
sed -i "s/node2.sutpc.cc/bigdata-4.sutpc.com/g"  transpaas_std_dev.hql


sed -i "s/transpass/transpaas_$1/g"  transpass_tag.hql
sed -i "s/transpass/transpaas_$1/g"  transpass_std.hql
sed -i "s/transpaas/transpaas_$1/g"  transpaas_std_dev.hql


hive -f transpass_tag.hql
hive -f transpass_std.hql
hive -f transpaas_std_dev.hql

hive -e "show databases;"
