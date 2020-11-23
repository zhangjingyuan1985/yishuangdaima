package com.sutpc.bigdata.job.batch.real

import com.sutpc.bigdata.utils.CharsetUtils
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/10/28  19:12
  */

case class EtlTask(
                         task_name: String,
                         input: String,
                         output: String,
                         encode: String = "UTF-8",
                         parsed_date: String
                       )

case class EtlTask_cj(
                    task_name: String,
                    input: String,
                    output: String,
                    encode: String = "UTF-8",
                    parsed_date: String,
                    batchSize: String
                  )


trait BaseTask[T] extends Serializable {

  def hdfs2RDD(inputPath: String, spark: SparkSession, task: EtlTask) = {
    println("读取【" + inputPath + "】数据")

    val sc = spark.sparkContext
    var charset = task.encode

    if (task.encode.equals("#")) {
      println("检测编码格式")
      val samples = spark.read.textFile(inputPath).filter(!_.isEmpty).take(1)
      charset = CharsetUtils.getEncode(samples(0))
    }

    println("编码格式" + charset)

    sc.hadoopFile(inputPath, classOf[TextInputFormat],
      classOf[LongWritable], classOf[Text])
      .map(pair => new String(pair._2.getBytes, 0, pair._2.getLength, charset))
  }

  def filter(x: String): Boolean

  def execute()

  def smartLoop(inputPath: String, spark: SparkSession) = {
    val path = new Path(inputPath)
    val hadoopConf = spark.sparkContext.hadoopConfiguration
    val hdfs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)

    var flag = false


    if (hdfs.exists(path)) {

      val stats = hdfs.listStatus(path)
      //      val arr = hdfs.listFiles(path, false)

      //      import collection.JavaConversions._
      //      import collection.convert.wrapAsScala._ //只是完成 Java  到 Scala 集合的隐式转换

      for (i <- 0 to stats.length - 1) {
        if (stats(i).isFile) {
          flag = false
        } else if (stats(i).isDirectory) {
          flag = true
        } else if (stats(i).isSymlink) {
          flag = false
        }
      }


      //全是目录
      if (flag) {
        for (i <- 0 to stats.length - 1) {
          val e = stats(i)
          println("循环读取子目录：" + e.getPath.toString)
          process(e.getPath.toString)
        }
      }
      if (!flag) {
        println("读取单个目录")
        process(inputPath)
      }

    } else {
      println("输入路径【" + path + "】不存在，已跳过")
    }


  }

  def process(dir: String)

  def cache(res: Dataset[T]): Unit = {
    //    res.persist(StorageLevel.MEMORY_AND_DISK_SER)
    //    println("输入数据量【" + res.count() + " 】")
    res.show(false)
  }

}
