#!/bin/bash
#--------------------------------------------
# hive元数据导出脚本
#--------------------------------------------

databases=$(hive -e "show databases; exit;")

for database in $databases;
do
         echo "create database if not exists $database;" >> $1/$database.hql
         echo "use $database;" >> $1/$database.hql
        #获取hive建表语句
        tables=$(hive -e "use $database; show tables;")
        for table in $tables;
        do
                echo "--=========== db: $database , table: $table ==========="
                echo "--=========== db: $database , table: $table ===========" >> $1/$database.hql
                echo "$(hive -e "use $database;show create table $table; " >> $1/$database.hql
        done
done



tables=$(hive -e "use transpaas_cj; show tables;")
echo "create database if not exists transpaas_cj;" >> $1/$database.hql
for table in $tables;
do
        echo "--=========== db: transpaas_cj , table: $table ==========="
        echo "--=========== db: transpaas_cj , table: $table ===========" >> $1/$database.hql
        echo "$(hive -e "use $database;show create table $table;");" >> $1/$database.hql
done