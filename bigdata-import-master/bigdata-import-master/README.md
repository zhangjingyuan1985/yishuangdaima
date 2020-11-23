# bigdata-import 
大数据ELT程序


# 目录结构
script 
    
    ddl 
    
        hive ddl语句（以开发人员姓名拼音作为后缀加以区分） 
        std 为hive标准库
        tag 为hive指标库
    spark
        sparksubmit 作业提交脚本（以开发人员姓名拼音作为后缀加以区分）

src 源码

    common 公共常量类
    executor 具体ETL数据解析、清洗处理包
    job 作业执行入口
    util 工具类
    jdbc 读取作业相关维度表
    schema hive表映射的实体类
    

# 包含：

## 1、大数据导入脚本



## 2、标准化程序代码
